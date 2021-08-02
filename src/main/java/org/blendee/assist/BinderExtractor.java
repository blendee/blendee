package org.blendee.assist;

import org.blendee.jdbc.ContextManager;
import org.blendee.sql.Bindable;
import org.blendee.sql.Binder;
import org.blendee.sql.ValueExtractors;
import org.blendee.sql.ValueExtractorsConfigure;

class BinderExtractor {

	private final ValueExtractors valueExtractors = ContextManager.get(ValueExtractorsConfigure.class).getValueExtractors();

	Binder extract(Object object) {
		if (object instanceof Bindable) return ((Bindable) object).toBinder();

		return valueExtractors.selectValueExtractor(object.getClass()).extractAsBinder(object);
	}
}
