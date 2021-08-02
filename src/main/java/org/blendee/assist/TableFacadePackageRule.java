package org.blendee.assist;

import javax.lang.model.SourceVersion;

/**
 * 自動生成される {@link TableFacade} を格納するパッケージに関するルールを定義します。
 * @author 千葉 哲嗣
 */
public class TableFacadePackageRule {

	/**
	 * パッケージ名に使用できるように文字列を加工します。
	 * @param name パッケージ名
	 * @return for packageName
	 */
	public static String care(String name) {
		name = name.toLowerCase();

		//パッケージの並び順を考慮し、後ろに$を付ける
		return SourceVersion.isName(name) ? name : name.toUpperCase();
	}
}
