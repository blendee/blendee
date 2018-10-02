package org.blendee.sql;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

	private final List<TablePath> relationshipPath;

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
		List<TablePath> relationshipPath,
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

		this.relationshipPath = relationshipPath;

		this.converter = converter;

		ColumnMetadata[] metadatas = MetadataUtilities.getColumnMetadatas(path);
		columns = new Column[metadatas.length];
		DecimalFormat columnFormat = RelationshipFactory.createDigitFormat(metadatas.length);
		for (int i = 0; i < metadatas.length; i++) {
			ColumnMetadata metadata = metadatas[i];
			Column column = new RelationshipColumn(this, metadata, converter, columnFormat.format(i));
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
			throw new IllegalStateException(TablePath.class.getName() + " と比較することはできません");

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

			List<TablePath> myRelationshipPath = new LinkedList<>(relationshipPath);
			myRelationshipPath.add(path);

			CrossReference[] references = MetadataUtilities.getCrossReferencesOfImportedKeys(path);

			DecimalFormat relationshipFormat = RelationshipFactory.createDigitFormat(references.length);

			foreignKeyNameMap = new HashMap<>();
			foreignKeyIdMap = new HashMap<>();

			for (int i = 0; i < references.length; i++) {
				CrossReference element = references[i];
				ConcreteRelationship child = new ConcreteRelationship(
					this.root,
					this,
					element,
					element.getPrimaryKeyTable(),
					id + "_" + relationshipFormat.format(i),
					myRelationshipPath,
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
		if (column == null) throw new NotFoundException(this + " に " + columnName + " が見つかりません");
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
		return this + " では " + base + " は使用できません";
	}

	private static String createForeignKeyId(String[] foreignKeyColumnNames) {
		Arrays.sort(foreignKeyColumnNames);
		return String.join(",", foreignKeyColumnNames);
	}
}
