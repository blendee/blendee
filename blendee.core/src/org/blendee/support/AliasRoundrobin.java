package org.blendee.support;

import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;

import org.blendee.jdbc.TablePath;
import org.blendee.sql.AliasTablePath;

public class AliasRoundrobin {

	private static final Object lock = new Object();

	private static long next = 0;

	private static final WeakHashMap<AliasTablePath, String> pathMap = new WeakHashMap<>();

	private static final List<String> allAliases = new LinkedList<>();

	private static final LinkedList<String> aliasPool = new LinkedList<>();

	public static AliasTablePath publish(TablePath base) {
		synchronized (lock) {
			List<String> remain = new LinkedList<>(allAliases);
			remain.removeAll(pathMap.values());
			aliasPool.addAll(remain);

			String alias;
			if (aliasPool.size() == 0) {
				alias = "r" + next++;
				allAliases.add(alias);
			} else {
				alias = aliasPool.pop();
			}

			AliasTablePath path = new AliasTablePath(base, alias);

			pathMap.put(path, alias);

			return path;
		}
	}
}
