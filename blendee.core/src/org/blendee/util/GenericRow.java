package org.blendee.util;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataObject;
import org.blendee.orm.NullPrimaryKeyException;
import org.blendee.sql.Bindable;
import org.blendee.sql.Binder;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.support.Row;

/**
 * {@link Row} の汎用実装クラスです。
 */
public class GenericRow extends java.lang.Object implements Row {

	private final TablePath tablePath;

	private final DataObject data;

	private final Relationship relationship;

	/**
	 * 登録用コンストラクタです。
	 * @param tablePath 対象となるテーブル
	 */
	public GenericRow(TablePath tablePath) {
		this.tablePath = tablePath;
		relationship = ContextManager.get(RelationshipFactory.class).getInstance(tablePath);
		data = new DataObject(relationship);
	}

	/**
	 * 参照、更新用コンストラクタです。
	 * @param data 値を持つ {@link DataObject}
	 */
	public GenericRow(DataObject data) {
		tablePath = data.getRelationship().getTablePath();
		relationship = ContextManager.get(RelationshipFactory.class).getInstance(tablePath);
		this.data = data;
	}

	/**
	 * 指定されたカラムの値を boolean として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public boolean getBoolean(String columnName) {
		return data.getBoolean(columnName);
	}

	/**
	 * 指定されたカラムの値を double として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public double getDouble(String columnName) {
		return data.getDouble(columnName);
	}

	/**
	 * 指定されたカラムの値を float として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public float getFloat(String columnName) {
		return data.getFloat(columnName);
	}

	/**
	 * 指定されたカラムの値を int として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public int getInt(String columnName) {
		return data.getInt(columnName);
	}

	/**
	 * 指定されたカラムの値を long として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public long getLong(String columnName) {
		return data.getLong(columnName);
	}

	/**
	 * 指定されたカラムの値を文字列として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public String getString(String columnName) {
		return data.getString(columnName);
	}

	/**
	 * 指定されたカラムの値を {@link Timestamp} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public Timestamp getTimestamp(String columnName) {
		return data.getTimestamp(columnName);
	}

	/**
	 * 指定されたカラムの値を {@link BigDecimal} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public BigDecimal getBigDecimal(String columnName) {
		return data.getBigDecimal(columnName);
	}

	/**
	 * 指定されたカラムの値を {@link UUID} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public UUID getUUID(String columnName) {
		return data.getUUID(columnName);
	}

	/**
	 * 指定されたカラムの値を {@link Object} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public Object getObject(String columnName) {
		return data.getObject(columnName);
	}

	/**
	 * 指定されたカラムの値をバイトの配列として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public byte[] getBytes(String columnName) {
		return data.getBytes(columnName);
	}

	/**
	 * 指定されたカラムの値を {@link Blob} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public Blob getBlob(String columnName) {
		return data.getBlob(columnName);
	}

	/**
	 * 指定されたカラムの値を {@link Clob} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public Clob getClob(String columnName) {
		return data.getClob(columnName);
	}

	/**
	 * 指定されたカラムの値を {@link Binder} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public Binder getBinder(String columnName) {
		return data.getValue(columnName);
	}

	/**
	 * 指定されたカラムの値が NULL かどうか検査します。
	 * @param columnName カラム名
	 * @return カラムの値が NULL の場合、true
	 */
	public boolean isNull(String columnName) {
		return data.isNull(columnName);
	}

	/**
	 * キーが項目名、値がその項目の値となるMapを返します。
	 * @return 全値を持つ {@link Map}
	 */
	public Map<String, Object> getValues() {
		return data.getValues();
	}

