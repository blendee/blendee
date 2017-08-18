package org.blendee.sql;

import org.blendee.jdbc.BPreparedStatement;
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

	private Condition condition = ConditionFactory.createCondition();

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
		setCondition(searchable.getCondition(factory.getInstance(path)));
	}

	/**
	 * WHERE 句を設定します。
	 * @param condition WHERE 句
	 */
	public void setCondition(Condition condition) {
		this.condition = condition.replicate();
		this.condition.adjustColumns(factory.getInstance(path));
	}

	@Override
	public String toString() {
		condition.setKeyword("WHERE");
		return "DELETE FROM " + path + condition.toString(false);
	}

	@Override
	public int complement(BPreparedStatement statement) {
		return condition.getComplementer().complement(statement);
	}
}
