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
	static final TableFacadeContext<TableFacadeColumn> OTHER = (assist, name) -> new TableFacadeColumn(
		assist,
		name);

	/**
	 * WHERE 句用
	 * @param <O> {@link SelectStatement} 実装
	 * @return WHERE 句用 Context
	 */
	static <O extends LogicalOperators<?>> TableFacadeContext<WhereColumn<O>> newWhereBuilder() {
		return (assist, name) -> new WhereColumn<>(
			assist.getSelectStatement(),
			assist.getContext(),
			Helper.buildRuntimeIdColumn(assist, name));
	}

	/**
	 * HAVING 句用
	 * @param <O> {@link SelectStatement} 実装
	 * @return HAVING 句用 Context
	 */
	static <O extends LogicalOperators<?>> TableFacadeContext<HavingColumn<O>> newHavingBuilder() {
		return (assist, name) -> new HavingColumn<>(
			assist.getSelectStatement(),
			assist.getContext(),
			Helper.buildRuntimeIdColumn(assist, name));
	}

	/**
	 * ON 句 (LEFT) 用
	 * @param <O> {@link SelectStatement} 実装
	 * @return ON 句用 Context
	 */
	static <O extends LogicalOperators<?>> TableFacadeContext<OnLeftColumn<O>> newOnLeftBuilder() {
		return (assist, name) -> new OnLeftColumn<>(
			assist.getSelectStatement(),
			assist.getContext(),
			Helper.buildRuntimeIdColumn(assist, name));
	}

	/**
	 * ON 句 (RIGHT) 用
	 * @param <O> {@link SelectStatement} 実装
	 * @return ON 句用 Context
	 */
	static <O extends LogicalOperators<?>> TableFacadeContext<OnRightColumn<O>> newOnRightBuilder() {
		return (assist, name) -> new OnRightColumn<>(
			assist.getSelectStatement(),
			assist.getContext(),
			Helper.buildRuntimeIdColumn(assist, name));
	}

	/**
	 * WHERE 句用
	 * @param <O> {@link SelectStatement} 実装
	 * @return WHERE 句用 Context
	 */
	static <O extends LogicalOperators<?>> TableFacadeContext<WhereColumn<O>> newDMSWhereBuilder() {
		return (assist, name) -> new WhereColumn<>(
			assist.getDataManipulationStatement(),
			assist.getContext(),
			Helper.buildRuntimeIdColumnForUpdate(assist, name));
	}

	/**
	 * このインスタンスに対応したカラムインスタンスを生成します。
	 * @param relationship 条件作成に必要な情報を持った {@link TableFacadeAssist}
	 * @param name カラム名
	 * @return カラムインスタンス
	 */
	T buildColumn(TableFacadeAssist relationship, String name);
}