	/**
	 * 指定されたカラムの値をパラメータの boolean 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setBoolean(String columnName, boolean value) {
		data.setBoolean(columnName, value);
	}

	/**
	 * 指定されたカラムの値をパラメータの double 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setDouble(String columnName, double value) {
		data.setDouble(columnName, value);
	}

	/**
	 * 指定されたカラムの値をパラメータの float 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setFloat(String columnName, float value) {
		data.setFloat(columnName, value);
	}

	/**
	 * 指定されたカラムの値をパラメータの int 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setInt(String columnName, int value) {
		data.setInt(columnName, value);
	}

	/**
	 * 指定されたカラムの値をパラメータの long 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setLong(String columnName, long value) {
		data.setLong(columnName, value);
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link String} 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setString(String columnName, String value) {
		data.setString(columnName, value);
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Timestamp} 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setTimestamp(String columnName, Timestamp value) {
		data.setTimestamp(columnName, value);
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link BigDecimal} 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setBigDecimal(String columnName, BigDecimal value) {
		data.setBigDecimal(columnName, value);
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link UUID} 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setUUID(String columnName, UUID value) {
		data.setUUID(columnName, value);
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Object} 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setObject(String columnName, Object value) {
		data.setObject(columnName, value);
	}

	/**
	 * 指定されたカラムの値をパラメータの byte 配列で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setBytes(String columnName, byte[] value) {
		data.setBytes(columnName, value);
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Blob} 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setBlob(String columnName, Blob value) {
		data.setBlob(columnName, value);
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Clob} 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setClob(String columnName, Clob value) {
		data.setClob(columnName, value);
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Bindable} が持つ値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setValue(String columnName, Bindable value) {
		data.setValue(columnName, value);
	}

	/**
	 * 指定されたカラムに、 UPDATE 文に組み込む SQL 文を設定します。
	 * @param columnName 対象カラム
	 * @param sqlFragment SQL 文の一部
	 */
	public void setSQLFragment(String columnName, String sqlFragment) {
		data.setSQLFragment(columnName, sqlFragment);
	}

	/**
	 * 指定されたカラムに、 UPDATE 文に組み込む SQL 文とそのプレースホルダの値を設定します。
	 * @param columnName 対象カラム
	 * @param sqlFragment SQL 文の一部
	 * @param value プレースホルダの値
	 */
	public void setSQLFragmentAndValue(
		String columnName,
		String sqlFragment,
		Bindable value) {
		data.setSQLFragmentAndValue(columnName, sqlFragment, value);
	}

	/**
	 * 内部に保持している {@link Relationship} を返します。
	 * @return このインスタンスが内部にもつ {@link Relationship}
	 */
	public Relationship getRelationship() {
		return relationship;
	}

	/**
	 * 更新された値をデータベースに反映させるため、 UPDATE を実行します。<br>
	 * 更新された値が一件も無かった場合、このメソッドは何もせず false を返します。
	 * @return 更新された値が一件も無かった場合、 false
	 * @throws NullPrimaryKeyException このインスタンスの主キーが NULL の場合
	 */
	@Override
	public boolean update() {
		return data.update();
	}

	/**
	 * 更新された値をデータベースに反映させるため、 UPDATE をバッチ実行します。<br>
	 * 更新された値が一件も無かった場合、このメソッドは何もせず false を返します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @throws NullPrimaryKeyException このインスタンスの主キーが NULL の場合
	 */
	@Override
	public void update(BatchStatement statement) {
		data.update(statement);
	}

	@Override
	public DataObject dataObject() {
		return data;
	}

	@Override
	public TablePath tablePath() {
		return tablePath;
	}

	/**
	 * キーがFK名、値がこのクラスのインスタンスとなるMapを返します。
	 * @return このインスタンスのテーブルが参照している全テーブルの {@link DataObject} の {@link Map}
	 */
	public Map<String, GenericRow> getRows() {
		Map<String, DataObject> source = data.getDataObjects();
		Map<String, GenericRow> dest = new HashMap<>();
		source.forEach((key, value) -> dest.put(key, new GenericRow(value)));

		return dest;
	}

	/**
	 * このレコードが参照しているレコードの Row を返します。<br>
	 * @param fkName 外部キー名
	 * @return 参照しているレコードの Row
	 */
	public GenericRow get(String fkName) {
		return new GenericRow(data.getDataObject(fkName));
	}

	/**
	 * このレコードが参照しているレコードの Row を返します。<br>
	 * @param fkColumnNames 外部キーを構成するカラム名
	 * @return 参照しているレコードの Row
	 */
	public GenericRow get(String[] fkColumnNames) {
		return new GenericRow(data.getDataObject(fkColumnNames));
	}
}
