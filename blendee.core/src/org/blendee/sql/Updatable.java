package org.blendee.sql;

/**
 * 自身の値をデータベースへ格納することが可能なクラスのインターフェイスを定義します。
 * @author 千葉 哲嗣
 */
@FunctionalInterface
public interface Updatable {

	/**
	 * 自身が保持する値を SQL 文にセットします。
	 * @param updater 対象となる {@link Updater}
	 */
	void setValuesTo(Updater updater);
}
