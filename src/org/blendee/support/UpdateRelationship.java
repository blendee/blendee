package org.blendee.support;

import java.util.Arrays;

/**
 * 自動生成される ConcreteQueryRelationship の振る舞いを定義したインターフェイスです。<br>
 * これらのメソッドは、内部使用を目的としていますので、直接使用しないでください。
 * @author 千葉 哲嗣
 */
public interface UpdateRelationship extends TableFacadeRelationship {

	/**
	 * INSERT 文生成用メソッドです。<br>
	 * パラメータの項目と順序を UPDATE 文に割り当てます。
	 * @param columns UPDATE 文に含めるテーブルおよびカラム
	 * @return element
	 */
	default SetElement list(UpdateColumn... columns) {
		return ls(columns);
	}

	/**
	 * {@link #list} の短縮形です。
	 * パラメータの項目と順序を UPDATE 文に割り当てます。
	 * @param columns UPDATE 文に含めるテーブルおよびカラム
	 * @return element
	 */
	default SetElement ls(UpdateColumn... columns) {
		SetElement element = new SetElement(this);
		Arrays.stream(columns).map(c -> c.column()).forEach(c -> element.addColumn(c));

		return element;
	}

	/**
	 * INSERT 文生成用メソッドです。<br>
	 * パラメータの項目と順序を UPDATE 文に割り当てます。
	 * @param proofs INSERT 文に含めるテーブルおよびカラム
	 */
	default void list(SetProof... proofs) {}

	/**
	 * {@link #list} の短縮形です。
	 * パラメータの項目と順序を UPDATE 文に割り当てます。
	 * @param proofs INSERT 文に含めるテーブルおよびカラム
	 */
	default void ls(SetProof... proofs) {}
}
