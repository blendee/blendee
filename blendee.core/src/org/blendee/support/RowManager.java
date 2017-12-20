package org.blendee.support;

import java.util.Optional;

import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObject;
import org.blendee.orm.DataObjectNotFoundException;
import org.blendee.orm.PrimaryKey;
import org.blendee.orm.RowLockOption;
import org.blendee.orm.SequenceGenerator;
import org.blendee.selector.Optimizer;
import org.blendee.selector.SimpleOptimizer;
import org.blendee.sql.Bindable;
import org.blendee.sql.Criteria;
import org.blendee.sql.SQLAdjuster;
import org.blendee.sql.Updatable;

/**
 * 自動生成される RowManager の共通の振る舞いを定義したインターフェイスです。
 * @author 千葉 哲嗣
 * @param <T> {@link Row}
 */
public interface RowManager<T extends Row> {

	/**
	 * 空の検索結果
	 */
	static final RowIterator<Row> EMPTY_ITERATOR = new RowIterator<Row>(
		DataAccessHelper.EMPTY_UPDATABLE_DATA_OBJECT_ITERATOR) {

		@Override
		public Row next() {
			throw new UnsupportedOperationException();
		}
	};

	/**
	 * {@link Row} を生成するメソッドです。
	 * @param data {@link Row} の全要素の値を持つ検索結果オブジェクト
	 * @return 生成された {@link Row}
	 */
	T createRow(DataObject data);

	/**
	 * サブクラスで固有の {@link TablePath} を返します。
	 * @return 固有の {@link TablePath}
	 */
	TablePath getTablePath();

