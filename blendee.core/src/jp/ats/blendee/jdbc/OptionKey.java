package jp.ats.blendee.jdbc;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link Configure} 経由で受け取ることのできるオプション情報のキーとして使用するクラスです。
 *
 * @author 千葉 哲嗣
 *
 * @param <T> オプション値の型
 */
public class OptionKey<T> {

	/**
	 * オプションマップから、このキーに対応する値を取り出します。
	 *
	 * @param options オプションマップ
	 * @return オプション値
	 */
	@SuppressWarnings("unchecked")
	public Optional<T> extract(Map<OptionKey<?>, ?> options) {
		return Optional.ofNullable((T) Objects.requireNonNull(options).get(this));
	}
}
