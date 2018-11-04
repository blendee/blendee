package org.blendee.support;

import org.blendee.sql.Criteria;
import org.blendee.sql.FromClause.JoinType;

class JoinResource {

	JoinType joinType;

	SelectStatement rightRoot;

	Criteria onCriteria;
}
