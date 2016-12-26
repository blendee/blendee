package jp.ats.blendee.sql;

import jp.ats.blendee.jdbc.BlendeeContext;
import jp.ats.blendee.jdbc.BPreparedStatement;
import jp.ats.blendee.jdbc.PreparedStatementComplementer;
import jp.ats.blendee.jdbc.ResourceLocator;

/**
 * SQL の DELETE 文を生成するクラスです。
 *
 * @author 千葉 哲嗣
 */
public class DeleteDMLBuilder implements PreparedStatementComplementer {

	private final RelationshipFactory factory = BlendeeContext.get(RelationshipFactory.class);

	private final ResourceLocator locator;

	private Condition condition = ConditionFactory.createCondition();

	/**
	 * パラメータのテーブルを対象にするインスタンスを生成します。
	 *
	 * @param locator DELETE 対象テーブル
	 */
	public DeleteDMLBuilder(ResourceLocator locator) {
		this.locator = locator;
	}

	/**
	 * {@link Searchable} から、コンストラクタで渡したテーブルをルートにした WHERE 句を設定します。
	 *
	 * @param searchable {@link Searchable}
	 */
	public void setSearchable(Searchable searchable) {
		setCondition(searchable.getCondition(factory.getInstance(locator)));
	}

	/**
	 * WHERE 句を設定します。
	 *
	 * @param condition WHERE 句
	 */
	public void setCondition(Condition condition) {
		this.condition = condition.replicate();
		this.condition.adjustColumns(factory.getInstance(locator));
	}

	@Override
	public String toString() {
		condition.setKeyword("WHERE");
		return "DELETE FROM " + locator + condition.toString(false);
	}

	@Override
	public int complement(BPreparedStatement statement) {
		return condition.getComplementer().complement(statement);
	}
}
