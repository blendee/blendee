package jp.ats.blendee.support;

import java.util.Optional;

import jp.ats.blendee.jdbc.BatchStatement;
import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.orm.DataAccessHelper;
import jp.ats.blendee.orm.DataObjectNotFoundException;
import jp.ats.blendee.orm.PrimaryKey;
import jp.ats.blendee.orm.RowLockOption;
import jp.ats.blendee.orm.SequenceGenerator;
import jp.ats.blendee.orm.DataObject;
import jp.ats.blendee.selector.Optimizer;
import jp.ats.blendee.selector.SimpleOptimizer;
import jp.ats.blendee.sql.Bindable;
import jp.ats.blendee.sql.Condition;
import jp.ats.blendee.sql.SQLAdjuster;
import jp.ats.blendee.sql.Updatable;

/**
 * 自動生成される EntityManager の共通の振る舞いを定義したインターフェイスです。
 *
 * @author 千葉 哲嗣
 * @param <T> Entity
 */
public interface BEntityManager<T extends BEntity> {

	/**
	 * 空の検索結果
	 */
	public static final EntityIterator<BEntity> EMPTY_ETERATOR = new EntityIterator<BEntity>(
		DataAccessHelper.EMPTY_UPDATABLE_DATA_OBJECT_ITERATOR) {

		@Override
		public BEntity next() {
			throw new UnsupportedOperationException();
		}
	};

	/**
	 * Entity を生成するメソッドです。
	 *
	 * @param data {@link BEntity} の全要素の値を持つ検索結果オブジェクト
	 * @return 生成された {@link BEntity}
	 */
	T createEntity(DataObject data);

	/**
	 * サブクラスで固有の {@link ResourceLocator} を返します。
	 *
	 * @return 固有の {@link ResourceLocator}
	 */
	ResourceLocator getResourceLocator();

	/**
	 * この EntityManager で使用する {@link DataAccessHelper}
	 *
	 * @return {@link DataAccessHelper}
	 */
	DataAccessHelper getDataAccessHelper();

	/**
	 * 空の EntityIterator を返します。
	 *
	 * @param <T> {@link EntityIterator} の要素型
	 * @return {@link EntityIterator}
	 */
	@SuppressWarnings("unchecked")
	public static <T extends BEntity> EntityIterator<T> getEmptyEntityIterator() {
		return (EntityIterator<T>) EMPTY_ETERATOR;
	}

