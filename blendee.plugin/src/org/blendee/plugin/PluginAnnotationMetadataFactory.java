package org.blendee.plugin;

import org.blendee.support.BEntity;
import org.blendee.util.AnnotationMetadataFactory;

public class PluginAnnotationMetadataFactory extends AnnotationMetadataFactory {

	private static final String entityClassName = BEntity.class.getName();

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
		return hasEntity(clazz.getInterfaces()) && !clazz.isInterface();
	}

	private static boolean hasEntity(Class<?>[] interfaces) {
		for (Class<?> clazz : interfaces) {
			if (clazz.getName().equals(entityClassName)) return true;
		}

		return false;
	}
}
