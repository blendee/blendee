package org.blendee.selector;

import java.util.Objects;

import org.blendee.internal.U;
import org.blendee.jdbc.BResult;
import org.blendee.jdbc.BlendeeContext;
import org.blendee.jdbc.ResourceLocator;
import org.blendee.sql.Column;
import org.blendee.sql.Relationship;
import org.blendee.sql.SelectClause;
import org.blendee.sql.ValueExtractors;
import org.blendee.sql.ValueExtractorsConfigure;

/**
 *
 * @author 千葉 哲嗣
 */
public class RuntimeOptimizer implements Optimizer {

	private final ResourceLocator locator;

	private final ValueExtractors extractors = BlendeeContext.get(ValueExtractorsConfigure.class).getValueExtractors();

	private final SelectClause select = new SelectClause();

	/**
	 * インスタンスを生成します。
	 *
	 * @param locator 対象テーブル
	 */
	public RuntimeOptimizer(ResourceLocator locator) {
		this.locator = Objects.requireNonNull(locator);
	}

	/**
	 * SELECT 句に含めるカラムを追加します。
	 *
	 * @param column SELECT 対象
	 */
	public void add(Column column) {
		Objects.requireNonNull(column);
		check(column.getRelationship().getRoot());
		select.add(column);
	}

	/**
	 * SELECT 句に含めるカラムを追加します。
	 * <br>
	 * {@link Relationship} の全カラムが対象となります。
	 *
	 * @param relation SELECT 対象
	 */
	public void addAll(Relationship relation) {
		Objects.requireNonNull(relation);
		check(relation.getRoot());
		select.add(relation);
	}

	@Override
	public ResourceLocator getResourceLocator() {
		return locator;
	}

	@Override
	public SelectClause getOptimizedSelectClause() {
		return select;
	}

	@Override
	public SelectedValues convert(BResult result, Column[] columns) {
		return new ConcreteSelectedValues(result, columns, extractors);
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	void check(Relationship root) {
		if (!locator.equals(root.getResourceLocator()))
			throw new IllegalArgumentException(locator + " でなければなりません");
	}
}
