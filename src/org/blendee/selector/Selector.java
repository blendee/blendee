package org.blendee.selector;

import org.blendee.internal.U;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Column;
import org.blendee.sql.Criteria;
import org.blendee.sql.FromClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.SQLQueryBuilder;
import org.blendee.sql.SelectClause;

/**
 * {@link Optimizer} を使用して SELECT 句を定義し、データベースの検索を行うクラスです。
 * @author 千葉 哲嗣
 */
public class Selector {

	/**
	 * 空の検索結果
	 */
	public static final SelectedValuesIterator EMPTY_SELECTED_VALUES_ITERATOR = new EmptySelectedValuesIterator();

	private final Relationship root;

	private final SQLQueryBuilder builder;

	private final Optimizer optimizer;

	/**
	 * パラメータのテーブルをルートテーブルとしたインスタンスを生成します。<br>
	 * {@link Optimizer} は {@link SimpleOptimizer} が使用されます。
	 * @param path ルートテーブル
	 * @param id {@link RuntimeId}
	 */
	public Selector(TablePath path, RuntimeId id) {
		this(new SimpleOptimizer(path, id));
	}

	/**
	 * {@link Optimizer} を使用するインスタンスを生成します。
	 * @param optimizer 使用する {@link Optimizer}
	 */
	public Selector(Optimizer optimizer) {
		TablePath path = optimizer.getTablePath();
		root = RelationshipFactory.getInstance().getInstance(path);
		builder = new SQLQueryBuilder(new FromClause(path, optimizer.getRuntimeId()));
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
	 * {@link SQLDecorator} を設定します。
	 * @param decorators {@link SQLDecorator}
	 */
	public void addDecorator(SQLDecorator... decorators) {
		builder.addDecorator(decorators);
	}

	/**
	 * このインスタンスをサブクエリとして使用するかどうかを指定します。<br>
	 * サブクエリで使用すると、すべてのカラムにテーブル ID が補完されます。
	 * @param forSubquery true の場合、サブクエリとして使用
	 */
	public void forSubquery(boolean forSubquery) {
		builder.forSubquery(forSubquery);
	}

	/**
	 * 検索を実行します。
	 * @return 検索結果
	 */
	public SelectedValuesIterator select() {
		SelectClause clause = getSelectClause();
		prepareBuilder(clause);

		return select(builder.sql(), builder, clause.getColumns(), optimizer);
	}

	/**
	 * SQL とプレースホルダの値をセットします。
	 * @return {@link ComposedSQL}
	 */
	public ComposedSQL composeSQL() {
		prepareBuilder(getSelectClause());
		return builder;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	/**
	 * 検索を実行します。
	 * @param sql SQL
	 * @param complementer {@link PreparedStatementComplementer}
	 * @param selectColumns SELECT 句で選択されたカラム
	 * @param converter {@link SelectedValuesConverter}
	 * @return 検索結果
	 */
	public static SelectedValuesIterator select(
		String sql,
		PreparedStatementComplementer complementer,
		Column[] selectColumns,
		SelectedValuesConverter converter) {
		BStatement statement = BlendeeManager.getConnection().getStatement(sql, complementer);
		return new SelectedValuesIterator(
			statement,
			statement.executeQuery(),
			selectColumns,
			converter);
	}

	/**
	 * このインスタンスが使用する SELECT 句を返します。
	 * @return このインスタンスが使用する SELECT 句
	 */
	public SelectClause getSelectClause() {
		return optimizer.getOptimizedSelectClause();
	}

	private void prepareBuilder(SelectClause clause) {
		builder.setSelectClause(clause);
	}
}
