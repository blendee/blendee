package org.blendee.sql;

import java.util.Collection;

import org.blendee.internal.Traversable;
import org.blendee.internal.Traverser;
import org.blendee.internal.TraverserOperator;
import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.TablePath;

/**
 * 検索対象となるテーブルと、そのテーブルが参照しているテーブルのツリーを構成する要素を表すクラスです。<br>
 * データベース上では同じテーブルとなる Relationship どうしでも、ルートとなる Relationship が異なる場合、それらは別物として扱われます。
 * @author 千葉 哲嗣
 */
public interface Relationship extends Traversable, Comparable<Relationship> {

	/**
	 * この要素が表すテーブルを返します。
	 * @return この要素が表すテーブル
	 */
	TablePath getTablePath();

	/**
	 * この要素が直接参照している子要素の配列を返します。
	 * @return この要素が参照している要素の配列
	 */
	Relationship[] getRelationships();

	/**
	 * {@link Traverser} にこの要素以下のツリーを走査させます。
	 * @param traverser ツリーを走査する {@link Traverser}
	 */
	default void traverse(Traverser traverser) {
		TraverserOperator.operate(traverser, this);
	}

	/**
	 * この要素を Blendee 内で一意に特定する ID を返します。 ID はテーブル別名として使用されます。
	 * @return ID
	 */
	String getId();

	/**
	 * 指定されたカラム名が存在するか検査します。
	 * @param columnName カラム名
	 * @return カラムを含む場合、true
	 */
	boolean hasColumn(String columnName);

	/**
	 * この要素が表すテーブルに存在するカラムを {@link Column} のインスタンスとして返します。
	 * @param columnName カラム名
	 * @return カラム名に対応する {@link Column} のインスタンス
	 * @throws NotFoundException テーブルにカラムが存在しない場合
	 */
	Column getColumn(String columnName);

	/**
	 * この要素が表すテーブルの全カラムを返します。
	 * @return 全カラム
	 */
	Column[] getColumns();

	/**
	 * この要素が表すテーブルの主キーを構成する全カラムを返します。
	 * @return 主キーを構成する全カラム
	 */
	Column[] getPrimaryKeyColumns();

	/**
	 * この要素が表すテーブルの主キーに、パラメータのカラムが含まれるか検査します。
	 * @param column 検査するカラム
	 * @return 主キーを構成するカラムと同一の場合、 true
	 */
	boolean belongsPrimaryKey(Column column);

	/**
	 * この要素が直接参照している子要素を、外部キー名をもとに探して返します。
	 * @param foreignKeyName 外部キー名
	 * @return 外部キー名に対応する参照先
	 * @throws NotFoundException 外部キー名に対応する参照先がない場合
	 */
	Relationship find(String foreignKeyName);

	/**
	 * この要素が直接参照している子要素を、外部キーを構成するカラム名をもとに探して返します。
	 * @param foreignKeyColumnNames 外部キーカラム名
	 * @return 外部キーカラム名に対応する参照先
	 * @throws NotFoundException 外部キーカラム名に対応する参照先がない場合
	 */
	Relationship find(String[] foreignKeyColumnNames);

	/**
	 * この要素と、この要素を直接参照している親要素との間の関連情報を返します。
	 * @return 要素間の関連情報
	 * @throws UnsupportedOperationException この要素がルートの場合
	 */
	CrossReference getCrossReference();

	/**
	 * この要素を直接参照している親要素を返します。
	 * @return この要素を直接参照している要素
	 * @throws UnsupportedOperationException この要素がルートの場合
	 */
	Relationship getParent();

	/**
	 * この要素がルートかどうか検査します。
	 * @return ルートの場合 true
	 */
	boolean isRoot();

	/**
	 * この要素が属するツリーのルート要素を返します。
	 * @return ルート要素
	 */
	Relationship getRoot();

	/**
	 * パラメータのコレクションにこの要素の親要素を連鎖的に全て追加していきます。
	 * @param parents 追加してほしいコレクション
	 */
	void addParentTo(Collection<Relationship> parents);

	/**
	 * この Relationship が含まれるツリーに、パラメータのテーブルがある場合、それに対応する Relationship を返します。<br>
	 * このツリーに path に対応する Relationship がない場合は、長さ 0 の配列、複数件ある場合は、全ての Relationship を持つ配列を返します。
	 * @param path 変換したい {@link TablePath}
	 * @return 変換された {@link Relationship} の配列
	 */
	Relationship[] convert(TablePath path);

	/**
	 * この {@link Relationship} の文字列表現を返します。
	 * @return 基本的には table id
	 */
	@Override
	String toString();
}
