package org.blendee.selector;

import org.blendee.internal.U;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Column;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.SelectClause;

/**
 * 指定されたカラムで検索を行う {@link Optimizer} です。
 * @author 千葉 哲嗣
 */
public class SimpleOptimizer extends SimpleSelectedValuesConverter implements Optimizer {

	private final Relationship root;

	private final SelectClause select = new SelectClause();

	/**
	 * {@link Relationship} のルートとなるテーブルを元にインスタンスを生成します。<br>
	 * {@link #add(Column)} によるカラムの追加が行われない場合、 SELECT 句は path の全カラムが使用されます。
	 * @param path SELECT 句に使用するカラムを持つテーブル
	 */
	public SimpleOptimizer(TablePath path) {
		root = ContextManager.get(RelationshipFactory.class).getInstance(path);
	}

	@Override
	public TablePath getTablePath() {
		return root.getTablePath();
	}

	@Override
	public SelectClause getOptimizedSelectClause() {
		SelectClause result;
		synchronized (select) {
			result = select.replicate();
		}
		if (result.getColumnsSize() > 0) return result;
		result.add(root);
		return result;
	}

	/**
	 * SELECT 句を構成するカラムを追加します。
	 * @param column SELECT 句に含めるカラム
	 */
	public void add(Column column) {
		synchronized (select) {
			select.add(column);
		}
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
