package org.blendee.selector;

import java.util.LinkedList;
import java.util.List;

import org.blendee.internal.U;
import org.blendee.jdbc.BlenStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.StatementSource;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Criteria;
import org.blendee.sql.FromClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.QueryBuilder;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.Effector;
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

	private final List<WindowFunctionContainer> windowFunctions = new LinkedList<>();

	private final Object lock = new Object();

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
	public void setCriteria(Criteria clause) {
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
	 * {@link Effector} を設定します。
	 * @param effectors {@link Effector}
	 */
	public void addEffector(Effector... effectors) {
		builder.addEffector(effectors);
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
	 * 検索を実行します。
	 * @return 検索結果
	 */
	public SelectedValuesIterator select() {
		SelectClause clause = getSelectClause();
		String sql = buildSQL(clause);

		BlenStatement statement = manager.getConnection().getStatement(sql, builder);

		return new SelectedValuesIterator(
			statement,
			statement.executeQuery(),
			clause.getColumns(),
			optimizer);
	}

	/**
	 * SQL とプレースホルダの値をセットします。
	 * @return {@link StatementSource}
	 */
	public StatementSource buildStatementSource() {
		return new StatementSource(buildSQL(getSelectClause()), builder);
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

	private String buildSQL(SelectClause clause) {
		String sql;
		synchronized (lock) {
			windowFunctions.forEach(container -> {
				clause.add(container.function, container.alias);
			});

			builder.setSelectClause(clause);

			sql = builder.toString();
		}

		return sql;
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
