package org.blendee.util;

import java.sql.Types;

import org.blendee.internal.U;
import org.blendee.jdbc.ColumnMetadata;

/**
 * {@link ColumnMetadata} の簡易実装です。
 * @author 千葉 哲嗣
 */
public class VirtualColumnMetadata implements ColumnMetadata {

	private final String schemaName;

	private final String tableName;

	private final String name;

	private final int type;

	private final String typeName;

	private final int size;

	private final boolean hasDecimalDigits;

	private final int decimalDigits;

	private final String remarks;

	private final String defaultValue;

	private final int ordinalPosition;

	private final boolean isNotNull;

	/**
	 * このクラスのインスタンスを生成します。
	 * @param schemaName スキーマ名
	 * @param tableName テーブル名
	 * @param name 名称
	 * @param type {@link Types} における型
	 * @param typeName 型名
	 * @param size サイズ
	 * @param hasDecimalDigits 小数点以下の桁数を持つかどうか
	 * @param decimalDigits 小数点以下の桁数
	 * @param remarks コメント記述列
	 * @param defaultValue デフォルト値
	 * @param ordinalPosition テーブル内の位置 (1 から始まる )
	 * @param isNotNull NULL 値を許可するかしないか
	 */
	public VirtualColumnMetadata(
		String schemaName,
		String tableName,
		String name,
		int type,
		String typeName,
		int size,
		boolean hasDecimalDigits,
		int decimalDigits,
		String remarks,
		String defaultValue,
		int ordinalPosition,
		boolean isNotNull) {
		this.schemaName = schemaName;
		this.tableName = tableName;
		this.name = name;
		this.type = type;
		this.typeName = typeName;
		this.size = size;
		this.decimalDigits = decimalDigits;
		this.hasDecimalDigits = hasDecimalDigits;
		this.remarks = remarks;
		this.defaultValue = defaultValue;
		this.ordinalPosition = ordinalPosition;
		this.isNotNull = isNotNull;
	}

	@Override
	public String getSchemaName() {
		return schemaName;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public String getTypeName() {
		return typeName;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public boolean hasDecimalDigits() {
		return hasDecimalDigits;
	}

	@Override
	public int getDecimalDigits() {
		return decimalDigits;
	}

	@Override
	public String getRemarks() {
		return remarks;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public int getOrdinalPosition() {
		return ordinalPosition;
	}

	@Override
	public boolean isNotNull() {
		return isNotNull;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
