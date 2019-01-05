package org.blendee.util;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.sql.Binder;

/**
 * プレースホルダの型ヒントを表します。
 * @author 千葉 哲嗣
 */
public abstract class Placeholder extends Binder {

	/**
	 * {@link String}
	 */
	public static final Placeholder $STRING = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			statement.setString(index, "");
		}
	};

	/**
	 * {@link BigDecimal}
	 */
	public static final Placeholder $BIGDECIMAL = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			statement.setBigDecimal(index, BigDecimal.ZERO);
		}
	};

	/**
	 * {@link Timestamp}
	 */
	public static final Placeholder $TIMESTAMP = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			statement.setTimestamp(index, new Timestamp(0));
		}
	};

	/**
	 * int
	 */
	public static final Placeholder $INT = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			statement.setInt(index, 0);
		}
	};

	/**
	 * long
	 */
	public static final Placeholder $LONG = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			statement.setLong(index, 0L);
		}
	};

	/**
	 * float
	 */
	public static final Placeholder $FLOAT = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			statement.setFloat(index, 0F);
		}
	};

	/**
	 * double
	 */
	public static final Placeholder $DOUBLE = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			statement.setDouble(index, 0D);
		}
	};

	/**
	 * boolean
	 */
	public static final Placeholder $BOOLEAN = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			statement.setBoolean(index, false);
		}
	};

	/**
	 * {@link Object}
	 */
	public static final Placeholder $OBJECT = new Placeholder() {

		private final Object object = new Object();

		@Override
		public void bind(int index, BPreparedStatement statement) {
			statement.setObject(index, object);
		}
	};

	/**
	 * {@link #$STRING} の短縮形
	 */
	public static final Placeholder $S = $STRING;

	/**
	 * {@link #$BIGDECIMAL} の短縮形
	 */
	public static final Placeholder $B = $BIGDECIMAL;

	/**
	 * {@link #$TIMESTAMP} の短縮形
	 */
	public static final Placeholder $T = $TIMESTAMP;

	/**
	 * {@link #$INT} の短縮形
	 */
	public static final Placeholder $I = $INT;

	/**
	 * {@link #$LONG} の短縮形
	 */
	public static final Placeholder $L = $LONG;

	/**
	 * {@link #$FLOAT} の短縮形
	 */
	public static final Placeholder $F = $FLOAT;

	/**
	 * {@link #$DOUBLE} の短縮形
	 */
	public static final Placeholder $D = $DOUBLE;

	/**
	 * {@link #$BOOLEAN} の短縮形
	 */
	public static final Placeholder $BO = $BOOLEAN;

	/**
	 * {@link #$OBJECT} の短縮形
	 */
	public static final Placeholder $O = $OBJECT;

	@Override
	public Binder toBinder() {
		return this;
	}

	@Override
	public String toString() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Binder replicate() {
		return this;
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
	protected Object getSpecificallyValue() {
		throw new UnsupportedOperationException();
	}
}
