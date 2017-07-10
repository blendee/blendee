package org.blendee.sql;

/**
 * 自身の値を、データベースへ格納できる値に変換可能なクラスのインターフェイスを定義します。
 * @author 千葉 哲嗣
 */
@FunctionalInterface
public interface Bindable {

	/**
	 * 自身が保持する値を Binder 化します。
	 * @return 変換された {@link Binder}
	 */
	Binder toBinder();
}
