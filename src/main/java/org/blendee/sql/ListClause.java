package org.blendee.sql;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.blendee.jdbc.BPreparedStatement;
import org.blendee.jdbc.ChainPreparedStatementComplementer;

/**
 * 要素を並列に複数持つクエリの句を表す基底クラスです。
 * @author 千葉 哲嗣
 * @param <T> サブクラスの型
 */
public abstract class ListClause<T extends ListClause<?>> extends Clause {

	/**
	 * JOIN した場合に採用されるデフォルトの順序値
	 */
	public static final int DEFAULT_ORDER = Integer.MAX_VALUE;

	final List<ListQueryBlock> blocks = new LinkedList<>();

	/**
	 * @param id {@link RuntimeId}
	 */
	protected ListClause(RuntimeId id) {
		super(id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ListClause<?>)) return false;
		if (!getClass().equals(o.getClass())) return false;
		var target = (ListClause<?>) o;
		return blocks.equals(target.blocks);
	}

	@Override
	public int hashCode() {
		return Objects.hash(blocks);
	}

	@Override
	public int getColumnsSize() {
		int[] size = { 0 };
		blocks.forEach(b -> {
			size[0] += b.getColumnsSize();
		});

		return size[0];
	}

	@Override
	public T replicate() {
		T clone = createNewInstance(runtimeId);
		blocks.forEach(b -> clone.blocks.add(b.replicate()));
		return clone;
	}

	/**
	 * GROUP BY 句に新しいカラムを追加します。
	 * @param columns 新しいカラム
	 */
	public void add(Column... columns) {
		for (var column : columns) {
			add(column);
		}
	}

	/**
	 * GROUP BY 句に新しいカラムを追加します。
	 * @param columnNames 新しいカラム
	 */
	public void add(String... columnNames) {
		for (var columnName : columnNames) {
			add(new PhantomColumn(columnName));
		}
	}

	/**
	 * この句に記述可能な SQL 文のテンプレートを追加します。
	 * @param order JOIN したときの順序
	 * @param template SQL 文のテンプレート
	 * @param columns SQL 文に含まれるカラム
	 * @param complementer {@link ChainPreparedStatementComplementer}
	 * @see SQLFragmentFormat
	 */
	public void add(
		int order,
		String template,
		Column[] columns,
		ChainPreparedStatementComplementer complementer) {
		var block = new ListQueryBlock(runtimeId, order);

		for (var column : columns)
			block.addColumn(column);

		if (complementer != null)
			block.setComplementer(complementer);

		block.addTemplate(template.trim());

		addBlock(block);
	}

	@Override
	public int complement(int done, BPreparedStatement statement) {
		for (var block : blocks) {
			done = block.complement(done, statement);
		}

		return done;
	}

	/**
	 * @param id {@link RuntimeId}
	 * @return サブクラスのインスタンス
	 */
	protected abstract T createNewInstance(RuntimeId id);

	/**
	 * この句にカラムとテンプレートのブロックを追加します。
	 * @param block カラムとテンプレート
	 */
	protected void addBlock(ListQueryBlock block) {
		blocks.add(block);

		//常に順序付けしておく
		Collections.sort(blocks);
	}

	/**
	 * この句にカラムとテンプレート {0} を追加します。
	 * @param column カラム
	 */
	protected void addInternal(Column column) {
		addInternal(column, "{0}");
	}

	/**
	 * この句にカラムとテンプレートを追加します。
	 * @param column カラム
	 * @param template テンプレート
	 */
	protected void addInternal(Column column, String template) {
		addInternal(DEFAULT_ORDER, column, template);
	}

	/**
	 * この句にカラムとテンプレートを追加します。
	 * @param order JOIN したときの順序
	 * @param column カラム
	 * @param template テンプレート
	 */
	protected void addInternal(int order, Column column, String template) {
		var block = new ListQueryBlock(runtimeId, order);
		block.addColumn(column);
		block.addTemplate(template);
		addBlock(block);
	}

	/**
	 * 要素を持っているかどうかを返します。
	 * @return 要素を持っているかどうか
	 */
	protected boolean hasElements() {
		return blocks.size() > 0;
	}

	@Override
	String getTemplate() {
		var templates = new LinkedList<String>();

		var counter = new WholeCounter();
		blocks.forEach(b -> {
			templates.add(b.getTemplate(counter));
		});

		return String.join(", ", templates);
	}

	@Override
	List<Column> getColumnsInternal() {
		var columns = new LinkedList<Column>();

		blocks.forEach(b -> columns.addAll(b.getColumns(runtimeId)));

		return columns;
	}

	void merge(ListClause<?> another) {
		blocks.addAll(another.blocks);
	}

	void sortBlocks() {
		Collections.sort(blocks);
	}

	static class WholeCounter {

		int i = 0;
	}
}
