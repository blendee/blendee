package org.blendee.sql;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.blendee.internal.Traversable;
import org.blendee.internal.TraversableNode;
import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.DataTypeConverter;
import org.blendee.jdbc.MetadataUtilities;
import org.blendee.jdbc.TablePath;

/**
 * {@link Relationship} の実装クラスです。
 * @author 千葉 哲嗣
 */
final class ConcreteRelationship implements Relationship {

	private final TablePath path;

	private final ConcreteRelationship root;

	private final ConcreteRelationship parent;

	private final String id;

	private final CrossReference reference;

	private final Column[] columns;

	private final Map<String, Column> columnMap = new HashMap<>();

	private final Column[] primaryKeyColumns;

	private final DataTypeConverter converter;

	private final Object lock = new Object();

	private TraversableNode node;

	private Map<String, ConcreteRelationship> foreignKeyNameMap;

	private Map<String, ConcreteRelationship> foreignKeyIdMap;

	ConcreteRelationship(
		ConcreteRelationship root,
		ConcreteRelationship parent,
		CrossReference reference,
		TablePath path,
		String id,
		DataTypeConverter converter) {
		this.path = path;

		if (root == null) {
			this.root = this;
		} else {
			this.root = root;
		}

		this.parent = parent;
		this.reference = reference;
		this.id = id;

		this.converter = converter;

		ColumnMetadata[] metadatas = MetadataUtilities.getColumnMetadatas(path);
		columns = new Column[metadatas.length];
		for (int i = 0; i < metadatas.length; i++) {
			ColumnMetadata metadata = metadatas[i];
			Column column = new RelationshipColumn(this, metadata, converter, String.valueOf(i));
			columns[i] = column;
			columnMap.put(metadata.getName(), column);
		}

		String[] primaryKeyColumnNames = MetadataUtilities.getPrimaryKeyColumnNames(path);
		primaryKeyColumns = new Column[primaryKeyColumnNames.length];
		for (int i = 0; i < primaryKeyColumnNames.length; i++) {
			primaryKeyColumns[i] = columnMap.get(MetadataUtilities.regularize(primaryKeyColumnNames[i]));
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o.getClass().equals(TablePath.class))
			//TablePath.class.getName() + " と比較することはできません"
			throw new IllegalStateException(o.getClass().getName() + " can not be compared to " + TablePath.class.getName() + ".");

		return o instanceof ConcreteRelationship && id.equals(((ConcreteRelationship) o).id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public TablePath getTablePath() {
		return path;
	}

	@Override
	public int compareTo(Relationship target) {
		return id.compareTo(target.getId());
	}

	@Override
	public Relationship[] getRelationships() {
		Traversable[] traversables = getSubNode().getTraversables();
		ConcreteRelationship[] relations = new ConcreteRelationship[traversables.length];
		for (int i = 0; i < traversables.length; i++) {
			relations[i] = (ConcreteRelationship) traversables[i];
		}

		return relations;
	}

	@Override
	public TraversableNode getSubNode() {
		synchronized (lock) {
			if (node != null) return node;

			node = new TraversableNode();

			CrossReference[] references = MetadataUtilities.getCrossReferencesOfImportedKeys(path);

			foreignKeyNameMap = new HashMap<>();
			foreignKeyIdMap = new HashMap<>();

			for (int i = 0; i < references.length; i++) {
				CrossReference element = references[i];
				ConcreteRelationship child = new ConcreteRelationship(
					this.root,
					this,
					element,
					element.getPrimaryKeyTable(),
					id + "_" + i,
					converter);
				foreignKeyNameMap.put(MetadataUtilities.regularize(element.getForeignKeyName()), child);
				String[] foreignKeyColumns = element.getForeignKeyColumnNames();
				foreignKeyIdMap.put(createForeignKeyId(foreignKeyColumns), child);
				node.add(child);
			}
		}

		return node;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean hasColumn(String columnName) {
		return columnMap.containsKey(MetadataUtilities.regularize(columnName));
	}

	@Override
	public Column getColumn(String columnName) {
		Column column = columnMap.get(MetadataUtilities.regularize(columnName));
		//this + " に " + columnName + " が見つかりません"
		if (column == null) throw new NotFoundException(columnName + " can not be found in " + this + ".");
		return column;
	}

	@Override
	public Column[] getColumns() {
		return columns.clone();
	}

	@Override
	public Column[] getPrimaryKeyColumns() {
		return primaryKeyColumns.clone();
	}

	@Override
	public boolean belongsPrimaryKey(Column column) {
		for (Column pkColumn : primaryKeyColumns) {
			if (pkColumn.equals(column)) return true;
		}

		return false;
	}

	@Override
	public ConcreteRelationship find(String foreignKeyName) {
		ConcreteRelationship relationship;
		synchronized (lock) {
			if (foreignKeyNameMap == null) getSubNode();
			relationship = foreignKeyNameMap.get(MetadataUtilities.regularize(foreignKeyName));
		}

		if (relationship == null)
			throw new NotFoundException(createErrorMessage(foreignKeyName));

		return relationship;
	}

	@Override
	public ConcreteRelationship find(String[] foreignKeyColumnNames) {
		String keyId = createForeignKeyId(MetadataUtilities.regularize(foreignKeyColumnNames));

		ConcreteRelationship relationship;
		synchronized (lock) {
			if (foreignKeyIdMap == null) getSubNode();
			relationship = foreignKeyIdMap.get(keyId);
		}

		if (relationship == null)
			throw new NotFoundException(createErrorMessage(String.join(" ", foreignKeyColumnNames)));

		return relationship;
	}

	@Override
	public CrossReference getCrossReference() {
		if (isRoot()) throw new UnsupportedOperationException();
		return reference;
	}

	@Override
	public ConcreteRelationship getParent() {
		if (isRoot()) throw new UnsupportedOperationException();
		return parent;
	}

	@Override
	public boolean isRoot() {
		return root == this;
	}

	@Override
	public ConcreteRelationship getRoot() {
		return root;
	}

	@Override
	public void addParentTo(Collection<Relationship> parents) {
		if (parent == null) return;
		parent.addParentTo(parents);
		parents.add(parent);
	}

	@Override
	public String toString() {
		return path + " " + id;
	}

	private String createErrorMessage(String base) {
		//this + " では " + base + " は使用できません"
		return base + " can not be used in " + this + ".";
	}

	private static String createForeignKeyId(String[] foreignKeyColumnNames) {
		Arrays.sort(foreignKeyColumnNames);
		return String.join(",", foreignKeyColumnNames);
	}
}
