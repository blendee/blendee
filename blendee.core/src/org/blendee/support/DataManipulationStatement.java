package org.blendee.support;

import org.blendee.sql.Column;

public interface DataManipulationStatement {

	void addInsertColumns(Column column);

	void addSetElement(SetElement element);
}
