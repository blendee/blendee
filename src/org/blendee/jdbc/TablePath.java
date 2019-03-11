package org.blendee.jdbc;

import java.util.Objects;

/**
 * スキーマ名、テーブル名を持ち、テーブルを一意で特定する指標となるクラスです。
 * @author 千葉 哲嗣
 */
public class TablePath implements Comparable<TablePath> {

	/**
	 * 空の配列
	 */
	public static final TablePath[] EMPTY_ARRAY = {};

	private static final String delimiter = ".";

	private final String schemaName;

	private final String tableName;

	/**
	 * スキーマ名とテーブル名から、インスタンスを生成します。
	 * @param schemaName スキーマ名
	 * @param tableName テーブル名
	 * @throws IllegalArgumentException 使用不可能な文字を使用した場合
	 */
	public TablePath(String schemaName, String tableName) {
		Objects.requireNonNull(schemaName);
		Objects.requireNonNull(tableName);
		this.schemaName = schemaName;
		this.tableName = tableName;
	}

	/**
	 * テーブル名のみで、インスタンスを生成します。<br>
	 * スキーマ名を省略しているので、一意に決定できるように Blendee に設定されるスキーマは一つでなければなりません。
	 * @param tableName テーブル名
	 * @throws IllegalStateException スキーマ名が複数設定されている場合
	 * @throws IllegalArgumentException 使用不可能な文字を使用した場合
	 * @see Initializer#addSchemaName(String)
	 */
	public TablePath(String tableName) {
		Objects.requireNonNull(tableName);
		schemaName = null;
		this.tableName = tableName;
	}

	/**
	 * コピーコンストラクタです。
	 * @param path コピーされるインスタンス
	 */
	public TablePath(TablePath path) {
		this(path.getSchemaName(), path.getTableName());
	}

	/**
	 * テーブルを一意で特定する指標から、このクラスのインスタンスを生成します。
	 * @param tablePath テーブルを一意で特定する指標
	 * @return tablePath に対応するインスタンス
	 * @see TablePath#toString()
	 */
	public static TablePath parse(String tablePath) {
		int index = tablePath.indexOf(delimiter);
		if (index == -1) return new TablePath(tablePath);
		return new TablePath(tablePath.substring(0, index), tablePath.substring(index + 1));
	}

	/**
	 * このインスタンスが持つスキーマ名を返します。
	 * @return スキーマ名
	 */
	public final String getSchemaName() {
		if (schemaName != null) return schemaName;

		//Blendee 初期化以前にインスタンス化されても大丈夫なように、デフォルトスキーマ名は、この時点で取得する
		String[] schemaNames = ContextManager.get(BlendeeManager.class).getConfigure().getSchemaNames();
		//スキーマ名が複数設定されています
		if (schemaNames.length > 1) throw new IllegalStateException("Multiple schema names are set.");

		return schemaNames[0];
	}

	/**
	 * このインスタンスが持つテーブル名を返します。
	 * @return テーブル名
	 */
	public final String getTableName() {
		return tableName;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof TablePath && id().equals(((TablePath) o).id());
	}

	@Override
	public int hashCode() {
		return id().hashCode();
	}

	/**
	 * スキーマ名、テーブル名を元に、テーブルを一意で特定する指標となる文字列を返します。
	 * @return テーブルを一意で特定する指標
	 * @see Initializer#addSchemaName(String)
	 */
	@Override
	public String toString() {
		return getSchemaName() + delimiter + tableName;
	}

	@Override
	public int compareTo(TablePath target) {
		return id().compareTo(target.id());
	}

	/**
	 * このインスタンスが指し示すテーブルが実際に存在するかどうかを検査します。
	 * @return 存在する場合、 true
	 */
	public boolean exists() {
		return MetadataUtilities.getColumnMetadatas(this).length > 0;
	}

	/**
	 * 大文字、小文字を意識しないようにする比較用の文字列
	 * @return 識別用 ID
	 */
	protected String id() {
		return toString().toUpperCase();
	}
}
