package org.blendee.selector;

import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.Result;
import org.blendee.sql.Column;
import org.blendee.sql.ValueExtractors;
import org.blendee.sql.ValueExtractorsConfigure;

/**
 * {@link SelectedValuesConverter} の簡易実装クラスです。
 * @author 千葉 哲嗣
 */
public class SimpleSelectedValuesConverter implements SelectedValuesConverter {

	private final ValueExtractors extractors = ContextManager.get(ValueExtractorsConfigure.class).getValueExtractors();

	@Override
	public SelectedValues convert(Result result, Column[] columns) {
		return new ConcreteSelectedValues(result, columns, extractors);
	}
}
