package org.blendee.sql.binder;

import java.io.Reader;

import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.sql.Binder;

/**
 * {@link BlenPreparedStatement} に {@link Reader} から読み込んだ値を設定するための {@link Binder} です。
 * @author 千葉 哲嗣
 */
public final class CharacterStreamBinder extends Binder {

	private final Reader value;

	private final int length;

	/**
	 * 値を読み込む {@link Reader} を持つインスタンスを生成します。
	 * @param value 値を読み込む {@link Reader}
	 * @param length 値のサイズ
	 */
	public CharacterStreamBinder(Reader value, int length) {
		this.value = value;
		this.length = length;
	}

	@Override
	public String toString() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean canEvalValue() {
		return false;
	}

	@Override
	public Object getValue() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Binder replicate() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void bind(int index, BlenPreparedStatement statement) {
		statement.setCharacterStream(index, value, length);
	}

	@Override
	protected Object getSpecificallyValue() {
		throw new UnsupportedOperationException();
	}
}
