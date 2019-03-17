package org.blendee.dialect.postgresql;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import org.blendee.assist.Row;
import org.blendee.jdbc.BConnection;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.Result;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataObject;
import org.blendee.sql.Criteria;
import org.blendee.sql.DeleteDMLBuilder;
import org.blendee.sql.InsertDMLBuilder;
import org.blendee.sql.RuntimeIdFactory;
import org.blendee.sql.Updatable;
import org.blendee.sql.UpdateDMLBuilder;

/**
 * PostgreSQL の RETURNING を使用した DML を発行するユーティリティクラスです。
 */
public class ReturningUtilities {

	/**
	 * INSERT を行います。
	 * @param data INSERT 対象
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void insert(DataObject data, Consumer<Result> consumer, String... columnNames) {
		insert(data.getRelationship().getTablePath(), data, consumer, columnNames);
	}

	/**
	 * INSERT を行います。
	 * @param row INSERT 対象
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void insert(Row row, Consumer<Result> consumer, String... columnNames) {
		insert(row.tablePath(), row, consumer, columnNames);
	}

	/**
	 * INSERT を行います。
	 * @param path INSERT 対象テーブル
	 * @param data INSERT 対象
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void insert(TablePath path, Updatable data, Consumer<Result> consumer, String... columnNames) {
		insertAndReturn(path, data, r -> {
			consumer.accept(r);
			return null;
		}, columnNames);
	}

	/**
	 * INSERT を行います。
	 * @param data INSERT 対象
	 * @param function function
	 * @param columnNames RETURNING で使用する項目
	 * @return T
	 */
	public static <T> T insertAndReturn(DataObject data, Function<Result, T> function, String... columnNames) {
		return insertAndReturn(data.getRelationship().getTablePath(), data, function, columnNames);
	}

	/**
	 * INSERT を行います。
	 * @param row INSERT 対象
	 * @param function function
	 * @param columnNames RETURNING で使用する項目
	 * @return T
	 */
	public static <T> T insertAndReturn(Row row, Function<Result, T> function, String... columnNames) {
		return insertAndReturn(row.tablePath(), row, function, columnNames);
	}

	/**
	 * INSERT を行います。
	 * @param path INSERT 対象テーブル
	 * @param data INSERT 対象
	 * @param function function
	 * @param columnNames RETURNING で使用する項目
	 * @return T
	 */
	public static <T> T insertAndReturn(TablePath path, Updatable data, Function<Result, T> function, String... columnNames) {
		Objects.requireNonNull(path);
		Objects.requireNonNull(data);
		Objects.requireNonNull(function);
		checkColumnNames(columnNames);

		BConnection connection = BlendeeManager.getConnection();
		InsertDMLBuilder builder = new InsertDMLBuilder(path);
		builder.add(data);

		String sql = builder.toString() + " RETURNING " + String.join(", ", columnNames);

		try (BPreparedStatement statement = connection.prepareStatement(sql)) {
			builder.complement(statement);

			try (BResultSet result = statement.executeQuery()) {
				result.next();
				return function.apply(result);
			}
		}
	}

	/**
	 * UPDATE を行います。
	 * @param data UPDATE 対象
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void update(DataObject data, Consumer<BResultSet> consumer, String... columnNames) {
		update(data.getRelationship().getTablePath(), data, data.getPrimaryKey().getCriteria(RuntimeIdFactory.stubInstance()), consumer, columnNames);
	}

	/**
	 * UPDATE を行います。
	 * @param row UPDATE 対象
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void update(Row row, Consumer<BResultSet> consumer, String... columnNames) {
		update(row.tablePath(), row, row.primaryKey().getCriteria(RuntimeIdFactory.stubInstance()), consumer, columnNames);
	}

	/**
	 * UPDATE を行います。
	 * @param path UPDATE 対象テーブル
	 * @param data UPDATE 対象
	 * @param criteria UPDATE 条件
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void update(TablePath path, Updatable data, Criteria criteria, Consumer<BResultSet> consumer, String... columnNames) {
		updateAndReturn(path, data, criteria, r -> {
			consumer.accept(r);
			return null;
		}, columnNames);
	}

	/**
	 * UPDATE を行います。
	 * @param data UPDATE 対象
	 * @param function function
	 * @param columnNames RETURNING で使用する項目
	 * @return T
	 */
	public static <T> T updateAndReturn(DataObject data, Function<BResultSet, T> function, String... columnNames) {
		return updateAndReturn(
			data.getRelationship().getTablePath(),
			data,
			data.getPrimaryKey().getCriteria(RuntimeIdFactory.stubInstance()),
			function,
			columnNames);
	}

