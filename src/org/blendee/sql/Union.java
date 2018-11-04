package org.blendee.sql;

import org.blendee.jdbc.ComposedSQL;
import org.blendee.sql.SQLQueryBuilder.UnionOperator;

/**
 * UNION されたクエリを構成する要素を保持するクラスです。
 * @author 千葉 哲嗣
 */
public class Union {

	private final UnionOperator unionOperator;

	private final ComposedSQL query;

	/**
	 * @param unionOperator {@link UnionOperator}
	 * @param query {@link ComposedSQL}
	 */
	public Union(UnionOperator unionOperator, ComposedSQL query) {
		this.unionOperator = unionOperator;
		this.query = query;
	}

	/**
	 * @return {@link UnionOperator}
	 */
	public UnionOperator getUnionOperator() {
		return unionOperator;
	}

	/**
	 * @return {@link ComposedSQL}
	 */
	public ComposedSQL getSQL() {
		return query;
	}
}
