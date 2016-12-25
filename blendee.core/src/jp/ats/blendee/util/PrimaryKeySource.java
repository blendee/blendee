package jp.ats.blendee.util;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.ColumnMetadata;
import jp.ats.blendee.jdbc.PrimaryKeyMetadata;
import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.jdbc.TableMetadata;

/**
 * {@link TableSource} を構成するために必要な主キー情報を表すクラスです。
 *
 * @author 千葉 哲嗣
 * @see TableSource#TableSource(ResourceLocator, TableMetadata, ColumnMetadata[], PrimaryKeySource, ForeignKeySource[])
 */
public class PrimaryKeySource implements PrimaryKeyMetadata {

	private final String name;

	private final String[] columns;

	private final boolean isPseudo;

	/**
	 * このクラスのインスタンスを生成します。
	 *
	 * @param name 主キー名
	 * @param columns 構成カラム
	 * @param isPseudo 疑似キーかどうか
	 */
	public PrimaryKeySource(String name, String[] columns, boolean isPseudo) {
		this.name = name;
		this.columns = columns.clone();
		this.isPseudo = isPseudo;
	}

	/**
	 * {@link PrimaryKeyMetadata} からインスタンスを生成します。
	 *
	 * @param metadata インスタンス化に必要な情報を持つ {@link PrimaryKeyMetadata}
	 */
	public PrimaryKeySource(PrimaryKeyMetadata metadata) {
		this.name = metadata.getName();
		this.columns = metadata.getColumnNames().clone();
		this.isPseudo = metadata.isPseudo();
	}

	/**
	 * コンストラクタで渡された主キー名を返します。
	 *
	 * @return 主キー名
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * コンストラクタで渡された構成カラムを返します。
	 *
	 * @return 構成カラム
	 */
	@Override
	public String[] getColumnNames() {
		return columns.clone();
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
