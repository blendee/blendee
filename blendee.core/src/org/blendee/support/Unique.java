package org.blendee.support;

import java.util.Iterator;
import java.util.Optional;

class Unique {

	static <R, I extends Iterator<R>> Optional<R> get(I iterator) {
		if (!iterator.hasNext()) return Optional.empty();
		R row = iterator.next();
		if (iterator.hasNext()) throw new NotUniqueException();
		return Optional.of(row);
	}
}
