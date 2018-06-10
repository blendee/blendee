package org.blendee.support;

import org.blendee.sql.Column;

/**
 * SELECT 句で使用する、別名を付けることが可能な要素候補です。
 * @author 千葉 哲嗣
 */
public abstract class AliasableOffer implements SelectOffer {

	/**
	 * SQL AS
	 * @param alias 別名
	 * @return alias
	 */
	public abstract SelectOffer AS(String alias);

	abstract Column column();
}