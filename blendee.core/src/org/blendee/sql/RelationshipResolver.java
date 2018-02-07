package org.blendee.sql;

import java.util.List;

import org.blendee.jdbc.TablePath;

public interface RelationshipResolver {

	boolean canTraverse(List<TablePath> relationshipPath, TablePath target);
}
