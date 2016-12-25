package jp.ats.blendee.orm;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import jp.ats.blendee.internal.TraversableNode;
import jp.ats.blendee.jdbc.BatchStatement;
import jp.ats.blendee.jdbc.BContext;
import jp.ats.blendee.jdbc.BlendeeManager;
import jp.ats.blendee.jdbc.BResult;
import jp.ats.blendee.jdbc.Transaction;
import jp.ats.blendee.orm.DataAccessHelper.BatchStatementFacade;
import jp.ats.blendee.orm.DataAccessHelper.StatementFacade;
import jp.ats.blendee.selector.Optimizer;
import jp.ats.blendee.selector.SelectedValues;
import jp.ats.blendee.sql.Bindable;
import jp.ats.blendee.sql.Binder;
import jp.ats.blendee.sql.Column;
import jp.ats.blendee.sql.Relationship;
import jp.ats.blendee.sql.RelationshipFactory;
import jp.ats.blendee.sql.Updatable;
import jp.ats.blendee.sql.UpdateDMLBuilder;
import jp.ats.blendee.sql.Updater;
import jp.ats.blendee.sql.binder.BigDecimalBinder;
import jp.ats.blendee.sql.binder.BlobBinder;
import jp.ats.blendee.sql.binder.BooleanBinder;
import jp.ats.blendee.sql.binder.ByteArrayBinder;
import jp.ats.blendee.sql.binder.ClobBinder;
import jp.ats.blendee.sql.binder.DoubleBinder;
import jp.ats.blendee.sql.binder.FloatBinder;
import jp.ats.blendee.sql.binder.IntBinder;
import jp.ats.blendee.sql.binder.LongBinder;
import jp.ats.blendee.sql.binder.NullBinder;
import jp.ats.blendee.sql.binder.ObjectBinder;
import jp.ats.blendee.sql.binder.StringBinder;
import jp.ats.blendee.sql.binder.TimestampBinder;
import jp.ats.blendee.sql.binder.UUIDBinder;

/**
 * データベースの一行を表すクラスです。
 * <br>
 * このクラスのインスタンスを使用して、データベースの一行に対する参照と更新が可能です。
 *
 * @author 千葉 哲嗣
 * @see DataAccessHelper#getUpdatableDataObject(Optimizer, PrimaryKey, QueryOption...)
 * @see DataAccessHelper#regetUpdatableDataObject(Optimizer)
 * @see DataObjectIterator#next()
 */
