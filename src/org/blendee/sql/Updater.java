package org.blendee.sql;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.blendee.internal.CollectionMap;
import org.blendee.jdbc.BConnection;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.Batch;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.binder.BigDecimalBinder;
import org.blendee.sql.binder.BooleanBinder;
import org.blendee.sql.binder.DoubleBinder;
import org.blendee.sql.binder.FloatBinder;
import org.blendee.sql.binder.IntBinder;
import org.blendee.sql.binder.LongBinder;
import org.blendee.sql.binder.ObjectBinder;
import org.blendee.sql.binder.StringBinder;
import org.blendee.sql.binder.TimestampBinder;
import org.blendee.sql.binder.UUIDBinder;

/**
 * 登録更新用 SQL 文を生成するための抽象基底クラスです。
 * @author 千葉 哲嗣
 */
public abstract class Updater implements ComposedSQL {

	private final TablePath path;

	private final Set<String> columns = new LinkedHashSet<>();

	private final CollectionMap<String, Binder> values = CollectionMap.newInstance(LinkedHashMap.class);

	private final Map<String, String> fragmentMap = new HashMap<>();

	private final List<SQLDecorator> decorators = new LinkedList<>();

	/**
	 * パラメータの表すテーブルに対する更新を行うインスタンスを生成します。
	 * @param path 対象となるテーブル
	 */
	protected Updater(TablePath path) {
		this.path = Objects.requireNonNull(path);
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
	 * @param value 値
	 */
	public void add(String columnName, BigDecimal value) {
		columns.add(columnName);
		values.put(columnName, new BigDecimalBinder(value));
	}

	/**
	 * SQL 文に挿入する項目名とプレースホルダにバインドする値を追加します。<br>
	 * 追加された順に SQL 文に反映されます。
	 * @param columnName テーブルの項目名
	 * @param value 値
	 */
	public void add(String columnName, boolean value) {
		columns.add(columnName);
		values.put(columnName, new BooleanBinder(value));
	}

	/**
	 * SQL 文に挿入する項目名とプレースホルダにバインドする値を追加します。<br>
	 * 追加された順に SQL 文に反映されます。
	 * @param columnName テーブルの項目名
	 * @param value 値
	 */
	public void add(String columnName, double value) {
		columns.add(columnName);
		values.put(columnName, new DoubleBinder(value));
	}

	/**
	 * SQL 文に挿入する項目名とプレースホルダにバインドする値を追加します。<br>
	 * 追加された順に SQL 文に反映されます。
	 * @param columnName テーブルの項目名
	 * @param value 値
	 */
	public void add(String columnName, float value) {
		columns.add(columnName);
		values.put(columnName, new FloatBinder(value));
	}

	/**
	 * SQL 文に挿入する項目名とプレースホルダにバインドする値を追加します。<br>
	 * 追加された順に SQL 文に反映されます。
	 * @param columnName テーブルの項目名
	 * @param value 値
	 */
	public void add(String columnName, int value) {
		columns.add(columnName);
		values.put(columnName, new IntBinder(value));
	}

	/**
	 * SQL 文に挿入する項目名とプレースホルダにバインドする値を追加します。<br>
	 * 追加された順に SQL 文に反映されます。
	 * @param columnName テーブルの項目名
	 * @param value 値
	 */
	public void add(String columnName, long value) {
		columns.add(columnName);
		values.put(columnName, new LongBinder(value));
	}

	/**
	 * SQL 文に挿入する項目名とプレースホルダにバインドする値を追加します。<br>
	 * 追加された順に SQL 文に反映されます。
	 * @param columnName テーブルの項目名
	 * @param value 値
	 */
	public void add(String columnName, Object value) {
		columns.add(columnName);
		values.put(columnName, new ObjectBinder(value));
	}

	/**
	 * SQL 文に挿入する項目名とプレースホルダにバインドする値を追加します。<br>
	 * 追加された順に SQL 文に反映されます。
	 * @param columnName テーブルの項目名
	 * @param value 値
	 */
	public void add(String columnName, String value) {
		columns.add(columnName);
		values.put(columnName, new StringBinder(value));
	}

	/**
	 * SQL 文に挿入する項目名とプレースホルダにバインドする値を追加します。<br>
	 * 追加された順に SQL 文に反映されます。
	 * @param columnName テーブルの項目名
	 * @param value 値
	 */
	public void add(String columnName, Timestamp value) {
		columns.add(columnName);
		values.put(columnName, new TimestampBinder(value));
	}

	/**
	 * SQL 文に挿入する項目名とプレースホルダにバインドする値を追加します。<br>
	 * 追加された順に SQL 文に反映されます。
	 * @param columnName テーブルの項目名
	 * @param value 値
	 */
	public void add(String columnName, UUID value) {
		columns.add(columnName);
		values.put(columnName, new UUIDBinder(value));
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
	 * @param fragment SQLの断片、たとえば TO_CHAR(?, 'FM099') など
	 * @param bindables バインド可能な値
	 */
	public void addBindableSQLFragment(
		String columnName,
		String fragment,
		Bindable... bindables) {
		addSQLFragment(columnName, fragment);

		Arrays.stream(bindables).forEach(b -> values.put(columnName, b.toBinder()));
	}

	/**
	 * DML に対する微調整をするための {@link SQLDecorator} をセットします。
	 * @param decorators SQL 文を調整する {@link SQLDecorator}
	 */
	public void addDecorator(SQLDecorator... decorators) {
		for (SQLDecorator decorator : decorators) {
			this.decorators.add(decorator);
		}
	}

	@Override
	public String sql() {
		//保存対象が設定されていません
		if (columns.size() == 0) throw new IllegalStateException("empty columns");

		String sql = build();
		for (SQLDecorator decorator : decorators) {
			sql = decorator.decorate(sql);
		}

		return sql;
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		for (Iterator<Binder> i = getBindersInternal().iterator(); i.hasNext(); done++) {
			i.next().bind(done + 1, statement);
		}

		return done;
	}

	/**
	 * データ更新を実行します。
	 * @return 対象件数
	 */
	public int executeUpdate() {
		BConnection connection = BlendeeManager.getConnection();
		try (BStatement statement = connection.getStatement(this)) {
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
	 * 現在保持している {@link Binder} を返します。
	 * @return {@link Binder}
	 */
	public Binder[] getBinders() {
		Collection<Binder> binders = getBindersInternal();
		return binders.toArray(new Binder[binders.size()]);
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

	private List<Binder> getBindersInternal() {
		List<Binder> binders = new LinkedList<>();
		values.keySet().forEach(key -> values.get(key).forEach(b -> binders.add(b)));

		return binders;
	}
}
