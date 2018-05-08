package org.blendee.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.blendee.internal.CollectionMap;
import org.blendee.internal.CollectionMapMap;
import org.blendee.internal.U;
import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.Metadata;
import org.blendee.jdbc.PrimaryKeyMetadata;
import org.blendee.jdbc.TableMetadata;
import org.blendee.jdbc.TablePath;
import org.blendee.jdbc.impl.SimpleCrossReference;

/**
 * 実際のデータベース以外から定義情報を取得し、 {@link Metadata} として使用できるクラスです。
 * @author 千葉 哲嗣
 */
public class VirtualSpace implements Metadata {

	private static final VirtualTable nullTable = new NullVirtualTable();

	private static final ColumnMetadata[] emptyColumnMetadataArray = {};

	private final Map<TablePath, TableSource> tables = new HashMap<>();

	private final CollectionMap<String, TablePath> schemas = new CollectionMap<String, TablePath>() {

		@Override
		protected Collection<TablePath> createNewCollection() {
			return new LinkedHashSet<>();
		}
	};

	private final Map<TablePath, VirtualTable> virtualTables = new HashMap<>();

	private final CollectionMapMap<TablePath, TablePath, CrossReference> crossReferences = CollectionMapMap.newInstance();

	private boolean started = false;

	/**
	 * テーブル情報を追加します。
	 * @param table 新しいテーブル情報
	 */
	public synchronized void addTable(TableSource table) {
		TablePath path = table.getTablePath();
		tables.put(path, table);
		schemas.put(path.getSchemaName(), path);
	}

	/**
	 * 追加済みのテーブル情報を元に {@link Metadata} として使用できるようにします。
	 * @param depends このインスタンスが持つ定義情報が参照する {@link Metadata}
	 * @throws IllegalStateException 既にこのメソッドが実行されている場合
	 */
	public synchronized void start(Metadata depends) {
		if (started) throw new IllegalStateException("既にスタートしています");
		CollectionMap<TablePath, TablePath> exported = new CollectionMap<TablePath, TablePath>() {

			@Override
			protected Collection<TablePath> createNewCollection() {
				return new HashSet<>();
			}
		};

		for (TableSource table : tables.values())
			processForExported(table, exported, depends);

		for (TableSource table : tables.values())
			registTable(table, exported);

		started = true;
	}

	/**
	 * 既に {@link #start(Metadata)} が実行しているかどうかを検査します。
	 * @return 既に {@link #start(Metadata)} が実行している場合、 true
	 */
	public synchronized boolean isStarted() {
		return started;
	}

	@Override
	public synchronized TablePath[] getTables(String schemaName) {
		Collection<TablePath> paths = schemas.get(schemaName);
		return paths.toArray(new TablePath[paths.size()]);
	}

	@Override
	public TableMetadata getTableMetadata(TablePath path) {
		return getTable(path).getTableMetadata();
	}

	@Override
	public ColumnMetadata[] getColumnMetadatas(TablePath path) {
		return getTable(path).getColumnMetadatas();
	}

	@Override
	public PrimaryKeyMetadata getPrimaryKeyMetadata(TablePath path) {
		return getTable(path).getPrimaryKey();
	}

	@Override
	public TablePath[] getResourcesOfImportedKey(TablePath path) {
		return getTable(path).getResourcesOfImportedKey();
	}

	@Override
	public TablePath[] getResourcesOfExportedKey(TablePath path) {
		return getTable(path).getResourcesOfExportedKey();
	}

