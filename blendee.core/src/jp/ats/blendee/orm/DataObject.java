package jp.ats.blendee.orm;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jp.ats.blendee.internal.Traversable;
import jp.ats.blendee.internal.TraversableNode;
import jp.ats.blendee.internal.Traverser;
import jp.ats.blendee.internal.TraverserOperator;
import jp.ats.blendee.internal.U;
import jp.ats.blendee.selector.Optimizer;
import jp.ats.blendee.selector.SelectedValues;
import jp.ats.blendee.sql.Binder;
import jp.ats.blendee.sql.Column;
import jp.ats.blendee.sql.NotFoundException;
import jp.ats.blendee.sql.Relationship;

/**
 * データベースの一行を表すクラスです。
 * <br>
 * このクラスのインスタンスを使用して、データベースの一行に対する参照が可能です。
 *
 * @author 千葉 哲嗣
 * @see DataAccessHelper#getDataObject(Optimizer, PrimaryKey, QueryOption...)
 * @see DataAccessHelper#regetDataObject(Optimizer)
 * @see DataObjectIterator#next()
 */
public class DataObject implements Traversable {

	/**
	 * このインスタンスが表すテーブルの {@link Relationship}
	 */
	protected final Relationship relationship;

	/**
	 * このインスタンスが持つ値
	 */
	protected final SelectedValues values;

	/**
	 * {@link DataObject} からこのクラスのインスタンスを生成するコピーコンストラクタです。
	 *
	 * @param dataObject コピーする {@link DataObject}
	 */
	public DataObject(DataObject dataObject) {
		this(dataObject.relationship, dataObject.values);
	}

	DataObject(Relationship relationship, SelectedValues values) {
		this.relationship = relationship;
		this.values = values;
	}

	/**
	 * 指定されたカラムの値を boolean として返します。
	 *
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public boolean getBoolean(String columnName) {
		return values.getBoolean(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を double として返します。
	 *
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public double getDouble(String columnName) {
		return values.getDouble(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を float として返します。
	 *
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public float getFloat(String columnName) {
		return values.getFloat(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を int として返します。
	 *
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public int getInt(String columnName) {
		return values.getInt(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を long として返します。
	 *
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public long getLong(String columnName) {
		return values.getLong(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を文字列として返します。
	 *
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public String getString(String columnName) {
		return values.getString(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を {@link Timestamp} として返します。
	 *
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public Timestamp getTimestamp(String columnName) {
		return values.getTimestamp(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を {@link BigDecimal} として返します。
	 *
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public BigDecimal getBigDecimal(String columnName) {
		return values.getBigDecimal(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を {@link UUID} として返します。
	 *
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public UUID getUUID(String columnName) {
		return values.getUUID(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を {@link Object} として返します。
	 *
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public Object getObject(String columnName) {
		return values.getObject(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値をバイトの配列として返します。
	 *
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public byte[] getBytes(String columnName) {
		return values.getBytes(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を {@link Blob} として返します。
	 *
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public Blob getBlob(String columnName) {
		return values.getBlob(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を {@link Clob} として返します。
	 *
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public Clob getClob(String columnName) {
		return values.getClob(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値を {@link Binder} として返します。
	 *
	 * @param columnName カラム名
	 * @return カラムの値
	 */
	public Binder getBinder(String columnName) {
		return values.getBinder(relationship.getColumn(columnName));
	}

	/**
	 * 指定されたカラムの値が NULL かどうか検査します。
	 *
	 * @param columnName カラム名
	 * @return カラムの値が NULL の場合、true
	 */
	public boolean isNull(String columnName) {
		return values.isNull(relationship.getColumn(columnName));
	}

	/**
	 * キーが項目名、値がその項目の値となるMapを返します。
	 *
	 * @return 全値を持つ {@link Map}
	 */
	public Map<String, Object> getValues() {
		return new ValuesMap();
	}

	/**
	 * このインスタンスのテーブルが参照しているテーブルの {@link DataObject} を生成し、返します。
	 *
	 * @param foreignKeyName 外部キー名
	 * @return 参照しているテーブルの {@link DataObject}
	 */
	public DataObject getDataObject(String foreignKeyName) {
		return new DataObject(relationship.find(foreignKeyName), values);
	}

