package org.blendee.util;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import org.blendee.internal.U;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Result;
import org.blendee.jdbc.ResultSetIterator;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.ColumnNameDataObjectBuilder;
import org.blendee.orm.DataObject;
import org.blendee.orm.DataObjectIterator;
import org.blendee.orm.NullPrimaryKeyException;
import org.blendee.selector.AnchorOptimizerFactory;
import org.blendee.selector.Optimizer;
import org.blendee.sql.Bindable;
import org.blendee.sql.Binder;
import org.blendee.sql.Criteria;
import org.blendee.sql.FromClause.JoinType;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.RuntimeIdFactory;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.SQLQueryBuilder;
import org.blendee.sql.ValueExtractorsConfigure;
import org.blendee.support.CriteriaContext;
import org.blendee.support.DataManipulationStatement;
import org.blendee.support.DataManipulationStatementBehavior;
import org.blendee.support.DataManipulator;
import org.blendee.support.DeleteStatementIntermediate;
import org.blendee.support.GroupByColumn;
import org.blendee.support.GroupByOfferFunction;
import org.blendee.support.GroupByRelationship;
import org.blendee.support.HavingColumn;
import org.blendee.support.HavingRelationship;
import org.blendee.support.InsertColumn;
import org.blendee.support.InsertOfferFunction;
import org.blendee.support.InsertRelationship;
import org.blendee.support.InsertStatementIntermediate;
import org.blendee.support.InstantOneToManyQuery;
import org.blendee.support.LogicalOperators;
import org.blendee.support.Many;
import org.blendee.support.OnClause;
import org.blendee.support.OnLeftColumn;
import org.blendee.support.OnLeftRelationship;
import org.blendee.support.OnRightColumn;
import org.blendee.support.OnRightRelationship;
import org.blendee.support.OneToManyQuery;
import org.blendee.support.OneToManyRelationship;
import org.blendee.support.OrderByColumn;
import org.blendee.support.OrderByOfferFunction;
import org.blendee.support.OrderByRelationship;
import org.blendee.support.Query;
import org.blendee.support.RightTable;
import org.blendee.support.Row;
import org.blendee.support.RowIterator;
import org.blendee.support.SQLDecorators;
import org.blendee.support.SelectColumn;
import org.blendee.support.SelectOfferFunction;
import org.blendee.support.SelectRelationship;
import org.blendee.support.SelectStatement;
import org.blendee.support.SelectStatementBehavior;
import org.blendee.support.SelectStatementBehavior.PlaybackQuery;
import org.blendee.support.Statement;
import org.blendee.support.TableFacade;
import org.blendee.support.TableFacadeColumn;
import org.blendee.support.TableFacadeContext;
import org.blendee.support.TableFacadeRelationship;
import org.blendee.support.UpdateColumn;
import org.blendee.support.UpdateRelationship;
import org.blendee.support.UpdateStatementIntermediate;
import org.blendee.support.WhereColumn;
import org.blendee.support.WhereRelationship;

/**
 * @author 千葉 哲嗣
 */
