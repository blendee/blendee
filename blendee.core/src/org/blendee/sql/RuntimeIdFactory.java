package org.blendee.sql;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * {@link RuntimeId} 生成用ファクトリクラスです。
 * @author 千葉 哲嗣
 */
public class RuntimeIdFactory {

	private static final Object lock = new Object();

	private static final String aliasPrefix = "r";

	private static final int threshold = 100;

	private static long next = 0;

	private static final WeakHashMap<RuntimeId, String> weakMap = new WeakHashMap<>();

	private static final List<String> allIds = new ArrayList<>();

	private static final LinkedList<String> idPool = new LinkedList<>();

	private static final RuntimeId stubInstance = new StubRuntimeId();

	/**
	 * インスタンスを生成します。<br>
	 * エイリアスは一時的にこのインスタンスで確保され、このインスタンスが破棄されると再利用されます。
	 * @return instance
	 */
	public static RuntimeId getRuntimeInstance() {
		synchronized (lock) {
			int allSize = allIds.size();
			if (allSize == 0 || allSize < threshold) {
				newId();
			} else if (idPool.size() == 0) {
				preparePool();
			}

			String idString = idPool.pop();

			RuntimeId id = new ConcreteRuntimeId(idString);

			weakMap.put(id, idString);

			return id;
		}
	}

	/**
	 * スタブインスタンスを返します。<br>
	 * スタブインスタンスは、 ID 文字列を持ちません。
	 * @return スタブインスタンス
	 */
	public static RuntimeId getInstance() {
		return stubInstance;
	}

	private static void newId() {
		String alias = aliasPrefix + next++;
		allIds.add(alias);
		idPool.add(alias);
	}

	private static void preparePool() {
		List<String> remain = new ArrayList<>(allIds);
		remain.removeAll(weakMap.values());

		if (remain.size() == 0) {
			newId();
			return;
		}

		idPool.addAll(remain);
	}

	private static class ConcreteRuntimeId implements RuntimeId {

		private final String id;

		private ConcreteRuntimeId(String id) {
			Objects.requireNonNull(id);
			this.id = id;
		}

		@Override
		public String getId() {
			return id;
		}
	}

	private static class StubRuntimeId implements RuntimeId {

		@Override
		public String getId() {
			return "";
		}
	}
}
