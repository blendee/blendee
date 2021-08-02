package org.blendee.sql;

import org.blendee.jdbc.ComposedSQL;
import org.blendee.sql.SQLQueryBuilder.CombineOperator;

/**
 * UNION されたクエリを構成する要素を保持するクラスです。
 * @author 千葉 哲嗣
 */
public class CombiningQuery {

	private final CombineOperator combineOperator;

	private final ComposedSQL query;

	/**
	 * @param combineOperator {@link CombineOperator}
	 * @param query {@link ComposedSQL}
	 */
	public CombiningQuery(CombineOperator combineOperator, ComposedSQL query) {
		this.combineOperator = combineOperator;
		this.query = query;
	}

	/**
	 * @return {@link CombineOperator}
	 */
	public CombineOperator getCombineOperator() {
		return combineOperator;
	}

	/**
	 * @return {@link ComposedSQL}
	 */
	public ComposedSQL getSQL() {
		return query;
	}
}
