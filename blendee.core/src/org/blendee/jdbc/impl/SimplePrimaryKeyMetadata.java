package org.blendee.jdbc.impl;

import org.blendee.jdbc.PrimaryKeyMetadata;

/**
 * {@link PrimaryKeyMetadata} の簡易実装クラスです。
 *
 * @author 千葉 哲嗣
 */
public class SimplePrimaryKeyMetadata implements PrimaryKeyMetadata {

	String name;

	String[] columnNames;

	boolean isPseudo;

	/**
	 * 主要情報をもとにインスタンスを生成します。
	 *
	 * @param name 主キー名
	 * @param columnNames 主キーカラム名
	 * @param isPseudo 疑似キーかどうか
	 */
	public SimplePrimaryKeyMetadata(
		String name,
		String[] columnNames,
		boolean isPseudo) {
		this.name = name;
		this.columnNames = columnNames;
		this.isPseudo = isPseudo;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String[] getColumnNames() {
		return columnNames;
	}

	@Override
	public boolean isPseudo() {
		return isPseudo;
	}

}
