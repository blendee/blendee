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

import org.blendee.assist.AssistColumn;
import org.blendee.assist.CriteriaAnyColumn;
import org.blendee.assist.CriteriaAssistColumn;
import org.blendee.assist.CriteriaContext;
import org.blendee.assist.DataManipulationStatement;
import org.blendee.assist.DataManipulationStatementBehavior;
import org.blendee.assist.DataManipulator;
import org.blendee.assist.DeleteStatementIntermediate;
import org.blendee.assist.GroupByClauseAssist;
import org.blendee.assist.GroupByColumn;
import org.blendee.assist.GroupByOfferFunction;
import org.blendee.assist.HavingClauseAssist;
import org.blendee.assist.HavingColumn;
import org.blendee.assist.Helper;
import org.blendee.assist.InsertClauseAssist;
import org.blendee.assist.InsertColumn;
import org.blendee.assist.InsertOfferFunction;
import org.blendee.assist.InsertStatementIntermediate;
import org.blendee.assist.InstantOneToManyQuery;
import org.blendee.assist.ListGroupByClauseAssist;
import org.blendee.assist.ListInsertClauseAssist;
import org.blendee.assist.ListOrderByClauseAssist;
import org.blendee.assist.ListSelectClauseAssist;
import org.blendee.assist.ListUpdateClauseAssist;
import org.blendee.assist.LogicalOperators;
import org.blendee.assist.Many;
import org.blendee.assist.OnClause;
import org.blendee.assist.OnLeftClauseAssist;
import org.blendee.assist.OnLeftColumn;
import org.blendee.assist.OnRightClauseAssist;
import org.blendee.assist.OnRightColumn;
import org.blendee.assist.OneToManyBehavior;
import org.blendee.assist.OneToManyQuery;
import org.blendee.assist.OrderByClauseAssist;
import org.blendee.assist.OrderByColumn;
import org.blendee.assist.OrderByOfferFunction;
import org.blendee.assist.Query;
import org.blendee.assist.RightTable;
import org.blendee.assist.Row;
import org.blendee.assist.RowIterator;
import org.blendee.assist.SQLDecorators;
import org.blendee.assist.SelectClauseAssist;
import org.blendee.assist.SelectColumn;
import org.blendee.assist.SelectOfferFunction;
import org.blendee.assist.SelectStatement;
import org.blendee.assist.SelectStatementBehavior;
import org.blendee.assist.SelectStatementBehavior.PlaybackQuery;
import org.blendee.assist.Statement;
import org.blendee.assist.TableFacade;
import org.blendee.assist.TableFacadeAssist;
import org.blendee.assist.TableFacadeColumn;
import org.blendee.assist.TableFacadeContext;
import org.blendee.assist.UpdateClauseAssist;
import org.blendee.assist.UpdateColumn;
import org.blendee.assist.UpdateStatementIntermediate;
import org.blendee.assist.Vargs;
import org.blendee.assist.WhereClauseAssist;
import org.blendee.assist.WhereColumn;
import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.Batch;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Result;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.ColumnNameDataObjectBuilder;
import org.blendee.orm.DataObject;
import org.blendee.orm.DataObjectIterator;
import org.blendee.orm.NullPrimaryKeyException;
import org.blendee.orm.SelectContext;
import org.blendee.sql.Bindable;
import org.blendee.sql.Binder;
import org.blendee.sql.Criteria;
import org.blendee.sql.FromClause.JoinType;
import org.blendee.sql.GroupByClause;
import org.blendee.sql.MultiColumn;
import org.blendee.sql.OrderByClause;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.RuntimeIdFactory;
import org.blendee.sql.SQLDecorator;
import org.blendee.sql.SQLQueryBuilder;
import org.blendee.sql.ValueExtractorsConfigure;

/**
 * @author 千葉 哲嗣
 */
