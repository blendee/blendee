package org.blendee.selector;

import org.blendee.jdbc.TablePath;
import org.blendee.sql.Criteria;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.SelectClause;

/**
 * {@link Optimizer} を使用して SELECT 句を定義し、データベースの検索を行うクラスです。
 * @author 千葉 哲嗣
 */
public class SynchronizedSelector extends Selector {

	/**
	 * パラメータのテーブルをルートテーブルとしたインスタンスを生成します。<br>
	 * {@link Optimizer} は {@link SimpleOptimizer} が使用されます。
	 * @param path ルートテーブル
	 */
	public SynchronizedSelector(TablePath path) {
		super(path);
	}

	/**
	 * {@link Optimizer} を使用するインスタンスを生成します。
	 * @param optimizer 使用する {@link Optimizer}
	 */
	public SynchronizedSelector(Optimizer optimizer) {
		super(optimizer);
	}

	@Override
	public synchronized TablePath getTablePath() {
		return super.getTablePath();
	}

	@Override
	public synchronized void setCriteria(Criteria clause) {
		super.setCriteria(clause);
	}

	@Override
	public synchronized void setOrder(OrderByClause clause) {
		super.setOrder(clause);
	}

	@Override
	public synchronized void addDecorator(SQLDecorator... decorators) {
		super.addDecorator(decorators);
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
