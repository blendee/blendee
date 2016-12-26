package jp.ats.blendee.orm;

import java.util.HashMap;
import java.util.Map;

import jp.ats.blendee.internal.LRUCache;
import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.BConnection;
import jp.ats.blendee.jdbc.BlendeeContext;
import jp.ats.blendee.jdbc.BResultSet;
import jp.ats.blendee.jdbc.BStatement;
import jp.ats.blendee.jdbc.BatchStatement;
import jp.ats.blendee.jdbc.BlendeeManager;
import jp.ats.blendee.jdbc.DuplicateKeyException;
import jp.ats.blendee.jdbc.PreparedStatementComplementer;
import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.selector.Optimizer;
import jp.ats.blendee.selector.Selector;
import jp.ats.blendee.sql.Bindable;
import jp.ats.blendee.sql.Condition;
import jp.ats.blendee.sql.ConditionFactory;
import jp.ats.blendee.sql.DeleteDMLBuilder;
import jp.ats.blendee.sql.FromClause;
import jp.ats.blendee.sql.InsertDMLBuilder;
import jp.ats.blendee.sql.OrderByClause;
import jp.ats.blendee.sql.QueryBuilder;
import jp.ats.blendee.sql.Relationship;
import jp.ats.blendee.sql.RelationshipFactory;
import jp.ats.blendee.sql.SQLAdjuster;
import jp.ats.blendee.sql.SelectClause;
import jp.ats.blendee.sql.Updatable;
import jp.ats.blendee.sql.UpdateDMLBuilder;

