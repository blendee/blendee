package org.blendee.sql;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.CrossReference;

/**
 * データベースのカラムを表すインターフェイスです。
 * @author 千葉 哲嗣
 * @see Relationship#getColumn(String)
 * @see Relationship#getColumns()
 */
public interface Column extends Comparable<Column> {

	/**
	 * 空配列
	 */
	static final Column[] EMPTY_ARRAY = {};

	@Override
	int compareTo(Column target);

	/**
	 * このカラムを Blendee 内で一意に特定する ID を返します。 ID はカラム別名として使用されます。
	 * @return ID
	 */
	public String getId();

	/**
	 * このインスタンスがさし示すカラムの値とパラメータの値が等しいものという条件句を生成します。
	 * @param id {@link RuntimeId}
	 * @param bindable 比較値
	 * @return 生成された条件句
	 */
	default Criteria getCriteria(RuntimeId id, Bindable bindable) {
		return new CriteriaFactory(id).create(this, bindable);
	}

	/**
	 * このインスタンスが含まれる {@link Relationship} を返します。
	 * @return このインスタンスが含まれる {@link Relationship}
	 */
	Relationship getRelationship();

	/**
	 * このインスタンスが含まれる {@link Relationship} のルートを返します。
	 * @return このインスタンスが含まれる {@link Relationship} のルート
	 */
	Relationship getRootRelationship();

	/**
	 * このインスタンスが含まれる {@link Relationship} を {@link Consumer} に渡します。
	 * @param consumer {@link Relationship} の {@link Consumer}
	 */
	void setRelationship(Consumer<Relationship> consumer);

	/**
	 * このカラムの名称を返します。
	 * @return このカラムの名称
	 */
	String getName();

	/**
	 * このカラムのデータ型を返します。
	 * @return このカラムのデータ型
	 */
	Class<?> getType();

	/**
	 * このカラムの定義情報を返します。
	 * @return このカラムの定義情報
	 */
	ColumnMetadata getColumnMetadata();

	/**
	 * テーブル別名を含むカラム名を返します。
	 * @param id {@link RuntimeId}
	 * @return テーブル別名を含むカラム名
	 */
	String getComplementedName(RuntimeId id);

	/**
	 * このカラムと同じカラムを、他の {@link Relationship} のツリーから探します。
	 * @param another 探す対象となる {@link Relationship}
	 * @return パラメータの {@link Relationship} に含まれるカラム
	 * @throws NotFoundException このカラムの属する {@link Relationship} の参照先に another が含まれない場合
	 */
	default Column findAnotherRootColumn(Relationship another) {
		Relationship myRelation = getRelationship();
		List<String[]> fks = new LinkedList<>();
		while (true) {
			if (myRelation.getTablePath().equals(another.getTablePath())) {
				//同じテーブルが見つかったら、今度は保存しておいた同じ FK を辿って
				//目的のカラムを見つける
				Relationship newRelation = another;
				for (String[] fk : fks) {
					newRelation = newRelation.find(fk);
				}

				return newRelation.getColumn(getName());
			}

			if (myRelation.isRoot()) break;

			CrossReference reference = myRelation.getCrossReference();
			fks.add(reference.getForeignKeyColumnNames());
			myRelation = myRelation.getParent();
		}

		//このカラムを含む Relationship が another のツリーから見つかりませんでした
		throw new NotFoundException(getRelationship() + " not found in " + another);
	}

	/**
	 * このカラムが、主キーがどうか検査します。
	 * @return このカラムが主キーの場合、 true
	 */
	default boolean isPrimaryKey() {
		return getRelationship().belongsPrimaryKey(this);
	}

	/**
	 * このインスタンスのコピーを返します。
	 * @return このインスタンスのコピー
	 */
	Column replicate();

	/**
	 * SQL 生成のために、このカラムが属している（はずの） {@link Relationship} root の通知を受けます。
	 * @param sqlRoot {@link Relationship} の root 要素
	 */
	default void prepareForSQL(Relationship sqlRoot) {
	}
}
