package org.blendee.assist;

/**
 * 自動生成される QueryBuilder クラスのインスタンスのタイプを表すインターフェイスです。<br>
 * 内部使用を目的としています。
 * @param <T> このインターフェイスの実装クラスに対応したカラムクラス
 * @author 千葉 哲嗣
 */
@FunctionalInterface
public interface TableFacadeContext<T> {

	/**
	 * 参照用
	 */
	static final TableFacadeContext<TableFacadeColumn> OTHER = (relationship, name) -> new TableFacadeColumn(
		relationship.getRelationship(),
		name);

	/**
	 * WHERE 句用
	 * @param <O> {@link SelectStatement} 実装
	 * @return WHERE 句用 Context
	 */
	static <O extends LogicalOperators<?>> TableFacadeContext<WhereColumn<O>> newWhereBuilder() {
		return (relationship, name) -> new WhereColumn<>(
			relationship.getSelectStatement(),
			relationship.getContext(),
			relationship.getRelationship().getColumn(name));
	}

	/**
	 * HAVING 句用
	 * @param <O> {@link SelectStatement} 実装
	 * @return HAVING 句用 Context
	 */
	static <O extends LogicalOperators<?>> TableFacadeContext<HavingColumn<O>> newHavingBuilder() {
		return (relationship, name) -> new HavingColumn<>(
			relationship.getSelectStatement(),
			relationship.getContext(),
			relationship.getRelationship().getColumn(name));
	}

	/**
	 * ON 句 (LEFT) 用
	 * @param <O> {@link SelectStatement} 実装
	 * @return ON 句用 Context
	 */
	static <O extends LogicalOperators<?>> TableFacadeContext<OnLeftColumn<O>> newOnLeftBuilder() {
		return (relationship, name) -> new OnLeftColumn<>(
			relationship.getSelectStatement(),
			relationship.getContext(),
			relationship.getRelationship().getColumn(name));
	}

	/**
	 * ON 句 (RIGHT) 用
	 * @param <O> {@link SelectStatement} 実装
	 * @return ON 句用 Context
	 */
	static <O extends LogicalOperators<?>> TableFacadeContext<OnRightColumn<O>> newOnRightBuilder() {
		return (relationship, name) -> new OnRightColumn<>(
			relationship.getSelectStatement(),
			relationship.getContext(),
			relationship.getRelationship().getColumn(name));
	}

	/**
	 * WHERE 句用
	 * @param <O> {@link SelectStatement} 実装
	 * @return WHERE 句用 Context
	 */
	static <O extends LogicalOperators<?>> TableFacadeContext<WhereColumn<O>> newDMSWhereBuilder() {
		return (relationship, name) -> new WhereColumn<>(
			relationship.getDataManipulationStatement(),
			relationship.getContext(),
			relationship.getRelationship().getColumn(name));
	}

	/**
	 * このインスタンスに対応したカラムインスタンスを生成します。
	 * @param relationship 条件作成に必要な情報を持った {@link TableFacadeAssist}
	 * @param name カラム名
	 * @return カラムインスタンス
	 */
	T buildColumn(TableFacadeAssist relationship, String name);
}