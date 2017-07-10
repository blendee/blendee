package org.blendee.selector;

import java.util.Objects;

import org.blendee.internal.U;
import org.blendee.jdbc.BResult;
import org.blendee.jdbc.BlendeeContext;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Column;
import org.blendee.sql.SelectClause;
import org.blendee.sql.ValueExtractors;
import org.blendee.sql.ValueExtractorsConfigure;

/**
 * static かつ final なフィールドを ID として {@link ColumnRepository} から SELECT 句構成カラムを取得する {@link Optimizer} です。
 * @author 千葉 哲嗣
 */
public class AnchorOptimizer implements Optimizer {

	private final TablePath hint;

	private final boolean canAddNewEntries;

	private final ValueExtractors extractors = BlendeeContext.get(ValueExtractorsConfigure.class).getValueExtractors();

	private final String id;

	private final Class<?> using;

	private final ColumnRepository repository;

	@Override
	public String toString() {
		return U.toString(this);
	}

	AnchorOptimizer(
		AnchorOptimizerFactory factory,
		String id,
		TablePath hint,
		Class<?> using,
		boolean canAddNewEntries) {
		Objects.requireNonNull(factory);
		Objects.requireNonNull(id);

		this.hint = hint;
		this.canAddNewEntries = canAddNewEntries;
		this.id = id;
		this.using = using;

		if (canAddNewEntries) {
			repository = factory.createColumnRepository();
		} else {
			repository = new StrictColumnRepository(factory.createColumnRepository());
		}
	}

	@Override
	public TablePath getTablePath() {
		TablePath path = repository.getTablePath(id);
		if (path == null) {
			if (canAddNewEntries && hint != null) {
				repository.add(id, hint, using.getName());
				return hint;
			}

			throw new IllegalStateException(id + " がリポジトリにありません");
		}

		return path;
	}

	@Override
	public SelectClause getOptimizedSelectClause() {
		Column[] columns = getColumns();

		if (columns.length == 0) {
			if (canAddNewEntries) return new InitialSelectClause();

			// canAddNewEntries されていないのに 検索項目が 0 件のものはエラーとする
			throw new IllegalStateException("この optimizer は項目が一つもありません");
		}

		SelectClause clause = new SelectClause();
		for (int i = 0; i < columns.length; i++) {
			clause.add(columns[i]);
		}
		return clause;
	}

	@Override
	public SelectedValues convert(BResult result, Column[] columns) {
		if (canAddNewEntries) return new AddingSelectedValues(
			extractors,
			repository,
			id,
			result,
			columns);

		return new ConcreteSelectedValues(result, columns, extractors);
	}

	private Column[] getColumns() {
		return repository.getColumns(id);
	}

	private static class InitialSelectClause extends SelectClause {

		@Override
		public String toString(boolean joining) {
			if (getColumnsSize() == 0) return "SELECT * ";
			return super.toString(joining);
		}

		@Override
		protected SelectClause createNewInstance() {
			return new InitialSelectClause();
		}
	}

	private class AddingSelectedValues extends ConcreteSelectedValues {

		private final String id;

		private final ColumnRepository repository;

		private AddingSelectedValues(
			ValueExtractors extractors,
			ColumnRepository repository,
			String id,
			BResult result,
			Column[] columns) {
			super(result, columns, extractors);
			this.repository = repository;
			this.id = id;
		}

		@Override
		public boolean isNull(Column column) {
			try {
				return super.isNull(column);
			} catch (IllegalValueException e) {
				repository.addColumn(id, column, using.getName());
				throw newException(e, id);
			} finally {
				repository.markColumn(id, column, using.getName());
				repository.commit();
			}
		}

		@Override
		public Object getObject(Column column) {
			try {
				return super.getObject(column);
			} catch (IllegalValueException e) {
				repository.addColumn(id, column, using.getName());
				throw newException(e, id);
			} finally {
				repository.markColumn(id, column, using.getName());
				repository.commit();
			}
		}
	}

	private static IllegalValueException newException(IllegalValueException e, String id) {
		return new IllegalValueException(e.getMessage() + U.LINE_SEPARATOR + "新たに " + id + " に追加されました");
	}
}
