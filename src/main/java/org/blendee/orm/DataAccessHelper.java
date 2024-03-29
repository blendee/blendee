package org.blendee.orm;

import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.blendee.internal.U;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.Batch;
import org.blendee.jdbc.BlendeeException;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.jdbc.TablePath;
import org.blendee.jdbc.exception.UniqueConstraintViolationException;
import org.blendee.sql.Bindable;
import org.blendee.sql.Column;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.DeleteDMLBuilder;
import org.blendee.sql.FromClause;
import org.blendee.sql.InsertDMLBuilder;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.RuntimeIdFactory;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.SQLQueryBuilder;
import org.blendee.sql.SelectCountClause;
import org.blendee.sql.Updatable;
import org.blendee.sql.UpdateDMLBuilder;

/**
 * データベースに対する CRUD 処理を簡易に行うためのユーティリティクラスです。
 * @author 千葉 哲嗣
 */
public class DataAccessHelper {

	/**
	 * 空の検索結果
	 */
	public static final DataObjectIterator EMPTY_DATA_OBJECT_ITERATOR = new EmptyDataObjectIterator();

	/**
	 * 空の検索結果
	 */
	public static final DataObjectIterator EMPTY_UPDATABLE_DATA_OBJECT_ITERATOR = new EmptyDataObjectIterator();

	private static final ThreadLocal<StatementFacade> threadStatement = new ThreadLocal<StatementFacade>();

	private final RelationshipFactory factory = RelationshipFactory.getInstance();

	private final RuntimeId id;

	/**
	 * インスタンスを生成します。
	 * @param id {@link RuntimeId}
	 */
	public DataAccessHelper(RuntimeId id) {
		this.id = id;
	}

	/**
	 * インスタンスを生成します。
	 */
	public DataAccessHelper() {
		this(RuntimeIdFactory.stubInstance());
	}

	/**
	 * パラメータの主キーの値を持つ {@link DataObject} を検索し返します。
	 * @param context SELECT 句を制御する {@link SelectContext}
	 * @param primaryKey 主キー
	 * @param options 検索オプション
	 * @return 主キーにマッチする {@link DataObject}
	 * @throws DataObjectNotFoundException データが存在しなかった場合
	 * @throws IllegalStateException 検索結果が複数件存在した場合
	 */
	public DataObject getDataObject(
		SelectContext context,
		PrimaryKey primaryKey,
		SQLDecorator... options) {
		checkArgument(context, primaryKey);

		var iterator = select(
			context,
			primaryKey.getCriteria(id),
			null,
			options,
			false);

		return getFirst(iterator);
	}

	/**
	 * パラメータの条件にマッチする {@link DataObject} を検索し返します。
	 * @param context SELECT 句を制御する {@link SelectContext}
	 * @param criteria WHERE 句となる条件
	 * @param order ORDER 句
	 * @param options 検索オプション
	 * @return 条件にマッチする {@link DataObject} を持つ {@link DataObjectIterator}
	 */
	public DataObjectIterator getDataObjects(
		SelectContext context,
		Criteria criteria,
		OrderByClause order,
		SQLDecorator... options) {
		adjustArgument(context, criteria, order);
		return select(context, criteria, order, options, false);
	}

	/**
	 * パラメータの条件にマッチする件数を返します。
	 * @param path 対象となるテーブル
	 * @param criteria WHERE 句となる条件
	 * @return パラメータの条件にマッチする件数
	 */
	public int count(TablePath path, Criteria criteria) {
		var builder = new SQLQueryBuilder(new FromClause(path, id));

		builder.setSelectClause(new SelectCountClause());

		if (criteria != null) builder.setWhereClause(criteria);

		var connection = BlendeeManager.getConnection();

		try (var statement = connection.getStatement(builder)) {
			try (var result = statement.executeQuery()) {
				result.next();
				return result.getInt(1);
			}
		}
	}

