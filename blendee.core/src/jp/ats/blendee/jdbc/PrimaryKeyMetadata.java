package jp.ats.blendee.jdbc;

/**
 * 主キーに関するメタデータを保持し、参照することが可能であることを表すインターフェイスです。
 *
 * @author 千葉 哲嗣
 */
public interface PrimaryKeyMetadata {

	/**
	 * 主キーの名称を返します。
	 *
	 * @return 主キー名
	 */
	String getName();

	/**
	 * 主キーを構成する項目名を返します。
	 *
	 * @return 主キー項目名
	 */
	String[] getColumnNames();

	/**
	 * この主キーが、実際のデータベースで定義されたものではなく、擬似的に追加されたものであるかどうかを返します。
	 *
	 * @return 疑似主キーがどうか
	 */
	boolean isPseudo();
}