	/**
	 * UPDATE を行います。
	 * @param row UPDATE 対象
	 * @param function function
	 * @param columnNames RETURNING で使用する項目
	 * @return T
	 */
	public static <T> T updateAndReturn(Row row, Function<BResultSet, T> function, String... columnNames) {
		return updateAndReturn(
			row.tablePath(),
			row,
			row.primaryKey().getCriteria(RuntimeIdFactory.stubInstance()),
			function,
			columnNames);
	}

	/**
	 * UPDATE を行います。
	 * @param path UPDATE 対象テーブル
	 * @param data UPDATE 対象
	 * @param criteria UPDATE 条件
	 * @param function consumer
	 * @param columnNames RETURNING で使用する項目
	 * @return T
	 */
	public static <T> T updateAndReturn(
		TablePath path,
		Updatable data,
		Criteria criteria,
		Function<BResultSet, T> function,
		String... columnNames) {
		Objects.requireNonNull(path);
		Objects.requireNonNull(data);
		Objects.requireNonNull(criteria);
		Objects.requireNonNull(function);
		checkColumnNames(columnNames);

		BConnection connection = BlendeeManager.getConnection();
		UpdateDMLBuilder builder = new UpdateDMLBuilder(path);
		builder.add(data);
		builder.setCriteria(criteria);

		String sql = builder.toString() + " RETURNING " + String.join(", ", columnNames);

		try (BPreparedStatement statement = connection.prepareStatement(sql)) {
			builder.complement(statement);

			try (BResultSet result = statement.executeQuery()) {
				return function.apply(result);
			}
		}
	}

	/**
	 * DELETE を行います。
	 * @param data DELETE 対象
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void delete(DataObject data, Consumer<BResultSet> consumer, String... columnNames) {
		delete(data.getRelationship().getTablePath(), data.getPrimaryKey().getCriteria(RuntimeIdFactory.stubInstance()), consumer, columnNames);
	}

	/**
	 * DELETE を行います。
	 * @param row DELETE 対象
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void delete(Row row, Consumer<BResultSet> consumer, String... columnNames) {
		delete(row.tablePath(), row.primaryKey().getCriteria(RuntimeIdFactory.stubInstance()), consumer, columnNames);
	}

	/**
	 * DELETE を行います。
	 * @param path DELETE 対象テーブル
	 * @param criteria DELETE 条件
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void delete(TablePath path, Criteria criteria, Consumer<BResultSet> consumer, String... columnNames) {
		deleteAndReturn(path, criteria, r -> {
			consumer.accept(r);
			return null;
		}, columnNames);
	}

	/**
	 * DELETE を行います。
	 * @param data DELETE 対象
	 * @param function function
	 * @param columnNames RETURNING で使用する項目
	 * @return T
	 */
	public static <T> T deleteAndReturn(DataObject data, Function<BResultSet, T> function, String... columnNames) {
		return deleteAndReturn(
			data.getRelationship().getTablePath(),
			data.getPrimaryKey().getCriteria(RuntimeIdFactory.stubInstance()),
			function,
			columnNames);
	}

	/**
	 * DELETE を行います。
	 * @param row DELETE 対象
	 * @param function function
	 * @param columnNames RETURNING で使用する項目
	 * @return T
	 */
	public static <T> T deleteAndReturn(Row row, Function<BResultSet, T> function, String... columnNames) {
		return deleteAndReturn(
			row.tablePath(),
			row.primaryKey().getCriteria(RuntimeIdFactory.stubInstance()),
			function,
			columnNames);
	}

	/**
	 * DELETE を行います。
	 * @param path DELETE 対象テーブル
	 * @param criteria DELETE 条件
	 * @param function function
	 * @param columnNames RETURNING で使用する項目
	 * @return T
	 */
	public static <T> T deleteAndReturn(
		TablePath path,
		Criteria criteria,
		Function<BResultSet, T> function,
		String... columnNames) {
		Objects.requireNonNull(path);
		Objects.requireNonNull(criteria);
		Objects.requireNonNull(function);
		checkColumnNames(columnNames);

		BConnection connection = BlendeeManager.getConnection();
		DeleteDMLBuilder builder = new DeleteDMLBuilder(path);
		builder.setCriteria(criteria);

		String sql = builder.toString() + " RETURNING " + String.join(", ", columnNames);

		try (BPreparedStatement statement = connection.prepareStatement(sql)) {
			builder.complement(statement);

			try (BResultSet result = statement.executeQuery()) {
				return function.apply(result);
			}
		}
	}

	private static void checkColumnNames(String... columnNames) {
		if (columnNames.length == 0) throw new IllegalStateException("RETURNING 項目が 0 です");
	}
}
