package jp.ats.blendee.sql;

import java.util.LinkedList;
import java.util.List;

import jp.ats.blendee.jdbc.BlendeeContext;
import jp.ats.blendee.jdbc.BPreparedStatement;
import jp.ats.blendee.jdbc.ResourceLocator;

/**
 * SQL の UPDATE 文を生成するクラスです。
 *
 * @author 千葉 哲嗣
 */
public class UpdateDMLBuilder extends Updater {

	private final RelationshipFactory factory = BlendeeContext.get(RelationshipFactory.class);

	private Condition condition = ConditionFactory.createCondition();

	/**
	 * パラメータのテーブルを対象にするインスタンスを生成します。
	 *
	 * @param locator UPDATE 対象テーブル
	 */
	public UpdateDMLBuilder(ResourceLocator locator) {
		super(locator);
	}

	@Override
	public int complement(BPreparedStatement statement) {
		int complemented = super.complement(statement);
		return complemented + condition.getComplementer(complemented).complement(statement);
	}

	/**
	 * {@link Searchable} から、コンストラクタで渡したテーブルをルートにした WHERE 句を設定します。
	 *
	 * @param searchable {@link Searchable}
	 */
	public void setSearchable(Searchable searchable) {
		setCondition(searchable.getCondition(factory.getInstance(getResourceLocator())));
	}

	/**
	 * WHERE 句を設定します。
	 *
	 * @param condition WHERE 句
	 */
	public void setCondition(Condition condition) {
		this.condition = condition.replicate();
		this.condition.adjustColumns(factory.getInstance(getResourceLocator()));
	}

	@Override
	protected String buildSQL() {
		String[] columnNames = getColumnNames();
		List<String> list = new LinkedList<>();
		for (int i = 0; i < columnNames.length; i++) {
			String columnName = columnNames[i];
			list.add(columnName + " = " + getPlaceHolderOrFragment(columnName));
		}
		condition.setKeyword("WHERE");
		return "UPDATE " + getResourceLocator() + " SET " + String.join(", ", list) + condition.toString(false);
	}
}
