package org.blendee.support;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.blendee.jdbc.ContextManager;
import org.blendee.sql.Binder;
import org.blendee.sql.Column;
import org.blendee.sql.Criteria;
import org.blendee.sql.CriteriaFactory;
import org.blendee.sql.QueryId;
import org.blendee.sql.ValueExtractors;
import org.blendee.sql.ValueExtractorsConfigure;

/**
 * {@link CriteriaRelationship#with(String, java.util.function.Consumer)} の template にセットするカラムとプレースホルダの値を渡すための入れ物クラスです。
 * @author 千葉 哲嗣
 */
public class WithValues {

	private final List<Column> columns = new LinkedList<>();

	private final List<Binder> binders = new LinkedList<>();

	/**
	 * template にセットするカラムを保持します。
	 * @param columns template にセットするカラム
	 * @return self
	 */
	public WithValues columns(CriteriaColumn<?>... columns) {
		Arrays.stream(columns).map(c -> c.column()).forEach(this.columns::add);
		return this;
	}

	/**
	 * template にセットするプレースホルダの値を保持します。
	 * @param values template にセットするプレースホルダの値
	 * @return self
	 */
	public WithValues values(Object... values) {
		ValueExtractors extractors = ContextManager.get(ValueExtractorsConfigure.class).getValueExtractors();

		for (Object value : values) {
			binders.add(extractors.selectValueExtractor(value.getClass()).extractAsBinder(value));
		}

		return this;
	}

	Criteria createCriteria(QueryId id, String clause) {
		return new CriteriaFactory(id).createCriteria(
			clause,
			columns.toArray(new Column[columns.size()]),
			binders.toArray(new Binder[binders.size()]));
	}
}