	/**
	 * このインスタンスのテーブルが参照しているテーブルの {@link DataObject} を生成し、返します。
	 *
	 * @param foreignKeyColumnNames 外部キーを構成するカラム名
	 * @return 参照しているテーブルの {@link DataObject}
	 */
	public DataObject getDataObject(String[] foreignKeyColumnNames) {
		return new DataObject(relationship.find(foreignKeyColumnNames), values);
	}

	/**
	 * キーがFK名、値がこのクラスのインスタンスとなるMapを返します。
	 *
	 * @return このインスタンスのテーブルが参照している全テーブルの {@link DataObject} の {@link Map}
	 */
	public Map<String, DataObject> getDataObjects() {
		return new DataObjectsMap();
	}

	/**
	 * {@link Traverser} にこのインスタンスが参照するテーブルのツリーを巡回させます。
	 *
	 * @param traverser {@link Traverser}
	 */
	public void traverse(Traverser traverser) {
		TraverserOperator.operate(traverser, this);
	}

	@Override
	public TraversableNode getSubNode() {
		TraversableNode node = new TraversableNode();
		Relationship[] sub = relationship.getRelationships();
		for (int i = 0; i < sub.length; i++) {
			node.add(new DataObject(sub[i], values));
		}

		return node;
	}

	/**
	 * このインスタンスの持つPKの値が、NULLかどうかを検査します。
	 *
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
	 *
	 * @return このテーブルの主キー
	 * @throws IllegalStateException このテーブルが主キーを持たない場合
	 * @throws NullPrimaryKeyException このインスタンスの主キーが NULL の場合
	 */
	public PrimaryKey getPrimaryKey() {
		return new PrimaryKey(relationship.getResourceLocator(), getPrimaryKeyBinders());
	}

	/**
	 * このインスタンスのテーブルが持つ外部キーを返します。
	 *
	 * @param foreignKeyName 外部キー名
	 * @return このテーブルが持つ外部キー
	 * @throws NullPrimaryKeyException 参照する主キーが NULL の場合
	 */
	public ForeignKey getForeignKey(String foreignKeyName) {
		return getDataObject(foreignKeyName).getPrimaryKey()
			.getReferences(relationship.getResourceLocator(), foreignKeyName);
	}

	/**
	 * このインスタンスのテーブルが持つ外部キーを返します。
	 *
	 * @param foreignKeyColumnNames 外部キーを構成するカラム名
	 * @return このテーブルが持つ外部キー
	 * @throws NullPrimaryKeyException 参照する主キーが NULL の場合
	 */
	public ForeignKey getForeignKey(String[] foreignKeyColumnNames) {
		return getDataObject(foreignKeyColumnNames).getPrimaryKey()
			.getReferences(relationship.getResourceLocator(), foreignKeyColumnNames);
	}

	/**
	 * 内部に保持している {@link Relationship} を返します。
	 *
	 * @return このインスタンスが内部にもつ {@link Relationship}
	 */
	public Relationship getRelationship() {
		return relationship;
	}

	/**
	 * 内部に保持している {@link SelectedValues} を返します。
	 *
	 * @return このインスタンスが内部にもつ {@link SelectedValues}
	 */
	public SelectedValues getSelectedValues() {
		return values;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	/**
	 * このインスタンスの持つ主キーの値を {@link Binder} として返します。
	 *
	 * @return 主キーの値を持つ {@link Binder}
	 * @throws IllegalStateException このテーブルが主キーを持たない場合
	 * @throws NullPrimaryKeyException このインスタンスの主キーが NULL の場合
	 */
	protected Binder[] getPrimaryKeyBinders() {
		Column[] columns = relationship.getPrimaryKeyColumns();
		if (columns.length == 0)
			throw new IllegalStateException(relationship.getResourceLocator() + " は PK を持ちません");

		Binder[] binders = new Binder[columns.length];
		for (int i = 0; i < columns.length; i++) {
			Column column = columns[i];
			if (values.isNull(column)) throw new NullPrimaryKeyException(relationship);
			binders[i] = values.getBinder(column);
		}

		return binders;
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
}
