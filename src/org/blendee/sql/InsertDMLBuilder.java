package org.blendee.sql;

import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.TablePath;

/**
 * SQL の INSERT 文を生成するクラスです。
 * @author 千葉 哲嗣
 */
public class InsertDMLBuilder extends Updater {

	/**
	 * パラメータのテーブルを対象にするインスタンスを生成します。
	 * @param path INSERT 対象テーブル
	 */
	public InsertDMLBuilder(TablePath path) {
		super(path);
	}

	@Override
	protected String build() {
		String[] columnNames = getColumnNames();
		List<String> columns = new LinkedList<>();
		List<String> values = new LinkedList<>();
		for (int i = 0; i < columnNames.length; i++) {
			String columnName = columnNames[i];
			columns.add(columnName);
			values.add(getPlaceHolderOrFragment(columnName));
		}

		return "INSERT INTO "
			+ getTablePath()
			+ " ("
			+ String.join(", ", columns)
			+ ")"
			+ " VALUES "
			+ "("
			+ String.join(", ", values)
			+ ")";
	}
}
