package org.blendee.sql;

import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.Batch;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.TablePath;

/**
 * SQL の DELETE 文を生成するクラスです。
 * @author 千葉 哲嗣
 */
public class DeleteDMLBuilder implements ComposedSQL {

	private final RelationshipFactory factory = RelationshipFactory.getInstance();

	private final TablePath path;

	private final String alias;

	private final List<SQLDecorator> decorators = new LinkedList<>();

	private Criteria criteria = new CriteriaFactory(RuntimeIdFactory.stubInstance()).create();

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
	 * @param id {@link RuntimeId}
	 * @param forSubquery サブクエリを使用する
	 */
	public DeleteDMLBuilder(TablePath path, RuntimeId id, boolean forSubquery) {
		this.path = path;
		if (forSubquery) {
			alias = " " + id.toAlias(factory.getInstance(path));
		} else {
			alias = "";
		}
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
		for (var decorator : decorators) {
			this.decorators.add(decorator);
		}
	}

	/**
	 * データ更新を実行します。
	 * @return 対象件数
	 */
	public int executeUpdate() {
		var connection = BlendeeManager.getConnection();
		try (var statement = connection.getStatement(this)) {
			return statement.executeUpdate();
		}
	}

	/**
	 * データ更新をバッチ実行します。
	 * @param batch {@link Batch}
	 */
	public void executeUpdate(Batch batch) {
		batch.add(this);
	}

	@Override
	public String toString() {
		return sql();
	}

	@Override
	public String sql() {
		criteria.setKeyword("WHERE");
		var sql = "DELETE FROM " + path + alias + criteria.toString(false);

		for (var decorator : decorators) {
			sql = decorator.decorate(sql);
		}

		return sql;
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		return criteria.complement(done, statement);
	}
}