	/**
	 * このスレッドが {@link ThreadBatchCallback} 内で行う更新処理を全てバッチ実行します。
	 * @param batch バッチ実行を依頼する {@link Batch}
	 * @param callback 更新処理を定義したコールバック
	 * @throws BlendeeException コールバック内で例外が発生した場合
	 */
	public static void startThreadBatch(
		Batch batch,
		ThreadBatchCallback callback) {
		threadStatement.set(new BatchFacade(batch));
		try {
			callback.execute();
		} catch (Throwable t) {
			throw new BlendeeException(t);
		} finally {
			threadStatement.remove();
		}
	}

	/**
	 * パラメータのテーブルに対して INSERT を行います。
	 * @param path 対象となるテーブル
	 * @param updatable INSERT する値を持つ {@link Updatable}
	 * @param options INSERT 文を調整する {@link SQLDecorator}
	 */
	public void insert(
		TablePath path,
		Updatable updatable,
		SQLDecorator... options) {
		insertInternal(getThreadStatement(), path, updatable, options);
	}

	/**
	 * パラメータのテーブルに対して INSERT をバッチ実行します。
	 * @param batch バッチ実行を依頼する {@link Batch}
	 * @param path 対象となるテーブル
	 * @param updatable INSERT する値を持つ {@link Updatable}
	 * @param options INSERT 文を調整する {@link SQLDecorator}
	 */
	public void insert(
		Batch batch,
		TablePath path,
		Updatable updatable,
		SQLDecorator... options) {
		insertInternal(new BatchFacade(batch), path, updatable, options);
	}

	/**
	 * 連続値を持つテーブルに対して INSERT を行います。
	 * @param path 対象となるテーブル
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param updatable INSERT する値を持つ {@link Updatable}
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @param options INSERT 文を調整する {@link SQLDecorator}
	 * @return INSERT された実際の連続値
	 */
	public Bindable insert(
		TablePath path,
		SequenceGenerator generator,
		Updatable updatable,
		int retry,
		SQLDecorator... options) {
		return insertInternal(
			getThreadStatement(),
			path,
			generator,
			updatable,
			retry,
			options);
	}

	/**
	 * 連続値を持つテーブルに対して INSERT をバッチ実行します。<br>
	 * バッチ実行では、実際に値が登録されるのは後になるので、そのことを考慮した {@link SequenceGenerator} を用意する必要があります。
	 * @param batch バッチ実行を依頼する {@link Batch}
	 * @param path 対象となるテーブル
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param updatable INSERT する値を持つ {@link Updatable}
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @param options INSERT 文を調整する {@link SQLDecorator}
	 * @return INSERT された実際の連続値
	 */
	public Bindable insert(
		Batch batch,
		TablePath path,
		SequenceGenerator generator,
		Updatable updatable,
		int retry,
		SQLDecorator... options) {
		return insertInternal(
			new BatchFacade(batch),
			path,
			generator,
			updatable,
			retry,
			options);
	}

	/**
	 * 対象となるテーブルの、条件に該当するレコードに対して UPDATE を実行します。
	 * @param path 対象となるテーブル
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param criteria WHERE 句となる条件
	 * @param options UPDATE 文を調整する {@link SQLDecorator}
	 * @return 更新件数
	 */
	public int update(
		TablePath path,
		Updatable updatable,
		Criteria criteria,
		SQLDecorator... options) {
		return updateInternal(
			getThreadStatement(),
			path,
			updatable,
			criteria,
			options);
	}

	/**
	 * 対象となるテーブルの、条件に該当するレコードに対して UPDATE をバッチ実行します。
	 * @param batch バッチ実行を依頼する {@link Batch}
	 * @param path 対象となるテーブル
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param criteria WHERE 句となる条件
	 * @param options UPDATE 文を調整する {@link SQLDecorator}
	 */
	public void update(
		Batch batch,
		TablePath path,
		Updatable updatable,
		Criteria criteria,
		SQLDecorator... options) {
		updateInternal(
			new BatchFacade(batch),
			path,
			updatable,
			criteria,
			options);
	}