/**
 * データベースに対する CRUD 処理を簡易に行うためのユーティリティクラスです。
 * <br>
 * このクラスのインスタンスで検索を行うと、使用した SQL 文をパラメータで渡す {@link Optimizer} をキーとしてキャッシュします。
 *
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

	private final BlendeeManager manager = BlendeeContext.get(BlendeeManager.class);

	private final RelationshipFactory factory = BlendeeContext.get(RelationshipFactory.class);

	private final LRUCache<Optimizer, Selector> selectorCache = LRUCache.newInstance(50);

	private final boolean useCache;

	/**
	 * インスタンスを生成します。
	 */
	public DataAccessHelper() {
		this(true);
	}

	/**
	 * インスタンスを生成します。
	 *
	 * @param useCache select 文をキャッシュするかどうか
	 */
	public DataAccessHelper(boolean useCache) {
		this.useCache = useCache;
	}

	/**
	 * パラメータの主キーの値を持つ {@link DataObject} を検索し返します。
	 *
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
		QueryOption... options) {
		checkArgument(optimizer, primaryKey);
		DataObjectIterator iterator = select(
			optimizer,
			primaryKey.getCondition(),
			null,
			options,
			false);
		return getFirst(iterator);
	}

	/**
	 * パラメータの条件にマッチする {@link DataObject} を検索し返します。
	 *
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param condition WHERE 句となる条件
	 * @param order ORDER 句
	 * @param options 検索オプション
	 * @return 条件にマッチする {@link DataObject} を持つ {@link DataObjectIterator}
	 */
	public DataObjectIterator getDataObjects(
		Optimizer optimizer,
		Condition condition,
		OrderByClause order,
		QueryOption... options) {
		adjustArgument(optimizer, condition, order);
		return select(optimizer, condition, order, options, false);
	}

	/**
	 * パラメータの主キーの値を持つ {@link DataObject} を検索する SQL 文をあらかじめこのインスタンスにキャッシュしておきます。
	 *
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param primaryKey 主キー
	 * @param options 検索オプション
	 */
	public void study(
		Optimizer optimizer,
		PrimaryKey primaryKey,
		QueryOption... options) {
		if (!useCache) throw new UnsupportedOperationException("キャッシュが使用できないと、この操作はできません");

		checkArgument(optimizer, primaryKey);
		prepareSelector(optimizer, primaryKey.getCondition(), null, options);
	}

	/**
	 * パラメータの条件にマッチする {@link DataObject} を検索する SQL 文をあらかじめこのインスタンスにキャッシュしておきます。
	 *
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param condition WHERE 句となる条件
	 * @param order ORDER 句
	 * @param options 検索オプション
	 */
	public void study(
		Optimizer optimizer,
		Condition condition,
		OrderByClause order,
		QueryOption... options) {
		if (!useCache) throw new UnsupportedOperationException("キャッシュが使用できないと、この操作はできません");

		adjustArgument(optimizer, condition, order);
		prepareSelector(optimizer, condition, order, options);
	}

	/**
	 * パラメータの {@link Optimizer} を使用して前回行われた検索を再実行します。
	 *
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
	 *
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
	 *
	 * @param locator 対象となるテーブル
	 * @param condition WHERE 句となる条件
	 * @return パラメータの条件にマッチする件数
	 */
	public int count(ResourceLocator locator, Condition condition) {
		QueryBuilder builder = new QueryBuilder(new FromClause(locator));
		builder.setSelectClause(SelectClause.COUNT_CLAUSE);
		if (condition != null) builder.setWhereClause(condition);
		BConnection connection = manager.getConnection();
		try (BStatement statement = connection.getStatement(builder.toString(), builder)) {
			try (BResultSet result = statement.executeQuery()) {
				result.next();
				return result.getInt(1);
			}
		}
	}

	/**
	 * このスレッドが {@link ThreadBatchCallback} 内で行う更新処理を全てバッチ実行します。
	 *
	 * @param batchStatement バッチ実行を依頼する {@link BatchStatement}
	 * @param callback 更新処理を定義したコールバック
	 * @throws Exception コールバック内で例外が発生した場合
	 */
	public static void startThreadBatch(
		BatchStatement batchStatement,
		ThreadBatchCallback callback) throws Exception {
		threadStatement.set(new BatchStatementFacade(batchStatement));
		try {
			callback.execute();
		} finally {
			threadStatement.set(null);
		}
	}

	/**
	 * パラメータのテーブルに対して INSERT を行います。
	 *
	 * @param locator 対象となるテーブル
	 * @param updatable INSERT する値を持つ {@link Updatable}
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 */
	public void insert(
		ResourceLocator locator,
		Updatable updatable,
		SQLAdjuster adjuster) {
		insertInternal(getThreadStatement(), locator, updatable, adjuster);
	}

	/**
	 * パラメータのテーブルに対して INSERT をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param locator 対象となるテーブル
	 * @param updatable INSERT する値を持つ {@link Updatable}
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 */
	public void insert(
		BatchStatement statement,
		ResourceLocator locator,
		Updatable updatable,
		SQLAdjuster adjuster) {
		insertInternal(new BatchStatementFacade(statement), locator, updatable, adjuster);
	}

	/**
	 * 連続値を持つテーブルに対して INSERT を行います。
	 *
	 * @param locator 対象となるテーブル
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param updatable INSERT する値を持つ {@link Updatable}
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 * @return INSERT された実際の連続値
	 */
	public Bindable insert(
		ResourceLocator locator,
		SequenceGenerator generator,
		Updatable updatable,
		int retry,
		SQLAdjuster adjuster) {
		return insertInternal(
			getThreadStatement(),
			locator,
			generator,
			updatable,
			retry,
			adjuster);
	}

	/**
	 * 連続値を持つテーブルに対して INSERT をバッチ実行します。
	 * <p>
	 * バッチ実行では、実際に値が登録されるのは後になるので、そのことを考慮した {@link SequenceGenerator} を用意する必要があります。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param locator 対象となるテーブル
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param updatable INSERT する値を持つ {@link Updatable}
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 * @return INSERT された実際の連続値
	 */
	public Bindable insert(
		BatchStatement statement,
		ResourceLocator locator,
		SequenceGenerator generator,
		Updatable updatable,
		int retry,
		SQLAdjuster adjuster) {
		return insertInternal(
			new BatchStatementFacade(statement),
			locator,
			generator,
			updatable,
			retry,
			adjuster);
	}

	/**
	 * 対象となるテーブルの、条件に該当するレコードに対して UPDATE を実行します。
	 *
	 * @param locator 対象となるテーブル
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param condition WHERE 句となる条件
	 * @param adjuster UPDATE 文を調整する {@link SQLAdjuster}
	 * @return 更新件数
	 */
	public int update(
		ResourceLocator locator,
		Updatable updatable,
		Condition condition,
		SQLAdjuster adjuster) {
		return updateInternal(
			getThreadStatement(),
			locator,
			updatable,
			condition,
			adjuster);
	}

	/**
	 * 対象となるテーブルの、条件に該当するレコードに対して UPDATE をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param locator 対象となるテーブル
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param condition WHERE 句となる条件
	 * @param adjuster UPDATE 文を調整する {@link SQLAdjuster}
	 */
	public void update(
		BatchStatement statement,
		ResourceLocator locator,
		Updatable updatable,
		Condition condition,
		SQLAdjuster adjuster) {
		updateInternal(
			new BatchStatementFacade(statement),
			locator,
			updatable,
			condition,
			adjuster);
	}

	/**
	 * 対象となるテーブルの、全レコードに対して UPDATE を実行します。
	 *
	 * @param locator 対象となるテーブル
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param adjuster UPDATE 文を調整する {@link SQLAdjuster}
	 * @return 更新件数
	 */
	public int update(
		ResourceLocator locator,
		Updatable updatable,
		SQLAdjuster adjuster) {
		StatementFacade statement = getThreadStatement();
		return updateInternalFinally(
			statement,
			locator,
			updatable,
			null,
			adjuster);
	}

	/**
	 * 対象となるテーブルの、全レコードに対して UPDATE をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param locator 対象となるテーブル
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param adjuster UPDATE 文を調整する {@link SQLAdjuster}
	 */
	public void update(
		BatchStatement statement,
		ResourceLocator locator,
		Updatable updatable,
		SQLAdjuster adjuster) {
		updateInternalFinally(
			new BatchStatementFacade(statement),
			locator,
			updatable,
			null,
			adjuster);
	}

	/**
	 * 対象となるテーブルに対して、条件に該当するレコードに対する DELETE を実行します。
	 *
	 * @param locator 対象となるテーブル
	 * @param condition WHERE 句となる条件
	 * @return 削除件数
	 */
	public int delete(ResourceLocator locator, Condition condition) {
		return deleteInternal(getThreadStatement(), locator, condition);
	}

	/**
	 * 対象となるテーブルに対して、条件に該当するレコードに対する DELETE をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param locator 対象となるテーブル
	 * @param condition WHERE 句となる条件
	 */
	public void delete(
		BatchStatement statement,
		ResourceLocator locator,
		Condition condition) {
		deleteInternal(
			new BatchStatementFacade(statement),
			locator,
			condition);
	}

	/**
	 * 対象となるテーブルに対して、全レコードに対する DELETE を実行します。
	 *
	 * @param locator 対象となるテーブル
	 * @return 削除件数
	 */
	public int delete(ResourceLocator locator) {
		return deleteInternalFinally(getThreadStatement(), locator, null);
	}

	/**
	 * 対象となるテーブルに対して、全レコードに対する DELETE をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param locator 対象となるテーブル
	 */
	public void delete(BatchStatement statement, ResourceLocator locator) {
		deleteInternalFinally(new BatchStatementFacade(statement), locator, null);
	}

	/**
	 * このインスタンスの SELECT 文のキャッシュ容量を変更します。
	 *
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

	@Override
	public String toString() {
		return U.toString(this);
	}

	static StatementFacade getThreadStatement() {
		StatementFacade statement = threadStatement.get();
		if (statement == null) return new PreparedStatementFacade();
		return statement;
	}

	static int deleteInternal(ResourceLocator locator, Condition condition) {
		return deleteInternal(getThreadStatement(), locator, condition);
	}

	private DataObjectIterator select(
		Optimizer optimizer,
		Condition condition,
		OrderByClause order,
		QueryOption[] options,
		boolean readonly) {
		return select(prepareSelector(optimizer, condition, order, options), readonly);
	}

	private Selector prepareSelector(
		Optimizer optimizer,
		Condition condition,
		OrderByClause order,
		QueryOption[] options) {
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

		if (condition != null) selector.setCondition(condition);
		if (order != null) selector.setOrder(order);

		for (QueryOption option : options) {
			if (option != null) option.process(selector);
		}

		return selector;
	}

	private DataObjectIterator select(
		Selector selector,
		boolean readonly) {
		return new DataObjectIterator(
			factory.getInstance(selector.getResourceLocator()),
			selector.select(),
			readonly);
	}

	private static void checkArgument(Optimizer optimizer, PrimaryKey primaryKey) {
		if (!optimizer.getResourceLocator().equals(primaryKey.getResourceLocator()))
			throw new IllegalArgumentException("optimizer と primaryKey のテーブルが違います");
	}

	private void adjustArgument(
		Optimizer optimizer,
		Condition condition,
		OrderByClause order) {
		if (condition != null) condition.adjustColumns(factory.getInstance(optimizer.getResourceLocator()));
		if (order != null) order.adjustColumns(factory.getInstance(optimizer.getResourceLocator()));
	}

	private static DataObject getFirst(
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

	private static void insertInternal(
		StatementFacade statement,
		ResourceLocator locator,
		Updatable updatable,
		SQLAdjuster adjuster) {
		InsertDMLBuilder builder = new InsertDMLBuilder(locator);
		if (adjuster != null) builder.setSQLAdjuster(adjuster);
		builder.add(updatable);
		statement.process(builder.toString(), builder);
		statement.execute();
	}

	private Bindable insertInternal(
		StatementFacade statement,
		ResourceLocator locator,
		SequenceGenerator sequencer,
		Updatable updatable,
		int retry,
		SQLAdjuster adjuster) {
		final Map<String, Bindable> map = new HashMap<>();
		InsertDMLBuilder builder = new InsertDMLBuilder(locator) {

			@Override
			public void add(String columnName, Bindable bindable) {
				super.add(columnName, bindable);
				//このメソッドで追加された値のみ、depends として使える
				map.put(columnName, bindable);
			}
		};

		if (adjuster != null) builder.setSQLAdjuster(adjuster);
		builder.add(updatable);

		String[] depends = sequencer.getDependsColumnNames();
		Condition condition = ConditionFactory.createCondition();
		Relationship relationship = factory.getInstance(locator);
		for (String columnName : depends) {
			condition.and(ConditionFactory.createCondition(relationship.getColumn(columnName), map.get(columnName)));
		}

		String targetColumnName = sequencer.getTargetColumnName();

		if (retry <= 0) retry = 3;
		for (int i = 0; i < retry; i++) {
			try {
				Bindable bindable = sequencer.next(condition);
				builder.add(targetColumnName, bindable);
				statement.process(builder.toString(), builder);
				statement.execute();
				return bindable;
			} catch (DuplicateKeyException e) {}
		}

		throw new IllegalStateException("retry 回数を超えてしまいました");
	}

	private static int updateInternal(
		StatementFacade statement,
		ResourceLocator locator,
		Updatable updatable,
		Condition condition,
		SQLAdjuster adjuster) {
		if (!condition.isAvailable()) throw new IllegalArgumentException("条件がありません");
		return updateInternalFinally(statement, locator, updatable, condition, adjuster);
	}

	private static int updateInternalFinally(
		StatementFacade statement,
		ResourceLocator locator,
		Updatable updatable,
		Condition condition,
		SQLAdjuster adjuster) {
		UpdateDMLBuilder builder = new UpdateDMLBuilder(locator);
		if (adjuster != null) builder.setSQLAdjuster(adjuster);
		builder.add(updatable);
		if (condition != null) builder.setCondition(condition);
		statement.process(builder.toString(), builder);
		return statement.execute();
	}

	private static int deleteInternal(
		StatementFacade statement,
		ResourceLocator locator,
		Condition condition) {
		if (!condition.isAvailable()) throw new IllegalArgumentException("条件がありません");
		return deleteInternalFinally(statement, locator, condition);
	}

	private static int deleteInternalFinally(
		StatementFacade statement,
		ResourceLocator locator,
		Condition condition) {
		DeleteDMLBuilder builder = new DeleteDMLBuilder(locator);
		if (condition != null) builder.setCondition(condition);
		statement.process(builder.toString(), builder);
		return statement.execute();
	}

	static abstract class StatementFacade {

		abstract void process(String sql, PreparedStatementComplementer complementer);

		abstract int execute();
	}

	static class BatchStatementFacade extends StatementFacade {

		static final int DUMMY_RESULT = -1;

		private BatchStatement statement;

		BatchStatementFacade(BatchStatement statement) {
			this.statement = statement;
		}

		@Override
		void process(String sql, PreparedStatementComplementer complementer) {
			statement.addBatch(sql, complementer);
		}

		@Override
		int execute() {
			return DUMMY_RESULT;
		}
	}

	private static class PreparedStatementFacade extends StatementFacade {

		private BStatement statement;

		@Override
		void process(String sql, PreparedStatementComplementer complementer) {
			statement = BlendeeContext.get(BlendeeManager.class).getConnection().getStatement(sql, complementer);
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
