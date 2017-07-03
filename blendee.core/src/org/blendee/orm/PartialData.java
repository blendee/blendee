package org.blendee.orm;

import org.blendee.internal.U;
import org.blendee.jdbc.BlendeeContext;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Bindable;
import org.blendee.sql.Column;
import org.blendee.sql.Condition;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.Searchable;
import org.blendee.sql.Updatable;
import org.blendee.sql.Updater;

/**
 * テーブルとその中の複数のカラムと、それらの値を持つ物を定義した基底クラスです。
 *
 * @author 千葉 哲嗣
 */
public class PartialData implements Searchable, Updatable {

	/**
	 * このインスタンスの属するテーブル。
	 */
	protected final TablePath path;

	/**
	 * このインスタンスが持つカラム。
	 */
	protected final String[] columnNames;

	/**
	 * このインスタンスが持つカラムの値。
	 */
	protected final Bindable[] bindables;

	/**
	 * このクラスのインスタンスを生成します。
	 *
	 * @param path このインスタンスの属するテーブル
	 * @param columnNames このインスタンスが持つカラム
	 * @param bindables このインスタンスが持つカラムの値
	 * @throws IllegalArgumentException columnNames と bindables の要素数が一致しない場合
	 */
	public PartialData(
		TablePath path,
		String[] columnNames,
		Bindable[] bindables) {
		if (columnNames.length != bindables.length)
			throw new IllegalArgumentException("columnNames と bindables の要素数が一致しません");
		this.path = path;
		this.columnNames = columnNames.clone();
		this.bindables = bindables.clone();
	}

	/**
	 * このインスタンスの属するテーブルを返します。
	 *
	 * @return このインスタンスの属するテーブル
	 */
	public TablePath getTablePath() {
		return path;
	}

	/**
	 * このインスタンスが持つカラムを返します。
	 *
	 * @return このインスタンスが持つカラム
	 */
	public String[] getColumnNames() {
		return columnNames.clone();
	}

	/**
	 * このインスタンスが持つカラムの値を返します。
	 *
	 * @return このインスタンスが持つカラムの値
	 */
	public Bindable[] getBindables() {
		return bindables.clone();
	}

	/**
	 * このインスタンス持つ全カラムとその値を AND で結合した検索条件を生成し、返します。
	 * <br>
	 * 検索条件のテーブルは、このインスタンスが持つテーブルになります。
	 *
	 * @return 検索条件
	 */
	public Condition getCondition() {
		return getCondition(BlendeeContext.get(RelationshipFactory.class).getInstance(path));
	}

	/**
	 * このインスタンス持つ全カラムとその値を AND で結合した検索条件を生成し、返します。
	 * <br>
	 * 検索条件のテーブルはパラメータとして受け取ります。
	 *
	 * @param relationship 検索条件のテーブル
	 * @return 検索条件
	 */
	@Override
	public Condition getCondition(Relationship relationship) {
		Column[] columns = new Column[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			columns[i] = relationship.getColumn(columnNames[i]);
		}
		return createCondition(columns, bindables);
	}

	@Override
	public void setValuesTo(Updater updater) {
		for (int i = 0; i < columnNames.length; i++) {
			updater.add(columnNames[i], bindables[i]);
		}
	}

	static Condition createCondition(Column[] columns, Bindable[] bindables) {
		Column column = columns[0];
		Condition condition = column.getCondition(bindables[0]);
		for (int i = 1; i < columns.length; i++) {
			column = columns[i];
			condition.and(column.getCondition(bindables[i]));
		}

		return condition;
	}

	@Override
	public String toString() {
		return U.toString(this);
	}
}
