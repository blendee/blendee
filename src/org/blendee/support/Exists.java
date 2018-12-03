package org.blendee.support;

import java.util.List;

import org.blendee.sql.Binder;
import org.blendee.sql.Column;
import org.blendee.sql.ComplementerValues;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.SQLQueryBuilder;

class Exists {

	static void setExists(RuntimeId main, CriteriaRelationship<?> relationship, SelectStatement subquery, String keyword) {
		relationship.getStatement().forSubquery(true);

		SQLQueryBuilder builder = subquery.toSQLQueryBuilder();
		builder.forSubquery(true);

		builder.sql();

		//サブクエリのFrom句からBinderを取り出す前にsql化して内部のFrom句をマージしておかないとBinderが準備されないため、先に実行
		String subqueryString = keyword + " (" + builder.sql() + ")";

		List<Binder> binders = new ComplementerValues(builder).binders();
		relationship.getContext().addCriteria(
			new CriteriaFactory(main).createCriteria(subqueryString, Column.EMPTY_ARRAY, binders.toArray(new Binder[binders.size()])));
	}
}
