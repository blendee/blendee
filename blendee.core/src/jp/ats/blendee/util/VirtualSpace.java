package jp.ats.blendee.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jp.ats.blendee.internal.CollectionMap;
import jp.ats.blendee.internal.CollectionMapMap;
import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.ColumnMetadata;
import jp.ats.blendee.jdbc.CrossReference;
import jp.ats.blendee.jdbc.Metadata;
import jp.ats.blendee.jdbc.PrimaryKeyMetadata;
import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.jdbc.TableMetadata;
import jp.ats.blendee.jdbc.impl.SimpleCrossReference;

/**
 * 実際のデータベース以外から定義情報を取得し、 {@link Metadata} として使用できるクラスです。
 *
 * @author 千葉 哲嗣
 */
public class VirtualSpace implements Metadata {

	private static final VirtualTable nullTable = new NullVirtualTable();

	private static final ColumnMetadata[] emptyColumnMetadataArray = {};

	private final Map<ResourceLocator, TableSource> tables = new HashMap<>();

	private final CollectionMap<String, ResourceLocator> schemas = new CollectionMap<String, ResourceLocator>() {

		@Override
		protected Collection<ResourceLocator> createNewCollection() {
			return new LinkedHashSet<>();
		}
	};

	private final Map<ResourceLocator, VirtualTable> virtualTables = new HashMap<>();

	private final CollectionMapMap<ResourceLocator, ResourceLocator, CrossReference> crossReferences = CollectionMapMap.newInstance();

	private boolean started = false;

	/**
	 * テーブル情報を追加します。
	 *
	 * @param table 新しいテーブル情報
	 */
	public synchronized void addTable(TableSource table) {
		ResourceLocator locator = table.getResourceLocator();
		tables.put(locator, table);
		schemas.put(locator.getSchemaName(), locator);
	}