public class UpdatableDataObject
	extends DataObject
	implements Updatable, Transaction {

	private static final NullSelectedValues nullSelectedValues = new NullSelectedValues();

	private Map<String, UpdateValue> updateValues;

	private Map<String, UpdateValue> updatedValues;

	/**
	 * このインスタンスが持つ値が更新された場合、ずっと true
	 */
	private boolean changed = false;

	private boolean autoCommit = false;

	/**
	 * {@link DataObject} からこのクラスのインスタンスを生成するコピーコンストラクタです。
	 *
	 * @param dataObject コピーする {@link DataObject}
	 */
	public UpdatableDataObject(DataObject dataObject) {
		super(dataObject);
	}

	/**
	 * このクラスのインスタンスを、単に INSERT 用等の Updatable として使用するためのコンストラクタです。
	 *
	 * @param relationship INSERT 対象のテーブル
	 */
	public UpdatableDataObject(Relationship relationship) {
		super(relationship, nullSelectedValues);
	}

	UpdatableDataObject(Relationship relationship, SelectedValues values) {
		super(relationship, values);
	}

	/**
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	@Override
	public boolean getBoolean(String columnName) {
		BooleanBinder binder = (BooleanBinder) getUpdateValue(columnName);
		if (binder != null) return binder.getBooleanValue();
		return super.getBoolean(columnName);
	}

	/**
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	@Override
	public double getDouble(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) {
			return ((Number) binder.getValue()).doubleValue();
		}
		return super.getDouble(columnName);
	}

	/**
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	@Override
	public float getFloat(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) {
			return ((Number) binder.getValue()).floatValue();
		}
		return super.getFloat(columnName);
	}

	/**
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	@Override
	public int getInt(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) {
			return ((Number) binder.getValue()).intValue();
		}
		return super.getInt(columnName);
	}

	/**
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	@Override
	public long getLong(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) {
			return ((Number) binder.getValue()).longValue();
		}
		return super.getLong(columnName);
	}

	/**
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	@Override
	public String getString(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) return binder.getValue().toString();
		return super.getString(columnName);
	}

	/**
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	@Override
	public Timestamp getTimestamp(String columnName) {
		TimestampBinder binder = (TimestampBinder) getUpdateValue(columnName);
		if (binder != null) return binder.getTimestampValue();
		return super.getTimestamp(columnName);
	}

	/**
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	@Override
	public BigDecimal getBigDecimal(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) {
			Number number = (Number) binder.getValue();
			if (number instanceof BigDecimal) return (BigDecimal) number;
			return new BigDecimal(number.toString());
		}
		return super.getBigDecimal(columnName);
	}

	/**
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	@Override
	public UUID getUUID(String columnName) {
		UUIDBinder binder = (UUIDBinder) getUpdateValue(columnName);
		if (binder != null) return binder.getUUIDValue();
		return super.getUUID(columnName);
	}

	/**
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	@Override
	public Object getObject(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) return binder.getValue();
		return super.getObject(columnName);
	}

	/**
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	@Override
	public byte[] getBytes(String columnName) {
		ByteArrayBinder binder = (ByteArrayBinder) getUpdateValue(columnName);
		if (binder != null) return binder.getByteArrayValue();
		return super.getBytes(columnName);
	}

	/**
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	@Override
	public Blob getBlob(String columnName) {
		BlobBinder binder = (BlobBinder) getUpdateValue(columnName);
		if (binder != null) return binder.getBlobValue();
		return super.getBlob(columnName);
	}

	/**
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	@Override
	public Clob getClob(String columnName) {
		ClobBinder binder = (ClobBinder) getUpdateValue(columnName);
		if (binder != null) return binder.getClobValue();
		return super.getClob(columnName);
	}

	/**
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	@Override
	public Binder getBinder(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) return binder;
		return super.getBinder(columnName);
	}

	/**
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	@Override
	public boolean isNull(String columnName) {
		Binder binder = getUpdateValue(columnName);
		if (binder != null) return binder instanceof NullBinder || binder.getValue() == null;
		return super.isNull(columnName);
	}

	@Override
	public UpdatableDataObject getDataObject(String foreignKeyName) {
		return new UpdatableDataObject(relationship.find(foreignKeyName), values);
	}

	@Override
	public UpdatableDataObject getDataObject(String[] foreignKeyColumnNames) {
		return new UpdatableDataObject(relationship.find(foreignKeyColumnNames), values);
	}

	/**
	 * 指定されたカラムの値をパラメータの boolean 値で更新します。
	 *
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setBoolean(String columnName, boolean value) {
		setValue(columnName, new BooleanBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの double 値で更新します。
	 *
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setDouble(String columnName, double value) {
		setValue(columnName, new DoubleBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの float 値で更新します。
	 *
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setFloat(String columnName, float value) {
		setValue(columnName, new FloatBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの int 値で更新します。
	 *
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setInt(String columnName, int value) {
		setValue(columnName, new IntBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの long 値で更新します。
	 *
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setLong(String columnName, long value) {
		setValue(columnName, new LongBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link String} 値で更新します。
	 *
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setString(String columnName, String value) {
		setValue(columnName, new StringBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Timestamp} 値で更新します。
	 *
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setTimestamp(String columnName, Timestamp value) {
		setValue(columnName, new TimestampBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link BigDecimal} 値で更新します。
	 *
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setBigDecimal(String columnName, BigDecimal value) {
		setValue(columnName, new BigDecimalBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link UUID} 値で更新します。
	 *
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setUUID(String columnName, UUID value) {
		setValue(columnName, new UUIDBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Object} 値で更新します。
	 *
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setObject(String columnName, Object value) {
		setValue(columnName, new ObjectBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの byte 配列で更新します。
	 *
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setBytes(String columnName, byte[] value) {
		setValue(columnName, new ByteArrayBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Blob} 値で更新します。
	 *
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setBlob(String columnName, Blob value) {
		setValue(columnName, new BlobBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Clob} 値で更新します。
	 *
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public void setClob(String columnName, Clob value) {
		setValue(columnName, new ClobBinder(value));
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Bindable} が持つ値で更新します。
	 *
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public synchronized void setValue(String columnName, Bindable value) {
		Binder binder = value.toBinder();

		UpdateValue updatedValue;
		if (updatedValues != null && (updatedValue = updatedValues.get(columnName)) != null) {
			if (updatedValue.executeEquals(binder)) return;
		} else {
			if (binder.equals(getBinder(columnName))) return;
		}

		getUpdateValues().put(columnName, new BinderUpdateValue(binder));
		changed = true;
	}

	/**
	 * 指定されたカラムの値をパラメータの {@link Bindable} が持つ値で更新します。
	 * <br>
	 * {@link #setValue(String, Bindable)} との違いとして、検索時の値と比較せずに、必ずセットされた値で更新を行います。
	 *
	 * @param columnName 対象カラム
	 * @param value 更新値
	 */
	public synchronized void setValueForcibly(String columnName, Bindable value) {
		Binder binder = value.toBinder();
		getUpdateValues().put(columnName, new BinderUpdateValue(binder));
		changed = true;
	}

	/**
	 * 現在保持している値を、全て検索時の値で強制的に更新したことにします。
	 * <br>
	 * 用途としては、このインスタンスを用いてデータをコピーしたい場合、検索してきた値をそのまま INSERT する場合が考えられます。
	 * <br>
	 * 既に値が置き換えられている場合、置き換えられた値はそのまま保持されています。
	 */
	public synchronized void setAllValuesForcibly() {
		Map<String, UpdateValue> updateValues = getUpdateValues();
		for (Column column : relationship.getColumns()) {
			String name = column.getName();
			if (updateValues.containsKey(name)) continue;
			setValueForcibly(name, getBinder(name));
		}
		changed = true;
	}

	/**
	 * 指定されたカラムの更新された値を返します。
	 * <br>
	 * 指定されたカラムが更新されていない場合、 null を返します。
	 *
	 * @param columnName 対象カラム
	 * @return 更新された値
	 * @throws UnknownValueException 新しい値の代わりに SQL 文の関数等をセットした後に値を取得しようとした場合
	 */
	public synchronized Binder getUpdateValue(String columnName) {
		if (!changed) return null;
		Binder value;
		value = getUpdateValueInternal(updateValues, columnName);
		if (value != null) return value;
		return getUpdateValueInternal(updatedValues, columnName);
	}

	/**
	 * 指定されたカラムの更新された値を除去します。
	 *
	 * @param columnName 対象カラム
	 */
	public synchronized void removeUpdateValue(String columnName) {
		getUpdateValues().remove(columnName);
		changed = true;
	}

	/**
	 * 指定されたカラムに、 UPDATE 文に組み込む SQL 文を設定します。
	 *
	 * @param columnName 対象カラム
	 * @param sqlFragment SQL 文の一部
	 */
	public synchronized void setSQLFragment(String columnName, String sqlFragment) {
		getUpdateValues().put(columnName, new SQLFragmentUpdateValue(sqlFragment));
		changed = true;
	}

	/**
	 * 指定されたカラムに、 UPDATE 文に組み込む SQL 文とそのプレースホルダの値を設定します。
	 *
	 * @param columnName 対象カラム
	 * @param sqlFragment SQL 文の一部
	 * @param value プレースホルダの値
	 */
	public synchronized void setSQLFragmentAndValue(
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
	 * <br>
	 * 値が更新されている場合、 {@link #update()} または {@link #commit()} を実行しても false には戻りません。
	 *
	 * @return 更新されている場合、 true
	 */
	public synchronized boolean isValueUpdated() {
		return changed;
	}

	@Override
	public TraversableNode getSubNode() {
		TraversableNode node = new TraversableNode();
		Relationship[] sub = relationship.getRelationships();
		for (Relationship relationship : sub) {
			node.add(new UpdatableDataObject(relationship, values));
		}
		return node;
	}

	/**
	 * 更新された値をデータベースに反映させるため、 UPDATE を実行します。
	 * <br>
	 * 更新された値が一件も無かった場合、このメソッドは何もせず false を返します。
	 *
	 * @return 更新された値が一件も無かった場合、 false
	 * @throws NullPrimaryKeyException このインスタンスの主キーが NULL の場合
	 */
	public boolean update() {
		return updateInternal(DataAccessHelper.getThreadStatement());
	}

	/**
	 * 更新された値をデータベースに反映させるため、 UPDATE をバッチ実行します。
	 * <br>
	 * 更新された値が一件も無かった場合、このメソッドは何もせず false を返します。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 * @throws NullPrimaryKeyException このインスタンスの主キーが NULL の場合
	 */
	public void update(BatchStatement statement) {
		updateInternal(new BatchStatementFacade(statement));
	}

	/**
	 * このインスタンスにセットされた更新値で、検索結果を上書きすることを、 {@link #update()} と同時に行うかどうかを設定します。
	 * <br>
	 * false をセットした場合、この更新が属するトランザクションのコミット時に {@link #commit()} が実行されます。
	 *
	 * @param autoCommit 自動コミットを行うかどうか
	 * @see jp.ats.blendee.jdbc.BTransaction#commit()
	 */
	public synchronized void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	/**
	 * {@link #setAutoCommit(boolean)} で設定された値を返します。
	 *
	 * @return 自動コミットを行うかどうか
	 */
	public synchronized boolean isAutoCommit() {
		return autoCommit;
	}

	/**
	 * このインスタンスにセットされた更新値で、検索結果を上書きします。
	 */
	@Override
	public synchronized void commit() {
		if (updateValues == null) return;

		if (updatedValues == null) updatedValues = new HashMap<>();
		updatedValues.putAll(updateValues);
		updateValues.clear();
	}

	/**
	 * このインスタンスにセットされた更新値を捨て、検索結果の値に戻します。
	 */
	@Override
	public synchronized void rollback() {
		if (updateValues != null) updateValues.clear();
	}

	@Override
	public synchronized void setValuesTo(Updater updater) {
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
	private synchronized boolean updateInternal(StatementFacade statement) {
		if (updateValues == null || updateValues.size() == 0) return false;

		UpdateDMLBuilder builder = new UpdateDMLBuilder(relationship.getResourceLocator());
		setValuesTo(builder);

		Column[] primaryKeyColumns = relationship.getPrimaryKeyColumns();
		if (!relationship.isRoot()) {
			//root でないと、 Condition を作る際に、チェックに引っかかるので
			//root の Relationship のカラムに変換しておく
			Relationship root = BContext.get(RelationshipFactory.class).getInstance(relationship.getResourceLocator());
			for (int i = 0; i < primaryKeyColumns.length; i++) {
				primaryKeyColumns[i] = root.getColumn(primaryKeyColumns[i].getName());
			}
		}

		builder.setCondition(PartialData.createCondition(primaryKeyColumns, getPrimaryKeyBinders()));

		statement.process(builder.toString(), builder);
		int result = statement.execute();

		if (autoCommit) {
			commit();
		} else {
			BContext.get(BlendeeManager.class).synchroniseWithCurrentTransaction(this);
		}

		if (result == 1 || result == BatchStatementFacade.DUMMY_RESULT) return true;

		throw new IllegalStateException("更新結果が " + result + " 件です");
	}

	private Map<String, UpdateValue> getUpdateValues() {
		if (updateValues == null) updateValues = new LinkedHashMap<>();
		return updateValues;
	}

	private static abstract class UpdateValue {

		abstract boolean executeEquals(Binder value);

		abstract void add(String columnName, Updater updater);

		abstract Binder getValue(String columnName);
	}

	private static class BinderUpdateValue extends UpdateValue {

		private final Binder value;

		private BinderUpdateValue(Binder value) {
			this.value = value;
		}

		@Override
		boolean executeEquals(Binder value) {
			return this.value.equals(value);
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
		boolean executeEquals(Binder value) {
			return false;
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
		boolean executeEquals(Binder value) {
			return false;
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
		public BResult getResult() {
			return null;
		}

		@Override
		public boolean isSelected(Column column) {
			return false;
		}
	}
}
