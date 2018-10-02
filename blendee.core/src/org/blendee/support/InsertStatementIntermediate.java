package org.blendee.support;

import java.util.ArrayList;
import java.util.List;

import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Column;
import org.blendee.sql.InsertDMLBuilder;
import org.blendee.sql.ValueExtractors;
import org.blendee.sql.ValueExtractorsConfigure;
import org.blendee.sql.binder.NullBinder;

/**
 * VALUES 句
 */
public class InsertStatementIntermediate {

	private final TablePath table;

	private final List<Column> columns;

	public InsertStatementIntermediate(TablePath table, List<Column> columns) {
		this.table = table;
		this.columns = new ArrayList<>(columns);
	}

	/**
	 * @param values
	 * @return {@link SelectStatement}
	 */
	public DataManipulator VALUES(Object... values) {
		if (columns.size() != values.length) throw new IllegalStateException();

		ValueExtractors valueExtractors = ContextManager.get(ValueExtractorsConfigure.class).getValueExtractors();

		InsertDMLBuilder builder = new InsertDMLBuilder(table);

		for (int i = 0; i < values.length; i++) {
			Column c = columns.get(i);
			Object v = values[i];
			if (v != null) {
				builder.add(c.getName(), valueExtractors.selectValueExtractor(v.getClass()).extractAsBinder(v));
			} else {
				builder.add(c.getName(), new NullBinder(c.getColumnMetadata().getType()));
			}
		}

		return new PlaybackDataManipulator(builder.sql(), builder.getBinders());
	}
}
