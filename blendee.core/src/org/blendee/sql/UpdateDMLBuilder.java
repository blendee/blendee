package org.blendee.sql;

import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.TablePath;

/**
 * SQL の UPDATE 文を生成するクラスです。
 * @author 千葉 哲嗣
 */
public class UpdateDMLBuilder extends Updater {

	private final RelationshipFactory factory = ContextManager.get(RelationshipFactory.class);

	private Criteria criteria = CriteriaFactory.create();

	private final String alias;

	/**
	 * パラメータのテーブルを対象にするインスタンスを生成します。
	 * @param path UPDATE 対象テーブル
	 */
	public UpdateDMLBuilder(TablePath path) {
		super(path);
		alias = "";
	}

	/**
	 * パラメータのテーブルを対象にするインスタンスを生成します。
	 * @param path UPDATE 対象テーブル
	 */
	public UpdateDMLBuilder(RuntimeTablePath path) {
		super(path);
		alias = " " + path.getAlias();
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		done = super.complement(done, statement);
		return criteria.complement(done, statement);
	}

	/**
	 * {@link Searchable} から、コンストラクタで渡したテーブルをルートにした WHERE 句を設定します。
	 * @param searchable {@link Searchable}
	 */
	public void setSearchable(Searchable searchable) {
		setCriteria(searchable.getCriteria(factory.getInstance(getTablePath())));
	}

	/**
	 * WHERE 句を設定します。
	 * @param criteria WHERE 句
	 */
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria.replicate();
		this.criteria.prepareColumns(factory.getInstance(getTablePath()));
	}

	@Override
	protected String build() {
		String[] columnNames = getColumnNames();
		List<String> list = new LinkedList<>();
		for (int i = 0; i < columnNames.length; i++) {
			String columnName = columnNames[i];
			list.add(columnName + " = " + getPlaceHolderOrFragment(columnName));
		}

		criteria.setKeyword("WHERE");
		return "UPDATE " + getTablePath() + alias + " SET " + String.join(", ", list) + criteria.toString(false);
	}
}
