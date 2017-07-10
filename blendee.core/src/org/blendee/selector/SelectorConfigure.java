package org.blendee.selector;

import org.blendee.jdbc.ManagementSubject;

/**
 * {@link Selector} に対する設定値を保持するクラスです。
 * @author 千葉 哲嗣
 */
public class SelectorConfigure implements ManagementSubject {

	private String forUpdateClause = " FOR UPDATE";

	private String nowaitClause = " NOWAIT";

	/**
	 * SELECT 文に付加する FOR UPDATE 句 を変更します。
	 * @param clause 新しい FOR UPDATE 句
	 */
	public synchronized void setForUpdateClause(String clause) {
		forUpdateClause = " " + clause.trim();
	}

	/**
	 * 現在このクラスに設定されている FOR UPDATE 句 を変更します。
	 * @return 現在の FOR UPDATE 句
	 */
	public synchronized String getForUpdateClause() {
		return forUpdateClause;
	}

	/**
	 * SELECT 文に付加する NOWAIT 句 を変更します。
	 * @param clause 新しい NOWAIT 句
	 */
	public synchronized void setNowaitClause(String clause) {
		nowaitClause = " " + clause.trim();
	}

	/**
	 * 現在このクラスに設定されている NOWAIT 句 を変更します。
	 * @return 現在の NOWAIT 句
	 */
	public synchronized String getNowaitClause() {
		return nowaitClause;
	}
}
