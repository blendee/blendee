package org.blendee.assist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.blendee.jdbc.TablePath;
import org.blendee.sql.Column;
import org.blendee.sql.InsertDMLBuilder;
import org.blendee.sql.RelationshipFactory;
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
		prepareColumns();

		if (columns.size() != values.length)
			//カラム数と値の数が違います
			throw new IllegalStateException("different size: columns=" + columns.size() + ", values=" + values.length);

		var extractor = new BinderExtractor();

		var builder = new InsertDMLBuilder(table);

		for (int i = 0; i < values.length; i++) {
			var c = columns.get(i);
			var v = values[i];
			if (v != null) {
				builder.add(c.getName(), extractor.extract(v));
			} else {
				builder.add(c.getName(), new NullBinder(c.getColumnMetadata().getType()));
			}
		}

		builder.addDecorator(decorators.decorators());

		return new RawDataManipulator(builder);
	}

	/**
	 * @param consumer 値を {@link Values} に追加させる {@link Consumer}
	 * @return {@link SelectStatement}
	 */
	public DataManipulator VALUES(Consumer<Values> consumer) {
		prepareColumns();

		var builder = new InsertDMLBuilder(table);

		var values = new Values(builder, columns);
		consumer.accept(values);

		builder.addDecorator(decorators.decorators());

		return new RawDataManipulator(builder);
	}

	private void prepareColumns() {
		if (columns.size() == 0) {
			columns.addAll(
				Arrays.asList(RelationshipFactory.getInstance().getInstance(table).getColumns()));
		}
	}
}
