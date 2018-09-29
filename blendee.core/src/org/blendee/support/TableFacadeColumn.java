package org.blendee.support;

import org.blendee.sql.Bindable;
import org.blendee.sql.Column;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.CriteriaFactory.ComparisonOperator;
import org.blendee.sql.CriteriaFactory.Match;
import org.blendee.sql.CriteriaFactory.NullComparisonOperator;
import org.blendee.sql.Relationship;

/**
 * 汎用的な {@link SelectStatement} カラムクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 */
public class TableFacadeColumn {

	private final Column column;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param relationship 条件作成に必要な情報を持った {@link TableFacadeRelationship}
	 * @param name カラム名
	 */
	public TableFacadeColumn(Relationship relationship, String name) {
		column = relationship.getColumn(name);
	}

	/**
	 * このカラムの検索条件を生成します。
	 * @see CriteriaFactory#create(Column, String)
	 * @param value 検索条件の値
	 * @return 検索条件
	 */
	public Criteria createCriteria(String value) {
		return CriteriaFactory.create(column, value);
	}

	/**
	 * このカラムの検索条件を生成します。
	 * @see CriteriaFactory#create(Column, String)
	 * @param value 検索条件の値
	 * @return 検索条件
	 */
	public Criteria createCriteria(Number value) {
		return CriteriaFactory.create(column, value);
	}

	/**
	 * このカラムの検索条件を生成します。
	 * @see CriteriaFactory#create(Column, Bindable)
	 * @param value 検索条件の値
	 * @return 検索条件
	 */
	public Criteria createCriteria(Bindable value) {
		return CriteriaFactory.create(column, value);
	}

	/**
	 * このカラムの検索条件を生成します。
	 * @see CriteriaFactory#create(ComparisonOperator, Column, Bindable)
	 * @param operator 比較演算子
	 * @param value 検索条件の値
	 * @return 検索条件
	 */
	public Criteria createCriteria(ComparisonOperator operator, Bindable value) {
		return CriteriaFactory.create(operator, column, value);
	}

	/**
	 * このカラムの LIKE 検索条件を生成します。
	 * @see CriteriaFactory#createLikeCriteria(Match, Column, String)
	 * @param type LIKE 検索のタイプ
	 * @param value 検索条件の値
	 * @return 検索条件
	 */
	public Criteria createLikeCriteria(Match type, String value) {
		return CriteriaFactory.createLikeCriteria(type, column, value);
	}

	/**
	 * このカラムの IS NULL 検索条件を生成します。
	 * @see CriteriaFactory#create(NullComparisonOperator, Column)
	 * @return 検索条件
	 */
	public Criteria createIsNullCriteria() {
		return CriteriaFactory.create(NullComparisonOperator.IS_NULL, column);
	}

	/**
	 * このカラムの IS NOT NULL 検索条件を生成します。
	 * @see CriteriaFactory#create(NullComparisonOperator, Column)
	 * @return 検索条件
	 */
	public Criteria createIsNotNullCriteria() {
		return CriteriaFactory.create(NullComparisonOperator.IS_NOT_NULL, column);
	}

	/**
	 * このカラムの検索条件を生成します。
	 * @see CriteriaFactory#createCriteria(String, Column, Bindable)
	 * @param clause WHERE 句の元になるテンプレート
	 * @param value 検索条件の値
	 * @return 検索条件
	 */
	public Criteria createCriteria(String clause, Bindable value) {
		return CriteriaFactory.createCriteria(clause, column, value);
	}

	/**
	 * このインスタンスが表すカラムを {@link Column} として返します。
	 * @return カラムインスタンス
	 */
	public Column getColumn() {
		return column;
	}
}