	@Override
	public synchronized CrossReference[] getCrossReferences(
		TablePath exported,
		TablePath imported) {
		Collection<CrossReference> references = crossReferences.get(imported).get(exported);
		return references.toArray(new CrossReference[references.size()]);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private synchronized VirtualTable getTable(TablePath path) {
		VirtualTable table = virtualTables.get(path);
		if (table == null) return nullTable;
		return table;
	}

	private void registTable(
		TableSource table,
		CollectionMap<TablePath, TablePath> exportedMap) {
		ForeignKeySource[] fks = table.getForeignKeySources();

		Set<TablePath> imported = new LinkedHashSet<>();
		for (ForeignKeySource fk : fks)
			imported.add(fk.getImportedTable());

		TablePath[] importedPaths = imported.toArray(new TablePath[imported.size()]);

		TablePath path = table.getTablePath();

		Collection<TablePath> exported = exportedMap.get(path);
		TablePath[] exportedPaths = exported.toArray(new TablePath[exported.size()]);

		PrimaryKeySource pk = table.getPrimaryKeySource();

		VirtualTable virtualTable;
		if (pk == null) {
			virtualTable = new VirtualTable(
				table.getTabelMetadata(),
				table.getColumnMetadatas(),
				null,
				importedPaths,
				exportedPaths);
		} else {
			virtualTable = new VirtualTable(
				table.getTabelMetadata(),
				table.getColumnMetadatas(),
				pk,
				importedPaths,
				exportedPaths);
		}

		virtualTables.put(path, virtualTable);
	}

	private void processForExported(
		TableSource table,
		CollectionMap<TablePath, TablePath> exported,
		Metadata depends) {
		ForeignKeySource[] fks = table.getForeignKeySources();
		for (ForeignKeySource fk : fks) {
			TablePath importedTable = fk.getImportedTable();
			exported.put(importedTable, table.getTablePath());

			TableSource pkTableBase = tables.get(importedTable);
			TableSource pkTable;
			if (pkTableBase == null || pkTableBase.getPrimaryKeySource() == null) {
				pkTable = createPrimaryKeyTableInformation(depends, importedTable);
			} else {
				pkTable = pkTableBase;
			}

			String[] pkColumns = fk.getPKColumns();
			if (pkColumns.length == 0) pkColumns = pkTable.getPrimaryKeySource().getColumnNames();

			crossReferences.get(table.getTablePath()).put(
				pkTable.getTablePath(),
				new SimpleCrossReference(
					pkTable.getPrimaryKeySource().getName(),
					fk.getName(),
					pkTable.getTablePath(),
					table.getTablePath(),
					pkColumns,
					fk.getFKColumns(),
					true));
		}
	}

	private static TableSource createPrimaryKeyTableInformation(
		Metadata depends,
		TablePath path) {
		PrimaryKeyMetadata pkMetadata = depends.getPrimaryKeyMetadata(path);

		String[] pkColumnNames = pkMetadata.getColumnNames();
		if (pkColumnNames.length == 0) throw new IllegalStateException("参照先の " + path + " に PK が設定されていません");

		return new TableSource(
			path,
			depends.getTableMetadata(path),
			emptyColumnMetadataArray,
			new PrimaryKeySource(pkMetadata),
			ForeignKeySource.EMPTY_ARRAY);
	}

	private static class VirtualTable {

		private final TableMetadata tableMetadata;

		private final ColumnMetadata[] columnMetadatas;

		private final PrimaryKeyMetadata primaryKey;

		private final TablePath[] importedKeyResources;

		private final TablePath[] exportedKeyResources;

		VirtualTable(
			TableMetadata tableMetadata,
			ColumnMetadata[] columnMetadatas,
			PrimaryKeySource primaryKey,
			TablePath[] importedKeyResources,
			TablePath[] exportedKeyResources) {
			this.tableMetadata = tableMetadata;
			this.columnMetadatas = columnMetadatas;
			this.primaryKey = primaryKey;
			this.importedKeyResources = importedKeyResources;
			this.exportedKeyResources = exportedKeyResources;
		}

		TableMetadata getTableMetadata() {
			return tableMetadata;
		}

		ColumnMetadata[] getColumnMetadatas() {
			return columnMetadatas.clone();
		}

		PrimaryKeyMetadata getPrimaryKey() {
			return primaryKey;
		}

		TablePath[] getResourcesOfImportedKey() {
			return importedKeyResources.clone();
		}

		TablePath[] getResourcesOfExportedKey() {
			return exportedKeyResources.clone();
		}
	}

	private static class NullVirtualTable extends VirtualTable {

		private static final TablePath[] emptyResourcePaths = {};

		private NullVirtualTable() {
			super(null, null, null, null, null);
		}

		@Override
		ColumnMetadata[] getColumnMetadatas() {
			return emptyColumnMetadataArray;
		}

		@Override
		PrimaryKeyMetadata getPrimaryKey() {
			return null;
		}

		@Override
		TablePath[] getResourcesOfImportedKey() {
			return emptyResourcePaths;
		}

		@Override
		TablePath[] getResourcesOfExportedKey() {
			return emptyResourcePaths;
		}
	}
}
