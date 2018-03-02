package org.blendee.sql;

import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.DataTypeConverter;

/**
 * {@link Relationship} に含まれるカラムを表すクラスです。<br>
 * データベース上では同じカラムでも、カラムが属する {@link Relationship} が違う場合、それらは別物として扱われます。
 * @author 千葉 哲嗣
 * @see Relationship#getColumn(String)
 * @see Relationship#getColumns()
 */
public class Column implements Comparable<Column> {

	//!! このクラスに新たにメソッドを追加する場合は、サブクラスにも追加すること !!

	/**
	 * 空配列
	 */
	public static final Column[] EMPTY_ARRAY = {};

	private final Relationship relationship;

	private final ColumnMetadata metadata;

	private final String name;

	private final Class<?> type;

	private final String id;

	private final String complementedName;

	private final int hashCode;

	Column(
		Relationship relationship,
		ColumnMetadata metadata,
		DataTypeConverter converter,
		String index) {
		this.relationship = relationship;
		this.metadata = metadata;
		this.name = metadata.getName();
		this.type = converter.convert(metadata.getType(), metadata.getTypeName());
		id = relationship.getID() + "_c" + index;
		complementedName = relationship.getID() + "." + metadata.getName();
		hashCode = id.hashCode();
	}

	Column() {
		this.relationship = null;
		this.metadata = null;
		this.name = null;
		this.type = null;
		id = null;
		complementedName = null;
		hashCode = 0;
	}

	/**
	 * コピーコンストラクタ
	 * @param copyFrom コピー元
	 */
	Column(Column copyFrom) {
		this.relationship = copyFrom.relationship;
		this.metadata = copyFrom.metadata;
		this.name = copyFrom.name;
		this.type = copyFrom.type;
		id = copyFrom.id;
		complementedName = copyFrom.complementedName;
		hashCode = copyFrom.hashCode;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Column && id.equals(((Column) o).id);
	}

	@Override
	public int compareTo(Column target) {
		return id.compareTo(target.id);
	}

	/**
	 * このカラムを Blendee 内で一意に特定する ID を返します。 ID はカラム別名として使用されます。
	 * @return ID
	 */
	public String getID() {
		return id;
	}

	@Override
	public String toString() {
		return complementedName;
	}

	/**
	 * このインスタンスがさし示すカラムの値とパラメータの値が等しいものという条件句を生成します。
	 * @param bindable 比較値
	 * @return 生成された条件句
	 */
	public Criteria getCriteria(Bindable bindable) {
		return CriteriaFactory.create(this, bindable);
	}

	/**
	 * このインスタンスが含まれる {@link Relationship} を返します。
	 * @return このインスタンスが含まれる {@link Relationship}
	 */
	public Relationship getRelationship() {
		return relationship;
	}

	/**
	 * このカラムの名称を返します。
	 * @return このカラムの名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * このカラムのデータ型を返します。
	 * @return このカラムのデータ型
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * このカラムの定義情報を返します。
	 * @return このカラムの定義情報
	 */
	public ColumnMetadata getColumnMetadata() {
		return metadata;
	}

	/**
	 * テーブル別名を含むカラム名を返します。
	 * @return テーブル別名を含むカラム名
	 */
	public String getComplementedName() {
		return complementedName;
	}

	/**
	 * このカラムと同じカラムを、他の {@link Relationship} のツリーから探します。
	 * @param another 探す対象となる {@link Relationship}
	 * @return パラメータの {@link Relationship} に含まれるカラム
	 * @throws NotFoundException このカラムの属する {@link Relationship} の参照先に another が含まれない場合
	 */
	public Column findAnotherRootColumn(Relationship another) {
		Relationship myRelation = relationship;
		List<String[]> fks = new LinkedList<>();
		while (true) {
			if (myRelation.getTablePath().equals(another.getTablePath())) {
				//同じテーブルが見つかったら、今度は保存しておいた同じ FK を辿って
				//目的のカラムを見つける
				Relationship newRelation = another;
				for (String[] fk : fks) {
					newRelation = newRelation.find(fk);
				}
				return newRelation.getColumn(name);
			}

			if (myRelation.isRoot()) break;

			CrossReference reference = myRelation.getCrossReference();
			fks.add(reference.getForeignKeyColumnNames());
			myRelation = myRelation.getParent();
		}

		throw new NotFoundException("このカラムを含む Relationship が another のツリーから見つかりませんでした");
	}

	/**
	 * このカラムが、主キーがどうか検査します。
	 * @return このカラムが主キーの場合、 true
	 */
	public boolean isPrimaryKey() {
		return relationship.belongsPrimaryKey(this);
	}

	Column replicate() {
		return this;
	}

	void prepareForSQL(Relationship sqlRoot) {
		if (!sqlRoot.isRoot()) throw new IllegalStateException(sqlRoot + " はルートではありません");
		if (!getRelationship().getRoot().equals(sqlRoot))
			throw new IllegalStateException(getComplementedName() + " は SQL 文の Relationship のツリーに含まれないカラムです");
	}
}