	/**
	 * 対象となるテーブルの、全レコードに対して UPDATE を実行します。
	 * @param path 対象となるテーブル
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param options UPDATE 文を調整する {@link SQLDecorator}
	 * @return 更新件数
	 */
	public int update(
		TablePath path,
		Updatable updatable,
		SQLDecorator... options) {
		var statement = getThreadStatement();
		return updateInternalFinally(
			statement,
			path,
			updatable,
			null,
			options);
	}

	/**
	 * 対象となるテーブルの、全レコードに対して UPDATE をバッチ実行します。
	 * @param batch バッチ実行を依頼する {@link Batch}
	 * @param path 対象となるテーブル
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param options UPDATE 文を調整する {@link SQLDecorator}
	 */
	public void update(
		Batch batch,
		TablePath path,
		Updatable updatable,
		SQLDecorator... options) {
		updateInternalFinally(
			new BatchFacade(batch),
			path,
			updatable,
			null,
			options);
	}

	/**
	 * 対象となるテーブルに対して、条件に該当するレコードに対する DELETE を実行します。
	 * @param path 対象となるテーブル
	 * @param criteria WHERE 句となる条件
	 * @return 削除件数
	 */
	public int delete(TablePath path, Criteria criteria) {
		return deleteInternal(getThreadStatement(), path, criteria);
	}

	/**
	 * 対象となるテーブルに対して、条件に該当するレコードに対する DELETE をバッチ実行します。
	 * @param batch バッチ実行を依頼する {@link Batch}
	 * @param path 対象となるテーブル
	 * @param criteria WHERE 句となる条件
	 */
	public void delete(
		Batch batch,
		TablePath path,
		Criteria criteria) {
		deleteInternal(
			new BatchFacade(batch),
			path,
			criteria);
	}

	/**
	 * 対象となるテーブルに対して、全レコードに対する DELETE を実行します。
	 * @param path 対象となるテーブル
	 * @return 削除件数
	 */
	public int delete(TablePath path) {
		return deleteInternalFinally(getThreadStatement(), path, null);
	}

	/**
	 * 対象となるテーブルに対して、全レコードに対する DELETE をバッチ実行します。
	 * @param batch バッチ実行を依頼する {@link Batch}
	 * @param path 対象となるテーブル
	 */
	public void delete(Batch batch, TablePath path) {
		deleteInternalFinally(new BatchFacade(batch), path, null);
	}

	/**
	 * {@link SQLQueryBuilder} を生成します。
	 * @param context SELECT 句
	 * @param criteria WHERE 句
	 * @param order ORDER BY 句
	 * @param options オプション
	 * @return {@link SQLQueryBuilder}
	 */
	public SQLQueryBuilder buildSQLQueryBuilder(
		SelectContext context,
		Criteria criteria,
		OrderByClause order,
		SQLDecorator... options) {
		Objects.requireNonNull(context);

		var builder = new SQLQueryBuilder(new FromClause(context.tablePath(), context.runtimeId()));

		if (criteria != null) builder.setWhereClause(criteria);
		if (order != null) builder.setOrderByClause(order);
		if (options != null) builder.addDecorator(options);

		builder.setSelectClause(context.selectClause());

		return builder;
	}