	/**
	 * 追加済みのテーブル情報を元に {@link Metadata} として使用できるようにします。
	 *
	 * @param depends このインスタンスが持つ定義情報が参照する {@link Metadata}
	 * @throws IllegalStateException 既にこのメソッドが実行されている場合
	 */
	public synchronized void start(Metadata depends) {
		if (started) throw new IllegalStateException("既にスタートしています");
		CollectionMap<ResourceLocator, ResourceLocator> exported = new CollectionMap<ResourceLocator, ResourceLocator>() {

			@Override
			protected Collection<ResourceLocator> createNewCollection() {
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
	 *
	 * @return 既に {@link #start(Metadata)} が実行している場合、 true
	 */
	public synchronized boolean isStarted() {
		return started;
	}

	@Override
	public synchronized ResourceLocator[] getTables(String schemaName) {
		Collection<ResourceLocator> locators = schemas.get(schemaName);
		return locators.toArray(new ResourceLocator[locators.size()]);
	}

	@Override
	public TableMetadata getTableMetadata(ResourceLocator locator) {
		return getTable(locator).getTableMetadata();
	}

	@Override
	public ColumnMetadata[] getColumnMetadatas(ResourceLocator locator) {
		return getTable(locator).getColumnMetadatas();
	}

	@Override
	public PrimaryKeyMetadata getPrimaryKeyMetadata(ResourceLocator locator) {
		return getTable(locator).getPrimaryKey();
	}

	@Override
	public ResourceLocator[] getResourcesOfImportedKey(ResourceLocator locator) {
		return getTable(locator).getResourcesOfImportedKey();
	}

	@Override
	public ResourceLocator[] getResourcesOfExportedKey(ResourceLocator locator) {
		return getTable(locator).getResourcesOfExportedKey();
	}

	@Override
	public synchronized CrossReference[] getCrossReferences(
		ResourceLocator exported,
		ResourceLocator imported) {
		Collection<CrossReference> references = crossReferences.get(imported).get(exported);
		return references.toArray(new CrossReference[references.size()]);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	private synchronized VirtualTable getTable(ResourceLocator locator) {
		VirtualTable table = virtualTables.get(locator);
		if (table == null) return nullTable;
		return table;
	}

	private void registTable(
		TableSource table,
		CollectionMap<ResourceLocator, ResourceLocator> exportedMap) {
		ForeignKeySource[] fks = table.getForeignKeySources();

		Set<ResourceLocator> imported = new LinkedHashSet<>();
		for (ForeignKeySource fk : fks)
			imported.add(fk.getImportedTable());

		ResourceLocator[] importedLocators = imported.toArray(new ResourceLocator[imported.size()]);

		ResourceLocator locator = table.getResourceLocator();

		Collection<ResourceLocator> exported = exportedMap.get(locator);
		ResourceLocator[] exportedLocators = exported.toArray(new ResourceLocator[exported.size()]);

		PrimaryKeySource pk = table.getPrimaryKeySource();

		VirtualTable virtualTable;
		if (pk == null) {
			virtualTable = new VirtualTable(
				table.getTabelMetadata(),
				table.getColumnMetadatas(),
				null,
				importedLocators,
				exportedLocators);
		} else {
			virtualTable = new VirtualTable(
				table.getTabelMetadata(),
				table.getColumnMetadatas(),
				pk,
				importedLocators,
				exportedLocators);
		}

		virtualTables.put(locator, virtualTable);
	}

	private void processForExported(
		TableSource table,
		CollectionMap<ResourceLocator, ResourceLocator> exported,
		Metadata depends) {
		ForeignKeySource[] fks = table.getForeignKeySources();
		for (ForeignKeySource fk : fks) {
			ResourceLocator importedTable = fk.getImportedTable();
			exported.put(importedTable, table.getResourceLocator());

			TableSource pkTableBase = tables.get(importedTable);
			TableSource pkTable;
			if (pkTableBase == null || pkTableBase.getPrimaryKeySource() == null) {
				pkTable = createPrimaryKeyTableInformation(depends, importedTable);
			} else {
				pkTable = pkTableBase;
			}

			crossReferences.get(table.getResourceLocator()).put(
				pkTable.getResourceLocator(),
				new SimpleCrossReference(
					pkTable.getPrimaryKeySource().getName(),
					fk.getName(),
					pkTable.getResourceLocator(),
					table.getResourceLocator(),
					pkTable.getPrimaryKeySource().getColumnNames(),
					fk.getColumns(),
					true));
		}
	}

	private static TableSource createPrimaryKeyTableInformation(
		Metadata depends,
		ResourceLocator locator) {
		PrimaryKeyMetadata pkMetadata = depends.getPrimaryKeyMetadata(locator);

		String[] pkColumnNames = pkMetadata.getColumnNames();
		if (pkColumnNames.length == 0) throw new IllegalStateException("参照先の " + locator + " に PK が設定されていません");

		return new TableSource(
			locator,
			depends.getTableMetadata(locator),
			emptyColumnMetadataArray,
			new PrimaryKeySource(pkMetadata),
			ForeignKeySource.EMPTY_ARRAY);
	}

	private static class VirtualTable {

		private final TableMetadata tableMetadata;

		private final ColumnMetadata[] columnMetadatas;

		private final PrimaryKeyMetadata primaryKey;

		private final ResourceLocator[] importedKeyResources;

		private final ResourceLocator[] exportedKeyResources;

		VirtualTable(
			TableMetadata tableMetadata,
			ColumnMetadata[] columnMetadatas,
			PrimaryKeySource primaryKey,
			ResourceLocator[] importedKeyResources,
			ResourceLocator[] exportedKeyResources) {
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

		ResourceLocator[] getResourcesOfImportedKey() {
			return importedKeyResources.clone();
		}

		ResourceLocator[] getResourcesOfExportedKey() {
			return exportedKeyResources.clone();
		}
	}

	private static class NullVirtualTable extends VirtualTable {

		private static final ResourceLocator[] emptyResourceLocators = {};

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
		ResourceLocator[] getResourcesOfImportedKey() {
			return emptyResourceLocators;
		}

		@Override
		ResourceLocator[] getResourcesOfExportedKey() {
			return emptyResourceLocators;
		}
	}
}
