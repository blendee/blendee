package org.blendee.assist;

/**
 * SELECT 句で使用する、別名を付けることが可能な要素候補です。
 * @author 千葉 哲嗣
 */
public interface AliasableOffer extends SelectOffer, AssistColumn {

	/**
	 * SQL AS
	 * @param alias 別名
	 * @return alias
	 */
	SelectOffer AS(String alias);
}
