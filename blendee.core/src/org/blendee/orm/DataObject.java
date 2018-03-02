package org.blendee.orm;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.blendee.internal.Traversable;
import org.blendee.internal.TraversableNode;
import org.blendee.internal.Traverser;
import org.blendee.internal.TraverserOperator;
import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Result;
import org.blendee.orm.DataAccessHelper.BatchStatementFacade;
import org.blendee.orm.DataAccessHelper.StatementFacade;
import org.blendee.selector.Optimizer;
import org.blendee.selector.SelectedValues;
import org.blendee.sql.Bindable;
import org.blendee.sql.Binder;
import org.blendee.sql.Column;
import org.blendee.sql.Effector;
import org.blendee.sql.NotFoundException;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.Updatable;
import org.blendee.sql.UpdateDMLBuilder;
import org.blendee.sql.Updater;
import org.blendee.sql.binder.BigDecimalBinder;
import org.blendee.sql.binder.BlobBinder;
import org.blendee.sql.binder.BooleanBinder;
import org.blendee.sql.binder.ByteArrayBinder;
import org.blendee.sql.binder.ClobBinder;
import org.blendee.sql.binder.DoubleBinder;
import org.blendee.sql.binder.FloatBinder;
import org.blendee.sql.binder.IntBinder;
import org.blendee.sql.binder.LongBinder;
import org.blendee.sql.binder.NullBinder;
import org.blendee.sql.binder.ObjectBinder;
import org.blendee.sql.binder.StringBinder;
import org.blendee.sql.binder.TimestampBinder;
import org.blendee.sql.binder.UUIDBinder;

/**
 * データベースの一行を表すクラスです。<br>
 * このクラスのインスタンスを使用して、データベースの一行に対する参照と更新が可能です。
 * @author 千葉 哲嗣
 * @see DataAccessHelper#getDataObject(Optimizer, PrimaryKey, Effector...)
 * @see DataAccessHelper#regetDataObject(Optimizer)
 * @see DataObjectIterator#next()
 */
