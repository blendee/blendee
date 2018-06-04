package org.blendee.support;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.blendee.jdbc.BlenPreparedStatement;
import org.blendee.jdbc.BlenResultSet;
import org.blendee.jdbc.ChainPreparedStatementComplementer;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.sql.Binder;
import org.blendee.sql.ValueExtractor;
import org.blendee.sql.ValueExtractors;
import org.blendee.sql.ValueExtractorsConfigure;

/**
 * {@link BlenPreparedStatement} にセットする値を持つ入れ物クラスです。
 * @author 千葉 哲嗣
 */
public class ComplementerValues implements ChainPreparedStatementComplementer {

	private final List<ValueExtractor> extractors;

	private final List<Binder> binders;

	/**
	 * {@link PreparedStatementComplementer} からプレースホルダ値を抽出するコンストラクタです。
	 * @param complementer 抽出対象
	 */
	public ComplementerValues(PreparedStatementComplementer complementer) {
		Map<Integer, Object> map = new TreeMap<>();

		complementer.complement(new BlenPreparedStatement() {

			@Override
			public int getUpdateCount() {
				throw new UnsupportedOperationException();
			}

			@Override
			public BlenResultSet getResultSet() {
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
			public BlenResultSet executeQuery() {
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
				map.put(parameterIndex, x);
			}

			@Override
			public void setString(int parameterIndex, String x) {
				Objects.requireNonNull(x);
				map.put(parameterIndex, x);
			}

			@Override
			public void setObject(int parameterIndex, Object x) {
				Objects.requireNonNull(x);
				map.put(parameterIndex, x);
			}

			@Override
			public void setNull(int parameterIndex, int type) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void setLong(int parameterIndex, long x) {
				Objects.requireNonNull(x);
				map.put(parameterIndex, x);
			}

			@Override
			public void setInt(int parameterIndex, int x) {
				Objects.requireNonNull(x);
				map.put(parameterIndex, x);
			}

			@Override
			public void setFloat(int parameterIndex, float x) {
				Objects.requireNonNull(x);
				map.put(parameterIndex, x);
			}

			@Override
			public void setDouble(int parameterIndex, double x) {
				Objects.requireNonNull(x);
				map.put(parameterIndex, x);
			}

			@Override
			public void setClob(int parameterIndex, Clob x) {
				Objects.requireNonNull(x);
				map.put(parameterIndex, x);
			}

			@Override
			public void setCharacterStream(int parameterIndex, Reader reader, int length) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void setBytes(int parameterIndex, byte[] x) {
				Objects.requireNonNull(x);
				map.put(parameterIndex, x);
			}

			@Override
			public void setBoolean(int parameterIndex, boolean x) {
				Objects.requireNonNull(x);
				map.put(parameterIndex, x);
			}

			@Override
			public void setBlob(int parameterIndex, Blob x) {
				Objects.requireNonNull(x);
				map.put(parameterIndex, x);
			}

			@Override
			public void setBinaryStream(int parameterIndex, InputStream stream, int length) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void setBigDecimal(int parameterIndex, BigDecimal x) {
				Objects.requireNonNull(x);
				map.put(parameterIndex, x);
			}
		});

		ValueExtractors valueExtractors = ContextManager.get(ValueExtractorsConfigure.class).getValueExtractors();

		List<ValueExtractor> extractors = new LinkedList<>();
		List<Binder> binders = new LinkedList<>();
		map.values().forEach(v -> {
			ValueExtractor extractor = valueExtractors.selectValueExtractor(v.getClass());
			extractors.add(extractor);
			binders.add(extractor.extractAsBinder(v));
		});

		this.extractors = Collections.unmodifiableList(extractors);
		this.binders = Collections.unmodifiableList(binders);
	}

	/**
	 * 新しいプレースホルダ値で複製を生成します。
	 * @param placeHolderValues 新しいプレースホルダ値
	 * @return 複製
	 */
	public ComplementerValues reproduce(Object... placeHolderValues) {
		List<Binder> binders = new LinkedList<>();

		int index = 0;
		for (ValueExtractor extractor : extractors) {
			binders.add(extractor.extractAsBinder(placeHolderValues[index]));
			index++;
		}

		return new ComplementerValues(extractors, Collections.unmodifiableList(binders));
	}

	@Override
	public int complement(int done, BlenPreparedStatement statement) {
		int[] index = { done + 1 };
		binders.forEach(b -> b.bind(index[0]++, statement));
		return index[0];
	}

	private ComplementerValues(List<ValueExtractor> extractors, List<Binder> binders) {
		this.extractors = extractors;
		this.binders = binders;
	}
}
