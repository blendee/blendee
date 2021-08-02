package org.blendee.orm;

import java.util.Objects;

import org.blendee.internal.U;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Bindable;

/**
 * 主キーを参照する外部キーを表すクラスで、 {@link PrimaryKey} と対応しています。
 * @author 千葉 哲嗣
 * @see PrimaryKey#getAllReferences()
 * @see PrimaryKey#getReferences(TablePath, String)
 * @see PrimaryKey#getReferences(TablePath, String[])
 * @see DataObject#getForeignKey(String)
 * @see DataObject#getForeignKey(String[])
 */
public class ForeignKey extends PartialData {

	private final String name;

	private final int hashCode;

	private final PrimaryKey reference;

	ForeignKey(
		TablePath path,
		String name,
		String[] columnNames,
		Bindable[] bindables,
		PrimaryKey reference) {
		super(path, columnNames, bindables);
		this.name = name;
		this.reference = reference;
		Object[] objects = new Object[columnNames.length + bindables.length + 1];
		objects[0] = path;
		System.arraycopy(columnNames, 0, objects, 1, columnNames.length);
		System.arraycopy(bindables, 0, objects, columnNames.length + 1, bindables.length);
		hashCode = Objects.hash(objects);
	}

	/**
	 * この外部キーが参照している主キーのインスタンスを返します。
	 * @return 参照している主キー
	 */
	public PrimaryKey getReferenced() {
		return reference;
	}

	/**
	 * この外部キーの名前を返します。
	 * @return 外部キー名
	 */
	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ForeignKey)) return false;
		ForeignKey target = (ForeignKey) o;
		return path.equals(target.path)
			&& U.equals(columnNames, target.columnNames)
			&& U.equals(bindables, target.bindables);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}
