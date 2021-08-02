package org.blendee.jdbc.impl;

import java.util.Arrays;
import java.util.List;

import org.blendee.internal.U;
import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.TablePath;

/**
 * {@link CrossReference} の簡易実装クラスです。<br>
 * このクラスは immutable であり、インスタンスの持つ値を変更することはできません。
 * @author 千葉 哲嗣
 */
public class SimpleCrossReference implements CrossReference {

	private final String primaryKeyName;

	private final String foreignKeyName;

	private final TablePath primaryKeyResource;

	private final TablePath foreignKeyResource;

	private final List<String> primaryKeyColumns;

	private final List<String> foreignKeyColumns;

	private final boolean isPseudo;

	/**
	 * パラメータの値を持つインスタンスを生成します。
	 * @param primaryKeyName 主キー名
	 * @param foreignKeyName 外部キー名
	 * @param primaryKeyResource 主キー側テーブル
	 * @param foreignKeyResource 外部キー側テーブル
	 * @param primaryKeyColumns 主キーカラム名配列
	 * @param foreignKeyColumns 外部キーカラム名配列
	 * @param isPseudo 疑似キーかどうか
	 */
	public SimpleCrossReference(
		String primaryKeyName,
		String foreignKeyName,
		TablePath primaryKeyResource,
		TablePath foreignKeyResource,
		String[] primaryKeyColumns,
		String[] foreignKeyColumns,
		boolean isPseudo) {
		this.primaryKeyName = primaryKeyName;
		this.foreignKeyName = foreignKeyName;
		this.primaryKeyResource = primaryKeyResource;
		this.foreignKeyResource = foreignKeyResource;
		this.primaryKeyColumns = Arrays.asList(primaryKeyColumns);
		this.foreignKeyColumns = Arrays.asList(foreignKeyColumns);
		this.isPseudo = isPseudo;
	}

	@Override
	public String getPrimaryKeyName() {
		return primaryKeyName;
	}

	@Override
	public String getForeignKeyName() {
		return foreignKeyName;
	}

	@Override
	public TablePath getPrimaryKeyTable() {
		return primaryKeyResource;
	}

	@Override
	public TablePath getForeignKeyTable() {
		return foreignKeyResource;
	}

	@Override
	public String[] getPrimaryKeyColumnNames() {
		return primaryKeyColumns.toArray(new String[primaryKeyColumns.size()]);
	}

	@Override
	public String[] getForeignKeyColumnNames() {
		return foreignKeyColumns.toArray(new String[foreignKeyColumns.size()]);
	}

	@Override
	public String convertToForeignKeyColumnName(String primaryKeyColumnName) {
		return foreignKeyColumns.get(primaryKeyColumns.indexOf(primaryKeyColumnName));
	}

	@Override
	public String convertToPrimaryKeyColumnName(String foreignKeyColumnName) {
		return primaryKeyColumns.get(foreignKeyColumns.indexOf(foreignKeyColumnName));
	}

	@Override
	public boolean isPseudo() {
		return isPseudo;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
