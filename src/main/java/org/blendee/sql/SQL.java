package org.blendee.sql;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.Batch;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.ResultSetIterator;

/**
 * このクラスのインスタンスは、 SQL 文とプレースホルダにセットする値を保持し、その SQL を実行することが可能です。
 * @author 千葉 哲嗣
 */
public class SQL implements ComposedSQL, Reproducible<SQL> {

	private final String sql;

	private final ComplementerValues values;

	/**
	 * @param sql SQL 文
	 * @param values {@link Object} プレースホルダにセットする値
	 * @return {@link SQL}
	 */
	public static SQL getInstanceWithPlaceholderValues(String sql, Object... values) {
		return new SQL(Objects.requireNonNull(sql), ComplementerValues.getInstanceOfPlaceHolderValues(values));
	}

	/**
	 * @param sql SQL 文
	 * @param values {@link ComplementerValues}
	 * @return {@link SQL}
	 */
	public static SQL getInstance(String sql, ComplementerValues values) {
		return new SQL(sql, values);
	}

	/**
	 * @param sql {@link ComposedSQL}
	 * @return {@link SQL}
	 */
	public static SQL getInstance(ComposedSQL sql) {
		return new SQL(sql.sql(), ComplementerValues.of(sql));
	}

	private SQL(String sql, ComplementerValues values) {
		this.sql = sql;
		this.values = values;
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		return values.complement(done, statement);
	}

	@Override
	public SQL reproduce(Object... placeHolderValues) {
		return new SQL(sql, values.reproduce(placeHolderValues));
	}

	@Override
	public SQL reproduce() {
		return new SQL(sql, values);
	}

	@Override
	public Binder[] currentBinders() {
		return values.currentBinders();
	}

	@Override
	public String sql() {
		return sql;
	}

	/**
	 * 検索を実行します。
	 * @param action {@link Consumer}
	 */
	public void executeQuery(Consumer<BResultSet> action) {
		var connection = BlendeeManager.getConnection();
		try (var statement = connection.getStatement(this)) {
			try (var result = statement.executeQuery()) {
				action.accept(result);
			}
		}
	}

	/**
	 * 検索を実行します。
	 * @param action {@link Function}
	 * @param <T> 戻り値の型
	 * @return 任意の型の戻り値
	 */
	public <T> T executeQuery(Function<BResultSet, T> action) {
		var connection = BlendeeManager.getConnection();
		try (var statement = connection.getStatement(this)) {
			try (var result = statement.executeQuery()) {
				return action.apply(result);
			}
		}
	}

	/**
	 * 検索を実行します。
	 * @return {@link ResultSetIterator}
	 */
	public ResultSetIterator executeQuery() {
		return new ResultSetIterator(this);
	}

	/**
	 * データ更新を実行します。
	 * @return 対象件数
	 */
	public int executeUpdate() {
		var connection = BlendeeManager.getConnection();
		try (var statement = connection.getStatement(this)) {
			return statement.executeUpdate();
		}
	}

	/**
	 * データ更新をバッチ実行します。
	 * @param batch {@link Batch}
	 */
	public void executeUpdate(Batch batch) {
		batch.add(this);
	}
}