	/**
	 * 空の {@link RowIterator} を返します。
	 * @param <T> {@link RowIterator} の要素型
	 * @return {@link RowIterator}
	 */
	@SuppressWarnings("unchecked")
	static <T extends Row> RowIterator<T> getEmptyRowIterator() {
		return (RowIterator<T>) EMPTY_ITERATOR;
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link Row} 存在しなければ null
	 */
	default Optional<T> select(QueryOptions options, String... primaryKeyMembers) {
		return select(new SimpleOptimizer(getTablePath()), options, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する数値
	 * @return {@link Row} 存在しなければ null
	 */
	default Optional<T> select(QueryOptions options, Number... primaryKeyMembers) {
		return select(new SimpleOptimizer(getTablePath()), options, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link Row} 存在しなければ null
	 */
	default Optional<T> select(String... primaryKeyMembers) {
		return select(QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 * @param primaryKeyMembers 主キーを構成する数値
	 * @return {@link Row} 存在しなければ null
	 */
	default Optional<T> select(Number... primaryKeyMembers) {
		return select(QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link Row} 存在しなければ null
	 */
	default Optional<T> select(QueryOptions options, Bindable... primaryKeyMembers) {
		return select(new SimpleOptimizer(getTablePath()), options, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link Row} 存在しなければ null
	 */
	default Optional<T> select(Bindable... primaryKeyMembers) {
		return select(QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link Row} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, QueryOptions options, String... primaryKeyMembers) {
		DataObject object;
		try {
			object = new DataAccessHelper().getDataObject(
				optimizer,
				PrimaryKey.getInstance(getTablePath(), primaryKeyMembers),
				QueryOptions.care(options).get());
		} catch (DataObjectNotFoundException e) {
			return Optional.empty();
		}

		return Optional.of(createRow(object));
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する数値
	 * @return {@link Row} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, QueryOptions options, Number... primaryKeyMembers) {
		DataObject object;
		try {
			object = new DataAccessHelper().getDataObject(
				optimizer,
				PrimaryKey.getInstance(getTablePath(), primaryKeyMembers),
				QueryOptions.care(options).get());
		} catch (DataObjectNotFoundException e) {
			return Optional.empty();
		}

		return Optional.of(createRow(object));
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link Row} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, String... primaryKeyMembers) {
		return select(optimizer, QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param primaryKeyMembers 主キーを構成する数値
	 * @return {@link Row} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, Number... primaryKeyMembers) {
		return select(optimizer, QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link Row} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, QueryOptions options, Bindable... primaryKeyMembers) {
		DataObject object;
		try {
			object = new DataAccessHelper().getDataObject(
				optimizer,
				new PrimaryKey(getTablePath(), primaryKeyMembers),
				QueryOptions.care(options).get());
		} catch (DataObjectNotFoundException e) {
			return Optional.empty();
		}
		return Optional.of(createRow(object));
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link Row} 存在しなければ null
	 */
	default Optional<T> select(Optimizer optimizer, Bindable... primaryKeyMembers) {
		return select(optimizer, QueryOptions.EMPTY_OPTIONS, primaryKeyMembers);
	}

	/**
	 * パラメータの条件にマッチする件数を返します。
	 * @param criteria WHERE 句となる条件
	 * @return パラメータの条件にマッチする件数
	 */
	default int count(Criteria criteria) {
		return new DataAccessHelper().count(getTablePath(), criteria);
	}

	/**
	 * パラメータの {@link Row} の INSERT を行います。
	 * @param row INSERT 対象
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 */
	default void insert(T row, SQLAdjuster adjuster) {
		new DataAccessHelper().insert(getTablePath(), row, adjuster);
	}

	/**
	 * パラメータの {@link Row} の INSERT をバッチ実行します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param row INSERT 対象
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 */
	default void insert(BatchStatement statement, T row, SQLAdjuster adjuster) {
		new DataAccessHelper().insert(statement, getTablePath(), row, adjuster);
	}

	/**
	 * パラメータの {@link Row} の INSERT を行います。
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param row INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @return INSERT された実際の連続値
	 */
	default Bindable insert(SequenceGenerator generator, T row, int retry) {
		return new DataAccessHelper().insert(getTablePath(), generator, row, retry, null);
	}

	/**
	 * パラメータの {@link Row} の INSERT をバッチ実行します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param row INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @return INSERT された実際の連続値
	 */
	default Bindable insert(BatchStatement statement, SequenceGenerator generator, T row, int retry) {
		return new DataAccessHelper().insert(statement, getTablePath(), generator, row, retry, null);
	}

	/**
	 * パラメータの {@link Row} の INSERT を行います。
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param row INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 * @return INSERT された実際の連続値
	 */
	default Bindable insert(SequenceGenerator generator, T row, int retry, SQLAdjuster adjuster) {
		return new DataAccessHelper().insert(getTablePath(), generator, row, retry, adjuster);
	}

	/**
	 * パラメータの {@link Row} の INSERT をバッチ実行します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param row INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @param adjuster INSERT 文を調整する {@link SQLAdjuster}
	 * @return INSERT された実際の連続値
	 */
	default Bindable insert(
		BatchStatement statement,
		SequenceGenerator generator,
		T row,
		int retry,
		SQLAdjuster adjuster) {
		return new DataAccessHelper().insert(statement, getTablePath(), generator, row, retry, adjuster);
	}

	/**
	 * パラメータの条件に該当する行を更新します。
	 * @param criteria WHERE 句となる条件
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @return 更新件数
	 */
	default int update(Criteria criteria, Updatable updatable) {
		return new DataAccessHelper().update(getTablePath(), updatable, criteria, null);
	}

	/**
	 * パラメータの条件に該当する行の更新をバッチ実行します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param criteria WHERE 句となる条件
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 */
	default void update(BatchStatement statement, Criteria criteria, Updatable updatable) {
		new DataAccessHelper().update(statement, getTablePath(), updatable, criteria, null);
	}

	/**
	 * パラメータの条件に該当する行を更新します。
	 * @param criteria WHERE 句となる条件
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param adjuster UPDATE 文を調整する {@link SQLAdjuster}
	 * @return 更新件数
	 */
	default int update(Criteria criteria, Updatable updatable, SQLAdjuster adjuster) {
		return new DataAccessHelper().update(getTablePath(), updatable, criteria, adjuster);
	}

	/**
	 * パラメータの条件に該当する行の更新をバッチ実行します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param criteria WHERE 句となる条件
	 * @param adjuster UPDATE 文を調整する {@link SQLAdjuster}
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 */
	default void update(BatchStatement statement, Criteria criteria, Updatable updatable, SQLAdjuster adjuster) {
		new DataAccessHelper().update(statement, getTablePath(), updatable, criteria, adjuster);
	}

	/**
	 * パラメータの条件に該当する行を削除します。
	 * @param criteria WHERE 句となる条件
	 * @return 削除件数
	 */
	default int delete(Criteria criteria) {
		return new DataAccessHelper().delete(getTablePath(), criteria);
	}

	/**
	 * パラメータの条件に該当する行の削除をバッチ実行します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param criteria WHERE 句となる条件
	 */
	default void delete(BatchStatement statement, Criteria criteria) {
		new DataAccessHelper().delete(statement, getTablePath(), criteria);
	}
}