public class GenericTable
	implements TableFacade<Row>, SelectStatement, SQLDecorators, Query<GenericTable.Iterator, GenericTable.Row>, RightTable<GenericTable.OnRightRel> {

	private final TablePath tablePath;

	private final Relationship relationship$;

	private final List<SQLDecorator> decorators$ = new LinkedList<SQLDecorator>();

	/**
	 * 登録用コンストラクタです。
	 * @return {@link Row}
	 */
	public Row row() {
		return new Row(tablePath);
	}

	/**
	 * 参照、更新用コンストラクタです。<br>
	 * aggregate の検索結果からカラム名により値を取り込みます。
	 * @param result 値を持つ {@link Result}
	 * @return {@link Row}
	 */
	public Row row(Result result) {
		return new Row(tablePath, result);
	}

	/**
	 * 参照、更新用コンストラクタです。
	 * @param data 値を持つ {@link DataObject}
	 * @return {@link Row}
	 */
	public static Row row(DataObject data) {
		return new Row(data);
	}

	/**
	 * Row
	 */
	public static class Row implements org.blendee.support.Row {

		private final TablePath tablePath;

		private final DataObject data;

		private final Relationship relationship;

		/**
		 * 登録用コンストラクタです。
		 * @param tablePath 対象となるテーブル
		 */
		private Row(TablePath tablePath) {
			this.tablePath = tablePath;
			relationship = RelationshipFactory.getInstance().getInstance(tablePath);
			data = new DataObject(relationship);
		}

		/**
		 * 参照、更新用コンストラクタです。
		 * @param data 値を持つ {@link DataObject}
		 */
		private Row(DataObject data) {
			tablePath = data.getRelationship().getTablePath();
			relationship = RelationshipFactory.getInstance().getInstance(tablePath);
			this.data = data;
		}

		private Row(TablePath tablePath, Result result) {
			this.tablePath = tablePath;
			relationship = RelationshipFactory.getInstance().getInstance(tablePath);
			this.data = ColumnNameDataObjectBuilder.build(
				result,
				relationship,
				ContextManager.get(ValueExtractorsConfigure.class).getValueExtractors());
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
		public Map<String, Row> getRows() {
			Map<String, DataObject> source = data.getDataObjects();
			Map<String, Row> dest = new HashMap<>();
			source.forEach((key, value) -> dest.put(key, new Row(value)));

			return dest;
		}

		/**
		 * このレコードが参照しているレコードの Row を返します。<br>
		 * @param fkName 外部キー名
		 * @return 参照しているレコードの Row
		 */
		public Row tab(String fkName) {
			return new Row(data.getDataObject(fkName));
		}

		/**
		 * このレコードが参照しているレコードの Row を返します。<br>
		 * @param fkColumnNames 外部キーを構成するカラム名
		 * @return 参照しているレコードの Row
		 */
		public Row tab(String[] fkColumnNames) {
			return new Row(data.getDataObject(fkColumnNames));
		}
	}

	private static final TableFacadeContext<SelectCol> selectContext$ = (
		relationship,
		name) -> new SelectCol(relationship, name);

	private static final TableFacadeContext<GroupByCol> groupByContext$ = (
		relationship,
		name) -> new GroupByCol(relationship, name);

	private static final TableFacadeContext<OrderByCol> orderByContext$ = (
		relationship,
		name) -> new OrderByCol(relationship, name);

	private static final TableFacadeContext<InsertCol> insertContext$ = (
		relationship,
		name) -> new InsertCol(relationship, name);

	private static final TableFacadeContext<UpdateCol> updateContext$ = (
		relationship,
		name) -> new UpdateCol(relationship, name);

	private static final TableFacadeContext<WhereColumn<WhereLogicalOperators>> whereContext$ = TableFacadeContext
		.newWhereBuilder();

	private static final TableFacadeContext<HavingColumn<HavingLogicalOperators>> havingContext$ = TableFacadeContext
		.newHavingBuilder();

	private static final TableFacadeContext<OnLeftColumn<OnLeftLogicalOperators>> onLeftContext$ = TableFacadeContext
		.newOnLeftBuilder();

	private static final TableFacadeContext<OnRightColumn<OnRightLogicalOperators>> onRightContext$ = TableFacadeContext
		.newOnRightBuilder();

	private static final TableFacadeContext<WhereColumn<DMSWhereLogicalOperators>> dmsWhereContext$ = TableFacadeContext
		.newDMSWhereBuilder();

	/**
	 * WHERE 句 で使用する AND, OR です。
	 */
	public class WhereLogicalOperators implements LogicalOperators<WhereRel> {

		private WhereLogicalOperators() {}

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final WhereRel OR = new WhereRel(GenericTable.this, whereContext$, CriteriaContext.OR, null);

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final WhereRel AND = new WhereRel(GenericTable.this, whereContext$, CriteriaContext.AND, OR);

		@Override
		public WhereRel defaultOperator() {
			return AND;
		}
	}

	/**
	 * HAVING 句 で使用する AND, OR です。
	 */
	public class HavingLogicalOperators implements LogicalOperators<HavingRel> {

		private HavingLogicalOperators() {}

		/**
		 * HAVING 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final HavingRel OR = new HavingRel(GenericTable.this, havingContext$, CriteriaContext.OR, null);

		/**
		 * HAVING 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final HavingRel AND = new HavingRel(GenericTable.this, havingContext$, CriteriaContext.AND, OR);

		@Override
		public HavingRel defaultOperator() {
			return AND;
		}
	}

	/**
	 * ON 句 (LEFT) で使用する AND, OR です。
	 */
	public class OnLeftLogicalOperators implements LogicalOperators<OnLeftRel> {

		private OnLeftLogicalOperators() {}

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnLeftRel OR = new OnLeftRel(GenericTable.this, onLeftContext$, CriteriaContext.OR, null);

		/**
		 * ON 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnLeftRel AND = new OnLeftRel(GenericTable.this, onLeftContext$, CriteriaContext.AND, OR);

		@Override
		public OnLeftRel defaultOperator() {
			return AND;
		}
	}

	/**
	 * ON 句 (RIGHT) で使用する AND, OR です。
	 */
	public class OnRightLogicalOperators implements LogicalOperators<OnRightRel> {

		private OnRightLogicalOperators() {}

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnRightRel OR = new OnRightRel(GenericTable.this, onRightContext$, CriteriaContext.OR, null);

		/**
		 * ON 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final OnRightRel AND = new OnRightRel(GenericTable.this, onRightContext$, CriteriaContext.AND, OR);

		@Override
		public OnRightRel defaultOperator() {
			return AND;
		}
	}

	/**
	 * WHERE 句 で使用する AND, OR です。
	 */
	public class DMSWhereLogicalOperators implements LogicalOperators<DMSWhereRel> {

		private DMSWhereLogicalOperators() {}

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final DMSWhereRel OR = new DMSWhereRel(GenericTable.this, dmsWhereContext$, CriteriaContext.OR, null);

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeRelationship} です。
		 */
		public final DMSWhereRel AND = new DMSWhereRel(GenericTable.this, dmsWhereContext$, CriteriaContext.AND, OR);

		@Override
		public DMSWhereRel defaultOperator() {
			return AND;
		}
	}

	private OnRightLogicalOperators onRightOperators$;

	private RuntimeId id$;

	private SelectBehavior selectBehavior$;

	private SelectBehavior selectBehavior() {
		return selectBehavior$ == null ? (selectBehavior$ = new SelectBehavior()) : selectBehavior$;
	}

	@Override
	public RuntimeId getRuntimeId() {
		return id$ == null ? (id$ = RuntimeIdFactory.getRuntimeInstance()) : id$;
	}

	private class SelectBehavior
		extends SelectStatementBehavior<SelectRel, GroupByRel, WhereRel, HavingRel, OrderByRel, OnLeftRel> {

		private SelectBehavior() {
			super(tablePath, getRuntimeId(), GenericTable.this);
		}

		@Override
		protected SelectRel newSelect() {
			return new SelectRel(GenericTable.this, selectContext$);
		}

		@Override
		protected GroupByRel newGroupBy() {
			return new GroupByRel(GenericTable.this, groupByContext$);
		}

		@Override
		protected OrderByRel newOrderBy() {
			return new OrderByRel(GenericTable.this, orderByContext$);
		}

		@Override
		protected WhereLogicalOperators newWhereOperators() {
			return new WhereLogicalOperators();
		}

		@Override
		protected HavingLogicalOperators newHavingOperators() {
			return new HavingLogicalOperators();
		}

		@Override
		protected OnLeftLogicalOperators newOnLeftOperators() {
			return new OnLeftLogicalOperators();
		}
	}

	private DMSBehavior dmsBehavior$;

	private DMSBehavior dmsBehavior() {
		return dmsBehavior$ == null ? (dmsBehavior$ = new DMSBehavior()) : dmsBehavior$;
	}

	private class DMSBehavior extends DataManipulationStatementBehavior<InsertRel, UpdateRel, DMSWhereRel> {

		public DMSBehavior() {
			super(tablePath, GenericTable.this.getRuntimeId(), GenericTable.this);
		}

		@Override
		protected InsertRel newInsert() {
			return new InsertRel(GenericTable.this, insertContext$);
		}

		@Override
		protected UpdateRel newUpdate() {
			return new UpdateRel(GenericTable.this, updateContext$);
		}

		@Override
		protected LogicalOperators<DMSWhereRel> newWhereOperators() {
			return new DMSWhereLogicalOperators();
		}
	}

	/**
	 * このクラスのインスタンスを生成します。<br>
	 * インスタンスは ID として、引数で渡された id を使用します。<br>
	 * フィールド定義の必要がなく、簡易に使用できますが、 ID は呼び出し側クラス内で一意である必要があります。
	 * @param id {@link Query} を使用するクラス内で一意の ID
	 * @param tablePath 検索対象テーブル
	 * @return このクラスのインスタンス
	 */
	public static GenericTable of(String id, TablePath tablePath) {
		if (!U.presents(id))
			throw new IllegalArgumentException("id が空です");

		return new GenericTable(tablePath, getUsing(new Throwable().getStackTrace()[1]), id);
	}

	/**
	 * 空のインスタンスを生成します。
	 * @param tablePath 検索対象テーブル
	 */
	public GenericTable(TablePath tablePath) {
		this.tablePath = tablePath;
		relationship$ = RelationshipFactory.getInstance().getInstance(tablePath);
	}

	/**
	 * このクラスのインスタンスを生成します。<br>
	 * このコンストラクタで生成されたインスタンス の SELECT 句で使用されるカラムは、 パラメータの {@link Optimizer} に依存します。
	 * @param optimizer SELECT 句を決定する
	 */
	public GenericTable(Optimizer optimizer) {
		this(optimizer.getTablePath());
		selectBehavior().setOptimizer(Objects.requireNonNull(optimizer));
	}

	private GenericTable(TablePath tablePath, Class<?> using, String id) {
		this(tablePath);
		selectBehavior().setOptimizer(
			ContextManager.get(AnchorOptimizerFactory.class).getInstance(id, getRuntimeId(), tablePath, using));
	}

	@Override
	public Row createRow(DataObject data) {
		return new Row(data);
	}

	@Override
	public TablePath getTablePath() {
		return tablePath;
	}

	/**
	 *  {@link DataObjectIterator} を {@link RowIterator} に変換します。
	 * @param base 変換される {@link DataObjectIterator}
	 * @return {@link RowIterator}
	 */
	public Iterator wrap(DataObjectIterator base) {
		return new Iterator(base);
	}

	/**
	 * Iterator クラスです。
	 */
	public class Iterator extends RowIterator<Row> {

		/**
		 * 唯一のコンストラクタです。
		 * @param iterator
		 */
		private Iterator(DataObjectIterator iterator) {
			super(iterator);
		}

		@Override
		public Row next() {
			return createRow(nextDataObject());
		}
	}

	/**
	 * この {@link SelectStatement} のテーブルを表す {@link TableFacadeRelationship} を参照するためのインスタンスです。
	 * @return rel
	 */
	public ExtRel<TableFacadeColumn, Void> rel() {
		return new ExtRel<>(this, TableFacadeContext.OTHER, CriteriaContext.NULL);
	}

	/**
	 * SELECT 句を記述します。
	 * @param function
	 * @return この {@link SelectStatement}
	 */
	public GenericTable SELECT(SelectOfferFunction<SelectRel> function) {
		selectBehavior().SELECT(function);
		return this;
	}

	/**
	 * DISTINCT を使用した SELECT 句を記述します。
	 * @param function
	 * @return この {@link SelectStatement}
	 */
	public GenericTable SELECT_DISTINCT(SelectOfferFunction<SelectRel> function) {
		selectBehavior().SELECT_DISTINCT(function);
		return this;
	}

	/**
	 * COUNT(*) を使用した SELECT 句を記述します。
	 * @return この {@link SelectStatement}
	 */
	public GenericTable SELECT_COUNT() {
		selectBehavior().SELECT_COUNT();
		return this;
	}

	/**
	 * GROUP BY 句を記述します。
	 * @param function
	 * @return この {@link SelectStatement}
	 */
	public GenericTable GROUP_BY(GroupByOfferFunction<GroupByRel> function) {
		selectBehavior().GROUP_BY(function);
		return this;
	}

	/**
	 * WHERE 句を記述します。
	 * @param consumers
	 * @return この {@link SelectStatement}
	 */
	@SafeVarargs
	public final GenericTable WHERE(Consumer<WhereRel>... consumers) {
		selectBehavior().WHERE(consumers);
		return this;
	}

	/**
	 * WHERE 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return {@link Criteria}
	 */
	public Criteria createWhereCriteria(Consumer<WhereRel> consumer) {
		return selectBehavior().createWhereCriteria(consumer);
	}

	/**
	 * HAVING 句を記述します。
	 * @param consumers
	 * @return この {@link SelectStatement}
	 */
	@SafeVarargs
	public final GenericTable HAVING(Consumer<HavingRel>... consumers) {
		selectBehavior().HAVING(consumers);
		return this;
	}

	/**
	 * HAVING 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return {@link Criteria}
	 */
	public Criteria createHavingCriteria(Consumer<HavingRel> consumer) {
		return selectBehavior().createHavingCriteria(consumer);
	}

	/**
	 * このクエリに INNER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftRel, R, GenericTable> INNER_JOIN(RightTable<R> right) {
		return selectBehavior().INNER_JOIN(right, this);
	}

	/**
	 * このクエリに LEFT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftRel, R, GenericTable> LEFT_OUTER_JOIN(RightTable<R> right) {
		return selectBehavior().LEFT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに RIGHT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftRel, R, GenericTable> RIGHT_OUTER_JOIN(RightTable<R> right) {
		return selectBehavior().RIGHT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに FULL OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightRelationship> OnClause<OnLeftRel, R, GenericTable> FULL_OUTER_JOIN(RightTable<R> right) {
		return selectBehavior().FULL_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに CROSS JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return この {@link SelectStatement}
	 */
	public <R extends OnRightRelationship> GenericTable CROSS_JOIN(RightTable<R> right) {
		selectBehavior().CROSS_JOIN(right, this);
		return this;
	}

	/**
	 * UNION するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param select UNION 対象
	 * @return この {@link SelectStatement}
	 */
	public GenericTable UNION(SelectStatement select) {
		selectBehavior().UNION(select);
		return this;
	}

	/**
	 * UNION ALL するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param select UNION ALL 対象
	 * @return この {@link SelectStatement}
	 */
	public GenericTable UNION_ALL(SelectStatement select) {
		selectBehavior().UNION_ALL(select);
		return this;
	}

	/**
	 * ORDER BY 句を記述します。
	 * @param function
	 * @return この {@link SelectStatement}
	 */
	public GenericTable ORDER_BY(OrderByOfferFunction<OrderByRel> function) {
		selectBehavior().ORDER_BY(function);
		return this;
	}

	@Override
	public boolean hasWhereClause() {
		return selectBehavior().hasWhereClause();
	}

	/**
	 * 新規に GROUP BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return {@link SelectStatement} 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public GenericTable groupBy(GroupByClause clause) {
		selectBehavior().setGroupByClause(clause);
		return this;
	}

	/**
	 * 新規に ORDER BY 句をセットします。
	 * @param clause 新 ORDER BY 句
	 * @return {@link SelectStatement} 自身
	 * @throws IllegalStateException 既に ORDER BY 句がセットされている場合
	 */
	public GenericTable orderBy(OrderByClause clause) {
		selectBehavior().setOrderByClause(clause);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たな条件を AND 結合します。<br>
	 * AND 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria AND 結合する新条件
	 * @return {@link SelectStatement} 自身
	 */
	public GenericTable and(Criteria criteria) {
		selectBehavior().and(criteria);
		return this;
	}

	/**
	 * 現時点の WHERE 句に新たな条件を OR 結合します。<br>
	 * OR 結合する対象がなければ、新条件としてセットされます。
	 * @param criteria OR 結合する新条件
	 * @return {@link SelectStatement} 自身
	 */
	public GenericTable or(Criteria criteria) {
		selectBehavior().or(criteria);
		return this;
	}

	/**
	 * 生成された SQL 文を加工する {SQLDecorator} を設定します。
	 * @param decorators {@link SQLDecorator}
	 * @return {@link SelectStatement} 自身
	 */
	@Override
	public GenericTable apply(SQLDecorator... decorators) {
		for (SQLDecorator decorator : decorators) {
			this.decorators$.add(decorator);
		}

		return this;
	}

	@Override
	public Optimizer getOptimizer() {
		return selectBehavior().getOptimizer();
	}

	@Override
	public GroupByClause getGroupByClause() {
		return selectBehavior().getGroupByClause();
	}

	@Override
	public OrderByClause getOrderByClause() {
		return selectBehavior().getOrderByClause();
	}

	@Override
	public Criteria getWhereClause() {
		return selectBehavior().getWhereClause();
	}

	@Override
	public Relationship getRootRealtionship() {
		return relationship$;
	}

	@Override
	public LogicalOperators<WhereRel> getWhereLogicalOperators() {
		return selectBehavior().whereOperators();
	}

	@Override
	public LogicalOperators<HavingRel> getHavingLogicalOperators() {
		return selectBehavior().havingOperators();
	}

	@Override
	public LogicalOperators<OnLeftRel> getOnLeftLogicalOperators() {
		return selectBehavior().onLeftOperators();
	}

	@Override
	public OnRightLogicalOperators getOnRightLogicalOperators() {
		return onRightOperators$ == null ? (onRightOperators$ = new OnRightLogicalOperators()) : onRightOperators$;
	}

	@Override
	public SQLDecorator[] decorators() {
		return decorators$.toArray(new SQLDecorator[decorators$.size()]);
	}

	@Override
	public Iterator execute() {
		selectBehavior().checkRowMode();
		return wrap(selectBehavior().query().execute());
	}

	@Override
	public Optional<Row> fetch(String... primaryKeyMembers) {
		selectBehavior().checkRowMode();
		return selectBehavior().query().fetch(primaryKeyMembers).map(o -> createRow(o));
	}

	@Override
	public Optional<Row> fetch(Number... primaryKeyMembers) {
		selectBehavior().checkRowMode();
		return selectBehavior().query().fetch(primaryKeyMembers).map(o -> createRow(o));
	}

	@Override
	public Optional<Row> fetch(Bindable... primaryKeyMembers) {
		selectBehavior().checkRowMode();
		return selectBehavior().query().fetch(primaryKeyMembers).map(o -> createRow(o));
	}

	@Override
	public int count() {
		selectBehavior().checkRowMode();
		return selectBehavior().query().count();
	}

	@Override
	public ComposedSQL toCountSQL() {
		selectBehavior().checkRowMode();
		return selectBehavior().query().toCountSQL();
	}

	@Override
	public void aggregate(Consumer<BResultSet> consumer) {
		selectBehavior().quitRowMode();
		org.blendee.support.Query.super.aggregate(consumer);
	}

	@Override
	public <T> T aggregateAndGet(Function<BResultSet, T> function) {
		selectBehavior().quitRowMode();
		return org.blendee.support.Query.super.aggregateAndGet(function);
	}

	@Override
	public ResultSetIterator aggregate() {
		selectBehavior().quitRowMode();
		return org.blendee.support.Query.super.aggregate();
	}

	@Override
	public String sql() {
		return selectBehavior().composeSQL().sql();
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		return selectBehavior().composeSQL().complement(done, statement);
	}

	@Override
	public Query reproduce(Object... placeHolderValues) {
		return new Query(selectBehavior().query().reproduce(placeHolderValues));
	}

	@Override
	public Query reproduce() {
		return new Query(selectBehavior().query().reproduce());
	}

	@Override
	public Binder[] currentBinders() {
		return selectBehavior().query().currentBinders();
	}

	@Override
	public void joinTo(SQLQueryBuilder builder, JoinType joinType, Criteria onCriteria) {
		selectBehavior().joinTo(builder, joinType, onCriteria);
	}

	@Override
	public SQLQueryBuilder toSQLQueryBuilder() {
		return selectBehavior().buildBuilder();
	}

	@Override
	public void forSubquery(boolean forSubquery) {
		selectBehavior().forSubquery(forSubquery);
	}

	/**
	 * 現在保持している SELECT 文の WHERE 句をリセットします。
	 * @return このインスタンス
	 */
	public GenericTable resetWhere() {
		selectBehavior().resetWhere();
		return this;
	}

	/**
	 * 現在保持している HAVING 句をリセットします。
	 * @return このインスタンス
	 */
	public GenericTable resetHaving() {
		selectBehavior().resetHaving();
		return this;
	}

	/**
	 * 現在保持している SELECT 句をリセットします。
	 * @return このインスタンス
	 */
	public GenericTable resetSelect() {
		selectBehavior().resetSelect();
		return this;
	}

	/**
	 * 現在保持している GROUP BY 句をリセットします。
	 * @return このインスタンス
	 */
	public GenericTable resetGroupBy() {
		selectBehavior().resetGroupBy();
		return this;
	}

	/**
	 * 現在保持している ORDER BY 句をリセットします。
	 * @return このインスタンス
	 */
	public GenericTable resetOrderBy() {
		selectBehavior().resetOrderBy();
		return this;
	}

	/**
	 * 現在保持している UNION をリセットします。
	 * @return このインスタンス
	 */
	public GenericTable resetUnions() {
		selectBehavior().resetUnions();
		return this;
	}

	/**
	 * 現在保持している JOIN をリセットします。
	 * @return このインスタンス
	 */
	public GenericTable resetJoins() {
		selectBehavior().resetJoins();
		return this;
	}

	/**
	 * 現在保持している INSERT 文のカラムをリセットします。
	 * @return このインスタンス
	 */
	public GenericTable resetInsert() {
		dmsBehavior().resetInsert();
		return this;
	}

	/**
	 * 現在保持している UPDATE 文の更新要素をリセットします。
	 * @return このインスタンス
	 */
	public GenericTable resetUpdate() {
		dmsBehavior().resetUpdate();
		return this;
	}

	/**
	 * 現在保持している SET 文の更新要素をリセットします。
	 * @return このインスタンス
	 */
	public GenericTable resetDelete() {
		dmsBehavior().resetDelete();
		return this;
	}

	/**
	 * 現在保持している {@link SQLDecorator} をリセットします。
	 * @return このインスタンス
	 */
	public GenericTable resetDecorators() {
		decorators$.clear();
		return this;
	}

	/**
	 * 現在保持している条件、並び順をリセットします。
	 * @return このインスタンス
	 */
	public GenericTable reset() {
		selectBehavior().reset();
		dmsBehavior().reset();
		resetDecorators();
		return this;
	}

	@Override
	public void quitRowMode() {
		selectBehavior().quitRowMode();
	}

	@Override
	public boolean rowMode() {
		return selectBehavior().rowMode();
	}

	@Override
	public Query query() {
		return new Query(selectBehavior().query());
	}

	@Override
	public OnRightRel joint() {
		return getOnRightLogicalOperators().AND;
	}

	@Override
	public SelectStatement getSelectStatement() {
		return this;
	}

	/**
	 * INSERT 文を生成します。
	 * @param function function
	 * @return {@link InsertStatementIntermediate}
	 */
	public InsertStatementIntermediate INSERT(InsertOfferFunction<InsertRel> function) {
		return dmsBehavior().INSERT(function);
	}

	/**
	 * INSERT 文を生成します。<br>
	 * このインスタンスが現時点で保持しているカラムを使用します。<br>
	 * 以前使用した VALUES の値はクリアされています。
	 * @return {@link InsertStatementIntermediate}
	 */
	public InsertStatementIntermediate INSERT() {
		return dmsBehavior().INSERT();
	}

	/**
	 * INSERT 文を生成します。
	 * @param function function
	 * @param select select
	 * @return {@link InsertStatementIntermediate}
	 */
	public DataManipulator INSERT(InsertOfferFunction<InsertRel> function, SelectStatement select) {
		return dmsBehavior().INSERT(function, select);
	}

	/**
	 * INSERT 文を生成します。
	 * @param select select
	 * @return {@link InsertStatementIntermediate}
	 */
	public DataManipulator INSERT(SelectStatement select) {
		return dmsBehavior().INSERT(select);
	}

	/**
	 * UPDATE 文を生成します。
	 * @param consumer
	 * @return {@link UpdateStatementIntermediate}
	 */
	public UpdateStatementIntermediate<DMSWhereRel> UPDATE(Consumer<UpdateRel> consumer) {
		return dmsBehavior().UPDATE(consumer);
	}

	/**
	 * UPDATE 文を生成します。
	 * @return {@link UpdateStatementIntermediate}
	 */
	public UpdateStatementIntermediate<DMSWhereRel> UPDATE() {
		return dmsBehavior().UPDATE();
	}

	/**
	 * DELETE 文を生成します。
	 * @return {@link DeleteStatementIntermediate}
	 */
	public final DeleteStatementIntermediate<DMSWhereRel> DELETE() {
		return dmsBehavior().DELETE();
	}

	@Override
	public String toString() {
		return selectBehavior().toString();
	}

	private static Class<?> getUsing(StackTraceElement element) {
		try {
			return Class.forName(element.getClassName());
		} catch (Exception e) {
			throw new IllegalStateException(e.toString());
		}
	}

	/**
	 * 自動生成された {@link TableFacadeRelationship} の実装クラスです。<br>
	 * 条件として使用できるカラムを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class Rel<T, M> implements TableFacadeRelationship {

		private final TableFacadeContext<T> builder$;

		private final GenericTable table$;

		private final CriteriaContext context$;

		private final TableFacadeRelationship parent$;

		private final String fkName$;

		/**
		 * 直接使用しないでください。
		 * @param builder$ builder
		 * @param parent$ parent
		 * @param fkName$ fkName
		 */
		public Rel(TableFacadeContext<T> builder$, TableFacadeRelationship parent$, String fkName$) {
			this.builder$ = builder$;
			table$ = null;
			context$ = null;
			this.parent$ = parent$;
			this.fkName$ = fkName$;
		}

		private Rel(GenericTable table$, TableFacadeContext<T> builder$, CriteriaContext context$) {
			this.table$ = table$;
			this.builder$ = builder$;
			this.context$ = context$;
			parent$ = null;
			fkName$ = null;
		}

		/**
		 * @param name カラム名
		 * @return 使用されるカラムのタイプにあった型
		 */
		public T col(String name) {
			return builder$.buildColumn(this, name);
		}

		@Override
		public CriteriaContext getContext() {
			if (context$ == null)
				return parent$.getContext();

			return context$;
		}

		@Override
		public Relationship getRelationship() {
			if (parent$ != null) {
				return parent$.getRelationship().find(fkName$);
			}

			return table$.relationship$;
		}

		@Override
		public SelectStatement getSelectStatement() {
			if (table$ != null)
				return table$;
			return parent$.getSelectStatement();
		}

		@Override
		public DataManipulationStatement getDataManipulationStatement() {
			if (table$ != null)
				return table$.dmsBehavior();
			return parent$.getDataManipulationStatement();
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof TableFacadeRelationship))
				return false;
			return getRelationship().equals(((TableFacadeRelationship) o).getRelationship());
		}

		@Override
		public int hashCode() {
			return getRelationship().hashCode();
		}

		@Override
		public OneToManyRelationship getOneToManyRelationship() {
			return new OneToManyRelationship(
				parent$ == null ? null : parent$.getOneToManyRelationship(),
				Rel.this.getRelationship(),
				data -> new Row(data),
				table$ != null ? table$.id$ : parent$.getSelectStatement().getRuntimeId());
		}
	}

	/**
	 * 自動生成された {@link TableFacadeRelationship} の実装クラスです。<br>
	 * 条件として使用できるカラムと、参照しているテーブルを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class ExtRel<T, M> extends Rel<T, M> {

		private final TableFacadeContext<T> builder$;

		/**
		 * 直接使用しないでください。
		 * @param builder$ builder
		 * @param parent$ parent
		 * @param fkName$ fkName
		 */
		public ExtRel(
			TableFacadeContext<T> builder$,
			TableFacadeRelationship parent$,
			String fkName$) {
			super(builder$, parent$, fkName$);
			this.builder$ = builder$;
		}

		private ExtRel(GenericTable table$, TableFacadeContext<T> builder$, CriteriaContext context$) {
			super(table$, builder$, context$);
			this.builder$ = builder$;
		}

		/**
		 * この {@link TableFacadeRelationship} が表すテーブルの Row を一とし、多をもつ検索結果を生成する {@link OneToManyQuery} を返します。
		 * @return {@link OneToManyQuery}
		 */
		public OneToManyQuery<Row, M> intercept() {
			if (super.table$ != null)
				throw new IllegalStateException("このインスタンスでは直接使用することはできません");
			if (!getSelectStatement().rowMode())
				throw new IllegalStateException("集計モードでは実行できない処理です");
			return new InstantOneToManyQuery<>(this, getSelectStatement().decorators());
		}

		/**
		 * @param fkName 外部キー名
		 * @return 参照先の {@link ExtRel}
		 */
		public ExtRel<T, Many<Row, M>> tab(String fkName) {
			return new ExtRel<T, Many<Row, M>>(builder$, this, fkName);
		}
	}

	/**
	 * SELECT 句用
	 */
	public static class SelectRel extends ExtRel<SelectCol, Void> implements SelectRelationship {

		private SelectRel(GenericTable table$, TableFacadeContext<SelectCol> builder$) {
			super(table$, builder$, CriteriaContext.NULL);
		}
	}

	/**
	 * SELECT 文 WHERE 句用
	 */
	public static class WhereRel extends ExtRel<WhereColumn<WhereLogicalOperators>, Void> implements WhereRelationship {

		/**
		 * 条件接続 OR
		 */
		public final WhereRel OR;

		private WhereRel(
			GenericTable table$,
			TableFacadeContext<WhereColumn<WhereLogicalOperators>> builder$,
			CriteriaContext context$,
			WhereRel or$) {
			super(table$, builder$, context$);
			OR = or$ == null ? this : or$;
		}

		@Override
		public Statement getStatement() {
			return getSelectStatement();
		}
	}

	/**
	 * GROUB BY 句用
	 */
	public static class GroupByRel extends ExtRel<GroupByCol, Void> implements GroupByRelationship {

		private GroupByRel(GenericTable table$, TableFacadeContext<GroupByCol> builder$) {
			super(table$, builder$, CriteriaContext.NULL);
		}
	}

	/**
	 * HAVING 句用
	 */
	public static class HavingRel extends ExtRel<HavingColumn<HavingLogicalOperators>, Void>
		implements HavingRelationship {

		/**
		 * 条件接続 OR
		 */
		public final HavingRel OR;

		private HavingRel(
			GenericTable table$,
			TableFacadeContext<HavingColumn<HavingLogicalOperators>> builder$,
			CriteriaContext context$,
			HavingRel or$) {
			super(table$, builder$, context$);
			OR = or$ == null ? this : or$;
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByRel extends ExtRel<OrderByCol, Void> implements OrderByRelationship {

		private OrderByRel(GenericTable table$, TableFacadeContext<OrderByCol> builder$) {
			super(table$, builder$, CriteriaContext.NULL);
		}

		@Override
		public OrderByClause getOrderByClause() {
			return getSelectStatement().getOrderByClause();
		}
	}

	/**
	 * ON 句 (LEFT) 用
	 */
	public static class OnLeftRel extends ExtRel<OnLeftColumn<OnLeftLogicalOperators>, Void>
		implements OnLeftRelationship {

		/**
		 * 条件接続 OR
		 */
		public final OnLeftRel OR;

		private OnLeftRel(
			GenericTable table$,
			TableFacadeContext<OnLeftColumn<OnLeftLogicalOperators>> builder$,
			CriteriaContext context$,
			OnLeftRel or$) {
			super(table$, builder$, context$);
			OR = or$ == null ? this : or$;
		}
	}

	/**
	 * ON 句 (RIGHT) 用
	 */
	public static class OnRightRel extends Rel<OnRightColumn<OnRightLogicalOperators>, Void>
		implements OnRightRelationship {

		/**
		 * 条件接続 OR
		 */
		public final OnRightRel OR;

		private OnRightRel(
			GenericTable table$,
			TableFacadeContext<OnRightColumn<OnRightLogicalOperators>> builder$,
			CriteriaContext context$,
			OnRightRel or$) {
			super(table$, builder$, context$);
			OR = or$ == null ? this : or$;
		}
	}

	/**
	 * INSERT 用
	 */
	public static class InsertRel extends Rel<InsertCol, Void> implements InsertRelationship {

		private InsertRel(GenericTable table$, TableFacadeContext<InsertCol> builder$) {
			super(table$, builder$, CriteriaContext.NULL);
		}
	}

	/**
	 * UPDATE 用
	 */
	public static class UpdateRel extends Rel<UpdateCol, Void> implements UpdateRelationship {

		private UpdateRel(GenericTable table$, TableFacadeContext<UpdateCol> builder$) {
			super(table$, builder$, CriteriaContext.NULL);
		}
	}

	/**
	 * UPDATE, DELETE 文 WHERE 句用
	 */
	public static class DMSWhereRel extends Rel<WhereColumn<DMSWhereLogicalOperators>, Void>
		implements WhereRelationship {

		/**
		 * 条件接続 OR
		 */
		public final DMSWhereRel OR;

		private DMSWhereRel(
			GenericTable table$,
			TableFacadeContext<WhereColumn<DMSWhereLogicalOperators>> builder$,
			CriteriaContext context$,
			DMSWhereRel or$) {
			super(table$, builder$, context$);
			OR = or$ == null ? this : or$;
		}

		@Override
		public Statement getStatement() {
			return getDataManipulationStatement();
		}
	}

	/**
	 * SELECT 句用
	 */
	public static class SelectCol extends SelectColumn {

		private SelectCol(TableFacadeRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * GROUP BY 句用
	 */
	public static class GroupByCol extends GroupByColumn {

		private GroupByCol(TableFacadeRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByCol extends OrderByColumn {

		private OrderByCol(TableFacadeRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * INSERT 文用
	 */
	public static class InsertCol extends InsertColumn {

		private InsertCol(TableFacadeRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * UPDATE 文用
	 */
	public static class UpdateCol extends UpdateColumn {

		private UpdateCol(TableFacadeRelationship relationship, String name) {
			super(relationship, name);
		}
	}

	/**
	 * Query
	 */
	public class Query implements org.blendee.support.Query<Iterator, Row> {

		private final PlaybackQuery inner;

		private Query(PlaybackQuery inner) {
			this.inner = inner;
		}

		@Override
		public Iterator execute() {
			return wrap(inner.execute());
		}

		@Override
		public Optional<Row> fetch(Bindable... primaryKeyMembers) {
			return inner.fetch(primaryKeyMembers).map(object -> createRow(object));
		}

		@Override
		public int count() {
			return inner.count();
		}

		@Override
		public ComposedSQL toCountSQL() {
			return inner.toCountSQL();
		}

		@Override
		public boolean rowMode() {
			return inner.rowMode();
		}

		@Override
		public String sql() {
			return inner.sql();
		}

		@Override
		public int complement(int done, BPreparedStatement statement) {
			return inner.complement(done, statement);
		}

		@Override
		public Query reproduce(Object... placeHolderValues) {
			return new Query(inner.reproduce(placeHolderValues));
		}

		@Override
		public Query reproduce() {
			return new Query(inner.reproduce());
		}

		@Override
		public Binder[] currentBinders() {
			return inner.currentBinders();
		}
	}
}
