package org.blendee.sql;

import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.TablePath;

/**
 * SQL の UPDATE 文を生成するクラスです。
 * @author 千葉 哲嗣
 */
public class UpdateDMLBuilder extends Updater {

	private final RelationshipFactory factory = RelationshipFactory.getInstance();

	private Criteria criteria = new CriteriaFactory(RuntimeIdFactory.getInstance()).create();

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
	 * @param id {@link RuntimeId}
	 */
	public UpdateDMLBuilder(TablePath path, RuntimeId id) {
		super(path);
		alias = " " + id.toAlias(factory.getInstance(path));
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		done = super.complement(done, statement);
		return criteria.complement(done, statement);
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
