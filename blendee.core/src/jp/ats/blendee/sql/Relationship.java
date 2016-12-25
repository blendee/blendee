package jp.ats.blendee.sql;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jp.ats.blendee.internal.CollectionMap;
import jp.ats.blendee.internal.Traversable;
import jp.ats.blendee.internal.TraversableNode;
import jp.ats.blendee.internal.Traverser;
import jp.ats.blendee.internal.TraverserOperator;
import jp.ats.blendee.jdbc.ColumnMetadata;
import jp.ats.blendee.jdbc.CrossReference;
import jp.ats.blendee.jdbc.DataTypeConverter;
import jp.ats.blendee.jdbc.MetadataUtilities;
import jp.ats.blendee.jdbc.ResourceLocator;

/**
 * 検索対象となるテーブルと、そのテーブルが参照しているテーブルのツリーを構成する要素を表すクラスです。
 * <br>
 * データベース上では同じテーブルとなる Relationship どうしでも、ルートとなる Relationship が異なる場合、それらは別物として扱われます。
 *
 * @author 千葉 哲嗣
 */
public final class Relationship implements Traversable, Comparable<Relationship> {

	private final ResourceLocator locator;

	private final Relationship root;

	private final Relationship parent;

	private final String id;

	private final CrossReference reference;

	private final Map<String, Relationship> foreignKeyNameMap = new HashMap<>();

	private final Map<String, Relationship> foreignKeyIdMap = new HashMap<>();

	private final TraversableNode node = new TraversableNode();

	private final Column[] columns;

	private final Map<String, Column> columnMap = new HashMap<>();

	private final Column[] primaryKeyColumns;

	private final CollectionMap<ResourceLocator, Relationship> convertMap;

