package org.blendee.support;

import java.util.Optional;

import org.blendee.dialect.RowLockOption;
import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObject;
import org.blendee.orm.DataObjectNotFoundException;
import org.blendee.orm.PrimaryKey;
import org.blendee.orm.SequenceGenerator;
import org.blendee.selector.Optimizer;
import org.blendee.selector.SimpleOptimizer;
import org.blendee.sql.Bindable;
import org.blendee.sql.Criteria;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.Updatable;

/**
 * {@link TableFacade} を使用した各種操作を実装するクラスです。
 * @author 千葉 哲嗣
 * @param <T> {@link Row}
 */
public class TableFacadeManager<T extends Row> {

	private final TableFacade<T> tableFacade;

	@SuppressWarnings("javadoc")
	public TableFacadeManager(TableFacade<T> tableFacade) {
		this.tableFacade = tableFacade;
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(Vargs<SQLDecorator> options, String... primaryKeyMembers) {
		return select(new SimpleOptimizer(getTablePath()), options, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する数値
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(Vargs<SQLDecorator> options, Number... primaryKeyMembers) {
		return select(new SimpleOptimizer(getTablePath()), options, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(String... primaryKeyMembers) {
		return select(Vargs.of(), primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 * @param primaryKeyMembers 主キーを構成する数値
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(Number... primaryKeyMembers) {
		return select(Vargs.of(), primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(Vargs<SQLDecorator> options, Bindable... primaryKeyMembers) {
		return select(new SimpleOptimizer(getTablePath()), options, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(Bindable... primaryKeyMembers) {
		return select(Vargs.of(), primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(Optimizer optimizer, Vargs<SQLDecorator> options, String... primaryKeyMembers) {
		DataObject object;
		try {
			object = new DataAccessHelper().getDataObject(
				optimizer,
				PrimaryKey.getInstance(getTablePath(), primaryKeyMembers),
				options.get());
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
	public Optional<T> select(Optimizer optimizer, Vargs<SQLDecorator> options, Number... primaryKeyMembers) {
		DataObject object;
		try {
			object = new DataAccessHelper().getDataObject(
				optimizer,
				PrimaryKey.getInstance(getTablePath(), primaryKeyMembers),
				options.get());
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
	public Optional<T> select(Optimizer optimizer, String... primaryKeyMembers) {
		return select(optimizer, Vargs.of(), primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param primaryKeyMembers 主キーを構成する数値
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(Optimizer optimizer, Number... primaryKeyMembers) {
		return select(optimizer, Vargs.of(), primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(Optimizer optimizer, Vargs<SQLDecorator> options, Bindable... primaryKeyMembers) {
		DataObject object;
		try {
			object = new DataAccessHelper().getDataObject(
				optimizer,
				new PrimaryKey(getTablePath(), primaryKeyMembers),
				options.get());
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
	public Optional<T> select(Optimizer optimizer, Bindable... primaryKeyMembers) {
		return select(optimizer, Vargs.of(), primaryKeyMembers);
	}

	/**
	 * パラメータの条件にマッチする件数を返します。
	 * @param criteria WHERE 句となる条件
	 * @return パラメータの条件にマッチする件数
	 */
	public int count(Criteria criteria) {
		return new DataAccessHelper().count(getTablePath(), criteria);
	}

	/**
	 * パラメータの {@link Row} の INSERT を行います。
	 * @param row INSERT 対象
	 * @param options INSERT 文を調整する {@link SQLDecorator}
	 */
	public void insert(T row, SQLDecorator... options) {
		new DataAccessHelper().insert(getTablePath(), row, options);
	}

	/**
	 * パラメータの {@link Row} の INSERT をバッチ実行します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param row INSERT 対象
	 * @param options INSERT 文を調整する {@link SQLDecorator}
	 */
	public void insert(BatchStatement statement, T row, SQLDecorator... options) {
		new DataAccessHelper().insert(statement, getTablePath(), row, options);
	}

	/**
	 * パラメータの {@link Row} の INSERT を行います。
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param row INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @return INSERT された実際の連続値
	 */
	public Bindable insert(SequenceGenerator generator, T row, int retry) {
		return new DataAccessHelper().insert(getTablePath(), generator, row, retry);
	}

	/**
	 * パラメータの {@link Row} の INSERT をバッチ実行します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param row INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @return INSERT された実際の連続値
	 */
	public Bindable insert(BatchStatement statement, SequenceGenerator generator, T row, int retry) {
		return new DataAccessHelper().insert(statement, getTablePath(), generator, row, retry);
	}

	/**
	 * パラメータの {@link Row} の INSERT を行います。
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param row INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @param options INSERT 文を調整する {@link SQLDecorator}
	 * @return INSERT された実際の連続値
	 */
	public Bindable insert(SequenceGenerator generator, T row, int retry, SQLDecorator... options) {
		return new DataAccessHelper().insert(getTablePath(), generator, row, retry, options);
	}

	/**
	 * パラメータの {@link Row} の INSERT をバッチ実行します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param row INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @param options INSERT 文を調整する {@link SQLDecorator}
	 * @return INSERT された実際の連続値
	 */
	public Bindable insert(
		BatchStatement statement,
		SequenceGenerator generator,
		T row,
		int retry,
		SQLDecorator... options) {
		return new DataAccessHelper().insert(statement, getTablePath(), generator, row, retry, options);
	}

	/**
	 * パラメータの条件に該当する行を更新します。
	 * @param criteria WHERE 句となる条件
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @return 更新件数
	 */
	public int update(Criteria criteria, Updatable updatable) {
		return new DataAccessHelper().update(getTablePath(), updatable, criteria);
	}

	/**
	 * パラメータの条件に該当する行の更新をバッチ実行します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param criteria WHERE 句となる条件
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 */
	public void update(BatchStatement statement, Criteria criteria, Updatable updatable) {
		new DataAccessHelper().update(statement, getTablePath(), updatable, criteria);
	}

	/**
	 * パラメータの条件に該当する行を更新します。
	 * @param criteria WHERE 句となる条件
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param options UPDATE 文を調整する {@link SQLDecorator}
	 * @return 更新件数
	 */
	public int update(Criteria criteria, Updatable updatable, SQLDecorator... options) {
		return new DataAccessHelper().update(getTablePath(), updatable, criteria, options);
	}

	/**
	 * パラメータの条件に該当する行の更新をバッチ実行します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param criteria WHERE 句となる条件
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param options UPDATE 文を調整する {@link SQLDecorator}
	 */
	public void update(BatchStatement statement, Criteria criteria, Updatable updatable, SQLDecorator... options) {
		new DataAccessHelper().update(statement, getTablePath(), updatable, criteria, options);
	}

	/**
	 * パラメータの条件に該当する行を削除します。
	 * @param criteria WHERE 句となる条件
	 * @return 削除件数
	 */
	public int delete(Criteria criteria) {
		return new DataAccessHelper().delete(getTablePath(), criteria);
	}

	/**
	 * パラメータの条件に該当する行の削除をバッチ実行します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @param criteria WHERE 句となる条件
	 */
	public void delete(BatchStatement statement, Criteria criteria) {
		new DataAccessHelper().delete(statement, getTablePath(), criteria);
	}

	private T createRow(DataObject object) {
		return tableFacade.createRow(object);
	}

	private TablePath getTablePath() {
		return tableFacade.getTablePath();
	}
}
