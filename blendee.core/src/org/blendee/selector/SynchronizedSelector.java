package org.blendee.selector;

import org.blendee.jdbc.ResourceLocator;
import org.blendee.sql.Condition;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.SQLAdjuster;
import org.blendee.sql.SelectClause;

/**
 * {@link Optimizer} を使用して SELECT 句を定義し、データベースの検索を行うクラスです。
 *
 * @author 千葉 哲嗣
 */
public class SynchronizedSelector extends Selector {

	/**
	 * パラメータのテーブルをルートテーブルとしたインスタンスを生成します。
	 * <br>
	 * {@link Optimizer} は {@link SimpleOptimizer} が使用されます。
	 *
	 * @param locator ルートテーブル
	 */
	public SynchronizedSelector(ResourceLocator locator) {
		super(locator);
	}

	/**
	 * {@link Optimizer} を使用するインスタンスを生成します。
	 *
	 * @param optimizer 使用する {@link Optimizer}
	 */
	public SynchronizedSelector(Optimizer optimizer) {
		super(optimizer);
	}

	@Override
	public synchronized ResourceLocator getResourceLocator() {
		return super.getResourceLocator();
	}

	@Override
	public synchronized void setCondition(Condition clause) {
		super.setCondition(clause);
	}

	@Override
	public synchronized void setOrder(OrderByClause clause) {
		super.setOrder(clause);
	}

	@Override
	public synchronized void setSQLAdjuster(SQLAdjuster adjuster) {
		super.setSQLAdjuster(adjuster);
	}

	@Override
	public synchronized void forUpdate(boolean forUpdate) {
		super.forUpdate(forUpdate);
	}

	@Override
	public synchronized boolean isForUpdate() {
		return super.isForUpdate();
	}

	@Override
	public synchronized void nowait(boolean nowait) {
		super.nowait(nowait);
	}

	@Override
	public synchronized boolean isNowait() {
		return super.isNowait();
	}

	@Override
	public synchronized SelectedValuesIterator select() {
		return super.select();
	}

	@Override
	public synchronized String toString() {
		return super.toString();
	}

	@Override
	protected synchronized SelectClause getSelectClause() {
		return super.getSelectClause();
	}
}
