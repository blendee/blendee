package org.blendee.sql;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.blendee.jdbc.BPreparedStatement;

/**
 * プレースホルダの型ヒントを表します。
 * @author 千葉 哲嗣
 */
public abstract class Placeholder extends Binder {

	private static final ThreadLocal<List<Integer>> placeholderIndexes = new ThreadLocal<>();

	/**
	 * {@link String}
	 */
	public static final Placeholder $STRING = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			if (isStarted()) {
				addIndex(index);
				statement.setString(index, "");
			} else {
				statement.setObject(index, this);
			}
		}
	};

	/**
	 * {@link BigDecimal}
	 */
	public static final Placeholder $BIGDECIMAL = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			if (isStarted()) {
				addIndex(index);
				statement.setBigDecimal(index, BigDecimal.ZERO);
			} else {
				statement.setObject(index, this);
			}
		}
	};

	/**
	 * {@link Timestamp}
	 */
	public static final Placeholder $TIMESTAMP = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			if (isStarted()) {
				addIndex(index);
				statement.setTimestamp(index, new Timestamp(0));
			} else {
				statement.setObject(index, this);
			}
		}
	};

	/**
	 * int
	 */
	public static final Placeholder $INT = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			if (isStarted()) {
				addIndex(index);
				statement.setInt(index, 0);
			} else {
				statement.setObject(index, this);
			}
		}
	};

	/**
	 * long
	 */
	public static final Placeholder $LONG = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			if (isStarted()) {
				addIndex(index);
				statement.setLong(index, 0L);
			} else {
				statement.setObject(index, this);
			}
		}
	};

	/**
	 * float
	 */
	public static final Placeholder $FLOAT = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			if (isStarted()) {
				addIndex(index);
				statement.setFloat(index, 0F);
			} else {
				statement.setObject(index, this);
			}
		}
	};

	/**
	 * double
	 */
	public static final Placeholder $DOUBLE = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			if (isStarted()) {
				addIndex(index);
				statement.setDouble(index, 0D);
			} else {
				statement.setObject(index, this);
			}
		}
	};

	/**
	 * boolean
	 */
	public static final Placeholder $BOOLEAN = new Placeholder() {

		@Override
		public void bind(int index, BPreparedStatement statement) {
			if (isStarted()) {
				addIndex(index);
				statement.setBoolean(index, false);
			} else {
				statement.setObject(index, this);
			}
		}
	};

	/**
	 * UUID
	 */
	public static final Placeholder $UUID = new Placeholder() {

		private final UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

		@Override
		public void bind(int index, BPreparedStatement statement) {
			if (isStarted()) {
				addIndex(index);
				statement.setObject(index, uuid);
			} else {
				statement.setObject(index, this);
			}
		}
	};

	/**
	 * {@link Object}
	 */
	public static final Placeholder $OBJECT = new Placeholder() {

		private final Object object = new Object();

		@Override
		public void bind(int index, BPreparedStatement statement) {
			if (isStarted()) {
				addIndex(index);
				statement.setObject(index, object);
			} else {
				statement.setObject(index, this);
			}
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
	 * {@link #$UUID} の短縮形
	 */
	public static final Placeholder $U = $UUID;

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

	static void start() {
		placeholderIndexes.set(new ArrayList<>());
	}

	static List<Integer> getIndexes() {
		List<Integer> list = placeholderIndexes.get();
		if (list == null) throw new IllegalStateException();

		return list;
	}

	static void remove() {
		placeholderIndexes.remove();
	}

	private static boolean isStarted() {
		return placeholderIndexes.get() != null;
	}

	private static void addIndex(int index) {
		placeholderIndexes.get().add(index);
	}
}
