package org.blendee.util;

import org.blendee.dialect.RowLockOption;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObject;
import org.blendee.orm.DataObjectIterator;
import org.blendee.selector.Optimizer;
import org.blendee.selector.SimpleOptimizer;
import org.blendee.sql.Criteria;
import org.blendee.sql.SQLDecorator;
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
	 * @param criteria WHERE 句となる条件
	 * @param order  ORDER 句
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @return {@link RowIterator}
	 */
	public GenericRowIterator select(
		Criteria criteria,
		OrderByClause order,
		SQLDecorator... options) {
		return select(
			new SimpleOptimizer(getTablePath()),
			criteria,
			order,
			options);
	}

	/**
	 * パラメータの条件にマッチするレコードを検索し、 {@link GenericRowIterator} として返します。
	 * @param optimizer SELECT 句を制御する {@link Optimizer}
	 * @param criteria WHERE 句となる条件
	 * @param order  ORDER 句
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @return {@link RowIterator}
	 */
	public GenericRowIterator select(
		Optimizer optimizer,
		Criteria criteria,
		OrderByClause order,
		SQLDecorator... options) {
		return new GenericRowIterator(
			new DataAccessHelper().getDataObjects(
				optimizer,
				criteria,
				order,
				options));
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