public class GenericTable
	implements TableFacade<Row>, SelectStatement, SQLDecorators, Query<GenericTable.Iterator, GenericTable.Row>, RightTable<GenericTable.OnRightAssist> {

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
	public static class Row implements org.blendee.assist.Row {

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
		 * 指定されたカラムの値を文字列として返します。
		 * @param columnName カラム名
		 * @return カラムの値
		 */
		public Optional<String> string(String columnName) {
			return Optional.ofNullable(data.getString(columnName));
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
		 * 指定されたカラムの値を {@link Timestamp} として返します。
		 * @param columnName カラム名
		 * @return カラムの値
		 */
		public Optional<Timestamp> timestamp(String columnName) {
			return Optional.ofNullable(data.getTimestamp(columnName));
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
		 * 指定されたカラムの値を {@link BigDecimal} として返します。
		 * @param columnName カラム名
		 * @return カラムの値
		 */
		public Optional<BigDecimal> bigDecimal(String columnName) {
			return Optional.ofNullable(data.getBigDecimal(columnName));
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
		 * 指定されたカラムの値を {@link UUID} として返します。
		 * @param columnName カラム名
		 * @return カラムの値
		 */
		public Optional<UUID> UUID(String columnName) {
			return Optional.ofNullable(data.getUUID(columnName));
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
		 * 指定されたカラムの値を {@link Object} として返します。
		 * @param columnName カラム名
		 * @return カラムの値
		 */
		public Optional<Object> object(String columnName) {
			return Optional.ofNullable(data.getObject(columnName));
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
		 * 指定されたカラムの値をバイトの配列として返します。
		 * @param columnName カラム名
		 * @return カラムの値
		 */
		public Optional<byte[]> bytes(String columnName) {
			return Optional.ofNullable(data.getBytes(columnName));
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
		 * 指定されたカラムの値を {@link Blob} として返します。
		 * @param columnName カラム名
		 * @return カラムの値
		 */
		public Optional<Blob> blob(String columnName) {
			return Optional.ofNullable(data.getBlob(columnName));
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
		 * 指定されたカラムの値を {@link Clob} として返します。
		 * @param columnName カラム名
		 * @return カラムの値
		 */
		public Optional<Clob> clob(String columnName) {
			return Optional.ofNullable(data.getClob(columnName));
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
		 * @param batch バッチ実行を依頼する {@link Batch}
		 * @throws NullPrimaryKeyException このインスタンスの主キーが NULL の場合
		 */
		@Override
		public void update(Batch batch) {
			data.update(batch);
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
			var source = data.getDataObjects();
			var dest = new HashMap<String, Row>();
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
		assist,
		name) -> new SelectCol(assist, name);

	private static final TableFacadeContext<GroupByCol> groupByContext$ = (
		assist,
		name) -> new GroupByCol(assist, name);

	private static final TableFacadeContext<OrderByCol> orderByContext$ = (
		assist,
		name) -> new OrderByCol(assist, name);

	private static final TableFacadeContext<InsertCol> insertContext$ = (
		assist,
		name) -> new InsertCol(assist, name);

	private static final TableFacadeContext<UpdateCol> updateContext$ = (
		assist,
		name) -> new UpdateCol(assist, name);

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
	public class WhereLogicalOperators implements LogicalOperators<WhereAssist> {

		private WhereLogicalOperators() {
		}

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final WhereAssist OR = new WhereAssist(GenericTable.this, whereContext$, CriteriaContext.OR, null);

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final WhereAssist AND = new WhereAssist(GenericTable.this, whereContext$, CriteriaContext.AND, OR);

		@Override
		public WhereAssist defaultOperator() {
			return AND;
		}
	}

	/**
	 * HAVING 句 で使用する AND, OR です。
	 */
	public class HavingLogicalOperators implements LogicalOperators<HavingAssist> {

		private HavingLogicalOperators() {
		}

		/**
		 * HAVING 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final HavingAssist OR = new HavingAssist(GenericTable.this, havingContext$, CriteriaContext.OR, null);

		/**
		 * HAVING 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final HavingAssist AND = new HavingAssist(GenericTable.this, havingContext$, CriteriaContext.AND, OR);

		@Override
		public HavingAssist defaultOperator() {
			return AND;
		}
	}

	/**
	 * ON 句 (LEFT) で使用する AND, OR です。
	 */
	public class OnLeftLogicalOperators implements LogicalOperators<OnLeftAssist> {

		private OnLeftLogicalOperators() {
		}

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final OnLeftAssist OR = new OnLeftAssist(GenericTable.this, onLeftContext$, CriteriaContext.OR, null);

		/**
		 * ON 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final OnLeftAssist AND = new OnLeftAssist(GenericTable.this, onLeftContext$, CriteriaContext.AND, OR);

		@Override
		public OnLeftAssist defaultOperator() {
			return AND;
		}
	}

	/**
	 * ON 句 (RIGHT) で使用する AND, OR です。
	 */
	public class OnRightLogicalOperators implements LogicalOperators<OnRightAssist> {

		private OnRightLogicalOperators() {
		}

		/**
		 * ON 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final OnRightAssist OR = new OnRightAssist(GenericTable.this, onRightContext$, CriteriaContext.OR, null);

		/**
		 * ON 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final OnRightAssist AND = new OnRightAssist(GenericTable.this, onRightContext$, CriteriaContext.AND, OR);

		@Override
		public OnRightAssist defaultOperator() {
			return AND;
		}
	}

	/**
	 * WHERE 句 で使用する AND, OR です。
	 */
	public class DMSWhereLogicalOperators implements LogicalOperators<DMSWhereAssist> {

		private DMSWhereLogicalOperators() {
		}

		/**
		 * WHERE 句に OR 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final DMSWhereAssist OR = new DMSWhereAssist(GenericTable.this, dmsWhereContext$, CriteriaContext.OR, null);

		/**
		 * WHERE 句に AND 結合する条件用のカラムを選択するための {@link TableFacadeAssist} です。
		 */
		public final DMSWhereAssist AND = new DMSWhereAssist(GenericTable.this, dmsWhereContext$, CriteriaContext.AND, OR);

		@Override
		public DMSWhereAssist defaultOperator() {
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
		return id$ == null ? (id$ = RuntimeIdFactory.runtimeInstance()) : id$;
	}

	//@formatter:off
	private class SelectBehavior extends SelectStatementBehavior<
		SelectAssist,
		ListSelectAssist,
		GroupByAssist,
		ListGroupByAssist,
		WhereAssist,
		HavingAssist,
		OrderByAssist,
		ListOrderByAssist,
		OnLeftAssist> {
	//@formatter:on

		private SelectBehavior() {
			super(tablePath, getRuntimeId(), GenericTable.this);
		}

		@Override
		protected SelectAssist newSelect() {
			return new SelectAssist(GenericTable.this, selectContext$);
		}

		@Override
		protected ListSelectAssist newListSelect() {
			return new ListSelectAssist(GenericTable.this, selectContext$);
		}

		@Override
		protected GroupByAssist newGroupBy() {
			return new GroupByAssist(GenericTable.this, groupByContext$);
		}

		@Override
		protected ListGroupByAssist newListGroupBy() {
			return new ListGroupByAssist(GenericTable.this, groupByContext$);
		}

		@Override
		protected OrderByAssist newOrderBy() {
			return new OrderByAssist(GenericTable.this, orderByContext$);
		}

		@Override
		protected ListOrderByAssist newListOrderBy() {
			return new ListOrderByAssist(GenericTable.this, orderByContext$);
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

	private class DMSBehavior extends DataManipulationStatementBehavior<InsertAssist, ListInsertAssist, UpdateAssist, ListUpdateAssist, DMSWhereAssist> {

		public DMSBehavior() {
			super(tablePath, relationship$, GenericTable.this.getRuntimeId(), GenericTable.this);
		}

		@Override
		protected InsertAssist newInsert() {
			return new InsertAssist(GenericTable.this, insertContext$);
		}

		@Override
		protected ListInsertAssist newListInsert() {
			return new ListInsertAssist(GenericTable.this, insertContext$);
		}

		@Override
		protected UpdateAssist newUpdate() {
			return new UpdateAssist(GenericTable.this, updateContext$);
		}

		@Override
		protected ListUpdateAssist newListUpdate() {
			return new ListUpdateAssist(GenericTable.this, updateContext$);
		}

		@Override
		protected LogicalOperators<DMSWhereAssist> newWhereOperators() {
			return new DMSWhereLogicalOperators();
		}
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
	 * このコンストラクタで生成されたインスタンス の SELECT 句で使用されるカラムは、 パラメータの {@link SelectContext} に依存します。
	 * @param context SELECT 句を決定する
	 */
	public GenericTable(SelectContext context) {
		this(context.tablePath());
		selectBehavior().setSelectContext(Objects.requireNonNull(context));
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
	 * この {@link SelectStatement} のテーブルを表す {@link TableFacadeAssist} を参照するためのインスタンスです。
	 * @return assist
	 */
	public ExtAssist<TableFacadeColumn, Void> assist() {
		return new ExtAssist<>(this, TableFacadeContext.OTHER, CriteriaContext.NULL);
	}

	/**
	 * SELECT 句を作成する {@link Consumer}
	 * @param consumer {@link Consumer}
	 * @return this
	 */
	public GenericTable selectClause(Consumer<ListSelectAssist> consumer) {
		selectBehavior().selectClause(consumer);
		return this;
	}

	/**
	 * GROUP BY 句を作成する {@link Consumer}
	 * @param consumer {@link Consumer}
	 * @return this
	 */
	public GenericTable groupByClause(Consumer<ListGroupByAssist> consumer) {
		selectBehavior().groupByClause(consumer);
		return this;
	}

	/**
	 * GROUP BY 句を作成する {@link Consumer}
	 * @param consumer {@link Consumer}
	 * @return this
	 */
	public GenericTable orderByClause(Consumer<ListOrderByAssist> consumer) {
		selectBehavior().orderByClause(consumer);
		return this;
	}

	/**
	 * SELECT 句を記述します。
	 * @param function
	 * @return この {@link SelectStatement}
	 */
	public GenericTable SELECT(SelectOfferFunction<SelectAssist> function) {
		selectBehavior().SELECT(function);
		return this;
	}

	/**
	 * DISTINCT を使用した SELECT 句を記述します。
	 * @param function
	 * @return この {@link SelectStatement}
	 */
	public GenericTable SELECT_DISTINCT(SelectOfferFunction<SelectAssist> function) {
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
	public GenericTable GROUP_BY(GroupByOfferFunction<GroupByAssist> function) {
		selectBehavior().GROUP_BY(function);
		return this;
	}

	/**
	 * WHERE 句を記述します。
	 * @param consumers
	 * @return この {@link SelectStatement}
	 */
	@SafeVarargs
	public final GenericTable WHERE(Consumer<WhereAssist>... consumers) {
		selectBehavior().WHERE(consumers);
		return this;
	}

	/**
	 * WHERE 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return {@link Criteria}
	 */
	public Criteria createWhereCriteria(Consumer<WhereAssist> consumer) {
		return selectBehavior().createWhereCriteria(consumer);
	}

	/**
	 * HAVING 句を記述します。
	 * @param consumers
	 * @return この {@link SelectStatement}
	 */
	@SafeVarargs
	public final GenericTable HAVING(Consumer<HavingAssist>... consumers) {
		selectBehavior().HAVING(consumers);
		return this;
	}

	/**
	 * HAVING 句で使用できる {@link  Criteria} を作成します。
	 * @param consumer {@link Consumer}
	 * @return {@link Criteria}
	 */
	public Criteria createHavingCriteria(Consumer<HavingAssist> consumer) {
		return selectBehavior().createHavingCriteria(consumer);
	}

	/**
	 * このクエリに INNER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightClauseAssist<?>> OnClause<OnLeftAssist, R, GenericTable> INNER_JOIN(RightTable<R> right) {
		return selectBehavior().INNER_JOIN(right, this);
	}

	/**
	 * このクエリに LEFT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightClauseAssist<?>> OnClause<OnLeftAssist, R, GenericTable> LEFT_OUTER_JOIN(RightTable<R> right) {
		return selectBehavior().LEFT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに RIGHT OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightClauseAssist<?>> OnClause<OnLeftAssist, R, GenericTable> RIGHT_OUTER_JOIN(RightTable<R> right) {
		return selectBehavior().RIGHT_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに FULL OUTER JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return ON
	 */
	public <R extends OnRightClauseAssist<?>> OnClause<OnLeftAssist, R, GenericTable> FULL_OUTER_JOIN(RightTable<R> right) {
		return selectBehavior().FULL_OUTER_JOIN(right, this);
	}

	/**
	 * このクエリに CROSS JOIN で別テーブルを結合します。
	 * @param right 別クエリ
	 * @return この {@link SelectStatement}
	 */
	public <R extends OnRightClauseAssist<?>> GenericTable CROSS_JOIN(RightTable<R> right) {
		selectBehavior().CROSS_JOIN(right);
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
	 * INTERSECT するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param select INTERSECT 対象
	 * @return この {@link SelectStatement}
	 */
	public GenericTable INTERSECT(SelectStatement select) {
		selectBehavior().INTERSECT(select);
		return this;
	}

	/**
	 * INTERSECT ALL するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param select INTERSECT ALL 対象
	 * @return この {@link SelectStatement}
	 */
	public GenericTable INTERSECT_ALL(SelectStatement select) {
		selectBehavior().INTERSECT_ALL(select);
		return this;
	}

	/**
	 * EXCEPT するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param select EXCEPT 対象
	 * @return この {@link SelectStatement}
	 */
	public GenericTable EXCEPT(SelectStatement select) {
		selectBehavior().INTERSECT(select);
		return this;
	}

	/**
	 * EXCEPT ALL するクエリを追加します。<br>
	 * 追加する側のクエリには ORDER BY 句を設定することはできません。
	 * @param select EXCEPT ALL 対象
	 * @return この {@link SelectStatement}
	 */
	public GenericTable EXCEPT_ALL(SelectStatement select) {
		selectBehavior().EXCEPT_ALL(select);
		return this;
	}

	/**
	 * ORDER BY 句を記述します。
	 * @param function
	 * @return この {@link SelectStatement}
	 */
	public GenericTable ORDER_BY(OrderByOfferFunction<OrderByAssist> function) {
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
	public GenericTable accept(SQLDecorator... decorators) {
		for (var decorator : decorators) {
			this.decorators$.add(decorator);
		}

		return this;
	}

	@Override
	public SelectContext getSelectContext() {
		return selectBehavior().getSelectContext();
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
	public LogicalOperators<WhereAssist> getWhereLogicalOperators() {
		return selectBehavior().whereOperators();
	}

	@Override
	public LogicalOperators<HavingAssist> getHavingLogicalOperators() {
		return selectBehavior().havingOperators();
	}

	@Override
	public LogicalOperators<OnLeftAssist> getOnLeftLogicalOperators() {
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
	public Iterator retrieve() {
		SelectBehavior selectBehavior = selectBehavior();
		selectBehavior.checkRowMode();
		return wrap(selectBehavior.query().retrieve());
	}

	@Override
	public Optional<Row> fetch(String... primaryKeyMembers) {
		SelectBehavior selectBehavior = selectBehavior();
		selectBehavior.checkRowMode();
		return selectBehavior.query().fetch(primaryKeyMembers).map(o -> createRow(o));
	}

	@Override
	public Optional<Row> fetch(Number... primaryKeyMembers) {
		SelectBehavior selectBehavior = selectBehavior();
		selectBehavior.checkRowMode();
		return selectBehavior.query().fetch(primaryKeyMembers).map(o -> createRow(o));
	}

	@Override
	public Optional<Row> fetch(Bindable... primaryKeyMembers) {
		SelectBehavior selectBehavior = selectBehavior();
		selectBehavior.checkRowMode();
		return selectBehavior.query().fetch(primaryKeyMembers).map(o -> createRow(o));
	}

	@Override
	public int count() {
		SelectBehavior selectBehavior = selectBehavior();
		selectBehavior.checkRowMode();
		return selectBehavior.query().count();
	}

	@Override
	public ComposedSQL countSQL() {
		SelectBehavior selectBehavior = selectBehavior();
		selectBehavior.checkRowMode();
		return selectBehavior.query().countSQL();
	}

	@Override
	public ComposedSQL aggregateSQL() {
		selectBehavior().quitRowMode();
		return this;
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
	public ComposedSQL composeSQL() {
		return selectBehavior().composeSQL();
	}

	@Override
	public OnRightAssist joint() {
		return getOnRightLogicalOperators().AND;
	}

	@Override
	public SelectStatement getSelectStatement() {
		return this;
	}

	/**
	 * INSERT 文を作成する {@link Function}
	 * @param function {@link Function}
	 * @return {@link DataManipulator}
	 */
	public DataManipulator insertStatement(Function<ListInsertAssist, DataManipulator> function) {
		return dmsBehavior().insertStatement(function);
	}

	/**
	 * UPDATE 文を作成する {@link Function}
	 * @param function {@link Function}
	 * @return {@link DataManipulator}
	 */
	public DataManipulator updateStatement(Function<ListUpdateAssist, DataManipulator> function) {
		return dmsBehavior().updateStatement(function);
	}

	/**
	 * INSERT 文を生成します。
	 * @param function function
	 * @return {@link InsertStatementIntermediate}
	 */
	public InsertStatementIntermediate INSERT(InsertOfferFunction<InsertAssist> function) {
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
	public DataManipulator INSERT(InsertOfferFunction<InsertAssist> function, SelectStatement select) {
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
	public UpdateStatementIntermediate<DMSWhereAssist> UPDATE(Consumer<UpdateAssist> consumer) {
		return dmsBehavior().UPDATE(consumer);
	}

	/**
	 * UPDATE 文を生成します。
	 * @return {@link UpdateStatementIntermediate}
	 */
	public UpdateStatementIntermediate<DMSWhereAssist> UPDATE() {
		return dmsBehavior().UPDATE();
	}

	/**
	 * DELETE 文を生成します。
	 * @return {@link DeleteStatementIntermediate}
	 */
	public final DeleteStatementIntermediate<DMSWhereAssist> DELETE() {
		return dmsBehavior().DELETE();
	}

	@Override
	public String toString() {
		return selectBehavior().toString();
	}

	/**
	 * 自動生成された {@link TableFacadeAssist} の実装クラスです。<br>
	 * 条件として使用できるカラムを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class Assist<T, M> implements TableFacadeAssist {

		final GenericTable table$;

		private final TableFacadeContext<T> builder$;

		private final CriteriaContext context$;

		private final TableFacadeAssist parent$;

		private final String fkName$;

		/**
		 * 直接使用しないでください。
		 * @param builder$ builder
		 * @param parent$ parent
		 * @param fkName$ fkName
		 */
		public Assist(TableFacadeContext<T> builder$, TableFacadeAssist parent$, String fkName$) {
			this.builder$ = builder$;
			table$ = null;
			context$ = null;
			this.parent$ = parent$;
			this.fkName$ = fkName$;
		}

		private Assist(GenericTable table$, TableFacadeContext<T> builder$, CriteriaContext context$) {
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
		public T column(String name) {
			return col(name);
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
			if (!(o instanceof TableFacadeAssist))
				return false;
			return getRelationship().equals(((TableFacadeAssist) o).getRelationship());
		}

		@Override
		public int hashCode() {
			return getRelationship().hashCode();
		}

		@Override
		public OneToManyBehavior getOneToManyBehavior() {
			return new OneToManyBehavior(
				parent$ == null ? null : parent$.getOneToManyBehavior(),
				Assist.this.getRelationship(),
				data -> new Row(data),
				table$ != null ? table$.id$ : parent$.getSelectStatement().getRuntimeId());
		}
	}

	/**
	 * 自動生成された {@link TableFacadeAssist} の実装クラスです。<br>
	 * 条件として使用できるカラムと、参照しているテーブルを内包しており、それらを使用して検索 SQL を生成可能にします。
	 * @param <T> 使用されるカラムのタイプにあった型
	 * @param <M> Many 一対多の多側の型連鎖
	 */
	public static class ExtAssist<T, M> extends Assist<T, M> {

		private final TableFacadeContext<T> builder$;

		/**
		 * 直接使用しないでください。
		 * @param builder$ builder
		 * @param parent$ parent
		 * @param fkName$ fkName
		 */
		public ExtAssist(
			TableFacadeContext<T> builder$,
			TableFacadeAssist parent$,
			String fkName$) {
			super(builder$, parent$, fkName$);
			this.builder$ = builder$;
		}

		private ExtAssist(GenericTable table$, TableFacadeContext<T> builder$, CriteriaContext context$) {
			super(table$, builder$, context$);
			this.builder$ = builder$;
		}

		/**
		 * この {@link TableFacadeAssist} が表すテーブルの Row を一とし、多をもつ検索結果を生成する {@link OneToManyQuery} を返します。
		 * @return {@link OneToManyQuery}
		 */
		public OneToManyQuery<Row, M> intercept() {
			if (super.table$ != null)
				//このインスタンスでは直接使用することはできません
				throw new IllegalStateException("It can not be used directly in this instance.");
			if (!getSelectStatement().rowMode())
				//集計モードでは実行できない処理です
				throw new IllegalStateException("This operation can only in \"Row Mode\".");
			return new InstantOneToManyQuery<>(this, getSelectStatement().decorators());
		}

		/**
		 * @param fkName 外部キー名
		 * @return 参照先の {@link ExtAssist}
		 */
		public ExtAssist<T, Many<Row, M>> fkName(String fkName) {
			return fk(fkName);
		}

		/**
		 * @param fkName 外部キー名
		 * @return 参照先の {@link ExtAssist}
		 */
		public ExtAssist<T, Many<Row, M>> fk(String fkName) {
			return new ExtAssist<T, Many<Row, M>>(builder$, this, fkName);
		}
	}

	/**
	 * SELECT 句用
	 */
	public static class SelectAssist extends ExtAssist<SelectCol, Void> implements SelectClauseAssist {

		private SelectAssist(GenericTable table$, TableFacadeContext<SelectCol> builder$) {
			super(table$, builder$, CriteriaContext.NULL);
		}
	}

	/**
	 * SELECT 句用
	 */
	public static class ListSelectAssist extends SelectAssist implements ListSelectClauseAssist {

		private ListSelectAssist(GenericTable table$, TableFacadeContext<SelectCol> builder$) {
			super(table$, builder$);
		}

		@Override
		public SelectStatementBehavior<?, ?, ?, ?, ?, ?, ?, ?, ?> behavior() {
			return table$.selectBehavior();
		}
	}

	/**
	 * SELECT 文 WHERE 句用
	 */
	public static class WhereAssist extends ExtAssist<WhereColumn<WhereLogicalOperators>, Void> implements WhereClauseAssist<WhereAssist> {

		/**
		 * 条件接続 OR
		 */
		public final WhereAssist OR;

		private WhereAssist(
			GenericTable table$,
			TableFacadeContext<WhereColumn<WhereLogicalOperators>> builder$,
			CriteriaContext context$,
			WhereAssist or$) {
			super(table$, builder$, context$);
			OR = or$ == null ? this : or$;
		}

		/**
		 * 任意のカラムを生成します。
		 * @param expression SQL 内のカラムを構成する文字列
		 * @param values プレースホルダの値
		 * @return {@link CriteriaAssistColumn}
		 */
		public CriteriaAssistColumn<WhereLogicalOperators> expr(String expression, Object... values) {
			return new CriteriaAnyColumn<>(statement(), expression, values);
		}

		/**
		 * 任意のカラムを生成します。
		 * @param value プレースホルダの値
		 * @return {@link CriteriaAssistColumn}
		 */
		public CriteriaAssistColumn<WhereLogicalOperators> expr(Object value) {
			return new CriteriaAnyColumn<>(statement(), value);
		}

		@Override
		public WhereLogicalOperators EXISTS(SelectStatement subquery) {
			var statement = getSelectStatement();
			Helper.setExists(statement.getRuntimeId(), this, subquery);
			return (WhereLogicalOperators) statement.getWhereLogicalOperators();
		}

		@Override
		public WhereLogicalOperators NOT_EXISTS(SelectStatement subquery) {
			var statement = getSelectStatement();
			Helper.setNotExists(statement.getRuntimeId(), this, subquery);
			return (WhereLogicalOperators) statement.getWhereLogicalOperators();
		}

		@Override
		public WhereLogicalOperators IN(Vargs<AssistColumn> mainColumns, SelectStatement subquery) {
			Helper.addInCriteria(this, false, mainColumns, subquery);
			return (WhereLogicalOperators) getSelectStatement().getWhereLogicalOperators();
		}

		@Override
		public WhereLogicalOperators NOT_IN(Vargs<AssistColumn> mainColumns, SelectStatement subquery) {
			Helper.addInCriteria(this, true, mainColumns, subquery);
			return (WhereLogicalOperators) getSelectStatement().getWhereLogicalOperators();
		}

		/**
		 * この句に任意のカラムを追加します。
		 * @param template カラムのテンプレート
		 * @return {@link LogicalOperators} AND か OR
		 */
		@Override
		public WhereColumn<WhereLogicalOperators> any(String template) {
			var statement = getSelectStatement();
			return new WhereColumn<>(
				statement,
				getContext(),
				new MultiColumn(statement.getRootRealtionship(), template));
		}

		/**
		 * Consumer に渡された条件句を () で囲みます。
		 * @param consumer {@link Consumer}
		 * @return this
		 */
		@Override
		public WhereLogicalOperators paren(Consumer<WhereAssist> consumer) {
			var statement = getSelectStatement();
			Helper.paren(statement.getRuntimeId(), getContext(), consumer, this);
			return (WhereLogicalOperators) statement.getWhereLogicalOperators();
		}

		@Override
		public Statement statement() {
			return getSelectStatement();
		}
	}

	/**
	 * GROUB BY 句用
	 */
	public static class GroupByAssist extends ExtAssist<GroupByCol, Void> implements GroupByClauseAssist {

		private GroupByAssist(GenericTable table$, TableFacadeContext<GroupByCol> builder$) {
			super(table$, builder$, CriteriaContext.NULL);
		}

		@Override
		public GroupByClause getGroupByClause() {
			return getSelectStatement().getGroupByClause();
		}
	}

	/**
	 * GROUB BY 句用
	 */
	public static class ListGroupByAssist extends GroupByAssist implements ListGroupByClauseAssist {

		private ListGroupByAssist(GenericTable table$, TableFacadeContext<GroupByCol> builder$) {
			super(table$, builder$);
		}

		@Override
		public SelectStatementBehavior<?, ?, ?, ?, ?, ?, ?, ?, ?> behavior() {
			return table$.selectBehavior();
		}
	}

	/**
	 * HAVING 句用
	 */
	public static class HavingAssist extends ExtAssist<HavingColumn<HavingLogicalOperators>, Void>
		implements HavingClauseAssist<HavingAssist> {

		/**
		 * 条件接続 OR
		 */
		public final HavingAssist OR;

		private HavingAssist(
			GenericTable table$,
			TableFacadeContext<HavingColumn<HavingLogicalOperators>> builder$,
			CriteriaContext context$,
			HavingAssist or$) {
			super(table$, builder$, context$);
			OR = or$ == null ? this : or$;
		}

		/**
		 * 任意のカラムを生成します。
		 * @param expression SQL 内のカラムを構成する文字列
		 * @param values プレースホルダの値
		 * @return {@link CriteriaAssistColumn}
		 */
		public CriteriaAssistColumn<HavingLogicalOperators> expr(String expression, Object... values) {
			return new CriteriaAnyColumn<>(statement(), expression, values);
		}

		/**
		 * 任意のカラムを生成します。
		 * @param value プレースホルダの値
		 * @return {@link CriteriaAssistColumn}
		 */
		public CriteriaAssistColumn<HavingLogicalOperators> expr(Object value) {
			return new CriteriaAnyColumn<>(statement(), value);
		}

		@Override
		public HavingLogicalOperators EXISTS(SelectStatement subquery) {
			var statement = getSelectStatement();
			Helper.setExists(statement.getRuntimeId(), this, subquery);
			return (HavingLogicalOperators) statement.getHavingLogicalOperators();
		}

		@Override
		public HavingLogicalOperators NOT_EXISTS(SelectStatement subquery) {
			var statement = getSelectStatement();
			Helper.setNotExists(statement.getRuntimeId(), this, subquery);
			return (HavingLogicalOperators) statement.getHavingLogicalOperators();
		}

		@Override
		public HavingLogicalOperators IN(Vargs<AssistColumn> mainColumns, SelectStatement subquery) {
			Helper.addInCriteria(this, false, mainColumns, subquery);
			return (HavingLogicalOperators) getSelectStatement().getHavingLogicalOperators();
		}

		@Override
		public HavingLogicalOperators NOT_IN(Vargs<AssistColumn> mainColumns, SelectStatement subquery) {
			Helper.addInCriteria(this, true, mainColumns, subquery);
			return (HavingLogicalOperators) getSelectStatement().getHavingLogicalOperators();
		}

		/**
		 * この句に任意のカラムを追加します。
		 * @param template カラムのテンプレート
		 * @return {@link LogicalOperators} AND か OR
		 */
		@Override
		public HavingColumn<HavingLogicalOperators> any(String template) {
			var statement = getSelectStatement();
			return new HavingColumn<>(
				statement,
				getContext(),
				new MultiColumn(statement.getRootRealtionship(), template));
		}

		/**
		 * Consumer に渡された条件句を () で囲みます。
		 * @param consumer {@link Consumer}
		 * @return this
		 */
		@Override
		public HavingLogicalOperators paren(Consumer<HavingAssist> consumer) {
			var statement = getSelectStatement();
			Helper.paren(statement.getRuntimeId(), getContext(), consumer, this);
			return (HavingLogicalOperators) statement.getHavingLogicalOperators();
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByAssist extends ExtAssist<OrderByCol, Void> implements OrderByClauseAssist {

		private OrderByAssist(GenericTable table$, TableFacadeContext<OrderByCol> builder$) {
			super(table$, builder$, CriteriaContext.NULL);
		}

		@Override
		public OrderByClause getOrderByClause() {
			return getSelectStatement().getOrderByClause();
		}
	}

	/**
	 * GROUB BY 句用
	 */
	public static class ListOrderByAssist extends OrderByAssist implements ListOrderByClauseAssist {

		private ListOrderByAssist(GenericTable table$, TableFacadeContext<OrderByCol> builder$) {
			super(table$, builder$);
		}

		@Override
		public SelectStatementBehavior<?, ?, ?, ?, ?, ?, ?, ?, ?> behavior() {
			return table$.selectBehavior();
		}
	}

	/**
	 * ON 句 (LEFT) 用
	 */
	public static class OnLeftAssist extends ExtAssist<OnLeftColumn<OnLeftLogicalOperators>, Void>
		implements OnLeftClauseAssist<OnLeftAssist> {

		/**
		 * 条件接続 OR
		 */
		public final OnLeftAssist OR;

		private OnLeftAssist(
			GenericTable table$,
			TableFacadeContext<OnLeftColumn<OnLeftLogicalOperators>> builder$,
			CriteriaContext context$,
			OnLeftAssist or$) {
			super(table$, builder$, context$);
			OR = or$ == null ? this : or$;
		}

		/**
		 * 任意のカラムを生成します。
		 * @param expression SQL 内のカラムを構成する文字列
		 * @param values プレースホルダの値
		 * @return {@link CriteriaAssistColumn}
		 */
		public CriteriaAssistColumn<OnLeftLogicalOperators> expr(String expression, Object... values) {
			return new CriteriaAnyColumn<>(statement(), expression, values);
		}

		/**
		 * 任意のカラムを生成します。
		 * @param value プレースホルダの値
		 * @return {@link CriteriaAssistColumn}
		 */
		public CriteriaAssistColumn<OnLeftLogicalOperators> expr(Object value) {
			return new CriteriaAnyColumn<>(statement(), value);
		}

		@Override
		public OnLeftLogicalOperators EXISTS(SelectStatement subquery) {
			var statement = getSelectStatement();
			Helper.setExists(statement.getRuntimeId(), this, subquery);
			return (OnLeftLogicalOperators) statement.getOnLeftLogicalOperators();
		}

		@Override
		public OnLeftLogicalOperators NOT_EXISTS(SelectStatement subquery) {
			var statement = getSelectStatement();
			Helper.setNotExists(statement.getRuntimeId(), this, subquery);
			return (OnLeftLogicalOperators) statement.getOnLeftLogicalOperators();
		}

		@Override
		public OnLeftLogicalOperators IN(Vargs<AssistColumn> mainColumns, SelectStatement subquery) {
			Helper.addInCriteria(this, false, mainColumns, subquery);
			return (OnLeftLogicalOperators) getSelectStatement().getOnLeftLogicalOperators();
		}

		@Override
		public OnLeftLogicalOperators NOT_IN(Vargs<AssistColumn> mainColumns, SelectStatement subquery) {
			Helper.addInCriteria(this, true, mainColumns, subquery);
			return (OnLeftLogicalOperators) getSelectStatement().getOnLeftLogicalOperators();
		}

		/**
		 * この句に任意のカラムを追加します。
		 * @param template カラムのテンプレート
		 * @return {@link LogicalOperators} AND か OR
		 */
		@Override
		public OnLeftColumn<OnLeftLogicalOperators> any(String template) {
			var statement = getSelectStatement();
			return new OnLeftColumn<>(
				statement,
				getContext(),
				new MultiColumn(statement.getRootRealtionship(), template));
		}

		/**
		 * Consumer に渡された条件句を () で囲みます。
		 * @param consumer {@link Consumer}
		 * @return this
		 */
		@Override
		public OnLeftLogicalOperators paren(Consumer<OnLeftAssist> consumer) {
			var statement = getSelectStatement();
			Helper.paren(statement.getRuntimeId(), getContext(), consumer, this);
			return (OnLeftLogicalOperators) statement.getOnLeftLogicalOperators();
		}
	}

	/**
	 * ON 句 (RIGHT) 用
	 */
	public static class OnRightAssist extends Assist<OnRightColumn<OnRightLogicalOperators>, Void>
		implements OnRightClauseAssist<OnRightAssist> {

		/**
		 * 条件接続 OR
		 */
		public final OnRightAssist OR;

		private OnRightAssist(
			GenericTable table$,
			TableFacadeContext<OnRightColumn<OnRightLogicalOperators>> builder$,
			CriteriaContext context$,
			OnRightAssist or$) {
			super(table$, builder$, context$);
			OR = or$ == null ? this : or$;
		}

		/**
		 * 任意のカラムを生成します。
		 * @param expression SQL 内のカラムを構成する文字列
		 * @param values プレースホルダの値
		 * @return {@link CriteriaAssistColumn}
		 */
		public CriteriaAssistColumn<OnRightLogicalOperators> expr(String expression, Object... values) {
			return new CriteriaAnyColumn<>(statement(), expression, values);
		}

		/**
		 * 任意のカラムを生成します。
		 * @param value プレースホルダの値
		 * @return {@link CriteriaAssistColumn}
		 */
		public CriteriaAssistColumn<OnRightLogicalOperators> expr(Object value) {
			return new CriteriaAnyColumn<>(statement(), value);
		}

		@Override
		public OnRightLogicalOperators EXISTS(SelectStatement subquery) {
			var statement = getSelectStatement();
			Helper.setExists(statement.getRuntimeId(), this, subquery);
			return (OnRightLogicalOperators) statement.getOnRightLogicalOperators();
		}

		@Override
		public OnRightLogicalOperators NOT_EXISTS(SelectStatement subquery) {
			var statement = getSelectStatement();
			Helper.setNotExists(statement.getRuntimeId(), this, subquery);
			return (OnRightLogicalOperators) statement.getOnRightLogicalOperators();
		}

		@Override
		public OnRightLogicalOperators IN(Vargs<AssistColumn> mainColumns, SelectStatement subquery) {
			Helper.addInCriteria(this, false, mainColumns, subquery);
			return (OnRightLogicalOperators) getSelectStatement().getOnRightLogicalOperators();
		}

		@Override
		public OnRightLogicalOperators NOT_IN(Vargs<AssistColumn> mainColumns, SelectStatement subquery) {
			Helper.addInCriteria(this, true, mainColumns, subquery);
			return (OnRightLogicalOperators) getSelectStatement().getOnRightLogicalOperators();
		}

		/**
		 * この句に任意のカラムを追加します。
		 * @param template カラムのテンプレート
		 * @return {@link LogicalOperators} AND か OR
		 */
		@Override
		public OnRightColumn<OnRightLogicalOperators> any(String template) {
			var statement = getSelectStatement();
			return new OnRightColumn<>(
				statement,
				getContext(),
				new MultiColumn(statement.getRootRealtionship(), template));
		}

		/**
		 * Consumer に渡された条件句を () で囲みます。
		 * @param consumer {@link Consumer}
		 * @return this
		 */
		@Override
		public OnRightLogicalOperators paren(Consumer<OnRightAssist> consumer) {
			var statement = getSelectStatement();
			Helper.paren(statement.getRuntimeId(), getContext(), consumer, this);
			return (OnRightLogicalOperators) statement.getOnRightLogicalOperators();
		}
	}

	/**
	 * INSERT 用
	 */
	public static class InsertAssist extends Assist<InsertCol, Void> implements InsertClauseAssist {

		private InsertAssist(GenericTable table$, TableFacadeContext<InsertCol> builder$) {
			super(table$, builder$, CriteriaContext.NULL);
		}
	}

	/**
	 * INSERT 用
	 */
	public static class ListInsertAssist extends InsertAssist implements ListInsertClauseAssist {

		private ListInsertAssist(GenericTable table$, TableFacadeContext<InsertCol> builder$) {
			super(table$, builder$);
		}

		@Override
		public DataManipulationStatementBehavior<?, ?, ?, ?, ?> behavior() {
			return table$.dmsBehavior();
		}
	}

	/**
	 * UPDATE 用
	 */
	public static class UpdateAssist extends Assist<UpdateCol, Void> implements UpdateClauseAssist {

		private UpdateAssist(GenericTable table$, TableFacadeContext<UpdateCol> builder$) {
			super(table$, builder$, CriteriaContext.NULL);
		}
	}

	/**
	 * UPDATE 用
	 */
	public static class ListUpdateAssist extends UpdateAssist implements ListUpdateClauseAssist<DMSWhereAssist> {

		private ListUpdateAssist(GenericTable table$, TableFacadeContext<UpdateCol> builder$) {
			super(table$, builder$);
		}

		@Override
		public DMSBehavior behavior() {
			return table$.dmsBehavior();
		}
	}

	/**
	 * UPDATE, DELETE 文 WHERE 句用
	 */
	public static class DMSWhereAssist extends Assist<WhereColumn<DMSWhereLogicalOperators>, Void>
		implements WhereClauseAssist<DMSWhereAssist> {

		/**
		 * 条件接続 OR
		 */
		public final DMSWhereAssist OR;

		private DMSWhereAssist(
			GenericTable table$,
			TableFacadeContext<WhereColumn<DMSWhereLogicalOperators>> builder$,
			CriteriaContext context$,
			DMSWhereAssist or$) {
			super(table$, builder$, context$);
			OR = or$ == null ? this : or$;
		}

		/**
		 * 任意のカラムを生成します。
		 * @param expression SQL 内のカラムを構成する文字列
		 * @param values プレースホルダの値
		 * @return {@link CriteriaAssistColumn}
		 */
		public CriteriaAssistColumn<DMSWhereLogicalOperators> expr(String expression, Object... values) {
			return new CriteriaAnyColumn<>(statement(), expression, values);
		}

		/**
		 * 任意のカラムを生成します。
		 * @param value プレースホルダの値
		 * @return {@link CriteriaAssistColumn}
		 */
		public CriteriaAssistColumn<DMSWhereLogicalOperators> expr(Object value) {
			return new CriteriaAnyColumn<>(statement(), value);
		}

		@Override
		public DMSWhereLogicalOperators EXISTS(SelectStatement subquery) {
			var statement = getDataManipulationStatement();
			Helper.setExists(statement.getRuntimeId(), this, subquery);
			return (DMSWhereLogicalOperators) statement.getWhereLogicalOperators();
		}

		@Override
		public DMSWhereLogicalOperators NOT_EXISTS(SelectStatement subquery) {
			var statement = getDataManipulationStatement();
			Helper.setNotExists(statement.getRuntimeId(), this, subquery);
			return (DMSWhereLogicalOperators) statement.getWhereLogicalOperators();
		}

		@Override
		public DMSWhereLogicalOperators IN(Vargs<AssistColumn> mainColumns, SelectStatement subquery) {
			Helper.addInCriteria(this, false, mainColumns, subquery);
			return (DMSWhereLogicalOperators) getDataManipulationStatement().getWhereLogicalOperators();
		}

		@Override
		public DMSWhereLogicalOperators NOT_IN(Vargs<AssistColumn> mainColumns, SelectStatement subquery) {
			Helper.addInCriteria(this, true, mainColumns, subquery);
			return (DMSWhereLogicalOperators) getDataManipulationStatement().getWhereLogicalOperators();
		}

		/**
		 * この句に任意のカラムを追加します。
		 * @param template カラムのテンプレート
		 * @return {@link WhereColumn}
		 */
		@Override
		public WhereColumn<DMSWhereLogicalOperators> any(String template) {
			var statement = getDataManipulationStatement();
			return new WhereColumn<>(
				statement,
				getContext(),
				new MultiColumn(statement.getRootRealtionship(), template));
		}

		/**
		 * Consumer に渡された条件句を () で囲みます。
		 * @param consumer {@link Consumer}
		 * @return {@link DMSWhereLogicalOperators}
		 */
		@Override
		public DMSWhereLogicalOperators paren(Consumer<DMSWhereAssist> consumer) {
			var statement = getDataManipulationStatement();
			Helper.paren(statement.getRuntimeId(), getContext(), consumer, this);
			return (DMSWhereLogicalOperators) statement.getWhereLogicalOperators();
		}

		@Override
		public Statement statement() {
			return getDataManipulationStatement();
		}
	}

	/**
	 * SELECT 句用
	 */
	public static class SelectCol extends SelectColumn {

		private SelectCol(TableFacadeAssist assist, String name) {
			super(assist, name);
		}
	}

	/**
	 * GROUP BY 句用
	 */
	public static class GroupByCol extends GroupByColumn {

		private GroupByCol(TableFacadeAssist assist, String name) {
			super(assist, name);
		}
	}

	/**
	 * ORDER BY 句用
	 */
	public static class OrderByCol extends OrderByColumn {

		private OrderByCol(TableFacadeAssist assist, String name) {
			super(assist, name);
		}
	}

	/**
	 * INSERT 文用
	 */
	public static class InsertCol extends InsertColumn {

		private InsertCol(TableFacadeAssist assist, String name) {
			super(assist, name);
		}
	}

	/**
	 * UPDATE 文用
	 */
	public static class UpdateCol extends UpdateColumn {

		private UpdateCol(TableFacadeAssist assist, String name) {
			super(assist, name);
		}
	}

	/**
	 * Query
	 */
	public class Query implements org.blendee.assist.Query<Iterator, Row> {

		private final PlaybackQuery inner;

		private Query(PlaybackQuery inner) {
			this.inner = inner;
		}

		@Override
		public Iterator retrieve() {
			return wrap(inner.retrieve());
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
		public ComposedSQL countSQL() {
			return inner.countSQL();
		}

		@Override
		public ComposedSQL aggregateSQL() {
			return inner.aggregateSQL();
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
