package org.blendee.support;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.blendee.sql.Column;
import org.blendee.sql.MultiColumn;
import org.blendee.sql.TemplateColumn;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface OnQueryRelationship extends CriteriaQueryRelationship {

	/**
	 * ON 句に任意のカラムを追加します。
	 * @param <O> operator
	 * @param template カラムのテンプレート
	 * @param columns 使用するカラム
	 * @return {@link LogicalOperators} AND か OR
	 */
	default <O extends LogicalOperators<?>> OnQueryColumn<O> any(
		String template,
		OnQueryColumn<?>... columns) {
		if (columns.length == 1)
			return new OnQueryColumn<>(
				getRoot(),
				columns[0].getContext(),
				new TemplateColumn(template, columns[0].column()));

		List<Column> list = Arrays.asList(columns).stream().map(c -> c.column()).collect(Collectors.toList());

		return new OnQueryColumn<>(
			getRoot(),
			getContext(),
			new MultiColumn(getRelationship(), template, list.toArray(new Column[list.size()])));
	}
}