public class DataObject
	implements Updatable, Traversable {

	private static final NullSelectedValues nullSelectedValues = new NullSelectedValues();

	/**
	 * このインスタンスが表すテーブルの {@link Relationship}
	 */
	private final Relationship relationship;

	/**
	 * このインスタンスが持つ値
	 */
	private final SelectedValues values;

	private Map<String, UpdateValue> updateValues;

	/**
	 * このインスタンスが持つ値が更新された場合、ずっと true
	 */
	private boolean changed = false;

	/**
	 * {@link DataObject} からこのクラスのインスタンスを生成するコピーコンストラクタです。
	 * @param dataObject コピーする {@link DataObject}
	 */
	public DataObject(DataObject dataObject) {
		this(dataObject.relationship, dataObject.values);
	}

	/**
	 * このクラスのインスタンスを、単に INSERT 用等の Updatable として使用するためのコンストラクタです。
	 * @param relationship INSERT 対象のテーブル
	 */
	public DataObject(Relationship relationship) {
		this.relationship = relationship;
		values = nullSelectedValues;
	}

	DataObject(Relationship relationship, SelectedValues values) {
		this.relationship = relationship;
		this.values = values;
	}

	/**
	 * 指定されたカラムの値を boolean として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public boolean getBoolean(String columnName) {
		BooleanBinder binder = (BooleanBinder) getUpdateValue(columnName);
		if (binder != null) return binder.getBooleanValue();
		return values.getBoolean(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を double として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public double getDouble(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) {
			return ((Number) binder.getValue()).doubleValue();
		}

		return values.getDouble(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を float として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public float getFloat(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) {
			return ((Number) binder.getValue()).floatValue();
		}

		return values.getFloat(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を int として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public int getInt(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) {
			return ((Number) binder.getValue()).intValue();
		}

		return values.getInt(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を long として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public long getLong(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) {
			return ((Number) binder.getValue()).longValue();
		}

		return values.getLong(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を文字列として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public String getString(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) return binder.getValue().toString();
		return values.getString(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を {@link Timestamp} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public Timestamp getTimestamp(String columnName) {
		TimestampBinder binder = (TimestampBinder) getUpdateValue(columnName);
		if (binder != null) return binder.getTimestampValue();
		return values.getTimestamp(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を {@link BigDecimal} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public BigDecimal getBigDecimal(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) {
			Number number = (Number) binder.getValue();
			if (number instanceof BigDecimal) return (BigDecimal) number;
			return new BigDecimal(number.toString());
		}

		return values.getBigDecimal(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を {@link UUID} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public UUID getUUID(String columnName) {
		UUIDBinder binder = (UUIDBinder) getUpdateValue(columnName);
		if (binder != null) return binder.getUUIDValue();
		return values.getUUID(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を {@link Object} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public Object getObject(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) return binder.getValue();
		return values.getObject(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値をバイトの配列として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public byte[] getBytes(String columnName) {
		ByteArrayBinder binder = (ByteArrayBinder) getUpdateValue(columnName);
		if (binder != null) return binder.getByteArrayValue();
		return values.getBytes(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を {@link Blob} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public Blob getBlob(String columnName) {
		BlobBinder binder = (BlobBinder) getUpdateValue(columnName);
		if (binder != null) return binder.getBlobValue();
		return values.getBlob(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を {@link Clob} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public Clob getClob(String columnName) {
		ClobBinder binder = (ClobBinder) getUpdateValue(columnName);
		if (binder != null) return binder.getClobValue();
		return values.getClob(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を {@link Binder} として返します。
	 * @param columnName カラム名
	 * @return カラムの値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public Binder getBinder(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) return binder;
		return values.getBinder(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値が NULL かどうか検査します。
	 * @param columnName カラム名
	 * @return カラムの値が NULL の場合、true
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public boolean isNull(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) return binder instanceof NullBinder || binder.getValue() == null;
		return values.isNull(relationship.getColumn(columnName));
	}

	/**
	 * このインスタンスのテーブルが参照しているテーブルの {@link DataObject} を生成し、返します。
	 * @param foreignKeyName 外部キー名
	 * @return 参照しているテーブルの {@link DataObject}
	 */
	public DataObject getDataObject(String foreignKeyName) {
		return new DataObject(relationship.find(foreignKeyName), values);
	}

	/**
	 * キーがFK名、値がこのクラスのインスタンスとなるMapを返します。
	 * @return このインスタンスのテーブルが参照している全テーブルの {@link DataObject} の {@link Map}
	 */
	public Map<String, DataObject> getDataObjects() {
		return new DataObjectsMap();
	}

	/**
	 * このインスタンスのテーブルが参照しているテーブルの {@link DataObject} を生成し、返します。
	 * @param foreignKeyColumnNames 外部キーを構成するカラム名
	 * @return 参照しているテーブルの {@link DataObject}
	 */
	public DataObject getDataObject(String[] foreignKeyColumnNames) {
		return new DataObject(relationship.find(foreignKeyColumnNames), values);
	}

	/**
	 * キーが項目名、値がその項目の値となるMapを返します。
	 * @return 全値を持つ {@link Map}
	 */
	public Map<String, Object> getValues() {
		return new ValuesMap();
	}

	/**
	 * 指定されたカラムの値をパラメータの boolean 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setBoolean(String columnName, boolean value) {
		setValue(columnName, new BooleanBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの double 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setDouble(String columnName, double value) {
		setValue(columnName, new DoubleBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの float 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setFloat(String columnName, float value) {
		setValue(columnName, new FloatBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの int 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setInt(String columnName, int value) {
		setValue(columnName, new IntBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの long 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setLong(String columnName, long value) {
		setValue(columnName, new LongBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link String} 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setString(String columnName, String value) {
		setValue(columnName, new StringBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Timestamp} 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setTimestamp(String columnName, Timestamp value) {
		setValue(columnName, new TimestampBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link BigDecimal} 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setBigDecimal(String columnName, BigDecimal value) {
		setValue(columnName, new BigDecimalBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link UUID} 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setUUID(String columnName, UUID value) {
		setValue(columnName, new UUIDBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Object} 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setObject(String columnName, Object value) {
		setValue(columnName, new ObjectBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの byte 配列で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setBytes(String columnName, byte[] value) {
		setValue(columnName, new ByteArrayBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Blob} 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setBlob(String columnName, Blob value) {
		setValue(columnName, new BlobBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Clob} 値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setClob(String columnName, Clob value) {
		setValue(columnName, new ClobBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Bindable} が持つ値で更新します。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setValue(String columnName, Bindable value) {
		Binder binder = value.toBinder();
		getUpdateValues().put(columnName, new BinderUpdateValue(binder));
		changed = true;
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Bindable} が持つ値で更新します。<br>
	 * {@link #setValue(String, Bindable)} との違いとして、検索時の値と比較せずに、必ずセットされた値で更新を行います。
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setValueForcibly(String columnName, Bindable value) {
		Binder binder = value.toBinder();
		getUpdateValues().put(columnName, new BinderUpdateValue(binder));
		changed = true;
	}

	/**
	 * 現在保持している値を、全て検索時の値で強制的に更新したことにします。<br>
	 * 用途としては、このインスタンスを用いてデータをコピーしたい場合、検索してきた値をそのまま INSERT する場合が考えられます。<br>
	 * 既に値が置き換えられている場合、置き換えられた値はそのまま保持されています。
	 */
	public void setAllValuesForcibly() {
		Map<String, UpdateValue> updateValues = getUpdateValues();
		for (Column column : relationship.getColumns()) {
			String name = column.getName();
			if (updateValues.containsKey(name)) continue;
			setValueForcibly(name, getBinder(name));
		}
		changed = true;
	}

	/**
	 * 指定されたカラムの更新された値を返します。<br>
	 * 指定されたカラムが更新されていない場合、 null を返します。
	 * @param columnName 対象カラム
	 * @return 更新された値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public Binder getUpdateValue(String columnName) {
		if (!changed) return null;
		Binder value;
		value = getUpdateValueInternal(updateValues, columnName);
		return value;
	}

	/**
	 * 指定されたカラムの更新された値を除去します。
	 * @param columnName 対象カラム
	 */
	public void removeUpdateValue(String columnName) {
		getUpdateValues().remove(columnName);
		changed = true;
	}

	/**
	 * 指定されたカラムに、 UPDATE 文に組み込む SQL 文を設定します。
	 * @param columnName 対象カラム
	 * @param sqlFragment SQL 文の一部
	 */
	public void setSQLFragment(String columnName, String sqlFragment) {
		getUpdateValues().put(columnName, new SQLFragmentUpdateValue(sqlFragment));
		changed = true;
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
		getUpdateValues().put(
			columnName,
			new SQLFragmentAndBinderUpdateValue(sqlFragment, value.toBinder()));
		changed = true;
	}

	/**
	 * このインスタンスが持つ値が更新されているかどうかを判定します。
	 * @return 更新されている場合、 true
	 */
	public boolean isValueUpdated() {
		return changed;
	}

	/**
	 * {@link Traverser} にこのインスタンスが参照するテーブルのツリーを巡回させます。
	 * @param traverser {@link Traverser}
	 */
	public void traverse(Traverser traverser) {
		TraverserOperator.operate(traverser, this);
	}

	@Override
	public TraversableNode getSubNode() {
		TraversableNode node = new TraversableNode();
		Relationship[] sub = relationship.getRelationships();
		for (Relationship relationship : sub) {
			node.add(new DataObject(relationship, values));
		}
		return node;
	}

	/**
	 * このインスタンスの持つPKの値が、NULLかどうかを検査します。
	 * @return このインスタンスが外部結合によるものであれば、true
	 */
	public boolean isNullPrimaryKey() {
		Column[] columns = relationship.getPrimaryKeyColumns();
		for (int i = 0; i < columns.length; i++) {
			Column column = columns[i];
			if (values.isNull(column)) return true;
		}

		return false;
	}

	/**
	 * このインスタンスのテーブルが持つ主キーを返します。
	 * @return このテーブルの主キー
	 * @throws IllegalStateException このテーブルが主キーを持たない場合
	 * @throws NullPrimaryKeyException このインスタンスの主キーが NULL の場合
	 */
	public PrimaryKey getPrimaryKey() {
		return new PrimaryKey(relationship.getTablePath(), getPrimaryKeyBinders());
	}

	/**
	 * このインスタンスのテーブルが持つ外部キーを返します。
	 * @param foreignKeyName 外部キー名
	 * @return このテーブルが持つ外部キー
	 * @throws NullPrimaryKeyException 参照する主キーが NULL の場合
	 */
	public ForeignKey getForeignKey(String foreignKeyName) {
		return getDataObject(foreignKeyName).getPrimaryKey()
			.getReferences(relationship.getTablePath(), foreignKeyName);
	}

	/**
	 * このインスタンスのテーブルが持つ外部キーを返します。
	 * @param foreignKeyColumnNames 外部キーを構成するカラム名
	 * @return このテーブルが持つ外部キー
	 * @throws NullPrimaryKeyException 参照する主キーが NULL の場合
	 */
	public ForeignKey getForeignKey(String[] foreignKeyColumnNames) {
		return getDataObject(foreignKeyColumnNames).getPrimaryKey()
			.getReferences(relationship.getTablePath(), foreignKeyColumnNames);
	}

	/**
	 * 内部に保持している {@link Relationship} を返します。
	 * @return このインスタンスが内部にもつ {@link Relationship}
	 */
	public Relationship getRelationship() {
		return relationship;
	}

	/**
	 * 内部に保持している {@link SelectedValues} を返します。
	 * @return このインスタンスが内部にもつ {@link SelectedValues}
	 */
	public SelectedValues getSelectedValues() {
		return values;
	}

	/**
	 * 更新された値をデータベースに反映させるため、 UPDATE を実行します。<br>
	 * 更新された値が一件も無かった場合、このメソッドは何もせず false を返します。
	 * @return 更新された値が一件も無かった場合、 false
	 * @throws NullPrimaryKeyException このインスタンスの主キーが NULL の場合
	 */
	public boolean update() {
		return updateInternal(DataAccessHelper.getThreadStatement());
	}

	/**
	 * 更新された値をデータベースに反映させるため、 UPDATE をバッチ実行します。<br>
	 * 更新された値が一件も無かった場合、このメソッドは何もせず false を返します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @throws NullPrimaryKeyException このインスタンスの主キーが NULL の場合
	 */
	public void update(BatchStatement statement) {
		updateInternal(new BatchStatementFacade(statement));
	}

	@Override
	public void setValuesTo(Updater updater) {
		if (updateValues == null) return;

		for (Entry<String, UpdateValue> entry : updateValues.entrySet()) {
			UpdateValue updateValue = entry.getValue();
			updateValue.add(entry.getKey(), updater);
		}
	}

	private static Binder getUpdateValueInternal(
		Map<String, UpdateValue> map,
		String columnName) {
		if (map == null) return null;
		UpdateValue updateValue = map.get(columnName);
		if (updateValue == null) return null;
		return updateValue.getValue(columnName);
	}

	/**
	 * @throws NullPrimaryKeyException
	 */
	private boolean updateInternal(StatementFacade statement) {
		if (updateValues == null || updateValues.size() == 0) return false;

		UpdateDMLBuilder builder = new UpdateDMLBuilder(relationship.getTablePath());
		setValuesTo(builder);

		Column[] primaryKeyColumns = relationship.getPrimaryKeyColumns();
		if (!relationship.isRoot()) {
			//root でないと、 Criteria を作る際に、チェックに引っかかるので
			//root の Relationship のカラムに変換しておく
			Relationship root = ContextManager.get(RelationshipFactory.class).getInstance(relationship.getTablePath());
			for (int i = 0; i < primaryKeyColumns.length; i++) {
				primaryKeyColumns[i] = root.getColumn(primaryKeyColumns[i].getName());
			}
		}

		builder.setCriteria(PartialData.createCriteria(primaryKeyColumns, getPrimaryKeyBinders()));

		statement.process(builder.toString(), builder);
		int result = statement.execute();

		if (result == 1 || result == BatchStatementFacade.DUMMY_RESULT) return true;

		throw new IllegalStateException("更新結果が " + result + " 件です");
	}

	/**
	 * このインスタンスの持つ主キーの値を {@link Binder} として返します。
	 * @return 主キーの値を持つ {@link Binder}
	 * @throws IllegalStateException このテーブルが主キーを持たない場合
	 * @throws NullPrimaryKeyException このインスタンスの主キーが NULL の場合
	 */
	private Binder[] getPrimaryKeyBinders() {
		Column[] columns = relationship.getPrimaryKeyColumns();
		if (columns.length == 0)
			throw new IllegalStateException(relationship.getTablePath() + " は PK を持ちません");

		Binder[] binders = new Binder[columns.length];
		for (int i = 0; i < columns.length; i++) {
			Column column = columns[i];
			if (values.isNull(column)) throw new NullPrimaryKeyException(relationship);
			binders[i] = values.getBinder(column);
		}

		return binders;
	}

	private Map<String, UpdateValue> getUpdateValues() {
		if (updateValues == null) updateValues = new LinkedHashMap<>();
		return updateValues;
	}

	private static abstract class UpdateValue {

		abstract void add(String columnName, Updater updater);

		abstract Binder getValue(String columnName);
	}

	private static class BinderUpdateValue extends UpdateValue {

		private final Binder value;

		private BinderUpdateValue(Binder value) {
			this.value = value;
		}

		@Override
		void add(String columnName, Updater updater) {
			updater.add(columnName, value);
		}

		@Override
		Binder getValue(String columnName) {
			return value;
		}
	}

	private static class SQLFragmentUpdateValue extends UpdateValue {

		private final String sqlFragment;

		private SQLFragmentUpdateValue(String sqlFragment) {
			this.sqlFragment = sqlFragment;
		}

		@Override
		void add(String columnName, Updater updater) {
			updater.addSQLFragment(columnName, sqlFragment);
		}

		@Override
		Binder getValue(String columnName) {
			throw new UnknownValueException(columnName, sqlFragment);
		}
	}

	private static class SQLFragmentAndBinderUpdateValue extends UpdateValue {

		private final String sqlFragment;

		private final Binder value;

		private SQLFragmentAndBinderUpdateValue(String sqlFragment, Binder value) {
			this.sqlFragment = sqlFragment;
			this.value = value;
		}

		@Override
		void add(String columnName, Updater updater) {
			updater.addBindableSQLFragment(columnName, sqlFragment, value);
		}

		@Override
		Binder getValue(String columnName) {
			throw new UnknownValueException(columnName, sqlFragment);
		}
	}

	/**
	 * マップを変更するメソッドと、（値の選択の最適化の関係上）
	 * 全ての値にアクセスするメソッドは使用できません
	 */
	private class DataObjectsMap implements Map<String, DataObject> {

		private DataObjectsMap() {}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsKey(Object key) {
			try {
				getDataObjectInternal((String) key);
				return true;
			} catch (NotFoundException e) {
				return false;
			}
		}

		@Override
		public boolean containsValue(Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<Entry<String, DataObject>> entrySet() {
			Relationship[] relations = relationship.getRelationships();
			Set<Entry<String, DataObject>> result = new LinkedHashSet<>();
			for (Relationship relationship : relations) {
				String key = relationship.getCrossReference().getForeignKeyName();
				result.add(new DataObjectEntry<DataObject>(key, get(key)));
			}

			return result;
		}

		@Override
		public DataObject get(Object key) {
			return getDataObjectInternal((String) key);
		}

		@Override
		public boolean isEmpty() {
			return size() == 0;
		}

		@Override
		public Set<String> keySet() {
			Relationship[] relations = relationship.getRelationships();
			Set<String> result = new LinkedHashSet<>();
			for (Relationship relationship : relations) {
				result.add(relationship.getCrossReference().getForeignKeyName());
			}

			return result;
		}

		@Override
		public DataObject put(String key, DataObject value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(Map<? extends String, ? extends DataObject> map) {
			throw new UnsupportedOperationException();
		}

		@Override
		public DataObject remove(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return relationship.getRelationships().length;
		}

		@Override
		public Collection<DataObject> values() {
			throw new UnsupportedOperationException();
		}

		private DataObject getDataObjectInternal(String key) {
			if (key.indexOf(',') != -1) return getDataObject(key.split(" *, *"));
			return getDataObject(key);
		}
	}

	/**
	 * マップを変更するメソッドと、（値の選択の最適化の関係上）
	 * 全ての値にアクセスするメソッドは使用できません。
	 */
	private class ValuesMap implements Map<String, Object> {

		private ValuesMap() {}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsKey(Object key) {
			return relationship.getColumn((String) key) != null;
		}

		@Override
		public boolean containsValue(Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Set<Entry<String, Object>> entrySet() {
			Column[] columns = relationship.getColumns();
			Set<Entry<String, Object>> result = new LinkedHashSet<>();
			for (Column column : columns) {
				String key = column.getName();
				result.add(new DataObjectEntry<Object>(key, get(key)));
			}

			return result;
		}

		@Override
		public Object get(Object key) {
			return getObject((String) key);
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public Set<String> keySet() {
			Column[] columns = relationship.getColumns();
			Set<String> result = new LinkedHashSet<>();
			for (Column column : columns) {
				result.add(column.getName());
			}

			return result;
		}

		@Override
		public Object put(String key, Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void putAll(Map<? extends String, ? extends Object> map) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object remove(Object key) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return relationship.getColumns().length;
		}

		@Override
		public Collection<Object> values() {
			throw new UnsupportedOperationException();
		}
	}

	private static class DataObjectEntry<V> implements Entry<String, V> {

		private final String key;

		private final V value;

		private DataObjectEntry(String key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals(Object o) {
			Entry<?, ?> target = (Entry<?, ?>) o;
			return (key == null ? target.getKey() == null : key.equals(target.getKey()))
				&& (value == null ? target.getValue() == null : value.equals(target.getValue()));
		}

		@Override
		public int hashCode() {
			return Objects.hash(key, value);
		}
	}

	private static class NullSelectedValues implements SelectedValues {

		private static final Column[] emptyArray = {};

		@Override
		public BigDecimal getBigDecimal(Column column) {
			return null;
		}

		@Override
		public Binder getBinder(Column column) {
			return null;
		}

		@Override
		public Blob getBlob(Column column) {
			return null;
		}

		@Override
		public boolean getBoolean(Column column) {
			return false;
		}

		@Override
		public byte[] getBytes(Column column) {
			return null;
		}

		@Override
		public Clob getClob(Column column) {
			return null;
		}

		@Override
		public double getDouble(Column column) {
			return 0;
		}

		@Override
		public float getFloat(Column column) {
			return 0;
		}

		@Override
		public int getInt(Column column) {
			return 0;
		}

		@Override
		public long getLong(Column column) {
			return 0;
		}

		@Override
		public Object getObject(Column column) {
			return null;
		}

		@Override
		public Column[] getSelectedColumns() {
			return emptyArray;
		}

		@Override
		public String getString(Column column) {
			return null;
		}

		@Override
		public Timestamp getTimestamp(Column column) {
			return null;
		}

		@Override
		public UUID getUUID(Column column) {
			return null;
		}

		@Override
		public boolean isNull(Column column) {
			return false;
		}

		@Override
		public Result getResult() {
			return null;
		}

		@Override
		public boolean isSelected(Column column) {
			return false;
		}
	}
}
