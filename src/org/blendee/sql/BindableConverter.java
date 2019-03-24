package org.blendee.sql;

import java.sql.Timestamp;
import java.util.UUID;

import org.blendee.jdbc.ContextManager;
import org.blendee.sql.binder.StringBinder;

/**
 * 色々な型の値を {@link Bindable} に変換するユーティリティクラスです。
 * @author 千葉 哲嗣
 */
public class BindableConverter {

	/**
	 * 変換を行います。
	 * @param members 変換対象の文字列
	 * @return 変換された {@link Bindable} の配列
	 */
	public static Bindable[] convert(String... members) {
		Bindable[] bindables = new Bindable[members.length];
		for (int i = 0; i < bindables.length; i++) {
			bindables[i] = new StringBinder(members[i]);
		}

		return bindables;
	}

	/**
	 * 変換を行います。<br>
	 * 変換可能な型は、 {@link ValueExtractors} で取得可能なものに限られます。
	 * @param members 変換対象の数値
	 * @return 変換された {@link Bindable} の配列
	 */
	public static Bindable[] convert(Number... members) {
		return convertAllTypes(members);
	}

	/**
	 * 変換を行います。<br>
	 * 変換可能な型は、 {@link ValueExtractors} で取得可能なものに限られます。
	 * @param members 変換対象の数値
	 * @return 変換された {@link Bindable} の配列
	 */
	public static Bindable[] convert(UUID... members) {
		return convertAllTypes(members);
	}

	/**
	 * 変換を行います。<br>
	 * 変換可能な型は、 {@link ValueExtractors} で取得可能なものに限られます。
	 * @param members 変換対象の数値
	 * @return 変換された {@link Bindable} の配列
	 */
	public static Bindable[] convert(Timestamp... members) {
		return convertAllTypes(members);
	}

	/**
	 * 変換を行います。<br>
	 * 変換可能な型は、 {@link ValueExtractors} で取得可能なものに限られます。
	 * @param members 変換対象の数値
	 * @return 変換された {@link Bindable} の配列
	 */
	public static Bindable[] convertAllTypes(Object[] members) {
		Bindable[] bindables = new Bindable[members.length];
		ValueExtractors extractors = ContextManager.get(ValueExtractorsConfigure.class).getValueExtractors();
		for (int i = 0; i < members.length; i++) {
			Object keyMember = members[i];
			ValueExtractor extractor = extractors.selectValueExtractor(keyMember.getClass());

			//keyMember.getClass() + " に対応する Extractor がありません"
			if (extractor == null) throw new IllegalStateException("There is no ValueExtractor of " + keyMember.getClass() + ".");

			bindables[i] = extractor.extractAsBinder(keyMember);
		}

		return bindables;
	}
}
