package org.blendee.support;

import org.blendee.sql.Effector;

/**
 * {@link Effector} を複数持つことができるコンテナクラスです。
 * @author 千葉 哲嗣
 */
public class Effectors {

	/**
	 * 空のインスタンス
	 */
	public static final Effectors EMPTY_OPTIONS = new Effectors();

	private final Effector[] effectors;

	/**
	 * 短縮記述用メソッドです。<br>
	 * import static した場合、短縮して記述できます。
	 * @param effectors 複数の {@link Effector}
	 * @return instance
	 */
	public static Effectors of(Effector... effectors) {
		return new Effectors(effectors);
	}

	static Effectors care(Effectors effectors) {
		return effectors == null ? EMPTY_OPTIONS : effectors;
	}

	/**
	 * {@link Effector} を持つインスタンスを生成します。
	 * @param effectors 複数の {@link Effector}
	 */
	public Effectors(Effector... effectors) {
		this.effectors = effectors;
	}

	/**
	 * 保持する {@link Effector} を返します。
	 * @return {@link Effector} の配列
	 */
	public Effector[] get() {
		return effectors.clone();
	}
}
