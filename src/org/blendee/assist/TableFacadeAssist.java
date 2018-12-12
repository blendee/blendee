package org.blendee.assist;

import org.blendee.sql.Relationship;

/**
 * 自動生成される Assist の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface TableFacadeAssist {

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return QueryRelationship が WHERE 句用の場合、そのタイプに応じた {@link CriteriaContext}
	 */
	CriteriaContext getContext();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスが表す {@link Relationship}
	 */
	Relationship getRelationship();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスの大元の {@link SelectStatement}
	 */
	SelectStatement getSelectStatement();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスの大元の {@link DataManipulationStatement}
	 */
	DataManipulationStatement getDataManipulationStatement();

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return {@link OneToManyBehavior}
	 */
	OneToManyBehavior getOneToManyBehavior();
}
