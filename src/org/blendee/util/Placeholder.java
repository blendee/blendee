package org.blendee.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.sql.Binder;

/**
 * プレースホルダの型ヒントを表します。
 * @author 千葉 哲嗣
 */
public abstract class Placeholder extends Binder {

	private static final ThreadLocal<List<Integer>> placeholderIndexes = ThreadLocal.withInitial(() -> new ArrayList<>());

	/**
	 * {@link String}
	 */
	public static final Placeholder $STRING = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			placeholderIndexes.get().add(index);
			statement.setString(index, "");
		}
	};

	/**
	 * {@link BigDecimal}
	 */
	public static final Placeholder $BIGDECIMAL = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			placeholderIndexes.get().add(index);
			statement.setBigDecimal(index, BigDecimal.ZERO);
		}
	};

	/**
	 * {@link Timestamp}
	 */
	public static final Placeholder $TIMESTAMP = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			placeholderIndexes.get().add(index);
			statement.setTimestamp(index, new Timestamp(0));
		}
	};

	/**
	 * int
	 */
	public static final Placeholder $INT = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			placeholderIndexes.get().add(index);
			statement.setInt(index, 0);
		}
	};

	/**
	 * long
	 */
	public static final Placeholder $LONG = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			placeholderIndexes.get().add(index);
			statement.setLong(index, 0L);
		}
	};

	/**
	 * float
	 */
	public static final Placeholder $FLOAT = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			placeholderIndexes.get().add(index);
			statement.setFloat(index, 0F);
		}
	};

	/**
	 * double
	 */
	public static final Placeholder $DOUBLE = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			placeholderIndexes.get().add(index);
			statement.setDouble(index, 0D);
		}
	};

	/**
	 * boolean
	 */
	public static final Placeholder $BOOLEAN = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			placeholderIndexes.get().add(index);
			statement.setBoolean(index, false);
		}
	};

	/**
	 * {@link Object}
	 */
	public static final Placeholder $OBJECT = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			placeholderIndexes.get().add(index);
			statement.setObject(index, new Object());
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

	static List<Integer> getIndexes() {
		return placeholderIndexes.get();
	}

	static void remove() {
		placeholderIndexes.remove();
	}
}
