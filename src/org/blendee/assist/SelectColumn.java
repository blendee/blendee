package org.blendee.assist;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.blendee.sql.Column;

/**
 * SELECT 句に新しい要素を追加するクラスです。<br>
 * このクラスのインスタンスは、テーブルのカラムに対応しています。
 * @author 千葉 哲嗣
 */
public class SelectColumn implements AliasableOffer {

	private final TableFacadeAssist assist;

	private final Column column;

	/**
	 * 内部的にインスタンス化されるため、直接使用する必要はありません。
	 * @param assist 条件作成に必要な情報を持った {@link TableFacadeAssist}
	 * @param name カラム名
	 */
	public SelectColumn(TableFacadeAssist assist, String name) {
		this.assist = assist;
		column = assist.getRelationship().getColumn(name);
	}

	@Override
	public List<ColumnExpression> get() {
		List<ColumnExpression> list = new LinkedList<>();
		list.add(new ColumnExpression(assist.getSelectStatement(), column));

		return list;
	}

	/**
	 * カラムに別名を付けます。<br>
	 * 別名をつけてしまうと {@link Query#aggregate(Consumer)} しか使用できなくなります。
	 * @param alias 別名
	 * @return {@link SelectOffer}
	 */
	@Override
	public SelectOffer AS(String alias) {
		assist.getSelectStatement().quitRowMode();
		ColumnExpression expression = new ColumnExpression(assist.getSelectStatement(), column);
		expression.AS(alias);
		return expression;
	}

	@Override
	public Column column() {
		return column;
	}

	@Override
	public Statement statement() {
		return assist.getSelectStatement();
	}
}
