package org.blendee.sql.binder;

import java.sql.Types;

import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.sql.Binder;

/**
 * {@link BlenPreparedStatement} に NULL を設定するための {@link Binder} です。
 * @author 千葉 哲嗣
 */
public final class NullBinder extends Binder {

	private final int type;

	/**
	 * パラメータのタイプを持つインスタンスを生成します。
	 * @param type このインスタンスのタイプ
	 * @see Types
	 */
	public NullBinder(int type) {
		this.type = type;
	}

	@Override
	public void bind(int index, BlenPreparedStatement statement) {
		statement.setNull(index, type);
	}

	@Override
	public String toString() {
		return "null";
	}

	@Override
	public Binder replicate() {
		return this;
	}

	@Override
	public boolean canEvalValue() {
		return true;
	}

	@Override
	public Object getValue() {
		return null;
	}

	@Override
	protected Object getSpecificallyValue() {
		return null;
	}
}
