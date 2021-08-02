package org.blendee.orm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.blendee.internal.U;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.MetadataUtilities;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Bindable;
import org.blendee.sql.BindableConverter;
import org.blendee.sql.Criteria;
import org.blendee.sql.RuntimeId;
import org.blendee.sql.RuntimeIdFactory;
import org.blendee.sql.Relationship;
import org.blendee.sql.UpdateDMLBuilder;

/**
 * {@link DataObject} 内の主キー部分を表し、主キーに関連した操作を行うことができるクラスです。
 * @author 千葉 哲嗣
 * @see DataObject#getPrimaryKey()
 */
public class PrimaryKey extends PartialData {

	private final int hashCode;

	/**
	 * 文字列からこのクラスのインスタンスを作り出す簡易コンストラクタです。
	 * @param path 対象となるテーブル
	 * @param keyMembers 主キーを構成する値
	 * @return このクラスのインスタンス
	 */
	public static PrimaryKey getInstance(
		TablePath path,
		String... keyMembers) {
		return new PrimaryKey(path, BindableConverter.convert(keyMembers));
	}

	/**
	 * 数値からこのクラスのインスタンスを作り出す簡易コンストラクタです。
	 * @param path 対象となるテーブル
	 * @param keyMembers 主キーを構成する値
	 * @return このクラスのインスタンス
	 */
	public static PrimaryKey getInstance(
		TablePath path,
		Number... keyMembers) {
		return new PrimaryKey(path, BindableConverter.convert(keyMembers));
	}

	/**
	 * このクラスのインスタンスを生成します。
	 * @param path 対象となるテーブル
	 * @param bindables 値
	 * @throws IllegalArgumentException path の主キーのカラム数と bindables の要素数が違う場合
	 */
	public PrimaryKey(
		TablePath path,
		Bindable... bindables) {
		super(path, MetadataUtilities.getPrimaryKeyColumnNames(path), bindables);
		Object[] objects = new Object[bindables.length + 1];
		objects[0] = path;
		System.arraycopy(bindables, 0, objects, 1, bindables.length);
		hashCode = Objects.hash(objects);
	}

