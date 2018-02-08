package org.blendee.util.dialect.postgresql;

import java.util.Objects;
import java.util.function.Consumer;

import org.blendee.jdbc.BlenConnection;
import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.jdbc.BlenResultSet;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Result;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataObject;
import org.blendee.sql.Criteria;
import org.blendee.sql.DeleteDMLBuilder;
import org.blendee.sql.InsertDMLBuilder;
import org.blendee.sql.Updatable;
import org.blendee.sql.UpdateDMLBuilder;
import org.blendee.support.Row;

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
		insert(row.getTablePath(), row, consumer, columnNames);
	}

	/**
	 * INSERT を行います。
	 * @param path INSERT 対象テーブル
	 * @param data INSERT 対象
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void insert(TablePath path, Updatable data, Consumer<Result> consumer, String... columnNames) {
		Objects.requireNonNull(path);
		Objects.requireNonNull(data);
		Objects.requireNonNull(consumer);
		checkColumnNames(columnNames);

		BlenConnection connection = ContextManager.get(BlendeeManager.class).getConnection();
		InsertDMLBuilder builder = new InsertDMLBuilder(path);
		builder.add(data);

		String sql = builder.toString() + " RETURNING " + String.join(", ", columnNames);

		try (BlenPreparedStatement statement = connection.prepareStatement(sql)) {
			builder.complement(statement);

			try (BlenResultSet result = statement.executeQuery()) {
				while (result.next()) {
					consumer.accept(result);
				}
			}
		}
	}

	/**
	 * UPDATE を行います。
	 * @param data UPDATE 対象
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void update(DataObject data, Consumer<Result> consumer, String... columnNames) {
		update(data.getRelationship().getTablePath(), data, data.getPrimaryKey().getCriteria(), consumer, columnNames);
	}

	/**
	 * UPDATE を行います。
	 * @param row UPDATE 対象
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void update(Row row, Consumer<Result> consumer, String... columnNames) {
		update(row.getTablePath(), row, row.getPrimaryKey().getCriteria(), consumer, columnNames);
	}

	/**
	 * UPDATE を行います。
	 * @param path UPDATE 対象テーブル
	 * @param data UPDATE 対象
	 * @param criteria UPDATE 条件
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void update(TablePath path, Updatable data, Criteria criteria, Consumer<Result> consumer, String... columnNames) {
		Objects.requireNonNull(path);
		Objects.requireNonNull(data);
		Objects.requireNonNull(criteria);
		Objects.requireNonNull(consumer);
		checkColumnNames(columnNames);

		BlenConnection connection = ContextManager.get(BlendeeManager.class).getConnection();
		UpdateDMLBuilder builder = new UpdateDMLBuilder(path);
		builder.add(data);
		builder.setCriteria(criteria);

		String sql = builder.toString() + " RETURNING " + String.join(", ", columnNames);

		try (BlenPreparedStatement statement = connection.prepareStatement(sql)) {
			builder.complement(statement);

			try (BlenResultSet result = statement.executeQuery()) {
				while (result.next()) {
					consumer.accept(result);
				}
			}
		}
	}

	/**
	 * DELETE を行います。
	 * @param data DELETE 対象
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void delete(DataObject data, Consumer<Result> consumer, String... columnNames) {
		delete(data.getRelationship().getTablePath(), data.getPrimaryKey().getCriteria(), consumer, columnNames);
	}

	/**
	 * DELETE を行います。
	 * @param row DELETE 対象
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void delete(Row row, Consumer<Result> consumer, String... columnNames) {
		delete(row.getTablePath(), row.getPrimaryKey().getCriteria(), consumer, columnNames);
	}

	/**
	 * DELETE を行います。
	 * @param path DELETE 対象テーブル
	 * @param criteria DELETE 条件
	 * @param consumer consumer
	 * @param columnNames RETURNING で使用する項目
	 */
	public static void delete(TablePath path, Criteria criteria, Consumer<Result> consumer, String... columnNames) {
		Objects.requireNonNull(path);
		Objects.requireNonNull(criteria);
		Objects.requireNonNull(consumer);
		checkColumnNames(columnNames);

		BlenConnection connection = ContextManager.get(BlendeeManager.class).getConnection();
		DeleteDMLBuilder builder = new DeleteDMLBuilder(path);
		builder.setCriteria(criteria);

		String sql = builder.toString() + " RETURNING " + String.join(", ", columnNames);

		try (BlenPreparedStatement statement = connection.prepareStatement(sql)) {
			builder.complement(statement);

			try (BlenResultSet result = statement.executeQuery()) {
				while (result.next()) {
					consumer.accept(result);
				}
			}
		}
	}

	private static void checkColumnNames(String... columnNames) {
		if (columnNames.length == 0) throw new IllegalStateException("RETURNING 項目が 0 です");
	}
}