	/**
	 * パラメータの主キーの値を持つ {@link BEntity} を検索し返します。
	 * <br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 *
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link BEntity} 存在しなければ null
	 */
	default Optional<T> select(QueryOptions options, String... primaryKeyMembers) {
		return select(new SimpleOptimizer(getResourceLocator()), options, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link BEntity} を検索し返します。
	 * <br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 *
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する数値
	 * @return {@link BEntity} 存在しなければ null
	 */
	default Optional<T> select(QueryOptions options, Number... primaryKeyMembers) {
		return select(new SimpleOptimizer(getResourceLocator()), options, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link BEntity} を検索し返します。
	 * <br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 *
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link BEntity} 存在しなければ null
	 */
	default Optional<T> select(String... primaryKeyMembers) {
		return select(QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link BEntity} を検索し返します。
	 * <br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 *
	 * @param primaryKeyMembers 主キーを構成する数値
	 * @return {@link BEntity} 存在しなければ null
	 */
	default Optional<T> select(Number... primaryKeyMembers) {
		return select(QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link BEntity} を検索し返します。
	 * <br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 *
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link BEntity} 存在しなければ null
	 */
	default Optional<T> select(QueryOptions options, Bindable... primaryKeyMembers) {
		return select(new SimpleOptimizer(getResourceLocator()), options, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link BEntity} を検索し返します。
	 * <br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 *
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link BEntity} 存在しなければ null
	 */
	default Optional<T> select(Bindable... primaryKeyMembers) {
		return select(QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link BEntity} を検索し返します。
	 *
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link BEntity} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, QueryOptions options, String... primaryKeyMembers) {
		DataObject object;
		try {
			object = getDataAccessHelper().getDataObject(
				optimizer,
				PrimaryKey.getInstance(getResourceLocator(), primaryKeyMembers),
				QueryOptions.care(options).get());
		} catch (DataObjectNotFoundException e) {
			return Optional.empty();
		}

		return Optional.of(createEntity(object));
	}

	/**
	 * パラメータの主キーの値を持つ {@link BEntity} を検索し返します。
	 *
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する数値
	 * @return {@link BEntity} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, QueryOptions options, Number... primaryKeyMembers) {
		DataObject object;
		try {
			object = getDataAccessHelper().getDataObject(
				optimizer,
				PrimaryKey.getInstance(getResourceLocator(), primaryKeyMembers),
				QueryOptions.care(options).get());
		} catch (DataObjectNotFoundException e) {
			return Optional.empty();
		}

		return Optional.of(createEntity(object));
	}

	/**
	 * パラメータの主キーの値を持つ {@link BEntity} を検索し返します。
	 *
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link BEntity} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, String... primaryKeyMembers) {
		return select(optimizer, QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link BEntity} を検索し返します。
	 *
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param primaryKeyMembers 主キーを構成する数値
	 * @return {@link BEntity} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, Number... primaryKeyMembers) {
		return select(optimizer, QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link BEntity} を検索し返します。
	 *
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link BEntity} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, QueryOptions options, Bindable... primaryKeyMembers) {
		DataObject object;
		try {
			object = getDataAccessHelper().getDataObject(
				optimizer,
				new PrimaryKey(getResourceLocator(), primaryKeyMembers),
				QueryOptions.care(options).get());
		} catch (DataObjectNotFoundException e) {
			return Optional.empty();
		}
		return Optional.of(createEntity(object));
	}

	/**
	 * パラメータの主キーの値を持つ {@link BEntity} を検索し返します。
	 *
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link BEntity} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, Bindable... primaryKeyMembers) {
		return select(optimizer, QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの条件にマッチする件数を返します。
	 *
	 * @param condition WHERE 句となる条件
	 * @return パラメータの条件にマッチする件数
	 */
	default int count(Condition condition) {
		return getDataAccessHelper().count(getResourceLocator(), condition);
	}

	/**
	 * パラメータの Entity の INSERT を行います。
	 *
	 * @param entity INSERT 対象
	 */
	default void insert(T entity) {
		getDataAccessHelper().insert(getResourceLocator(), entity, null);
	}

	/**
	 * パラメータの Entity の INSERT をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param entity INSERT 対象
	 */
	default void insert(BatchStatement statement, T entity) {
		getDataAccessHelper().insert(statement, getResourceLocator(), entity, null);
	}

	/**
	 * パラメータの Entity の INSERT を行います。
	 *
	 * @param entity INSERT 対象
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 */
	default void insert(T entity, SQLAdjuster adjuster) {
		getDataAccessHelper().insert(getResourceLocator(), entity, adjuster);
	}

	/**
	 * パラメータの Entity の INSERT をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param entity INSERT 対象
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 */
	default void insert(BatchStatement statement, T entity, SQLAdjuster adjuster) {
		getDataAccessHelper().insert(statement, getResourceLocator(), entity, adjuster);
	}

	/**
	 * パラメータの Entity の INSERT を行います。
	 *
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param entity INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @return INSERT された実際の連続値
	 */
	default Bindable insert(SequenceGenerator generator, T entity, int retry) {
		return getDataAccessHelper().insert(getResourceLocator(), generator, entity, retry, null);
	}

	/**
	 * パラメータの Entity の INSERT をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param entity INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @return INSERT された実際の連続値
	 */
	default Bindable insert(BatchStatement statement, SequenceGenerator generator, T entity, int retry) {
		return getDataAccessHelper().insert(statement, getResourceLocator(), generator, entity, retry, null);
	}

	/**
	 * パラメータの Entity の INSERT を行います。
	 *
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param entity INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 * @return INSERT された実際の連続値
	 */
	default Bindable insert(SequenceGenerator generator, T entity, int retry, SQLAdjuster adjuster) {
		return getDataAccessHelper().insert(getResourceLocator(), generator, entity, retry, adjuster);
	}

	/**
	 * パラメータの Entity の INSERT をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param entity INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 * @return INSERT された実際の連続値
	 */
	default Bindable insert(
		BatchStatement statement,
		SequenceGenerator generator,
		T entity,
		int retry,
		SQLAdjuster adjuster) {
		return getDataAccessHelper().insert(statement, getResourceLocator(), generator, entity, retry, adjuster);
	}

	/**
	 * パラメータの Entity の DELETE を行います。
	 *
	 * @param entity DELETE 対象
	 * @return 削除が成功した場合、 true
	 */
	default boolean delete(T entity) {
		int result = getDataAccessHelper().delete(getResourceLocator(), entity.getPrimaryKey().getCondition());
		if (result > 1) throw new IllegalStateException("削除件数が複数件あります。");
		return result == 1;
	}

	/**
	 * パラメータの Entity の DELETE をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param entity DELETE 対象
	 */
	default void delete(BatchStatement statement, T entity) {
		getDataAccessHelper().delete(statement, getResourceLocator(), entity.getPrimaryKey().getCondition());
	}

	/**
	 * パラメータの条件に該当する行を更新します。
	 *
	 * @param condition WHERE 句となる条件
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @return 更新件数
	 */
	default int update(Condition condition, Updatable updatable) {
		return getDataAccessHelper().update(getResourceLocator(), updatable, condition, null);
	}

	/**
	 * パラメータの条件に該当する行の更新をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param condition WHERE 句となる条件
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 */
	default void update(BatchStatement statement, Condition condition, Updatable updatable) {
		getDataAccessHelper().update(statement, getResourceLocator(), updatable, condition, null);
	}

	/**
	 * パラメータの条件に該当する行を更新します。
	 *
	 * @param condition WHERE 句となる条件
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param adjuster UPDATE 文を調整する {@link SQLAdjuster}
	 * @return 更新件数
	 */
	default int update(Condition condition, Updatable updatable, SQLAdjuster adjuster) {
		return getDataAccessHelper().update(getResourceLocator(), updatable, condition, adjuster);
	}

	/**
	 * パラメータの条件に該当する行の更新をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param condition WHERE 句となる条件
	 * @param adjuster UPDATE 文を調整する {@link SQLAdjuster}
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 */
	default void update(BatchStatement statement, Condition condition, Updatable updatable, SQLAdjuster adjuster) {
		getDataAccessHelper().update(statement, getResourceLocator(), updatable, condition, adjuster);
	}

	/**
	 * パラメータの条件に該当する行を削除します。
	 *
	 * @param condition WHERE 句となる条件
	 * @return 削除件数
	 */
	default int delete(Condition condition) {
		return getDataAccessHelper().delete(getResourceLocator(), condition);
	}

	/**
	 * パラメータの条件に該当する行の削除をバッチ実行します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param condition WHERE 句となる条件
	 */
	default void delete(BatchStatement statement, Condition condition) {
		getDataAccessHelper().delete(statement, getResourceLocator(), condition);
	}
}
