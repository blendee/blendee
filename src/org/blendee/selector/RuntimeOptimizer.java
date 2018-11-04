package org.blendee.selector;

import java.util.Objects;

import org.blendee.internal.U;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Column;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.Relationship;
import org.blendee.sql.SelectClause;

/**
 * @author 千葉 哲嗣
 */
public class RuntimeOptimizer extends SimpleSelectedValuesConverter implements Optimizer {

	private final TablePath path;

	private final RuntimeId id;

	private final SelectClause select;

	/**
	 * インスタンスを生成します。
	 * @param path 対象テーブル
	 * @param id {@link RuntimeId}
	 */
	public RuntimeOptimizer(TablePath path, RuntimeId id) {
		this.path = Objects.requireNonNull(path);
		this.id = Objects.requireNonNull(id);
		select = new SelectClause(id);
	}

	/**
	 * SELECT 句に含めるカラムを追加します。
	 * @param column SELECT 対象
	 */
	public void add(Column column) {
		Objects.requireNonNull(column);
		check(column.getRootRelationship());
		select.add(column);
	}

	/**
	 * SELECT 句に含めるカラムを追加します。<br>
	 * {@link Relationship} の全カラムが対象となります。
	 * @param relation SELECT 対象
	 */
	public void addAll(Relationship relation) {
		Objects.requireNonNull(relation);
		check(relation.getRoot());
		select.add(relation);
	}

	@Override
	public TablePath getTablePath() {
		return path;
	}

	@Override
	public RuntimeId getRuntimeId() {
		return id;
	}

	@Override
	public SelectClause getOptimizedSelectClause() {
		return select;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}

	void check(Relationship root) {
		if (!path.equals(root.getTablePath()))
			throw new IllegalArgumentException(path + " でなければなりません");
	}
}
