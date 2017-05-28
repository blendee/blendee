package org.blendee.develop.ormgen;

import java.text.MessageFormat;

/**
 * {@link ORMGenerator} で生成されるクラスの出力前に割り込み、コードを組み立てるインターフェイスです。
 *
 * @author 千葉 哲嗣
 */
public interface CodeFormatter {

	/**
	 * {@link ConstantsBase} をテンプレートとしたコードを組み立てます。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatConstants(String template, String... arguments);

	/**
	 * {@link ConstantsBase} の項目生成部分のコードを組み立てます。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatConstantsColumnPart(String template, String... arguments);

	/**
	 * {@link ConstantsBase} のリレーション生成部分のコードを組み立てます。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatConstantsRelationshipPart(String template, String... arguments);

	/**
	 * {@link ManagerBase} をテンプレートとしたコードを組み立てます。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatEntityManager(String template, String... arguments);

	/**
	 * {@link EntityBase} をテンプレートとしたコードを組み立てます。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatEntity(String template, String... arguments);

	/**
	 * {@link EntityBase} の setter getter 生成部分のコードを組み立てます。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatEntityPropertyAccessorPart(String template, String... arguments);

	/**
	 * {@link EntityBase} のリレーション生成部分のコードを組み立てます。
	 *
	 * @param template {@link MessageFormat} 形式テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	String formatEntityRelationshipPart(String template, String... arguments);

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
