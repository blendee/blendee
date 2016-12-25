package jp.ats.blendee.selector;

/**
 * 値のクラスから、 {@link ValueExtractor} を決定する機能を定義したインターフェイスです。
 *
 * @author 千葉 哲嗣
 * @see SelectorConfigure#setValueExtractors(ValueExtractors)
 */
@FunctionalInterface
public interface ValueExtractors {

	/**
	 * 値のクラスから、 {@link ValueExtractor} を決定します。
	 *
	 * @param valueClass 値のクラス
	 * @return 対応する {@link ValueExtractor}
	 */
	ValueExtractor selectValueExtractor(Class<?> valueClass);
}
