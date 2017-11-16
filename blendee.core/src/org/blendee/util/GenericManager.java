package org.blendee.util;

import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObject;
import org.blendee.orm.DataObjectIterator;
import org.blendee.orm.QueryOption;
import org.blendee.orm.RowLockOption;
import org.blendee.selector.Optimizer;
import org.blendee.selector.SimpleOptimizer;
import org.blendee.sql.Condition;
import org.blendee.sql.OrderByClause;
import org.blendee.support.RowIterator;
import org.blendee.support.RowManager;

/**
 * {@link RowManager} の汎用実装クラスです。
 */
public class GenericManager extends java.lang.Object implements RowManager<GenericRow> {

	private final TablePath tablePath;

	/**
	 * @param tablePath 対象となるテーブル
	 */
	public GenericManager(TablePath tablePath) {
		this.tablePath = tablePath;
	}

	/**
	 * パラメータの条件にマッチするレコードを検索し、 {@link GenericRowIterator} として返します。<br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。
	 * @param condition WHERE 句となる条件
	 * @param order  ORDER 句
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @return {@link RowIterator}
	 */
	public GenericRowIterator select(
		Condition condition,
		OrderByClause order,
		QueryOption... options) {
		return select(
			new SimpleOptimizer(getTablePath()),
			condition,
			order,
			options);
	}

	/**
	 * パラメータの条件にマッチするレコードを検索し、 {@link GenericRowIterator} として返します。<br>
	 * {@link Optimizer} には {@link SimpleOptimizer} が使用されます。<br>
	 * {@link RowLockOption} には {@link RowLockOption#NONE} が使用されます。
	 * @param condition WHERE 句となる条件
	 * @param order  ORDER 句
	 * @return {@link RowIterator}
	 */
	public GenericRowIterator select(
		Condition condition,
		OrderByClause order) {
		return select(
			condition,
			order,
			null,
			RowLockOption.NONE);
	}

	/**
	 * パラメータの条件にマッチするレコードを検索し、 {@link GenericRowIterator} として返します。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param condition WHERE 句となる条件
	 * @param order  ORDER 句
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @return {@link RowIterator}
	 */
	public GenericRowIterator select(
		Optimizer optimizer,
		Condition condition,
		OrderByClause order,
		QueryOption... options) {
		return new GenericRowIterator(
			new DataAccessHelper().getDataObjects(
				optimizer,
				condition,
				order,
				options));
	}

	/**
	 * パラメータの条件にマッチするレコードを検索し、 {@link GenericRowIterator} として返します。<br>
	 * {@link RowLockOption} には {@link RowLockOption#NONE} が使用されます。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param condition WHERE 句となる条件
	 * @param order  ORDER 句
	 * @return {@link RowIterator}
	 */
	public GenericRowIterator select(
		Optimizer optimizer,
		Condition condition,
		OrderByClause order) {
		return select(
			optimizer,
			condition,
			order,
			null,
			RowLockOption.NONE);
	}

	@Override
	public GenericRow createRow(DataObject data) {
		return new GenericRow(data);
	}

	@Override
	public TablePath getTablePath() {
		return tablePath;
	}

	/**
	 * {@link GenericManager} が使用する Iterator クラスです。
	 */
	public class GenericRowIterator extends RowIterator<GenericRow> {

		/**
		 * 唯一のコンストラクタです。
		 * @param iterator 
		 */
		private GenericRowIterator(
			DataObjectIterator iterator) {
			super(iterator);
		}

		@Override
		public GenericRow next() {
			return createRow(nextDataObject());
		}
	}
}