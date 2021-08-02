package org.blendee.assist;

/**
 * 句生成補助クラスの基底インターフェイスです。<br>
 * 主に任意のカラム表現の生成処理を定義します。
 * @author 千葉 哲嗣
 */
public interface ClauseAssist {

	/**
	 * 任意のカラム表現を生成します。
	 * @param expression テンプレート
	 * @param columns テンプレートにセットする {@link AssistColumn}
	 * @return 任意のカラム
	 */
	default AssistColumn expr(String expression, AssistColumn... columns) {
		return new AnyColumn(statement(), expression, columns);
	}

	/**
	 * 任意のカラム表現を生成します。
	 * @param expression テンプレート
	 * @param values プレースホルダの値
	 * @return 任意のカラム
	 */
	default AssistColumn expr(String expression, Object... values) {
		return new AnyColumn(statement(), expression, values);
	}

	/**
	 * 任意のカラム表現を生成します。
	 * @param value プレースホルダの値
	 * @return 任意のカラム
	 */
	default AssistColumn expr(Object value) {
		return new AnyColumn(statement(), value);
	}

	/**
	 * 任意のカラム表現を生成します。
	 * @param expression テンプレート
	 * @param columns テンプレートにセットする {@link AssistColumn}
	 * @param values プレースホルダの値
	 * @return 任意のカラム
	 */
	default AssistColumn expr(String expression, Vargs<AssistColumn> columns, Object... values) {
		return new AnyColumn(statement(), expression, columns.get(), values);
	}

	/**
	 * @return {@link Statement}
	 */
	Statement statement();
}
