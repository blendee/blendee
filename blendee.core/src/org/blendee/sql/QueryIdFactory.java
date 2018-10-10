package org.blendee.sql;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;

import org.blendee.jdbc.TablePath;

@SuppressWarnings("javadoc")
public class QueryIdFactory {

	private static final Object lock = new Object();

	private static final String aliasPrefix = "r";

	private static final int threshold = 100;

	private static long next = 0;

	private static final WeakHashMap<QueryId, String> weakMap = new WeakHashMap<>();

	private static final List<String> allIds = new ArrayList<>();

	private static final LinkedList<String> idPool = new LinkedList<>();

	private static final QueryId stubInstance = new StubQueryId();

	/**
	 * ベースになる {@link TablePath} をもとにインスタンスを生成します。<br>
	 * エイリアスは一時的にこのインスタンスで確保され、このインスタンスが破棄されると再利用されます。
	 * @param table base
	 * @return instance
	 */
	public static QueryId getRuntimeInstance() {
		synchronized (lock) {
			int allSize = allIds.size();
			if (allSize == 0 || allSize < threshold) {
				newAlias();
			} else if (idPool.size() == 0) {
				preparePool();
			}

			String alias = idPool.pop();

			QueryId id = new ConcreteQueryId(alias);

			weakMap.put(id, alias);

			return id;
		}
	}

	public static QueryId getInstance() {
		return stubInstance;
	}

	private static void newAlias() {
		String alias = aliasPrefix + next++;
		allIds.add(alias);
		idPool.add(alias);
	}

	private static void preparePool() {
		List<String> remain = new ArrayList<>(allIds);
		remain.removeAll(weakMap.values());

		if (remain.size() == 0) {
			newAlias();
			return;
		}

		idPool.addAll(remain);
	}

	private static class ConcreteQueryId implements QueryId {

		private final String id;

		/**
		 * ベースになる {@link TablePath} をもとにインスタンスを生成します。
		 * @param table base
		 * @param id 指定 ID
		 */
		private ConcreteQueryId(String id) {
			Objects.requireNonNull(id);
			this.id = id;
		}

		@Override
		public String getId() {
			return id;
		}
	}

	private static class StubQueryId implements QueryId {

		@Override
		public String getId() {
			return "";
		}
	}
}
