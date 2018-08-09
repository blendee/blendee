package org.blendee.develop.ormgen;

import java.util.Map;

import org.blendee.support.Query;
import org.blendee.support.Row;

/**
 * {@link ORMGenerator} で生成されるクラスの出力前に割り込み、コードを組み立てるインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface CodeFormatter {

	/**
	 * {@link TableBase} をテンプレートとしたコードを組み立てます。
	 * @param template テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	default String format(String template, Map<String, String> arguments) {
		return Formatter.format(template, arguments);
	}

	/**
	 * @param template テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	default String formatColumnNamesPart(String template, Map<String, String> arguments) {
		return Formatter.format(template, arguments);
	}

	/**
	 * @param template テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	default String formatRelationshipsPart(String template, Map<String, String> arguments) {
		return Formatter.format(template, arguments);
	}

	/**
	 * {@link Row} の setter getter 生成部分のコードを組み立てます。
	 * @param template テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	default String formatRowPropertyAccessorPart(String template, Map<String, String> arguments) {
		return Formatter.format(template, arguments);
	}

	/**
	 * {@link Row} のリレーション生成部分のコードを組み立てます。
	 * @param template テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	default String formatRowRelationshipPart(String template, Map<String, String> arguments) {
		return Formatter.format(template, arguments);
	}

	/**
	 * {@link Query} の項目生成部分のコードを組み立てます。<br>
	 * @param template テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	default String formatQueryColumnPart1(String template, Map<String, String> arguments) {
		return Formatter.format(template, arguments);
	}

	/**
	 * {@link Query} の項目生成部分のコードを組み立てます。<br>
	 * @param template テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	default String formatQueryColumnPart2(String template, Map<String, String> arguments) {
		return Formatter.format(template, arguments);
	}

	/**
	 * {@link Query} のリレーション生成部分のコードを組み立てます。<br>
	 * @param template テンプレート
	 * @param arguments 引数
	 * @return 生成後のコード
	 */
	default String formatQueryRelationshipPart(String template, Map<String, String> arguments) {
		return Formatter.format(template, arguments);
	}
}
