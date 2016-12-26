package jp.ats.blendee.orm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.CrossReference;
import jp.ats.blendee.jdbc.BlendeeContext;
import jp.ats.blendee.jdbc.BlendeeManager;
import jp.ats.blendee.jdbc.BStatement;
import jp.ats.blendee.jdbc.MetadataUtilities;
import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.sql.Bindable;
import jp.ats.blendee.sql.Condition;
import jp.ats.blendee.sql.Relationship;
import jp.ats.blendee.sql.UpdateDMLBuilder;
import jp.ats.blendee.sql.binder.StringBinder;

/**
 * {@link DataObject} 内の主キー部分を表し、主キーに関連した操作を行うことができるクラスです。
 *
 * @author 千葉 哲嗣
 * @see DataObject#getPrimaryKey()
 */
public class PrimaryKey extends PartialData {

	private final int hashCode;

	/**
	 * 文字列からこのクラスのインスタンスを作り出す簡易コンストラクタです。
	 *
	 * @param locator 対象となるテーブル
	 * @param keyMembers 主キーを構成する値
	 * @return このクラスのインスタンス
	 */
	public static PrimaryKey getInstance(
		ResourceLocator locator,
		String... keyMembers) {
		Bindable[] bindables = new Bindable[keyMembers.length];
		for (int i = 0; i < keyMembers.length; i++) {
			bindables[i] = new StringBinder(keyMembers[i]);
		}
		return new PrimaryKey(locator, bindables);
	}

	/**
	 * このクラスのインスタンスを生成します。
	 *
	 * @param locator 対象となるテーブル
	 * @param bindables 値
	 * @throws IllegalArgumentException locator の主キーのカラム数と bindables の要素数が違う場合
	 */
	public PrimaryKey(
		ResourceLocator locator,
		Bindable[] bindables) {
		super(locator, MetadataUtilities.getPrimaryKeyColumnNames(locator), bindables);
		Object[] objects = new Object[bindables.length + 1];
		objects[0] = locator;
		System.arraycopy(bindables, 0, objects, 1, bindables.length);
		hashCode = Objects.hash(objects);
	}

