package org.blendee.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class PlaybackableCache {

	private static final Map<Class<Function<?, ?>>, Playbackable<?>> cache = new HashMap<>();

	public void regist(Class<Function<?, ?>> lambdaClass, Playbackable<?> playbackable) {
		Objects.requireNonNull(lambdaClass);
		Objects.requireNonNull(playbackable);
		synchronized (cache) {
			cache.put(lambdaClass, playbackable);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> Playbackable<T> get(Class<Function<?, ?>> lambdaClass) {
		synchronized (cache) {
			return (Playbackable<T>) cache.get(lambdaClass);
		}

	};
}
