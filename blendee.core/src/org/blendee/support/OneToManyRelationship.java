package org.blendee.support;

import java.util.function.Function;

import org.blendee.orm.DataObject;
import org.blendee.sql.Relationship;
import org.blendee.sql.RuntimeId;

/**
 * 一対多検索の内部の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public class OneToManyRelationship {

	private final OneToManyRelationship parent;

	private final Relationship relationship;

	private final Function<DataObject, Row> builder;

	private final RuntimeId id;

	/**
	 * @param parent {@link OneToManyRelationship}
	 * @param relationship {@link Relationship}
	 * @param builder {@link Function}
	 * @param id {@link RuntimeId}
	 */
	public OneToManyRelationship(
		OneToManyRelationship parent,
		Relationship relationship,
		Function<DataObject, Row> builder,
		RuntimeId id) {
		this.parent = parent;
		this.relationship = relationship;
		this.builder = builder;
		this.id = id;
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスが表す {@link Relationship}
	 */
	Relationship getRelationship() {
		return relationship;
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return このインスタンスをメンバとして保持している親インスタンス
	 */
	OneToManyRelationship getParent() {
		return parent;
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @param data {@link Row} の全要素の値を持つ検索結果オブジェクト
	 * @return 生成された {@link Row}
	 */
	Row createRow(DataObject data) {
		return builder.apply(data);
	}

	/**
	 * Query 内部処理用なので直接使用しないこと。
	 * @return {@link RuntimeId}
	 */
	RuntimeId getRuntimeId() {
		return id;
	}
}
