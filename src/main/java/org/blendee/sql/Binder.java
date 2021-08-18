package org.blendee.sql;

import org.blendee.jdbc.BPreparedStatement;

/**
 * {@link BPreparedStatement} に単一の値を設定するクラスです。<br>
 * サブクラスは、 {@link BPreparedStatement} に、具体的にどの型で値を設定するかを知っている必要があります。
 * @author 千葉 哲嗣
 */
public abstract class Binder implements Bindable {

	/**
	 * 空の配列
	 */
	public static final Binder[] EMPTY_ARRAY = {};

	/**
	 * {@link BPreparedStatement} にこのインスタンスの持つ値を設定します。<br>
	 * 渡されたパラメータの index でステートメントに値を設定する必要があります
	 * @param index ステートメントに値を設定する位置
	 * @param statement 対象となるステートメント
	 */
	public abstract void bind(int index, BPreparedStatement statement);

	@Override
	public abstract String toString();

	/**
	 * 自身の複製を生成し、返します。
	 * @return このインスタンスと同じ値を持つ複製
	 */
	public abstract Binder replicate();

	@Override
	public Binder toBinder() {
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Binder)) return false;
		var target = (Binder) o;
		if (target.overridesEquals()) return target.equals(this);
		if (!canEvalValue() || !target.canEvalValue()) return false;
		return equals(getSpecificallyValue(), target.getSpecificallyValue());
	}

	@Override
	public int hashCode() {
		var value = getSpecificallyValue();
		if (value == null) return getClass().hashCode();
		return value.hashCode();
	}

	/**
	 * サブクラスのインスタンス同士が {@link Object#equals(Object)} 可能かどうかを検査します。
	 * @return {@link Object#equals(Object)} 可能であれば、 true
	 */
	public abstract boolean canEvalValue();

	/**
	 * このインスタンスの持つ値を返します。<br>
	 * 返される値の状態が変更可能な場合、状態を変更したとしてもこのインスタンスの状態が変わってしまってはいけません。
	 * @return このインスタンスの持つ値（の複製）
	 */
	public abstract Object getValue();

	/**
	 * このインスタンスの持つ値を返します。<br>
	 * このメソッドが返す値は {@link Object#equals(Object)} でしか使用されないので、インスタンスが持つ値そのものを返すようにしてください。
	 * @return このインスタンスの持つ値
	 */
	protected abstract Object getSpecificallyValue();

	/**
	 * サブクラスが独自の同一検査を行いたい場合、 {@link Object#equals(Object)} をオーバーライドし、このメソッドで true を返すようにします。
	 * @return {@link Object#equals(Object)} がオーバーライドされている場合、 true
	 */
	protected boolean overridesEquals() {
		return false;
	}

	private static boolean equals(Object a, Object b) {
		if (a == null && b == null) return true;
		if (a == null || b == null) return false;
		return a.equals(b);
	}
}
