package org.blendee.sql;

import java.util.function.Consumer;

import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.TablePath;

/**
 * {@link Column} クラスのインスタンス取得を簡易にするために、仮の {@link Column} として使用できるクラスです。<br>
 * ただし、このクラスのインスタンスは一旦 {@link SQLQueryBuilder} で本当の {@link Column} が確定するか、直接 {@link #prepareForSQL(Relationship)} を実行するまでは、ほとんどの機能は使用することができません。
 * @author 千葉 哲嗣
 */
public class PhantomColumn implements Column {

	private final Object lock = new Object();

	private final String name;

	private final int phantomHashCode;

	private Column substance;

	/**
	 * このクラスのインスタンスを生成します。<br>
	 * 使用される {@link TablePath} は後に検索対象となる {@link Relationship} が持つものになります。
	 * @param name カラム名
	 */
	public PhantomColumn(String name) {
		this.name = name;
		phantomHashCode = name.hashCode();
	}

	/**
	 * 本当の {@link Column} が決定するまでは、このクラス独自のハッシュコードを返します。
	 * @return {@link Column} が決定すると、その {@link Column} のハッシュコード、そうでなければ path と name から算出したハッシュコード
	 */
	@Override
	public int hashCode() {
		Column substance = getSubstanceWithoutCheck();
		if (substance != null) return substance.hashCode();
		return phantomHashCode;
	}

	/**
	 * 本当の {@link Column} が決定するまでは、このクラスのインスタンス同士の equals になります。
	 * @return {@link Column} が決定すると、その {@link Column} との equals 、そうでなければ path と name での equals
	 */
	@Override
	public boolean equals(Object o) {
		Column substance = getSubstanceWithoutCheck();
		if (substance != null) return substance.equals(o);
		if (!(o instanceof PhantomColumn)) return false;
		PhantomColumn target = (PhantomColumn) o;

		return name.equals(target.name);
	}

	/**
	 * 本当の {@link Column} が決定するまでは、このクラスのインスタンス同士の比較値を返します。
	 * @return {@link Column} が決定すると、その {@link Column} との比較値 、そうでなければ path と name での比較値
	 */
	@Override
	public int compareTo(Column target) {
		Column substance = getSubstanceWithoutCheck();
		if (substance != null) return substance.compareTo(target);
		if (!(target instanceof PhantomColumn)) return -1;
		PhantomColumn phantomColumnTarget = (PhantomColumn) target;

		return name.compareTo(phantomColumnTarget.name);
	}

	@Override
	public String getId() {
		return getSubstanceWithCheck().getId();
	}

	@Override
	public String toString() {
		return getSubstanceWithCheck().toString();
	}

	@Override
	public Criteria getCriteria(RuntimeId id, Bindable bindable) {
		return getSubstanceWithCheck().getCriteria(id, bindable);
	}

	@Override
	public Relationship getRelationship() {
		return getSubstanceWithCheck().getRelationship();
	}

	@Override
	public Relationship getRootRelationship() {
		return getRelationship().getRoot();
	}

	@Override
	public void setRelationship(Consumer<Relationship> consumer) {
		getSubstanceWithCheck().setRelationship(consumer);
	}

	@Override
	public String getName() {
		return getSubstanceWithCheck().getName();
	}

	@Override
	public Class<?> getType() {
		return getSubstanceWithCheck().getType();
	}

	@Override
	public ColumnMetadata getColumnMetadata() {
		return getSubstanceWithCheck().getColumnMetadata();
	}

	@Override
	public String getComplementedName(RuntimeId id) {
		return getSubstanceWithCheck().getComplementedName(id);
	}

	@Override
	public Column findAnotherRootColumn(Relationship another) {
		return getSubstanceWithCheck().findAnotherRootColumn(another);
	}

	@Override
	public boolean isPrimaryKey() {
		return getSubstanceWithCheck().isPrimaryKey();
	}

	@Override
	public Column replicate() {
		return new PhantomColumn(name);
	}

	/**
	 * このインスタンスの Relationship ルートを決定します。
	 * @param sqlRoot このインスタンスのルートとなる {@link Relationship}
	 * @throws IllegalStateException sqlRoot がルートではないとき
	 * @throws IllegalStateException このインスタンスに既に別のルートが決定しているとき
	 */
	@Override
	public void prepareForSQL(Relationship sqlRoot) {
		//sqlRoot + " はルートではありません"
		if (!sqlRoot.isRoot()) throw new IllegalStateException(sqlRoot + " is not root.");
		synchronized (lock) {
			if (substance != null && !substance.getRootRelationship().equals(sqlRoot))
				//"このインスタンスは既に " + substance + " として使われています"
				throw new IllegalStateException("This instance is already used as " + substance + ".");

			substance = sqlRoot.getColumn(name);
		}
	}

	static Column[] convert(String[] columnNames) {
		Column[] columns = new Column[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			columns[i] = new PhantomColumn(columnNames[i]);
		}
		return columns;
	}

	private Column getSubstanceWithoutCheck() {
		synchronized (lock) {
			return substance;
		}
	}

	private Column getSubstanceWithCheck() {
		synchronized (lock) {
			if (substance == null) throw new IllegalStateException();
			return substance;
		}
	}
}
