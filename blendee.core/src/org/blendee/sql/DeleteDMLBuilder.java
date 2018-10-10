package org.blendee.sql;

import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.TablePath;

/**
 * SQL の DELETE 文を生成するクラスです。
 * @author 千葉 哲嗣
 */
public class DeleteDMLBuilder implements ComposedSQL {

	private final RelationshipFactory factory = ContextManager.get(RelationshipFactory.class);

	private final TablePath path;

	private final String alias;

	private final List<SQLDecorator> decorators = new LinkedList<>();

	private Criteria criteria = new CriteriaFactory(QueryIdFactory.getInstance()).create();

	/**
	 * パラメータのテーブルを対象にするインスタンスを生成します。
	 * @param path DELETE 対象テーブル
	 */
	public DeleteDMLBuilder(TablePath path) {
		this.path = path;
		alias = "";
	}

	/**
	 * パラメータのテーブルを対象にするインスタンスを生成します。
	 * @param path DELETE 対象テーブル
	 */
	public DeleteDMLBuilder(RuntimeTablePath path) {
		this.path = path;
		alias = " " + path.getAlias();
	}

	/**
	 * WHERE 句を設定します。
	 * @param criteria WHERE 句
	 */
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria.replicate();
		this.criteria.prepareColumns(factory.getInstance(path));
	}

	/**
	 * DML に対する微調整をするための {@link SQLDecorator} をセットします。
	 * @param decorators SQL 文を調整する {@link SQLDecorator}
	 */
	public void addDecorator(SQLDecorator... decorators) {
		for (SQLDecorator decorator : decorators) {
			this.decorators.add(decorator);
		}
	}

	@Override
	public String toString() {
		return sql();
	}

	@Override
	public String sql() {
		criteria.setKeyword("WHERE");
		String sql = "DELETE FROM " + path + alias + criteria.toString(false);

		for (SQLDecorator decorator : decorators) {
			sql = decorator.decorate(sql);
		}

		return sql;
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		return criteria.complement(done, statement);
	}
}