	@Override
	public Criteria getCriteria(Relationship relationship, RuntimeId id) {
		return createCriteria(id, relationship.getPrimaryKeyColumns(), bindables);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PrimaryKey)) return false;
		PrimaryKey target = (PrimaryKey) o;
		return path.equals(target.path) && U.equals(bindables, target.bindables);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	/**
	 * この主キーの名前を返します。
	 * @return この主キーの名前
	 */
	public String getName() {
		return MetadataUtilities.getPrimaryKeyName(path);
	}

	/**
	 * この主キーを参照している全外部キーのインスタンスを返します。
	 * @return この主キーを参照している全外部キーのインスタンス
	 */
	public ForeignKey[] getAllReferences() {
		CrossReference[] references = MetadataUtilities.getCrossReferencesOfExportedKeys(path);
		ForeignKey[] keys = new ForeignKey[references.length];
		for (int i = 0; i < references.length; i++) {
			CrossReference reference = references[i];
			keys[i] = getReferencesInternal(reference);
		}
		return keys;
	}

	/**
	 * この主キーを参照している外部キーのインスタンスを返します。
	 * @param referencesPath 参照しているテーブル
	 * @param columnNames 参照している外部キーのカラム
	 * @return この主キーを参照している外部キーのインスタンス
	 * @throws IllegalArgumentException referencesPath に columnNames で構成される外部キーがない場合
	 */
	public ForeignKey getReferences(
		TablePath referencesPath,
		String[] columnNames) {
		CrossReference[] references = MetadataUtilities.getCrossReferencesOfExportedKeys(path);
		String[] myColumns = columnNames.clone();
		Arrays.sort(myColumns);
		for (int i = 0; i < references.length; i++) {
			CrossReference reference = references[i];
			String[] foreignKeyColumnNames = reference.getForeignKeyColumnNames();
			Arrays.sort(foreignKeyColumnNames);
			if (referencesPath.equals(reference.getForeignKeyTable())
				&&
				U.equals(myColumns, foreignKeyColumnNames))
				return getReferencesInternal(reference);
		}

		//外部キーが見つかりません
		throw new IllegalArgumentException("FK not found");
	}

	/**
	 * この主キーを参照している外部キーのインスタンスを返します。
	 * @param referencesPath 参照しているテーブル
	 * @param foreignKeyName 参照している外部キーの名前
	 * @return この主キーを参照している外部キーのインスタンス
	 * @throws IllegalArgumentException referencesPath に foreignKeyName という外部キーがない場合
	 */
	public ForeignKey getReferences(
		TablePath referencesPath,
		String foreignKeyName) {
		CrossReference[] references = MetadataUtilities.getCrossReferencesOfExportedKeys(path);
		for (int i = 0; i < references.length; i++) {
			CrossReference reference = references[i];
			if (referencesPath.equals(reference.getForeignKeyTable())
				&&
				foreignKeyName.equals(reference.getForeignKeyName()))
				return getReferencesInternal(reference);
		}

		//外部キーが見つかりません
		throw new IllegalArgumentException("FK is not found.");
	}

	/**
	 * この主キーを参照している外部キーの値を、このインスタンスの値からパラメータのインスタンスの値に変更します。<br>
	 * to はこのインスタンスと同じテーブルの主キーである必要があります。
	 * @param to 変更する値
	 */
	public void switchReferences(PrimaryKey to) {
		switchAllReferences(MetadataUtilities.getCrossReferencesOfExportedKeys(path), this, to);
	}

	/**
	 * この主キーを参照している全テーブルの外部キー項目を NULL にします
	 * 参照している項目がそのテーブルの主キーに含まれている場合、 NULL にはできません
	 */
	public void eraseReferences() {
		TablePath[] tables = MetadataUtilities.getResourcesOfExportedKey(path);
		for (TablePath table : tables) {
			CrossReference[] references = MetadataUtilities.getCrossReferences(path, table);
			eraseReferences(table, references, this);
		}
	}

	private ForeignKey getReferencesInternal(CrossReference reference) {
		//MetadataUtilities.getPrimaryKeyColumnNames(path)の返す
		//項目名の順にbindablesを並び替え
		String[] columnNames = MetadataUtilities.getPrimaryKeyColumnNames(path);
		Map<String, Bindable> map = new HashMap<>();
		for (int i = 0; i < columnNames.length; i++) {
			map.put(reference.convertToForeignKeyColumnName(columnNames[i]), bindables[i]);
		}

		String[] foreignKeyColumnNames = reference.getForeignKeyColumnNames();
		Bindable[] foreignKeyBindables = new Bindable[bindables.length];
		for (int i = 0; i < foreignKeyColumnNames.length; i++) {
			foreignKeyBindables[i] = map.get(foreignKeyColumnNames[i]);
		}

		return new ForeignKey(
			reference.getForeignKeyTable(),
			reference.getForeignKeyName(),
			foreignKeyColumnNames,
			foreignKeyBindables,
			this);
	}

	private static void eraseReferences(
		TablePath path,
		CrossReference[] references,
		PrimaryKey primaryKey) {
		for (CrossReference reference : references) {
			UpdateDMLBuilder builder = new UpdateDMLBuilder(reference.getForeignKeyTable());
			String[] columnNames = reference.getForeignKeyColumnNames();
			for (String columnName : columnNames) {
				builder.addSQLFragment(columnName, "NULL");
			}

			builder.setCriteria(primaryKey.getReferencesInternal(reference).getCriteria(RuntimeIdFactory.stubInstance()));

			try (BStatement statement = BlendeeManager
				.getConnection()
				.getStatement(builder.toString(), builder)) {
				statement.executeUpdate();
			}
		}
	}

	private static void switchAllReferences(
		CrossReference[] references,
		PartialData from,
		PartialData to) {
		for (int i = 0; i < references.length; i++) {
			switchReference(references[i], from, to);
		}
	}

	private static void switchReference(
		CrossReference reference,
		PartialData from,
		PartialData to) {
		from = convertToReferences(reference, from);
		to = convertToReferences(reference, to);
		Set<String> checker = createChecker(to.getColumnNames());

		TablePath foreignKeyTable = reference.getForeignKeyTable();

		String[] keyNames = MetadataUtilities.getPrimaryKeyColumnNames(foreignKeyTable);
		for (String keyName : keyNames) {
			if (!checker.contains(keyName)) continue;
			CrossReference[] references = MetadataUtilities.getCrossReferencesOfExportedKeys(foreignKeyTable);
			if (references.length == 0) break;
			copy(reference.getForeignKeyTable(), from, to);
			switchAllReferences(references, from, to);
			DataAccessHelper.deleteInternal(from.path, from.getCriteria(RuntimeIdFactory.stubInstance()));
			return;
		}
		update(reference, from, to);
	}

	private static void copy(
		TablePath foreignKeyTable,
		PartialData from,
		final PartialData to) {
		StringBuilder sql = new StringBuilder("INSERT INTO " + foreignKeyTable + " SELECT ");

		Set<String> checker = createChecker(from.getColumnNames());

		String[] columnNames = MetadataUtilities.getColumnNames(foreignKeyTable);

		for (int i = 0; i < columnNames.length; i++) {
			if (checker.contains(columnNames[i])) columnNames[i] = "?";
		}
		sql.append(String.join(", ", columnNames));
		sql.append(" FROM ");
		sql.append(foreignKeyTable);
		final Criteria criteria = from.getCriteria(RuntimeIdFactory.stubInstance());
		sql.append(criteria.toString(false));

		try (BStatement statement = BlendeeManager
			.getConnection()
			.getStatement(sql.toString(), s -> {
				int i = 0;
				for (; i < to.bindables.length; i++) {
					to.bindables[i].toBinder().bind(i + 1, s);
				}

				criteria.complement(i, s);
			})) {
			statement.executeUpdate();
		}
	}

	private static void update(
		CrossReference reference,
		PartialData from,
		PartialData to) {
		UpdateDMLBuilder builder = new UpdateDMLBuilder(reference.getForeignKeyTable());
		builder.setCriteria(from.getCriteria(RuntimeIdFactory.stubInstance()));
		builder.add(to);
		try (BStatement statement = BlendeeManager
			.getConnection()
			.getStatement(builder.toString(), builder)) {
			statement.executeUpdate();
		}
	}

	private static Set<String> createChecker(String[] keyColumnNames) {
		Set<String> keyColumnSet = new HashSet<>();
		for (int i = 0; i < keyColumnNames.length; i++) {
			keyColumnSet.add(keyColumnNames[i]);
		}
		return keyColumnSet;
	}

	private static PartialData convertToReferences(
		final CrossReference reference,
		final PartialData persistence) {
		String[] columnNames = persistence.getColumnNames();
		String[] referencesNames = new String[columnNames.length];
		for (int i = 0; i < columnNames.length; i++) {
			referencesNames[i] = reference.convertToForeignKeyColumnName(columnNames[i]);
		}
		return new PartialData(reference.getForeignKeyTable(), referencesNames, persistence.bindables);
	}
}
