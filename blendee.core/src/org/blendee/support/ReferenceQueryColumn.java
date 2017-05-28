package org.blendee.support;

import org.blendee.sql.Bindable;
import org.blendee.sql.Column;
import org.blendee.sql.Condition;
import org.blendee.sql.ConditionFactory;
import org.blendee.sql.ConditionFactory.ComparisonOperator;
import org.blendee.sql.ConditionFactory.Match;
import org.blendee.sql.ConditionFactory.NullComparisonOperator;

/**
 * WHERE 句に追加する新しい条件を生成するクラスです。
 * <br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 *
 * @author 千葉 哲嗣
 */
public class ReferenceQueryColumn {

	private final Column column;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 *
	 * @param relationship 条件作成に必要な情報を持った {@link QueryRelationship}
	 * @param name カラム名
	 */
	public ReferenceQueryColumn(QueryRelationship relationship, String name) {
		column = relationship.getRelationship().getColumn(name);
	}

	/**
	 * このカラムの検索条件を生成します。
	 *
	 * @see ConditionFactory#createCondition(Column, String)
	 * @param value 検索条件の値
	 * @return 検索条件
	 */
	public Condition createCondition(String value) {
		return ConditionFactory.createCondition(column, value);
	}

	/**
	 * このカラムの検索条件を生成します。
	 *
	 * @see ConditionFactory#createCondition(Column, Bindable)
	 * @param value 検索条件の値
	 * @return 検索条件
	 */
	public Condition createCondition(Bindable value) {
		return ConditionFactory.createCondition(column, value);
	}

	/**
	 * このカラムの検索条件を生成します。
	 *
	 * @see ConditionFactory#createComparisonCondition(ComparisonOperator, Column, Bindable)
	 * @param operator 比較演算子
	 * @param value 検索条件の値
	 * @return 検索条件
	 */
	public Condition createCondition(ComparisonOperator operator, Bindable value) {
		return ConditionFactory.createComparisonCondition(operator, column, value);
	}

	/**
	 * このカラムの LIKE 検索条件を生成します。
	 *
	 * @see ConditionFactory#createLikeCondition(Match, Column, String)
	 * @param type LIKE 検索のタイプ
	 * @param value 検索条件の値
	 * @return 検索条件
	 */
	public Condition createLikeCondition(Match type, String value) {
		return ConditionFactory.createLikeCondition(type, column, value);
	}

	/**
	 * このカラムの IS NULL 検索条件を生成します。
	 *
	 * @see ConditionFactory#createNullCondition(NullComparisonOperator, Column)
	 * @return 検索条件
	 */
	public Condition createIsNullCondition() {
		return ConditionFactory.createNullCondition(NullComparisonOperator.IS_NULL, column);
	}

	/**
	 * このカラムの IS NOT NULL 検索条件を生成します。
	 *
	 * @see ConditionFactory#createNullCondition(NullComparisonOperator, Column)
	 * @return 検索条件
	 */
	public Condition createIsNotNullCondition() {
		return ConditionFactory.createNullCondition(NullComparisonOperator.IS_NOT_NULL, column);
	}

	/**
	 * このカラムの検索条件を生成します。
	 *
	 * @see ConditionFactory#createCondition(String, Column, Bindable)
	 * @param clause WHERE 句の元になるテンプレート
	 * @param value 検索条件の値
	 * @return 検索条件
	 */
	public Condition createCondition(String clause, Bindable value) {
		return ConditionFactory.createCondition(clause, column, value);
	}

	/**
	 * このインスタンスが表すカラムを {@link Column} として返します。
	 *
	 * @return カラムインスタンス
	 */
	public Column getColumn() {
		return column;
	}
}
