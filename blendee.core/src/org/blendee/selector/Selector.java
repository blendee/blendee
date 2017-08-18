package org.blendee.selector;

import java.util.LinkedList;
import java.util.List;

import org.blendee.internal.U;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Condition;
import org.blendee.sql.FromClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.QueryBuilder;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.SQLAdjuster;
import org.blendee.sql.SelectClause;
import org.blendee.sql.WindowFunction;

/**
 * {@link Optimizer} を使用して SELECT 句を定義し、データベースの検索を行うクラスです。
 * @author 千葉 哲嗣
 */
public class Selector {

	/**
	 * 空の検索結果
	 */
	public static final SelectedValuesIterator EMPTY_SELECTED_VALUES_ITERATOR = new EmptySelectedValuesIterator();

	private final BlendeeManager manager = ContextManager.get(BlendeeManager.class);

	private final Relationship root;

	private final QueryBuilder builder;

	private final Optimizer optimizer;

	private final String myForUpdateClause;

	private final String myNowaitClause;

	private final List<WindowFunctionContainer> windowFunctions = new LinkedList<>();

	private final Object lock = new Object();

	private boolean forUpdate = false;

	private boolean nowait = false;

	/**
	 * パラメータのテーブルをルートテーブルとしたインスタンスを生成します。<br>
	 * {@link Optimizer} は {@link SimpleOptimizer} が使用されます。
	 * @param path ルートテーブル
	 */
	public Selector(TablePath path) {
		this(new SimpleOptimizer(path));
	}

	/**
	 * {@link Optimizer} を使用するインスタンスを生成します。
	 * @param optimizer 使用する {@link Optimizer}
	 */
	public Selector(Optimizer optimizer) {
		TablePath path = optimizer.getTablePath();
		root = ContextManager.get(RelationshipFactory.class).getInstance(path);
		builder = new QueryBuilder(new FromClause(path));
		this.optimizer = optimizer;

		SelectorConfigure config = ContextManager.get(SelectorConfigure.class);

		myForUpdateClause = config.getForUpdateClause();
		myNowaitClause = config.getNowaitClause();
	}

	/**
	 * このインスタンスが使用するルートテーブルを返します。
	 * @return ルートテーブル
	 */
	public TablePath getTablePath() {
		return root.getTablePath();
	}

	/**
	 * SELECT 文に WHERE 句を設定します。
	 * @param clause WHERE 句
	 */
	public void setCondition(Condition clause) {
		builder.setWhereClause(clause);
	}

	/**
	 * SELECT 文に ORDER BY 句を設定します。
	 * @param clause ORDER BY 句
	 */
	public void setOrder(OrderByClause clause) {
		builder.setOrderByClause(clause);
	}

	/**
	 * {@link SQLAdjuster} を設定します。
	 * @param adjuster {@link SQLAdjuster}
	 */
	public void setSQLAdjuster(SQLAdjuster adjuster) {
		builder.setAdjuster(adjuster);
	}

	/**
	 * SELECT 句にウィンドウ関数を追加します。
	 * @param function {@link WindowFunction}
	 * @param alias 別名
	 */
	public void addWindowFunction(WindowFunction function, String alias) {
		synchronized (lock) {
			windowFunctions.add(new WindowFunctionContainer(function, alias));
		}
	}

	/**
	 * このインスタンスが生成する SELECT 文に FOR UPDATE 句を付加します。
	 * @param forUpdate FOR UPDATE 句を使用する場合、 true
	 */
	public void forUpdate(boolean forUpdate) {
		synchronized (lock) {
			this.forUpdate = forUpdate;
		}
	}

	/**
	 * このインスタンスが FOR UPDATE 句を使用するかどうか検査します。
	 * @return FOR UPDATE 句を使用する場合、 true
	 */
	public boolean isForUpdate() {
		synchronized (lock) {
			return forUpdate;
		}
	}

	/**
	 * このインスタンスが生成する SELECT 文に NOWAIT 句を付加します。
	 * @param nowait NOWAIT 句を使用する場合、 true
	 */
	public void nowait(boolean nowait) {
		synchronized (lock) {
			this.nowait = nowait;
		}
	}

	/**
	 * このインスタンスが NOWAIT 句を使用するかどうか検査します。
	 * @return NOWAIT 句を使用する場合、 true
	 */
	public boolean isNowait() {
		synchronized (lock) {
			return nowait;
		}
	}

	/**
	 * 検索を実行します。
	 * @return 検索結果
	 */
	public SelectedValuesIterator select() {
		String sql;
		SelectClause clause = getSelectClause();
		synchronized (lock) {
			windowFunctions.forEach(container -> {
				clause.add(container.function, container.alias);
			});

			builder.setSelectClause(clause);

			sql = builder.toString();
			if (forUpdate) {
				sql += myForUpdateClause;
				if (nowait) sql += myNowaitClause;
			}
		}

		BStatement statement = manager.getConnection().getStatement(sql, builder);

		return new SelectedValuesIterator(
			statement,
			statement.executeQuery(),
			clause.getColumns(),
			optimizer);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	/**
	 * このインスタンスが使用する SELECT 句を返します。
	 * @return このインスタンスが使用する SELECT 句
	 */
	protected SelectClause getSelectClause() {
		return optimizer.getOptimizedSelectClause();
	}

	private static class WindowFunctionContainer {

		private final WindowFunction function;

		private final String alias;

		private WindowFunctionContainer(WindowFunction function, String alias) {
			this.function = function;
			this.alias = alias;
		}
	}
}
