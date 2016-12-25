package jp.ats.blendee.develop;

import java.text.MessageFormat;

/**
 * {@link ORMGenerator} で生成されるクラスの出力前に割り込み、コードを組み立てるインターフェイスです。
 *
 * @author 千葉 哲嗣
 */
public interface CodeFormatter {

	/**
	 * {@link Constants} をテンプレートとしたコードを組み立てます。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatConstants(String template, String... arguments);

	/**
	 * {@link Constants} の項目生成部分のコードを組み立てます。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatConstantsColumnPart(String template, String... arguments);

	/**
	 * {@link Constants} のリレーション生成部分のコードを組み立てます。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatConstantsRelationshipPart(String template, String... arguments);

	/**
	 * {@link DAOBase} をテンプレートとしたコードを組み立てます。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatDAO(String template, String... arguments);

	/**
	 * {@link DTOBase} をテンプレートとしたコードを組み立てます。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatDTO(String template, String... arguments);

	/**
	 * {@link DTOBase} の setter getter 生成部分のコードを組み立てます。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatDTOPropertyAccessorPart(String template, String... arguments);

	/**
	 * {@link DTOBase} のリレーション生成部分のコードを組み立てます。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatDTORelationshipPart(String template, String... arguments);

	/**
	 * {@link QueryBase} をテンプレートとしたコードを組み立てます。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatQuery(String template, String... arguments);

	/**
	 * {@link QueryBase} の項目生成部分のコードを組み立てます。
	 * <br>
	 * 具体的な箇所は {@link QueryBase} を参照してください。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatQueryColumnPart1(String template, String... arguments);

	/**
	 * {@link QueryBase} の項目生成部分のコードを組み立てます。
	 * <br>
	 * 具体的な箇所は {@link QueryBase} を参照してください。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatQueryColumnPart2(String template, String... arguments);

	/**
	 * {@link QueryBase} のリレーション生成部分のコードを組み立てます。
	 * <br>
	 * 具体的な箇所は {@link QueryBase} を参照してください。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatQueryRelationshipPart1(String template, String... arguments);

	/**
	 * {@link QueryBase} のリレーション生成部分のコードを組み立てます。
	 * <br>
	 * 具体的な箇所は {@link QueryBase} を参照してください。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatQueryRelationshipPart2(String template, String... arguments);

	/**
	 * {@link QueryBase} のリレーション生成部分のコードを組み立てます。
	 * <br>
	 * 具体的な箇所は {@link QueryBase} を参照してください。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatQueryRelationshipPart3(String template, String... arguments);
}
