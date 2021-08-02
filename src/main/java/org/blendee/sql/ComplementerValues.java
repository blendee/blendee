package org.blendee.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.ChainPreparedStatementComplementer;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.JDBCBorrower;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.jdbc.ReturningJDBCBorrower;

/**
 * {@link BPreparedStatement} にセットする値を持つ入れ物クラスです。
 * @author 千葉 哲嗣
 */
public class ComplementerValues implements ChainPreparedStatementComplementer, Reproducible<ComplementerValues> {

	// 不変オブジェクトであること!!

	private final List<ValueExtractor> extractors;

	private final List<Binder> binders;

	/**
	 * {@link PreparedStatementComplementer} からプレースホルダ値を抽出するコンストラクタです。
	 * @param complementer 抽出対象
	 * @return {@link ComplementerValues}
	 */
	public static ComplementerValues of(PreparedStatementComplementer complementer) {
		Map<Integer, Object> map = new TreeMap<>();

		complementer.complement(new BPreparedStatement() {

			@Override
			public int getUpdateCount() {
				throw new UnsupportedOperationException();
			}

			@Override
			public BResultSet getResultSet() {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean getMoreResults() {
				throw new UnsupportedOperationException();
			}

			@Override
			public int executeUpdate() {
				throw new UnsupportedOperationException();
			}

			@Override
			public BResultSet executeQuery() {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean execute() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void close() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void setTimestamp(int parameterIndex, Timestamp x) {
				Objects.requireNonNull(x);
				checkParameterIndex(parameterIndex);
				map.put(parameterIndex, x);
			}

			@Override
			public void setString(int parameterIndex, String x) {
				Objects.requireNonNull(x);
				checkParameterIndex(parameterIndex);
				map.put(parameterIndex, x);
			}

			@Override
			public void setObject(int parameterIndex, Object x) {
				Objects.requireNonNull(x);
				checkParameterIndex(parameterIndex);
				map.put(parameterIndex, x);
			}

			@Override
			public void setNull(int parameterIndex, int type) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void setLong(int parameterIndex, long x) {
				Objects.requireNonNull(x);
				checkParameterIndex(parameterIndex);
				map.put(parameterIndex, x);
			}

			@Override
			public void setInt(int parameterIndex, int x) {
				Objects.requireNonNull(x);
				checkParameterIndex(parameterIndex);
				map.put(parameterIndex, x);
			}

			@Override
			public void setFloat(int parameterIndex, float x) {
				Objects.requireNonNull(x);
				checkParameterIndex(parameterIndex);
				map.put(parameterIndex, x);
			}

			@Override
			public void setDouble(int parameterIndex, double x) {
				Objects.requireNonNull(x);
				checkParameterIndex(parameterIndex);
				map.put(parameterIndex, x);
			}

			@Override
			public void setClob(int parameterIndex, Clob x) {
				Objects.requireNonNull(x);
				checkParameterIndex(parameterIndex);
				map.put(parameterIndex, x);
			}

			@Override
			public void setCharacterStream(int parameterIndex, Reader reader, int length) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void setBytes(int parameterIndex, byte[] x) {
				Objects.requireNonNull(x);
				checkParameterIndex(parameterIndex);
				map.put(parameterIndex, x);
			}

			@Override
			public void setBoolean(int parameterIndex, boolean x) {
				Objects.requireNonNull(x);
				checkParameterIndex(parameterIndex);
				map.put(parameterIndex, x);
			}

			@Override
			public void setBlob(int parameterIndex, Blob x) {
				Objects.requireNonNull(x);
				checkParameterIndex(parameterIndex);
				map.put(parameterIndex, x);
			}

			@Override
			public void setBinaryStream(int parameterIndex, InputStream stream, int length) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void setBigDecimal(int parameterIndex, BigDecimal x) {
				Objects.requireNonNull(x);
				checkParameterIndex(parameterIndex);
				map.put(parameterIndex, x);
			}

			@Override
			public void lendStatement(JDBCBorrower<Statement> borrower) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void lendPreparedStatement(JDBCBorrower<PreparedStatement> borrower) {
				throw new UnsupportedOperationException();
			}

			@Override
			public <R> R lendStatementAndGet(ReturningJDBCBorrower<Statement, R> borrower) {
				throw new UnsupportedOperationException();
			}

			@Override
			public <R> R lendPreparedStatementAndGet(ReturningJDBCBorrower<PreparedStatement, R> borrower) {
				throw new UnsupportedOperationException();
			}
		});

		ValueExtractors valueExtractors = ContextManager.get(ValueExtractorsConfigure.class).getValueExtractors();

		List<ValueExtractor> extractors = Arrays.asList(new ValueExtractor[map.size()]);
		List<Binder> binders = Arrays.asList(new Binder[map.size()]);
		map.forEach((k, v) -> {
			int position = k - 1;
			ValueExtractor extractor = valueExtractors.selectValueExtractor(v.getClass());
			extractors.set(position, extractor);
			binders.set(position, extractor.extractAsBinder(v));
		});

		return new ComplementerValues(extractors, binders);
	}

	/**
	 * @param values {@link Object}
	 * @return {@link ComplementerValues}
	 */
	public static ComplementerValues getInstanceOfPlaceHolderValues(Object... values) {
		ValueExtractors valueExtractors = ContextManager.get(ValueExtractorsConfigure.class).getValueExtractors();

		List<ValueExtractor> extractors = Arrays.asList(new ValueExtractor[values.length]);
		List<Binder> binders = Arrays.asList(new Binder[values.length]);
		for (int i = 0; i < values.length; i++) {
			Object value = values[i];
			ValueExtractor extractor = valueExtractors.selectValueExtractor(value.getClass());
			extractors.set(i, extractor);
			binders.set(i, extractor.extractAsBinder(value));
		}

		return new ComplementerValues(extractors, binders);
	}

	/**
	 * @param extractors {@link ValueExtractor}
	 * @param binders {@link Binder}
	 */
	private ComplementerValues(List<ValueExtractor> extractors, List<Binder> binders) {
		this.extractors = Collections.unmodifiableList(extractors);
		this.binders = Collections.unmodifiableList(binders);
	}

	/**
	 * 新しいプレースホルダ値で複製を生成します。
	 * @param placeHolderValues 新しいプレースホルダ値
	 * @return 複製
	 */
	@Override
	public ComplementerValues reproduce(Object... placeHolderValues) {
		List<Binder> binders = new LinkedList<>();

		int index = 0;
		for (ValueExtractor extractor : extractors) {
			try {
				binders.add(extractor.extractAsBinder(placeHolderValues[index]));
			} catch (ClassCastException e) {
				throw new IllegalStateException("ClassCastException at index: " + (index + 1), e);
			}
			index++;
		}

		return new ComplementerValues(extractors, binders);
	}

	@Override
	public ComplementerValues reproduce() {
		return new ComplementerValues(extractors, binders);
	}

	@Override
	public Binder[] currentBinders() {
		return binders.toArray(new Binder[binders.size()]);
	}

	/**
	 * 抽出した {@link Binder} を返します。
	 * @return {@link Binder} のリスト
	 */
	public List<Binder> binders() {
		return Collections.unmodifiableList(binders);
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		int[] index = { done };
		binders.forEach(b -> b.bind(++index[0], statement));
		return index[0];
	}

	private static void checkParameterIndex(int i) {
		if (i < 1) throw new IllegalStateException("invalid parameterIndex: " + i);
	}
}
