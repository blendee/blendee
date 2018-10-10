package org.blendee.orm;

import java.util.HashMap;
import java.util.Map;

import org.blendee.internal.LRUCache;
import org.blendee.internal.U;
import org.blendee.jdbc.BConnection;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.jdbc.TablePath;
import org.blendee.jdbc.exception.UniqueConstraintViolationException;
import org.blendee.selector.Optimizer;
import org.blendee.selector.SelectedValuesConverter;
import org.blendee.selector.Selector;
import org.blendee.sql.Bindable;
import org.blendee.sql.Column;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.DeleteDMLBuilder;
import org.blendee.sql.FromClause;
import org.blendee.sql.InsertDMLBuilder;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.QueryId;
import org.blendee.sql.QueryIdFactory;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.SQLQueryBuilder;
import org.blendee.sql.SelectCountClause;
import org.blendee.sql.Updatable;
import org.blendee.sql.UpdateDMLBuilder;

/**
 * データベースに対する CRUD 処理を簡易に行うためのユーティリティクラスです。<br>
 * このクラスのインスタンスで検索を行うと、使用した SQL 文をパラメータで渡す {@link Optimizer} をキーとしてキャッシュします。
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

	private final RelationshipFactory factory = ContextManager.get(RelationshipFactory.class);

	private final LRUCache<Optimizer, Selector> selectorCache = LRUCache.newInstance(50);

	private final QueryId id;

	private final boolean useCache;

	/**
	 * インスタンスを生成します。
	 * @param id
	 */
	public DataAccessHelper(QueryId id) {
		this(id, false);
	}

	/**
	 * インスタンスを生成します。
	 * @param id
	 * @param useCache select 文をキャッシュするかどうか
	 */
	public DataAccessHelper(QueryId id, boolean useCache) {
		this.id = id;
		this.useCache = useCache;
	}

	/**
	 * インスタンスを生成します。
	 */
	public DataAccessHelper() {
		this(QueryIdFactory.getInstance(), false);
	}

	/**
	 * インスタンスを生成します。
	 * @param useCache select 文をキャッシュするかどうか
	 */
	public DataAccessHelper(boolean useCache) {
		this(QueryIdFactory.getInstance(), useCache);
	}

	/**
	 * パラメータの主キーの値を持つ {@link DataObject} を検索し返します。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param primaryKey 主キー
	 * @param options 検索オプション
	 * @return 主キーにマッチする {@link DataObject}
	 * @throws DataObjectNotFoundException データが存在しなかった場合
	 * @throws IllegalStateException 検索結果が複数件存在した場合
	 */
	public DataObject getDataObject(
		Optimizer optimizer,
		PrimaryKey primaryKey,
		SQLDecorator... options) {
		checkArgument(optimizer, primaryKey);
		DataObjectIterator iterator = select(
			optimizer,
			primaryKey.getCriteria(id),
			null,
			options,
			false);
		return getFirst(iterator);
	}

	/**
	 * パラメータの条件にマッチする {@link DataObject} を検索し返します。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param criteria WHERE 句となる条件
	 * @param order ORDER 句
	 * @param options 検索オプション
	 * @return 条件にマッチする {@link DataObject} を持つ {@link DataObjectIterator}
	 */
	public DataObjectIterator getDataObjects(
		Optimizer optimizer,
		Criteria criteria,
		OrderByClause order,
		SQLDecorator... options) {
		adjustArgument(optimizer, criteria, order);
		return select(optimizer, criteria, order, options, false);
	}

	/**
	 * パラメータの主キーの値を持つ {@link DataObject} を検索する SQL 文をあらかじめこのインスタンスにキャッシュしておきます。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param primaryKey 主キー
	 * @param options 検索オプション
	 */
	public void study(
		Optimizer optimizer,
		PrimaryKey primaryKey,
		SQLDecorator... options) {
		if (!useCache) throw new UnsupportedOperationException("キャッシュが使用できないと、この操作はできません");

		checkArgument(optimizer, primaryKey);
		getSelector(optimizer, primaryKey.getCriteria(id), null, options);
	}

	/**
	 * パラメータの条件にマッチする {@link DataObject} を検索する SQL 文をあらかじめこのインスタンスにキャッシュしておきます。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param criteria WHERE 句となる条件
	 * @param order ORDER 句
	 * @param options 検索オプション
	 */
	public void study(
		Optimizer optimizer,
		Criteria criteria,
		OrderByClause order,
		SQLDecorator... options) {
		if (!useCache) throw new UnsupportedOperationException("キャッシュが使用できないと、この操作はできません");

		adjustArgument(optimizer, criteria, order);
		getSelector(optimizer, criteria, order, options);
	}

	/**
	 * パラメータの {@link Optimizer} を使用して前回行われた検索を再実行します。
	 * @param optimizer キーとなる {@link Optimizer}
	 * @return 再実行結果の {@link DataObject}
	 * @throws DataObjectNotFoundException データが存在しなかった場合
	 * @throws IllegalStateException 検索結果が複数件存在した場合
	 */
	public DataObject regetDataObject(Optimizer optimizer) {
		if (!useCache) throw new UnsupportedOperationException("キャッシュが使用できないと、この操作はできません");

		synchronized (selectorCache) {
			Selector selector = selectorCache.get(optimizer);
			if (selector == null)
				throw new IllegalStateException("この optimizer を使用して、一度 getDataObject か study を実行してください");

			return getFirst(select(selector, false));
		}
	}

	/**
	 * パラメータの {@link Optimizer} を使用して前回行われた検索を再実行します。
	 * @param optimizer キーとなる {@link Optimizer}
	 * @return 再実行結果の {@link DataObject} を持つ {@link DataObjectIterator}
	 */
	public DataObjectIterator regetDataObjects(Optimizer optimizer) {
		if (!useCache) throw new UnsupportedOperationException("キャッシュが使用できないと、この操作はできません");

		synchronized (selectorCache) {
			Selector selector = selectorCache.get(optimizer);
			if (selector == null) throw new IllegalStateException("この optimizer を使用して、一度 select か study を実行してください");

			return select(selector, false);
		}
	}

	/**
	 * パラメータの条件にマッチする件数を返します。
	 * @param path 対象となるテーブル
	 * @param criteria WHERE 句となる条件
	 * @return パラメータの条件にマッチする件数
	 */
	public int count(TablePath path, Criteria criteria) {
		SQLQueryBuilder builder = new SQLQueryBuilder(new FromClause(path, id));
		builder.setSelectClause(new SelectCountClause());
		if (criteria != null) builder.setWhereClause(criteria);
		BConnection connection = BlendeeManager.getConnection();
		try (BStatement statement = connection.getStatement(builder)) {
			try (BResultSet result = statement.executeQuery()) {
				result.next();
				return result.getInt(1);
			}
		}
	}

	/**
	 * このスレッドが {@link ThreadBatchCallback} 内で行う更新処理を全てバッチ実行します。
	 * @param batchStatement バッチ実行を依頼する {@link BatchStatement}
	 * @param callback 更新処理を定義したコールバック
	 * @throws Exception コールバック内で例外が発生した場合
	 */
	public static void startThreadBatch(
		BatchStatement batchStatement,
		ThreadBatchCallback callback)
		throws Exception {
		threadStatement.set(new BatchStatementFacade(batchStatement));
		try {
			callback.execute();
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
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param path 対象となるテーブル
	 * @param updatable INSERT する値を持つ {@link Updatable}
	 * @param options INSERT 文を調整する {@link SQLDecorator}
	 */
	public void insert(
		BatchStatement statement,
		TablePath path,
		Updatable updatable,
		SQLDecorator... options) {
		insertInternal(new BatchStatementFacade(statement), path, updatable, options);
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
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param path 対象となるテーブル
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param updatable INSERT する値を持つ {@link Updatable}
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @param options INSERT 文を調整する {@link SQLDecorator}
	 * @return INSERT された実際の連続値
	 */
	public Bindable insert(
		BatchStatement statement,
		TablePath path,
		SequenceGenerator generator,
		Updatable updatable,
		int retry,
		SQLDecorator... options) {
		return insertInternal(
			new BatchStatementFacade(statement),
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
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param path 対象となるテーブル
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param criteria WHERE 句となる条件
	 * @param options UPDATE 文を調整する {@link SQLDecorator}
	 */
	public void update(
		BatchStatement statement,
		TablePath path,
		Updatable updatable,
		Criteria criteria,
		SQLDecorator... options) {
		updateInternal(
			new BatchStatementFacade(statement),
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
		StatementFacade statement = getThreadStatement();
		return updateInternalFinally(
			statement,
			path,
			updatable,
			null,
			options);
	}

	/**
	 * 対象となるテーブルの、全レコードに対して UPDATE をバッチ実行します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param path 対象となるテーブル
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param options UPDATE 文を調整する {@link SQLDecorator}
	 */
	public void update(
		BatchStatement statement,
		TablePath path,
		Updatable updatable,
		SQLDecorator... options) {
		updateInternalFinally(
			new BatchStatementFacade(statement),
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
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param path 対象となるテーブル
	 * @param criteria WHERE 句となる条件
	 */
	public void delete(
		BatchStatement statement,
		TablePath path,
		Criteria criteria) {
		deleteInternal(
			new BatchStatementFacade(statement),
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
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param path 対象となるテーブル
	 */
	public void delete(BatchStatement statement, TablePath path) {
		deleteInternalFinally(new BatchStatementFacade(statement), path, null);
	}

	/**
	 * このインスタンスの SELECT 文のキャッシュ容量を変更します。
	 * @param capacity 新しいキャッシュ容量
	 */
	public void ensureCacheCapacity(int capacity) {
		synchronized (selectorCache) {
			selectorCache.ensureCapacity(capacity);
		}
	}

	/**
	 * このインスタンスのキャッシュを空にします。
	 */
	public void clearCache() {
		synchronized (selectorCache) {
			selectorCache.clear();
		}
	}

	/**
	 * {@link Selector} を取得します。
	 * @param optimizer SELECT 句
	 * @param criteria WHERE 句
	 * @param order ORDER BY 句
	 * @param options オプション
	 * @return {@link Selector}
	 */
	public Selector getSelector(
		Optimizer optimizer,
		Criteria criteria,
		OrderByClause order,
		SQLDecorator... options) {
		if (optimizer == null) throw new NullPointerException("optimizer は必須です");

		Selector selector;
		if (useCache) {
			synchronized (selectorCache) {
				selector = selectorCache.get(optimizer);
				if (selector == null) {
					selector = new Selector(optimizer);
					selectorCache.cache(optimizer, selector);
				}
			}
		} else {
			selector = new Selector(optimizer);
		}

		if (criteria != null) selector.setCriteria(criteria);
		if (order != null) selector.setOrder(order);

		if (options != null) selector.addDecorator(options);

		return selector;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	/**
	 * クラス内に保持する {@link ThreadLocal} の値をクリアします。
	 */
	public static void removeThreadLocal() {
		threadStatement.remove();
	}

	static StatementFacade getThreadStatement() {
		StatementFacade statement = threadStatement.get();
		if (statement == null) return new PreparedStatementFacade();
		return statement;
	}

	static int deleteInternal(TablePath path, Criteria criteria) {
		return deleteInternal(getThreadStatement(), path, criteria);
	}

	private DataObjectIterator select(
		Optimizer optimizer,
		Criteria criteria,
		OrderByClause order,
		SQLDecorator[] options,
		boolean readonly) {
		return select(getSelector(optimizer, criteria, order, options), readonly);
	}

	private DataObjectIterator select(
		Selector selector,
		boolean readonly) {
		return new DataObjectIterator(
			factory.getInstance(selector.getTablePath()),
			selector.select(),
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
			Selector.select(sql, complementer, selectColumns, converter),
			false);
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
			if (!iterator.hasNext()) throw new DataObjectNotFoundException("検索結果が 0 件です");
			dataObject = iterator.next();
			if (iterator.hasNext()) throw new IllegalStateException("検索結果が 1 件以上あります");
		} finally {
			iterator.close();
		}

		return dataObject;
	}

	private static void checkArgument(Optimizer optimizer, PrimaryKey primaryKey) {
		if (!optimizer.getTablePath().equals(primaryKey.getTablePath()))
			throw new IllegalArgumentException("optimizer と primaryKey のテーブルが違います");
	}

	private void adjustArgument(
		Optimizer optimizer,
		Criteria criteria,
		OrderByClause order) {
		if (criteria != null) criteria.prepareColumns(factory.getInstance(optimizer.getTablePath()));
		if (order != null) order.prepareColumns(factory.getInstance(optimizer.getTablePath()));
	}

	private static void insertInternal(
		StatementFacade statement,
		TablePath path,
		Updatable updatable,
		SQLDecorator[] options) {
		InsertDMLBuilder builder = new InsertDMLBuilder(path);

		for (SQLDecorator option : options) {
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
		final Map<String, Bindable> map = new HashMap<>();
		InsertDMLBuilder builder = new InsertDMLBuilder(path) {

			@Override
			public void add(String columnName, Bindable bindable) {
				super.add(columnName, bindable);
				//このメソッドで追加された値のみ、depends として使える
				map.put(columnName, bindable);
			}
		};

		for (SQLDecorator option : options) {
			builder.addDecorator(option);
		}

		builder.add(updatable);

		String[] depends = sequencer.getDependsColumnNames();
		Criteria criteria = new CriteriaFactory(id).create();
		Relationship relationship = factory.getInstance(path);
		for (String columnName : depends) {
			criteria.and(new CriteriaFactory(id).create(relationship.getColumn(columnName), map.get(columnName)));
		}

		String targetColumnName = sequencer.getTargetColumnName();

		if (retry <= 0) retry = 3;
		for (int i = 0; i < retry; i++) {
			try {
				Bindable bindable = sequencer.next(criteria);
				builder.add(targetColumnName, bindable);
				statement.process(builder);
				statement.execute();
				return bindable;
			} catch (UniqueConstraintViolationException e) {}
		}

		throw new IllegalStateException("retry 回数を超えてしまいました");
	}

	private static int updateInternal(
		StatementFacade statement,
		TablePath path,
		Updatable updatable,
		Criteria criteria,
		SQLDecorator... options) {
		if (!criteria.isAvailable()) throw new IllegalArgumentException("条件がありません");
		return updateInternalFinally(statement, path, updatable, criteria, options);
	}

	private static int updateInternalFinally(
		StatementFacade statement,
		TablePath path,
		Updatable updatable,
		Criteria criteria,
		SQLDecorator... options) {
		UpdateDMLBuilder builder = new UpdateDMLBuilder(path);

		for (SQLDecorator option : options) {
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
		if (!criteria.isAvailable()) throw new IllegalArgumentException("条件がありません");
		return deleteInternalFinally(statement, path, criteria);
	}

	private static int deleteInternalFinally(
		StatementFacade statement,
		TablePath path,
		Criteria criteria) {
		DeleteDMLBuilder builder = new DeleteDMLBuilder(path);
		if (criteria != null) builder.setCriteria(criteria);
		statement.process(builder);
		return statement.execute();
	}

	static abstract class StatementFacade {

		abstract void process(ComposedSQL sql);

		abstract int execute();
	}

	static class BatchStatementFacade extends StatementFacade {

		static final int DUMMY_RESULT = -1;

		private BatchStatement statement;

		BatchStatementFacade(BatchStatement statement) {
			this.statement = statement;
		}

		@Override
		void process(ComposedSQL sql) {
			statement.addBatch(sql);
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
}
