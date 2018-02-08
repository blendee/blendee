package org.blendee.sql;

import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.jdbc.TablePath;

/**
 * SQL の DELETE 文を生成するクラスです。
 * @author 千葉 哲嗣
 */
public class DeleteDMLBuilder implements PreparedStatementComplementer {

	private final RelationshipFactory factory = ContextManager.get(RelationshipFactory.class);

	private final TablePath path;

	private Criteria criteria = CriteriaFactory.create();

	/**
	 * パラメータのテーブルを対象にするインスタンスを生成します。
	 * @param path DELETE 対象テーブル
	 */
	public DeleteDMLBuilder(TablePath path) {
		this.path = path;
	}

	/**
	 * {@link Searchable} から、コンストラクタで渡したテーブルをルートにした WHERE 句を設定します。
	 * @param searchable {@link Searchable}
	 */
	public void setSearchable(Searchable searchable) {
		setCriteria(searchable.getCriteria(factory.getInstance(path)));
	}

	/**
	 * WHERE 句を設定します。
	 * @param criteria WHERE 句
	 */
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria.replicate();
		this.criteria.adjustColumns(factory.getInstance(path));
	}

	@Override
	public String toString() {
		criteria.setKeyword("WHERE");
		return "DELETE FROM " + path + criteria.toString(false);
	}

	@Override
	public int complement(BlenPreparedStatement statement) {
		return criteria.getComplementer().complement(statement);
	}
}
