package org.blendee.assist;

import java.util.Optional;

import org.blendee.dialect.RowLockOption;
import org.blendee.jdbc.Batch;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObject;
import org.blendee.orm.DataObjectNotFoundException;
import org.blendee.orm.PrimaryKey;
import org.blendee.orm.SelectContext;
import org.blendee.orm.SequenceGenerator;
import org.blendee.orm.SimpleSelectContext;
import org.blendee.sql.Bindable;
import org.blendee.sql.Criteria;
import org.blendee.sql.RuntimeIdFactory;
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
	 * {@link SelectContext} には {@link SimpleSelectContext} が使用されます。
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(Vargs<SQLDecorator> options, String... primaryKeyMembers) {
		return select(new SimpleSelectContext(getTablePath(), RuntimeIdFactory.stubInstance()), options, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link SelectContext} には {@link SimpleSelectContext} が使用されます。
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する数値
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(Vargs<SQLDecorator> options, Number... primaryKeyMembers) {
		return select(new SimpleSelectContext(getTablePath(), RuntimeIdFactory.stubInstance()), options, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link SelectContext} には {@link SimpleSelectContext} が使用されます。
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(String... primaryKeyMembers) {
		return select(Vargs.of(), primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link SelectContext} には {@link SimpleSelectContext} が使用されます。
	 * @param primaryKeyMembers 主キーを構成する数値
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(Number... primaryKeyMembers) {
		return select(Vargs.of(), primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link SelectContext} には {@link SimpleSelectContext} が使用されます。
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(Vargs<SQLDecorator> options, Bindable... primaryKeyMembers) {
		return select(new SimpleSelectContext(getTablePath(), RuntimeIdFactory.runtimeInstance()), options, primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。<br>
	 * {@link SelectContext} には {@link SimpleSelectContext} が使用されます。
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(Bindable... primaryKeyMembers) {
		return select(Vargs.of(), primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。
	 * @param optimizer SELECT 句を制御する {@link SelectContext}
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(SelectContext optimizer, Vargs<SQLDecorator> options, String... primaryKeyMembers) {
		DataObject object;
		try {
			object = new DataAccessHelper(optimizer.runtimeId()).getDataObject(
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
	 * @param optimizer SELECT 句を制御する {@link SelectContext}
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する数値
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(SelectContext optimizer, Vargs<SQLDecorator> options, Number... primaryKeyMembers) {
		DataObject object;
		try {
			object = new DataAccessHelper(optimizer.runtimeId()).getDataObject(
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
	 * @param optimizer SELECT 句を制御する {@link SelectContext}
	 * @param primaryKeyMembers 主キーを構成する文字列
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(SelectContext optimizer, String... primaryKeyMembers) {
		return select(optimizer, Vargs.of(), primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。
	 * @param optimizer SELECT 句を制御する {@link SelectContext}
	 * @param primaryKeyMembers 主キーを構成する数値
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(SelectContext optimizer, Number... primaryKeyMembers) {
		return select(optimizer, Vargs.of(), primaryKeyMembers);
	}

	/**
	 * パラメータの主キーの値を持つ {@link Row} を検索し返します。
	 * @param optimizer SELECT 句を制御する {@link SelectContext}
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(SelectContext optimizer, Vargs<SQLDecorator> options, Bindable... primaryKeyMembers) {
		DataObject object;
		try {
			object = new DataAccessHelper(optimizer.runtimeId()).getDataObject(
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
	 * @param optimizer SELECT 句を制御する {@link SelectContext}
	 * @param primaryKeyMembers 主キーを構成する値
	 * @return {@link Row} 存在しなければ null
	 */
	public Optional<T> select(SelectContext optimizer, Bindable... primaryKeyMembers) {
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
	 * @param batch バッチ実行を依頼する {@link Batch}
	 * @param row INSERT 対象
	 * @param options INSERT 文を調整する {@link SQLDecorator}
	 */
	public void insert(Batch batch, T row, SQLDecorator... options) {
		new DataAccessHelper().insert(batch, getTablePath(), row, options);
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
	 * @param batch バッチ実行を依頼する {@link Batch}
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param row INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @return INSERT された実際の連続値
	 */
	public Bindable insert(Batch batch, SequenceGenerator generator, T row, int retry) {
		return new DataAccessHelper().insert(batch, getTablePath(), generator, row, retry);
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
	 * @param batch バッチ実行を依頼する {@link Batch}
	 * @param generator 対象となる項目と値を持つ {@link SequenceGenerator}
	 * @param row INSERT 対象
	 * @param retry {@link SequenceGenerator} のリトライ回数
	 * @param options INSERT 文を調整する {@link SQLDecorator}
	 * @return INSERT された実際の連続値
	 */
	public Bindable insert(
		Batch batch,
		SequenceGenerator generator,
		T row,
		int retry,
		SQLDecorator... options) {
		return new DataAccessHelper().insert(batch, getTablePath(), generator, row, retry, options);
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
	 * @param batch バッチ実行を依頼する {@link Batch}
	 * @param criteria WHERE 句となる条件
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 */
	public void update(Batch batch, Criteria criteria, Updatable updatable) {
		new DataAccessHelper().update(batch, getTablePath(), updatable, criteria);
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
	 * @param batch バッチ実行を依頼する {@link Batch}
	 * @param criteria WHERE 句となる条件
	 * @param updatable UPDATE する値を持つ {@link Updatable}
	 * @param options UPDATE 文を調整する {@link SQLDecorator}
	 */
	public void update(Batch batch, Criteria criteria, Updatable updatable, SQLDecorator... options) {
		new DataAccessHelper().update(batch, getTablePath(), updatable, criteria, options);
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
	 * @param batch バッチ実行を依頼する {@link Batch}
	 * @param criteria WHERE 句となる条件
	 */
	public void delete(Batch batch, Criteria criteria) {
		new DataAccessHelper().delete(batch, getTablePath(), criteria);
	}

	private T createRow(DataObject object) {
		return tableFacade.createRow(object);
	}

	private TablePath getTablePath() {
		return tableFacade.getTablePath();
	}
}
