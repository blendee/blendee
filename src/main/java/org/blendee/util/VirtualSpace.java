package org.blendee.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.blendee.internal.CollectionMap;
import org.blendee.internal.CollectionMapMap;
import org.blendee.internal.U;
import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.Metadata;
import org.blendee.jdbc.PrimaryKeyMetadata;
import org.blendee.jdbc.StoredIdentifier;
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

	private StoredIdentifier storedIdentifier;

	private boolean started = false;

	/**
	 * テーブル情報を追加します。
	 * @param table 新しいテーブル情報
	 */
	public synchronized void addTable(TableSource table) {
		checkStarted();
		TablePath path = table.getTablePath();
		tables.put(path, table);
		schemas.put(path.getSchemaName(), path);
	}

	/**
	 * @param storedIdentifier {@link StoredIdentifier}
	 */
	public synchronized void setStoredIdentifier(StoredIdentifier storedIdentifier) {
		checkStarted();
		this.storedIdentifier = Objects.requireNonNull(storedIdentifier);
	}

	/**
	 * 追加済みのテーブル情報を元に {@link Metadata} として使用できるようにします。
	 * @param depends このインスタンスが持つ定義情報が参照する {@link Metadata}
	 * @throws IllegalStateException 既にこのメソッドが実行されている場合
	 */
	public synchronized void start(Metadata depends) {
		checkStarted();

		if (storedIdentifier == null) storedIdentifier = depends.getStoredIdentifier();
		Objects.requireNonNull(storedIdentifier);

		var exported = new CollectionMap<TablePath, TablePath>() {

			@Override
			protected Collection<TablePath> createNewCollection() {
				return new HashSet<>();
			}
		};

		//processForExported内でtablesに追加が発生するためtablesを退避
		for (var table : new HashMap<>(tables).values())
			processForExported(table, exported, depends);

		for (var table : tables.values())
			registerTable(table, exported);

		started = true;
	}

	/**
	 * 保持している情報を全てクリアし、 start = false となります。
	 */
	public synchronized void stop() {
		tables.clear();
		schemas.clear();
		virtualTables.clear();
		crossReferences.clear();
		storedIdentifier = null;
		started = false;
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
		var paths = schemas.get(storedIdentifier.regularize(schemaName));
		return paths.toArray(new TablePath[paths.size()]);
	}

	@Override
	public TableMetadata getTableMetadata(TablePath path) {
		return getTable(path).getTableMetadata().orElse(null);
	}

	@Override
	public Optional<TableMetadata> tableMetadata(TablePath path) {
		return getTable(path).getTableMetadata();
	}

	@Override
	public ColumnMetadata[] getColumnMetadatas(TablePath path) {
		return getTable(path).getColumnMetadatas();
	}

	@Override
	public PrimaryKeyMetadata getPrimaryKeyMetadata(TablePath path) {
		return getTable(path).getPrimaryKey().orElse(null);
	}

	@Override
	public Optional<PrimaryKeyMetadata> primaryKeyMetadata(TablePath path) {
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
		var references = crossReferences.get(imported).get(exported);
		return references.toArray(new CrossReference[references.size()]);
	}

	@Override
	public StoredIdentifier getStoredIdentifier() {
		return storedIdentifier;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private void checkStarted() {
		//既にスタートしています
		if (started) throw new IllegalStateException("already started");
	}

	private synchronized VirtualTable getTable(TablePath path) {
		var table = virtualTables.get(path);
		if (table == null) return nullTable;
		return table;
	}

	private void registerTable(
		TableSource table,
		CollectionMap<TablePath, TablePath> exportedMap) {
		var fks = table.getForeignKeySources();

		var imported = new LinkedHashSet<TablePath>();
		for (var fk : fks)
			imported.add(fk.getImportedTable());

		var importedPaths = imported.toArray(new TablePath[imported.size()]);

		var path = table.getTablePath();

		var exported = exportedMap.get(path);
		var exportedPaths = exported.toArray(new TablePath[exported.size()]);

		var pk = table.getPrimaryKeySource();

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
		var tablePath = table.getTablePath();

		for (var fk : table.getForeignKeySources()) {
			var importedTable = fk.getImportedTable();
			exported.put(importedTable, tablePath);

			var pkTableBase = tables.get(importedTable);
			TableSource pkTable;
			if (pkTableBase == null || pkTableBase.getPrimaryKeySource() == null) {
				pkTable = createPrimaryKeyTableInformation(depends, importedTable);

				//addされなかった参照テーブルも追加対象に
				tables.put(importedTable, pkTable);
			} else {
				pkTable = pkTableBase;
			}

			var pkColumns = fk.getPKColumns();
			if (pkColumns.length == 0) pkColumns = pkTable.getPrimaryKeySource().getColumnNames();

			crossReferences.get(tablePath)
				.put(
					pkTable.getTablePath(),
					new SimpleCrossReference(
						pkTable.getPrimaryKeySource().getName(),
						fk.getName(),
						pkTable.getTablePath(),
						tablePath,
						pkColumns,
						fk.getFKColumns(),
						pkTable.getPrimaryKeySource().isPseudo()));
		}
	}

	private static TableSource createPrimaryKeyTableInformation(
		Metadata depends,
		TablePath path) {
		var pkMetadata = depends.getPrimaryKeyMetadata(path);

		var pkColumnNames = pkMetadata.getColumnNames();
		//PK is not set to the reference "A".
		//"参照先の " + path + " に PK が設定されていません"
		if (pkColumnNames.length == 0) throw new IllegalStateException("PK is not found from " + path);

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

		Optional<TableMetadata> getTableMetadata() {
			return Optional.of(tableMetadata);
		}

		ColumnMetadata[] getColumnMetadatas() {
			return columnMetadatas.clone();
		}

		Optional<PrimaryKeyMetadata> getPrimaryKey() {
			return Optional.of(primaryKey);
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
		Optional<TableMetadata> getTableMetadata() {
			return Optional.empty();
		}

		@Override
		ColumnMetadata[] getColumnMetadatas() {
			return emptyColumnMetadataArray;
		}

		@Override
		Optional<PrimaryKeyMetadata> getPrimaryKey() {
			return Optional.empty();
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
