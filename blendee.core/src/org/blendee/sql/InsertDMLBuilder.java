package org.blendee.sql;

import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.ResourceLocator;

/**
 * SQL の INSERT 文を生成するクラスです。
 *
 * @author 千葉 哲嗣
 */
public class InsertDMLBuilder extends Updater {

	/**
	 * パラメータのテーブルを対象にするインスタンスを生成します。
	 *
	 * @param locator INSERT 対象テーブル
	 */
	public InsertDMLBuilder(ResourceLocator locator) {
		super(locator);
	}

	@Override
	protected String buildSQL() {
		String[] columnNames = getColumnNames();
		List<String> columns = new LinkedList<>();
		List<String> placeHolders = new LinkedList<>();
		for (int i = 0; i < columnNames.length; i++) {
			String columnName = columnNames[i];
			columns.add(columnName);
			placeHolders.add(getPlaceHolderOrFragment(columnName));
		}
		return "INSERT INTO "
			+ getResourceLocator()
			+ " ("
			+ String.join(", ", columns)
			+ ")"
			+ " VALUES "
			+ "("
			+ String.join(", ", placeHolders)
			+ ")";
	}
}
