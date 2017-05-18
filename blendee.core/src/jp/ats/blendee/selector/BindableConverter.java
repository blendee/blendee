package jp.ats.blendee.selector;

import jp.ats.blendee.jdbc.BlendeeContext;
import jp.ats.blendee.sql.Bindable;
import jp.ats.blendee.sql.binder.StringBinder;

/**
 * 色々な型の値を {@link Bindable} に変換するユーティリティクラスです。
 *
 * @author 千葉 哲嗣
 */
public class BindableConverter {

	/**
	 * 変換を行います。
	 *
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
	 * 変換を行います。
	 * <br>
	 * 変換可能な型は、 {@link ValueExtractors} で取得可能なものに限られます。
	 *
	 * @param members 変換対象の数値
	 * @return 変換された {@link Bindable} の配列
	 */
	public static Bindable[] convert(Number... members) {
		return convertAllTypes(members);
	}

	/**
	 * 変換を行います。
	 * <br>
	 * 変換可能な型は、 {@link ValueExtractors} で取得可能なものに限られます。
	 *
	 * @param members 変換対象の数値
	 * @return 変換された {@link Bindable} の配列
	 */
	public static Bindable[] convertAllTypes(Object[] members) {
		Bindable[] bindables = new Bindable[members.length];
		ValueExtractors extractors = BlendeeContext.get(SelectorConfigure.class).getValueExtractors();
		for (int i = 0; i < members.length; i++) {
			Object keyMember = members[i];
			ValueExtractor extractor = extractors.selectValueExtractor(keyMember.getClass());

			if (extractor == null) throw new IllegalStateException(keyMember.getClass() + " に対応する Extractor がありません");

			bindables[i] = extractor.extractAsBinder(keyMember);
		}

		return bindables;
	}
}
