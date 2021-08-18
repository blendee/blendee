package org.blendee.jdbc.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import org.blendee.internal.U;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.Configure;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.Metadata;
import org.blendee.jdbc.PrimaryKeyMetadata;
import org.blendee.jdbc.StoredIdentifier;
import org.blendee.jdbc.TableMetadata;
import org.blendee.jdbc.TablePath;

/**
 * Blendee が使用する {@link Metadata} の標準実装クラスです。
 * @author 千葉 哲嗣
 */
public class JDBCMetadata implements Metadata {

	private static final String[] tableTypes = { "TABLE", "VIEW" };

	private static final char[] illegalChars = "!@#$%^&*()-=+\\|`~[]{};:'\",.<>/? ".toCharArray();

	private final StoredIdentifier identifier;

	private final Configure config = ContextManager.get(BlendeeManager.class).getConfigure();

	/**
	 */
	public JDBCMetadata() {
		try {
			var metadata = connection().getMetaData();

			identifier = StoredIdentifier.getInstance(
				metadata.storesUpperCaseIdentifiers(),
				metadata.storesLowerCaseIdentifiers());
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public TablePath[] getTables(String schemaName) {
		if (!config.containsSchemaName(schemaName))
			//設定されていないスキーマ名です
			throw new IllegalArgumentException("Schema name [" + schemaName + "] not found");

		try (var result = connection().getMetaData()
			.getTables(
				null,
				identifier.regularize(schemaName),
				null,
				tableTypes)) {
			var tables = new ArrayList<String>();
			while (result.next()) {
				var tableName = result.getString("TABLE_NAME");
				//もし、使用不可となっている文字を含む場合、使用できるテーブルには含めない
				if (!checkObjectName(tableName)) continue;
				tables.add(tableName);
			}

			var paths = new TablePath[tables.size()];
			for (var i = 0; i < paths.length; i++) {
				paths[i] = new TablePath(schemaName, tables.get(i));
			}

			return paths;
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public TableMetadata getTableMetadata(TablePath path) {
		return tableMetadata(path).orElse(null);
	}

	@Override
	public Optional<TableMetadata> tableMetadata(TablePath path) {
		try (var result = connection().getMetaData()
			.getTables(null, identifier.regularize(path.getSchemaName()), identifier.regularize(path.getTableName()), null)) {

			if (!result.next()) return Optional.empty();

			return Optional.of(new ConcreteTableMetadata(result));
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public ColumnMetadata[] getColumnMetadatas(TablePath path) {
		try (var result = connection().getMetaData()
			.getColumns(null, identifier.regularize(path.getSchemaName()), identifier.regularize(path.getTableName()), null)) {

			var columns = new LinkedList<ColumnMetadata>();
			while (result.next()) {
				columns.add(new ConcreteColumnMetadata(result));
			}

			return columns.toArray(new ConcreteColumnMetadata[columns.size()]);
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public PrimaryKeyMetadata getPrimaryKeyMetadata(TablePath path) {
		return primaryKeyMetadata(path).orElse(null);
	}

	@Override
	public Optional<PrimaryKeyMetadata> primaryKeyMetadata(TablePath path) {
		try (var result = connection().getMetaData()
			.getPrimaryKeys(
				null,
				identifier.regularize(path.getSchemaName()),
				identifier.regularize(path.getTableName()))) {

			String name = null;
			var columnList = new ArrayList<Column>();
			while (result.next()) {
				name = name == null ? result.getString("PK_NAME") : name;
				columnList.add(
					new Column(
						result.getInt("KEY_SEQ"),
						result.getString("COLUMN_NAME")));
			}

			if (columnList.size() == 0) return Optional.empty();

			Collections.sort(columnList);
			var size = columnList.size();
			var columnNames = new String[size];
			for (var i = 0; i < size; i++) {
				columnNames[i] = columnList.get(i).name;
			}

			return Optional.of(new SimplePrimaryKeyMetadata(name, columnNames, false));
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public TablePath[] getResourcesOfImportedKey(TablePath path) {
		try {
			return getOtherResources(
				connection().getMetaData()
					.getImportedKeys(
						null,
						identifier.regularize(path.getSchemaName()),
						identifier.regularize(path.getTableName())),
				"PKTABLE_SCHEM",
				"PKTABLE_NAME");
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public TablePath[] getResourcesOfExportedKey(TablePath path) {
		try {
			return getOtherResources(
				connection().getMetaData()
					.getExportedKeys(
						null,
						identifier.regularize(path.getSchemaName()),
						identifier.regularize(path.getTableName())),
				"FKTABLE_SCHEM",
				"FKTABLE_NAME");
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}
	}

	@Override
	public CrossReference[] getCrossReferences(TablePath exportedTable, TablePath importedTable) {
		var resultList = new LinkedList<CrossReferenceResult>();
		try (var jdbcResult = connection().getMetaData()
			.getCrossReference(
				null,
				identifier.regularize(exportedTable.getSchemaName()),
				identifier.regularize(exportedTable.getTableName()),
				null,
				identifier.regularize(importedTable.getSchemaName()),
				identifier.regularize(importedTable.getTableName()))) {

			while (jdbcResult.next()) {
				resultList.add(new CrossReferenceResult(jdbcResult));
			}
		} catch (SQLException e) {
			throw config.getErrorConverter().convert(e);
		}

		Collections.sort(resultList);

		var builders = new LinkedList<CrossReferenceBuilder>();
		CrossReferenceBuilder builder = null;
		for (var result : resultList) {
			if (result.seq.intValue() == 1) {
				builder = new CrossReferenceBuilder(result);
				builders.add(builder);
			}

			//KEY_SEQ に 1 がありません
			if (builder == null) throw new IllegalStateException("KEY_SEQ: 1 not found");
			builder.add(result);
		}

		var references = new CrossReference[builders.size()];
		int index = 0;
		for (Iterator<CrossReferenceBuilder> i = builders.iterator(); i.hasNext(); index++)
			references[index] = i.next().build();

		return references;
	}

	/*
	 * DatabaseMetaData の各検索に条件として使用するスキーマ名、テーブル名の識別子パターンは、 JDBC の実装によっては
	 * 大文字小文字が厳密に適用される可能性があり、その場合は実際には存在するにもかかわらず結果が取得できない
	 * そこで、 DatabaseMetaData の情報を使用して登録された識別子名に変換後検索に使用するようにする
	 * ただし、データベースが全角英字識別子の大文字化小文字化を行わない場合、実際には存在するにもかかわらず
	 * 見つからなくなってしまうので、そのようなデータベースでは、全角英数を識別子内で使用しないこと
	 */
	@Override
	public StoredIdentifier getStoredIdentifier() {
		return identifier;
	}

	private Connection connection() {
		Connection[] container = { null };
		BlendeeManager.getConnection().lend(c -> container[0] = c);
		return container[0];
	}

	private static final TablePath[] getOtherResources(
		ResultSet result,
		String schemaColumnName,
		String tableColumnName)
		throws SQLException {
		try {
			var targets = new TreeSet<TablePath>();
			while (result.next()) {
				var tableName = result.getString(tableColumnName);
				//もし、使用不可となっている文字を含む場合、使用できるテーブルには含めない
				if (!checkObjectName(tableName)) continue;
				var path = new TablePath(result.getString(schemaColumnName), tableName);
				targets.add(path);
			}
			return targets.toArray(new TablePath[targets.size()]);
		} finally {
			U.close(result);
		}
	}

	private static boolean checkObjectName(String name) {
		for (var i = 0; i < illegalChars.length; i++) {
			if (name.indexOf(illegalChars[i]) != -1) return false;
		}

		return true;
	}

	/**
	 * あるテーブルから他の同一テーブルを複数参照している場合、<br>
	 * {@link DatabaseMetaData#getCrossReference(String, String, String, String, String, String)} の結果順序がおかしくなるのを防ぐクラスです。
	 * <p>
	 * このクラスのインスタンスが正しく機能するためには、外部キーに名前がつけられている必要があります。
	 */
	private static class CrossReferenceResult implements Comparable<CrossReferenceResult> {

		final String pkName;

		final String fkName;

		final String pkSchema;

		final String pkTable;

		final String fkSchema;

		final String fkTable;

		final String pkColumnName;

		final String fkColumnName;

		final Integer seq;

		private CrossReferenceResult(ResultSet result) throws SQLException {
			pkName = result.getString("PK_NAME");
			fkName = result.getString("FK_NAME");
			pkSchema = result.getString("PKTABLE_SCHEM");
			pkTable = result.getString("PKTABLE_NAME");
			fkSchema = result.getString("FKTABLE_SCHEM");
			fkTable = result.getString("FKTABLE_NAME");
			pkColumnName = result.getString("PKCOLUMN_NAME");
			fkColumnName = result.getString("FKCOLUMN_NAME");
			seq = Integer.valueOf(result.getInt("KEY_SEQ"));
		}

		@Override
		public int compareTo(CrossReferenceResult target) {
			var result = fkSchema.compareTo(target.fkSchema);
			if (result != 0) return result;
			result = fkTable.compareTo(target.fkTable);
			if (result != 0) return result;
			if (fkName != null && target.fkName != null) {
				result = fkName.compareTo(target.fkName);
				if (result != 0) return result;
			}
			result = seq.compareTo(target.seq);
			if (result != 0) return result;
			//FK_NAME が未設定のため、同一テーブルへの複数 FK 参照が解決できません
			throw new IllegalStateException("Multiple FK references to the same table can not be resolved. (FK_NAME not set)");
		}
	}

	private static class CrossReferenceBuilder {

		private final String pkName;

		private final String fkName;

		private final TablePath pkTable;

		private final TablePath fkTable;

		private final List<String> pkColumnNames = new ArrayList<>();

		private final List<String> fkColumnNames = new ArrayList<>();

		private CrossReferenceBuilder(CrossReferenceResult result) {
			pkName = result.pkName;
			fkName = result.fkName;
			pkTable = new TablePath(result.pkSchema, result.pkTable);
			fkTable = new TablePath(result.fkSchema, result.fkTable);
		}

		private void add(CrossReferenceResult result) {
			pkColumnNames.add(result.pkColumnName);
			fkColumnNames.add(result.fkColumnName);
		}

		private CrossReference build() {
			return new SimpleCrossReference(
				pkName,
				fkName,
				pkTable,
				fkTable,
				pkColumnNames.toArray(new String[pkColumnNames.size()]),
				fkColumnNames.toArray(new String[fkColumnNames.size()]),
				false);
		}
	}

	private static class Column implements Comparable<Column> {

		private final int sequence;

		private final String name;

		private Column(int sequence, String name) {
			this.sequence = sequence;
			this.name = name;
		}

		@Override
		public int compareTo(Column column) {
			return sequence - column.sequence;
		}
	}
}
