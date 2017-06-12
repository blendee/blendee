package org.blendee.sql;

import org.blendee.jdbc.ManagementSubject;
import org.blendee.sql.DefaultValueExtractors;
import org.blendee.sql.ValueExtractors;

/**
 * {@link ValueExtractors} に対する設定値を保持するクラスです。
 *
 * @author 千葉 哲嗣
 */
public class ValueExtractorsConfigure implements ManagementSubject {

	private ValueExtractors extractors = new DefaultValueExtractors();

	/**
	 * 独自の新しい {@link ValueExtractors} を設定します。
	 *
	 * @param extractorsClass 独自の新しい {@link ValueExtractors} クラス
	 */
	public synchronized void setValueExtractorsClass(Class<? extends ValueExtractors> extractorsClass) {
		try {
			extractors = extractorsClass.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 独自の新しい {@link ValueExtractors} を設定します。
	 *
	 * @param extractors 独自の新しい {@link ValueExtractors}
	 */
	public synchronized void setValueExtractors(ValueExtractors extractors) {
		this.extractors = extractors;
	}

	/**
	 * 現在このクラスに設定されている {@link ValueExtractors} を返します。
	 *
	 * @return 現在の {@link ValueExtractors}
	 */
	public synchronized ValueExtractors getValueExtractors() {
		return extractors;
	}
}