	/**
	 * 検索を実行します。
	 * @param sql SQL
	 * @param complementer {@link PreparedStatementComplementer}
	 * @param selectColumns SELECT 句で選択されたカラム
	 * @param converter {@link SelectContext}
	 * @return 検索結果
	 */
	public static SelectedValuesIterator select(
		String sql,
		PreparedStatementComplementer complementer,
		Column[] selectColumns,
		SelectContext converter) {
		var statement = BlendeeManager.getConnection().getStatement(sql, complementer);
		return new SelectedValuesIterator(
			statement,
			statement.executeQuery(),
			selectColumns,
			converter);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	static StatementFacade getThreadStatement() {
		var statement = threadStatement.get();
		if (statement == null) return new PreparedStatementFacade();
		return statement;
	}

	static int deleteInternal(TablePath path, Criteria criteria) {
		return deleteInternal(getThreadStatement(), path, criteria);
	}

	private DataObjectIterator select(
		SelectContext context,
		Criteria criteria,
		OrderByClause order,
		SQLDecorator[] options,
		boolean readonly) {
		var builder = buildSQLQueryBuilder(context, criteria, order, options);
		return new DataObjectIterator(
			factory.getInstance(context.tablePath()),
			selectInternal(builder.sql(), builder, builder.getSelectClause().getColumns(), context),
			readonly);
	}

	/**
	 * 検索を実行します。
	 * @param sql SQL
	 * @param complementer {@link PreparedStatementComplementer}
	 * @param relationship {@link Relationship}
	 * @param selectColumns SELECT 句で選択されたカラム
	 * @param converter {@link SelectedValuesConverter}
	 * @return 検索結果
	 */
	public static DataObjectIterator select(
		String sql,
		PreparedStatementComplementer complementer,
		Relationship relationship,
		Column[] selectColumns,
		SelectedValuesConverter converter) {
		return new DataObjectIterator(
			relationship,
			selectInternal(sql, complementer, selectColumns, converter),
			false);
	}

	/**
	 * 検索を実行します。
	 * @param sql SQL
	 * @param complementer {@link PreparedStatementComplementer}
	 * @param selectColumns SELECT 句で選択されたカラム
	 * @param converter {@link SelectedValuesConverter}
	 * @return 検索結果
	 */
	private static SelectedValuesIterator selectInternal(
		String sql,
		PreparedStatementComplementer complementer,
		Column[] selectColumns,
		SelectedValuesConverter converter) {
		var statement = BlendeeManager.getConnection().getStatement(sql, complementer);
		return new SelectedValuesIterator(
			statement,
			statement.executeQuery(),
			selectColumns,
			converter);
	}

	/**
	 * 検索結果の一件目を取得します。
	 * @param iterator 検索結果
	 * @return 一件目の {@link DataObject}
	 */
	public static DataObject getFirst(
		DataObjectIterator iterator) {
		DataObject dataObject;
		try {
			//検索結果が 0 件です
			if (!iterator.hasNext()) throw new DataObjectNotFoundException("The number of results is zero.");
			dataObject = iterator.next();
			//検索結果が 1 件以上あります
			if (iterator.hasNext()) throw new IllegalStateException("There are 1 or more search results.");
		} finally {
			iterator.close();
		}

		return dataObject;
	}

	private static void checkArgument(SelectContext context, PrimaryKey primaryKey) {
		if (!context.tablePath().equals(primaryKey.getTablePath()))
			//context と primaryKey のテーブルが違います
			throw new IllegalArgumentException("different tables: context=" + context.tablePath() + ", primaryKey=" + primaryKey.getTablePath());
	}

	private void adjustArgument(
		SelectContext context,
		Criteria criteria,
		OrderByClause order) {
		if (criteria != null) criteria.prepareColumns(factory.getInstance(context.tablePath()));
		if (order != null) order.prepareColumns(factory.getInstance(context.tablePath()));
	}

	private static void insertInternal(
		StatementFacade statement,
		TablePath path,
		Updatable updatable,
		SQLDecorator[] options) {
		var builder = new InsertDMLBuilder(path);

		for (var option : options) {
			builder.addDecorator(option);
		}

		builder.add(updatable);
		statement.process(builder);
		statement.execute();
	}

	private Bindable insertInternal(
		StatementFacade statement,
		TablePath path,
		SequenceGenerator sequencer,
		Updatable updatable,
		int retry,
		SQLDecorator... options) {
		var map = new HashMap<String, Bindable>();
		var builder = new InsertDMLBuilder(path) {

			@Override
			public void add(String columnName, Bindable bindable) {
				super.add(columnName, bindable);
				//このメソッドで追加された値のみ、depends として使える
				map.put(columnName, bindable);
			}
		};

		for (var option : options) {
			builder.addDecorator(option);
		}

		builder.add(updatable);

		var depends = sequencer.getDependsColumnNames();
		var criteria = new CriteriaFactory(id).create();
		var relationship = factory.getInstance(path);

		for (var columnName : depends) {
			criteria.and(new CriteriaFactory(id).create(relationship.getColumn(columnName), map.get(columnName)));
		}

		var targetColumnName = sequencer.getTargetColumnName();

		if (retry <= 0) retry = 3;
		for (var i = 0; i < retry; i++) {
			try {
				var bindable = sequencer.next(criteria);
				builder.add(targetColumnName, bindable);
				statement.process(builder);
				statement.execute();
				return bindable;
			} catch (UniqueConstraintViolationException e) {
			}
		}

		//retry 回数を超えてしまいました
		throw new IllegalStateException("The retry is up to " + retry + " times.");
	}

	private static int updateInternal(
		StatementFacade statement,
		TablePath path,
		Updatable updatable,
		Criteria criteria,
		SQLDecorator... options) {
		//条件がありません
		if (!criteria.isAvailable()) throw new IllegalArgumentException("Criteria not available");
		return updateInternalFinally(statement, path, updatable, criteria, options);
	}

	private static int updateInternalFinally(
		StatementFacade statement,
		TablePath path,
		Updatable updatable,
		Criteria criteria,
		SQLDecorator... options) {
		var builder = new UpdateDMLBuilder(path);

		for (var option : options) {
			builder.addDecorator(option);
		}

		builder.add(updatable);
		if (criteria != null) builder.setCriteria(criteria);
		statement.process(builder);
		return statement.execute();
	}

	private static int deleteInternal(
		StatementFacade statement,
		TablePath path,
		Criteria criteria) {
		//条件がありません
		if (!criteria.isAvailable()) throw new IllegalArgumentException("Criteria not available");
		return deleteInternalFinally(statement, path, criteria);
	}

	private static int deleteInternalFinally(
		StatementFacade statement,
		TablePath path,
		Criteria criteria) {
		var builder = new DeleteDMLBuilder(path);
		if (criteria != null) builder.setCriteria(criteria);
		statement.process(builder);
		return statement.execute();
	}

	static abstract class StatementFacade {

		abstract void process(ComposedSQL sql);

		abstract int execute();
	}

	static class BatchFacade extends StatementFacade {

		static final int DUMMY_RESULT = -1;

		private Batch batch;

		BatchFacade(Batch statement) {
			this.batch = statement;
		}

		@Override
		void process(ComposedSQL sql) {
			batch.add(sql);
		}

		@Override
		int execute() {
			return DUMMY_RESULT;
		}
	}

	private static class PreparedStatementFacade extends StatementFacade {

		private BStatement statement;

		@Override
		void process(ComposedSQL sql) {
			statement = BlendeeManager.getConnection().getStatement(sql);
		}

		@Override
		int execute() {
			int result;
			try {
				result = statement.executeUpdate();
			} finally {
				statement.close();
			}
			return result;
		}
	}

	private static class EmptyDataObjectIterator extends DataObjectIterator {

		EmptyDataObjectIterator() {
			super(null, null, false);
		}

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public DataObject next() {
			throw new NoSuchElementException();
		}

		@Override
		public DataObject nextDataObject() {
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int getCounter() {
			return 0;
		}

		@Override
		public void close() {
		}

		@Override
		public String toString() {
			return U.toString(this);
		}
	}
}
