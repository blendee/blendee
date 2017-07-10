package org.blendee.sql;

import java.util.LinkedList;
import java.util.List;

import org.blendee.internal.U;

/**
 * ウィンドウ関数で使用する OVER 句を表すクラスです。
 * @author 千葉 哲嗣
 */
public class OverClause {

	private final PartitionByClause partitionByClause;

	private final OrderByClause orderByClause;

	/**
	 * OVER 句を作成します。
	 * @param partitionByClause {@link PartitionByClause}
	 * @param orderByClause {@link OrderByClause}
	 */
	public OverClause(
		PartitionByClause partitionByClause,
		OrderByClause orderByClause) {
		this.partitionByClause = partitionByClause;
		this.orderByClause = orderByClause;
	}

	/**
	 * OVER 句を作成します。
	 * @param partitionByClause {@link PartitionByClause}
	 */
	public OverClause(PartitionByClause partitionByClause) {
		this.partitionByClause = partitionByClause;
		this.orderByClause = null;
	}

	/**
	 * OVER 句を作成します。
	 * @param orderByClause {@link OrderByClause}
	 */
	public OverClause(OrderByClause orderByClause) {
		this.partitionByClause = null;
		this.orderByClause = orderByClause;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	String getTemplate(int skip) {
		return "OVER("
			+ concat(
				buildTemplate(partitionByClause, skip),
				buildTemplate(orderByClause, partitionByClause.getColumnsSize() + skip))
			+ ")";
	}

	Column[] getColumns() {
		List<Column> columns = new LinkedList<>();
		if (partitionByClause != null) columns.addAll(partitionByClause.getColumnsInternal());
		if (orderByClause != null) columns.addAll(orderByClause.getColumnsInternal());
		return columns.toArray(new Column[columns.size()]);
	}

	private static final String buildTemplate(SimpleQueryClause<?> clause, int start) {
		if (clause == null) return "";
		List<String> localTemplates = new LinkedList<>();

		Column[] columns = clause.getColumns();
		for (int i = 0; i < columns.length; i++) {
			localTemplates.add("{" + (start + i) + "}");
		}

		return concat(
			clause.getKeyword(),
			SQLFragmentFormat
				.execute(
					clause.getTemplate().trim(),
					localTemplates.toArray(new String[localTemplates.size()])));
	}

	private static String concat(String src1, String src2) {
		return src1 + (U.isAvailable(src2) ? (" " + src2) : src2);

	}
}
