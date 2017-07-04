package org.blendee.plugin;

import org.blendee.support.Row;
import org.blendee.util.AnnotationMetadataFactory;

public class PluginAnnotationMetadataFactory extends AnnotationMetadataFactory {

	private static final String rowClassName = Row.class.getName();

	private static ClassLoader loader;

	static void setClassLoader(ClassLoader loader) {
		PluginAnnotationMetadataFactory.loader = loader;
	}

	@Override
	protected ClassLoader getClassLoader() {
		return loader;
	}

	@Override
	protected boolean matches(Class<?> clazz) {
		return hasRow(clazz.getInterfaces()) && !clazz.isInterface();
	}

	private static boolean hasRow(Class<?>[] interfaces) {
		for (Class<?> clazz : interfaces) {
			if (clazz.getName().equals(rowClassName)) return true;
		}

		return false;
	}
}
