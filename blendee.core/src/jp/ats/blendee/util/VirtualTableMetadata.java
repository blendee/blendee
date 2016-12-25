package jp.ats.blendee.util;

import jp.ats.blendee.jdbc.TableMetadata;

/**
 * {@link TableMetadata} の簡易実装です。
 *
 * @author 千葉 哲嗣
 */
public class VirtualTableMetadata implements TableMetadata {

	private final String schemaName;

	private final String name;

	private final String type;

	private final String remarks;

	/**
	 * このクラスのインスタンスを生成します。
	 *
	 * @param schemaName スキーマ名
	 * @param name テーブル名
	 * @param type テーブルの型
	 * @param remarks 説明
	 */
	public VirtualTableMetadata(
		String schemaName,
		String name,
		String type,
		String remarks) {
		this.schemaName = schemaName;
		this.name = name;
		this.type = type;
		this.remarks = remarks;;
	}

	@Override
	public String getSchemaName() {
		return schemaName;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getRemarks() {
		return remarks;
	}

}
