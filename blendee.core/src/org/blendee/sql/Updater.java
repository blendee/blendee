package org.blendee.sql;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.TablePath;

/**
 * 登録更新用 SQL 文を生成するための抽象基底クラスです。
 * @author 千葉 哲嗣
 */
public abstract class Updater implements ComposedSQL {

	private final TablePath path;

	private final Set<String> columns = new LinkedHashSet<>();

	private final Map<String, Binder> values = new LinkedHashMap<>();

	private final Map<String, String> fragmentMap = new HashMap<>();

	private final List<Effector> adjusters = new LinkedList<>();

	/**
	 * パラメータの表すテーブルに対する更新を行うインスタンスを生成します。
	 * @param path 対象となるテーブル
	 */
	protected Updater(TablePath path) {
		this.path = path;
	}

	/**
	 * SQL 文に挿入する DB 格納可能な値を追加します。<br>
	 * 追加された順に SQL 文に反映されます。
	 * @param updatable DB格納可能な値
	 */
	public void add(Updatable updatable) {
		updatable.setValuesTo(this);
	}

	/**
	 * SQL 文に挿入する項目名とプレースホルダにバインドする値を追加します。<br>
	 * 追加された順に SQL 文に反映されます。
	 * @param columnName テーブルの項目名
	 * @param bindable バインド可能な値
	 */
	public void add(String columnName, Bindable bindable) {
		columns.add(columnName);
		values.put(columnName, bindable.toBinder());
	}

	/**
	 * SQL 文に挿入する項目名と SQL の断片を追加します。<br>
	 * 追加された順に SQL 文に反映されます。
	 * @param columnName テーブルの項目名
	 * @param fragment SQLの断片、たとえば SYSDATE など
	 */
	public void addSQLFragment(String columnName, String fragment) {
		columns.add(columnName);
		fragmentMap.put(columnName, fragment);
	}

	/**
	 * SQL 文に挿入する項目名と、プレースホルダをもつ SQL の断片を追加します。<br>
	 * 追加された順に SQL 文に反映されます。
	 * @param columnName テーブルの項目名
	 * @param fragment SQLの断片、たとえば TO_CHAR(?,'FM099') など
	 * @param bindable バインド可能な値
	 */
	public void addBindableSQLFragment(
		String columnName,
		String fragment,
		Bindable bindable) {
		addSQLFragment(columnName, fragment);
		values.put(columnName, bindable.toBinder());
	}

	/**
	 * DML に対する微調整をするための {@link Effector} をセットします。
	 * @param effector SQL 文を調整する {@link Effector}
	 */
	public void addEffector(Effector effector) {
		adjusters.add(effector);
	}

	@Override
	public String sql() {
		if (columns.size() == 0) throw new IllegalStateException("保存対象が設定されていません");

		String sql = build();
		for (Effector adjuster : adjusters) {
			sql = adjuster.effect(sql);
		}

		return sql;
	}

	@Override
	public void complement(BlenPreparedStatement statement) {
		int counter = 0;
		for (Iterator<Binder> i = values.values().iterator(); i.hasNext(); counter++) {
			i.next().bind(counter + 1, statement);
		}
	}

	@Override
	public String toString() {
		return sql();
	}

	/**
	 * 更新対象となるテーブルを返します。
	 * @return 更新対象テーブル
	 */
	public TablePath getTablePath() {
		return path;
	}

	/**
	 * 現在保持しているカラムを返します。
	 * @return 現在保持しているカラム
	 */
	public String[] getColumnNames() {
		return columns.toArray(new String[columns.size()]);
	}

	/**
	 * 更新用 DML 文を生成します。
	 * @return 更新用 DML 文
	 */
	protected abstract String build();

	/**
	 * このカラム名に対応する SQL 文の一部かもしくはプレースホルダ '?' を返します。
	 * @param columnName 対象となるカラム名
	 * @return SQL 文の一部かもしくはプレースホルダ
	 */
	protected String getPlaceHolderOrFragment(String columnName) {
		String fragment = fragmentMap.get(columnName);
		if (fragment != null) return fragment;
		return "?";
	}
}
