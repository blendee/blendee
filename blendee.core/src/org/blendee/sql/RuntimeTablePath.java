package org.blendee.sql;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;

import org.blendee.jdbc.TablePath;

/**
 * 一時的なエイリアス名を持った {@link TablePath} です。
 * @author 千葉 哲嗣
 */
public class RuntimeTablePath extends TablePath {

	private static final Object lock = new Object();

	private static long next = 0;

	private static final WeakHashMap<RuntimeTablePath, String> pathMap = new WeakHashMap<>();

	private static final List<String> allAliases = new LinkedList<>();

	private static final LinkedList<String> aliasPool = new LinkedList<>();

	private final String alias;

	/**
	 * ベースになる {@link TablePath} をもとにインスタンスを生成します。<br>
	 * エイリアスは一時的にこのインスタンスで確保され、このインスタンスが破棄されると再利用されます。
	 * @param table base
	 * @return instance
	 */
	public static RuntimeTablePath getInstance(TablePath table) {
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

			RuntimeTablePath path = new RuntimeTablePath(table, alias);

			pathMap.put(path, alias);

			return path;
		}
	}

	/**
	 * ベースになる {@link TablePath} をもとにインスタンスを生成します。
	 * @param table base
	 * @param alias 指定エイリアス
	 * @return instance
	 */
	public static RuntimeTablePath getInstance(TablePath table, String alias) {
		return new RuntimeTablePath(table, alias);
	}

	private RuntimeTablePath(TablePath table, String alias) {
		super(table.getSchemaName(), table.getTableName());
		Objects.requireNonNull(alias);
		this.alias = alias;
	}

	/**
	 * @return alias
	 */
	public String getAlias() {
		return alias;
	}
}
