package org.blendee.support;

public interface AliasableOffer extends SelectOffer {

	SelectOffer AS(String alias);
}
