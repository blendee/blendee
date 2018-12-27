package org.blendee.assist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Column;
import org.blendee.sql.InsertDMLBuilder;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.ValueExtractors;
import org.blendee.sql.ValueExtractorsConfigure;
import org.blendee.sql.binder.NullBinder;

/**
 * INSERT 文中間形態です。
 * @author 千葉 哲嗣
 */
public class InsertStatementIntermediate {

	private final TablePath table;

	private final SQLDecorators decorators;

	private final List<Column> columns;

	InsertStatementIntermediate(TablePath table, SQLDecorators decorators, List<Column> columns) {
		this.table = table;
		this.decorators = decorators;
		this.columns = new ArrayList<>(columns);
	}

	/**
	 * @param values プレースホルダにセットする値
	 * @return {@link SelectStatement}
	 */
	public DataManipulator VALUES(Object... values) {
		if (columns.size() == 0) {
			columns.addAll(
				Arrays.asList(RelationshipFactory.getInstance().getInstance(table).getColumns()));
		}

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

		builder.addDecorator(decorators.decorators());

		return new PlaybackDataManipulator(builder.sql(), builder.getBinders());
	}
}