	@Override
	public Condition getCondition(Relationship relationship) {
		return createCondition(relationship.getPrimaryKeyColumns(), bindables);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PrimaryKey)) return false;
		PrimaryKey target = (PrimaryKey) o;
		return locator.equals(target.locator) && U.equals(bindables, target.bindables);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	/**
	 * この主キーの名前を返します。
	 *
	 * @return この主キーの名前
	 */
	public String getName() {
		return MetadataUtilities.getPrimaryKeyName(locator);
	}

	/**
	 * この主キーを参照している全外部キーのインスタンスを返します。
	 *
	 * @return この主キーを参照している全外部キーのインスタンス
	 */
	public ForeignKey[] getAllReferences() {
		CrossReference[] references = MetadataUtilities.getCrossReferencesOfExportedKeys(locator);
		ForeignKey[] keys = new ForeignKey[references.length];
		for (int i = 0; i < references.length; i++) {
			CrossReference reference = references[i];
			keys[i] = getReferencesInternal(reference);
		}
		return keys;
	}

	/**
	 * この主キーを参照している外部キーのインスタンスを返します。
	 *
	 * @param referencesLocator 参照しているテーブル
	 * @param columnNames 参照している外部キーのカラム
	 * @return この主キーを参照している外部キーのインスタンス
	 * @throws IllegalArgumentException referencesLocator に columnNames で構成される外部キーがない場合
	 */
	public ForeignKey getReferences(
		ResourceLocator referencesLocator,
		String[] columnNames) {
		CrossReference[] references = MetadataUtilities.getCrossReferencesOfExportedKeys(locator);
		String[] myColumns = columnNames.clone();
		Arrays.sort(myColumns);
		for (int i = 0; i < references.length; i++) {
			CrossReference reference = references[i];
			String[] foreignKeyColumnNames = reference.getForeignKeyColumnNames();
			Arrays.sort(foreignKeyColumnNames);
			if (U.equals(myColumns, foreignKeyColumnNames)) return getReferencesInternal(reference);
		}
		throw new IllegalArgumentException("外部キーが見つかりません");
	}

	/**
	 * この主キーを参照している外部キーのインスタンスを返します。
	 *
	 * @param referencesLocator 参照しているテーブル
	 * @param foreignKeyName 参照している外部キーの名前
	 * @return この主キーを参照している外部キーのインスタンス
	 * @throws IllegalArgumentException referencesLocator に foreignKeyName という外部キーがない場合
	 */
	public ForeignKey getReferences(
		ResourceLocator referencesLocator,
		String foreignKeyName) {
		CrossReference[] references = MetadataUtilities.getCrossReferencesOfExportedKeys(locator);
		for (int i = 0; i < references.length; i++) {
			CrossReference reference = references[i];
			if (foreignKeyName.equals(reference.getForeignKeyName())) return getReferencesInternal(reference);
		}
		throw new IllegalArgumentException("外部キーが見つかりません");
	}

	/**
	 * この主キーを参照している外部キーの値を、このインスタンスの値からパラメータのインスタンスの値に変更します。
	 * <br>
	 * to はこのインスタンスと同じテーブルの主キーである必要があります。
	 *
	 * @param to 変更する値
	 */
	public void switchReferences(PrimaryKey to) {
		switchAllReferences(MetadataUtilities.getCrossReferencesOfExportedKeys(locator), this, to);
	}

	/**
	 * この主キーを参照している全テーブルの外部キー項目を NULL にします
	 * 参照している項目がそのテーブルの主キーに含まれている場合、 NULL にはできません
	 */
	public void eraseReferences() {
		ResourceLocator[] tables = MetadataUtilities.getResourcesOfExportedKey(locator);
		for (ResourceLocator table : tables) {
			CrossReference[] references = MetadataUtilities.getCrossReferences(locator, table);
			eraseReferences(table, references, this);
		}
	}

	private ForeignKey getReferencesInternal(CrossReference reference) {
		//MetadataUtilities.getPrimaryKeyColumnNames(locator)の返す
		//項目名の順にbindablesを並び替え
		String[] columnNames = MetadataUtilities.getPrimaryKeyColumnNames(locator);
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
			reference.getForeignKeyResource(),
			reference.getForeignKeyName(),
			foreignKeyColumnNames,
			foreignKeyBindables,
			this);
	}

	private static void eraseReferences(
		ResourceLocator locator,
		CrossReference[] references,
		PrimaryKey primaryKey) {
		for (CrossReference reference : references) {
			UpdateDMLBuilder builder = new UpdateDMLBuilder(reference.getForeignKeyResource());
			String[] columnNames = reference.getForeignKeyColumnNames();
			for (String columnName : columnNames) {
				builder.addSQLFragment(columnName, "NULL");
			}

			builder.setCondition(primaryKey.getReferencesInternal(reference).getCondition());

			try (BStatement statement = BlendeeContext.get(BlendeeManager.class)
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

		ResourceLocator foreignKeyTable = reference.getForeignKeyResource();

		String[] keyNames = MetadataUtilities.getPrimaryKeyColumnNames(foreignKeyTable);
		for (String keyName : keyNames) {
			if (!checker.contains(keyName)) continue;
			CrossReference[] references = MetadataUtilities.getCrossReferencesOfExportedKeys(foreignKeyTable);
			if (references.length == 0) break;
			copy(reference.getForeignKeyResource(), from, to);
			switchAllReferences(references, from, to);
			DataAccessHelper.deleteInternal(from.locator, from.getCondition());
			return;
		}
		update(reference, from, to);
	}

	private static void copy(
		ResourceLocator foreignKeyTable,
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
		final Condition condition = from.getCondition();
		sql.append(condition.toString(false));

		try (BStatement statement = BlendeeContext.get(BlendeeManager.class).getConnection().getStatement(sql.toString(), s -> {
			int i = 0;
			for (; i < to.bindables.length; i++) {
				to.bindables[i].toBinder().bind(i + 1, s);
			}

			return condition.getComplementer(i).complement(s);
		})) {
			statement.executeUpdate();
		}
	}

	private static void update(
		CrossReference reference,
		PartialData from,
		PartialData to) {
		UpdateDMLBuilder builder = new UpdateDMLBuilder(reference.getForeignKeyResource());
		builder.setCondition(from.getCondition());
		builder.add(to);
		try (BStatement statement = BlendeeContext.get(BlendeeManager.class)
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
		return new PartialData(reference.getForeignKeyResource(), referencesNames, persistence.bindables);
	}
}