	Relationship(
		Relationship root,
		Relationship parent,
		CrossReference reference,
		ResourceLocator locator,
		String id,
		DataTypeConverter converter,
		CollectionMap<ResourceLocator, Relationship> convertMap) {
		this.locator = locator;

		convertMap.put(locator, this);
		this.convertMap = convertMap;

		if (root == null) {
			this.root = this;
		} else {
			this.root = root;
		}

		this.parent = parent;
		this.reference = reference;
		this.id = id;

		ColumnMetadata[] metadatas = MetadataUtilities.getColumnMetadatas(locator);
		columns = new Column[metadatas.length];
		DecimalFormat columnFormat = RelationshipFactory.createDigitFormat(metadatas.length);
		for (int i = 0; i < metadatas.length; i++) {
			ColumnMetadata metadata = metadatas[i];
			Column column = new Column(this, metadata, converter, columnFormat.format(i));
			columns[i] = column;
			columnMap.put(metadata.getName(), column);
		}
		String[] primaryKeyColumnNames = MetadataUtilities.getPrimaryKeyColumnNames(locator);
		primaryKeyColumns = new Column[primaryKeyColumnNames.length];
		for (int i = 0; i < primaryKeyColumnNames.length; i++) {
			primaryKeyColumns[i] = columnMap.get(MetadataUtilities.regularize(primaryKeyColumnNames[i]));
		}

		//テーブルの参照が循環している場合、これ以上探索しない
		if (isRecursive(parent, locator)) return;

		CrossReference[] references = MetadataUtilities.getCrossReferencesOfImportedKeys(locator);

		DecimalFormat relationshipFormat = RelationshipFactory.createDigitFormat(references.length);

		for (int i = 0; i < references.length; i++) {
			CrossReference element = references[i];
			Relationship child = new Relationship(
				this.root,
				this,
				element,
				element.getPrimaryKeyResource(),
				id + "_" + relationshipFormat.format(i),
				converter,
				convertMap);
			foreignKeyNameMap.put(MetadataUtilities.regularize(element.getForeignKeyName()), child);
			String[] foreignKeyColumns = element.getForeignKeyColumnNames();
			foreignKeyIdMap.put(createForeignKeyId(foreignKeyColumns), child);
			node.add(child);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o.getClass().equals(ResourceLocator.class))
			throw new IllegalStateException(ResourceLocator.class.getName() + " と比較することはできません");

		return o instanceof Relationship && id.equals(((Relationship) o).id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	/**
	 * この要素が表すテーブルを返します。
	 *
	 * @return この要素が表すテーブル
	 */
	public ResourceLocator getResourceLocator() {
		return locator;
	}

	@Override
	public int compareTo(Relationship target) {
		return id.compareTo(target.id);
	}

	/**
	 * この要素が直接参照している子要素の配列を返します。
	 *
	 * @return この要素が参照している要素の配列
	 */
	public Relationship[] getRelationships() {
		Traversable[] traversables = node.getTraversables();
		Relationship[] relations = new Relationship[traversables.length];
		for (int i = 0; i < traversables.length; i++) {
			relations[i] = (Relationship) traversables[i];
		}

		return relations;
	}

	/**
	 * {@link Traverser} にこの要素以下のツリーを走査させます。
	 *
	 * @param traverser ツリーを走査する {@link Traverser}
	 */
	public void traverse(Traverser traverser) {
		TraverserOperator.operate(traverser, this);
	}

	@Override
	public TraversableNode getSubNode() {
		return node;
	}

	/**
	 * この要素を Blendee 内で一意に特定する ID を返します。 ID はテーブル別名として使用されます。
	 *
	 * @return ID
	 */
	public String getID() {
		return id;
	}

	/**
	 * この要素が表すテーブルに存在するカラムを {@link Column} のインスタンスとして返します。
	 *
	 * @param columnName カラム名
	 * @return カラム名に対応する {@link Column} のインスタンス
	 * @throws NotFoundException テーブルにカラムが存在しない場合
	 */
	public Column getColumn(String columnName) {
		Column column = columnMap.get(MetadataUtilities.regularize(columnName));
		if (column == null) throw new NotFoundException(this + " に " + columnName + " が見つかりません");
		return column;
	}

	/**
	 * この要素が表すテーブルの全カラムを返します。
	 *
	 * @return 全カラム
	 */
	public Column[] getColumns() {
		return columns.clone();
	}

	/**
	 * この要素が表すテーブルの主キーを構成する全カラムを返します。
	 *
	 * @return 主キーを構成する全カラム
	 */
	public Column[] getPrimaryKeyColumns() {
		return primaryKeyColumns.clone();
	}

	/**
	 * この要素が表すテーブルの主キーに、パラメータのカラムが含まれるか検査します。
	 *
	 * @param column 検査するカラム
	 * @return 主キーを構成するカラムと同一の場合、 true
	 */
	public boolean belongsPrimaryKey(Column column) {
		for (Column pkColumn : primaryKeyColumns) {
			if (pkColumn.equals(column)) return true;
		}

		return false;
	}

	/**
	 * この要素が直接参照している子要素を、外部キー名をもとに探して返します。
	 *
	 * @param foreignKeyName 外部キー名
	 * @return 外部キー名に対応する参照先
	 * @throws NotFoundException 外部キー名に対応する参照先がない場合
	 */
	public Relationship find(String foreignKeyName) {
		Relationship relationship = foreignKeyNameMap.get(MetadataUtilities.regularize(foreignKeyName));
		if (relationship == null) throw new NotFoundException(createErrorMessage(foreignKeyName));

		return relationship;
	}

	/**
	 * この要素が直接参照している子要素を、外部キーを構成するカラム名をもとに探して返します。
	 *
	 * @param foreignKeyColumnNames 外部キーカラム名
	 * @return 外部キーカラム名に対応する参照先
	 * @throws NotFoundException 外部キーカラム名に対応する参照先がない場合
	 */
	public Relationship find(String[] foreignKeyColumnNames) {
		String keyId = createForeignKeyId(MetadataUtilities.regularize(foreignKeyColumnNames));

		Relationship relationship = foreignKeyIdMap.get(keyId);
		if (relationship == null) {
			throw new NotFoundException(createErrorMessage(String.join(" ", foreignKeyColumnNames)));
		}

		return relationship;
	}

	/**
	 * この要素と、この要素を直接参照している親要素との間の関連情報を返します。
	 *
	 * @return 要素間の関連情報
	 * @throws UnsupportedOperationException この要素がルートの場合
	 */
	public CrossReference getCrossReference() {
		if (isRoot()) throw new UnsupportedOperationException();
		return reference;
	}

	/**
	 * この要素を直接参照している親要素を返します。
	 *
	 * @return この要素を直接参照している要素
	 * @throws UnsupportedOperationException この要素がルートの場合
	 */
	public Relationship getParent() {
		if (isRoot()) throw new UnsupportedOperationException();
		return parent;
	}

	/**
	 * この要素がルートかどうか検査します。
	 *
	 * @return ルートの場合 true
	 */
	public boolean isRoot() {
		return root == this;
	}

	/**
	 * この要素が属するツリーのルート要素を返します。
	 *
	 * @return ルート要素
	 */
	public Relationship getRoot() {
		return root;
	}

	/**
	 * パラメータのコレクションにこの要素の親要素を連鎖的に全て追加していきます。
	 *
	 * @param parents 追加してほしいコレクション
	 */
	public void addParentTo(Collection<Relationship> parents) {
		if (parent == null) return;
		parent.addParentTo(parents);
		parents.add(parent);
	}

	/**
	 * この Relationship が含まれるツリーに、パラメータのテーブルがある場合、それに対応する Relationship を返します。
	 * <br>
	 * このツリーに locator に対応する Relationship がない場合は、長さ 0 の配列、複数件ある場合は、全ての Relationship を持つ配列を返します。
	 *
	 * @param locator 変換したい {@link ResourceLocator}
	 * @return 変換された {@link Relationship} の配列
	 */
	public Relationship[] convert(ResourceLocator locator) {
		Collection<Relationship> list = convertMap.get(locator);
		return list.toArray(new Relationship[list.size()]);
	}

	@Override
	public String toString() {
		return locator + " " + id;
	}

	private String createErrorMessage(String base) {
		return this + " では " + base + " は使用できません";
	}

	private static boolean isRecursive(Relationship parent, ResourceLocator locator) {
		if (parent == null) return false;
		if (parent.getResourceLocator().equals(locator)) return true;
		return isRecursive(parent.parent, locator);
	}

	private static String createForeignKeyId(String[] foreignKeyColumnNames) {
		Arrays.sort(foreignKeyColumnNames);
		return String.join(",", foreignKeyColumnNames);
	}
}
