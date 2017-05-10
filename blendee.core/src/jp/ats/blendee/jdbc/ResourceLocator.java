package jp.ats.blendee.jdbc;

/**
 * スキーマ名、テーブル名を持ち、テーブルを一意で特定する指標となるクラスです。
 *
 * @author 千葉 哲嗣
 */
public class ResourceLocator implements Comparable<ResourceLocator> {

	/**
	 * 空の配列
	 */
	public static final ResourceLocator[] EMPTY_ARRAY = {};

	private static final char[] illegalChars = "!@#$%^&*()-=+\\|`~[]{};:'\",.<>/? ".toCharArray();

	private static final String delimiter = ".";

	private final String schemaName;

	private final String tableName;

	/**
	 * スキーマ名とテーブル名から、インスタンスを生成します。
	 *
	 * @param schemaName スキーマ名
	 * @param tableName テーブル名
	 * @throws IllegalArgumentException 使用不可能な文字を使用した場合
	 */
	public ResourceLocator(String schemaName, String tableName) {
		checkObjectName("schemaName", schemaName);
		checkObjectName("tableName", tableName);
		this.schemaName = schemaName;
		this.tableName = tableName;
	}

	/**
	 * テーブル名のみで、インスタンスを生成します。
	 * <br>
	 * スキーマ名を省略しているので、一意に決定できるように Blendee に設定されるスキーマは一つでなければなりません。
	 *
	 * @param tableName テーブル名
	 * @throws IllegalStateException スキーマ名が複数設定されている場合
	 * @throws IllegalArgumentException 使用不可能な文字を使用した場合
	 * @see Initializer#addSchemaName(String)
	 */
	public ResourceLocator(String tableName) {
		checkObjectName("tableName", tableName);
		schemaName = null;
		this.tableName = tableName;
	}

	/**
	 * コピーコンストラクタです。
	 *
	 * @param locator コピーされるインスタンス
	 */
	public ResourceLocator(ResourceLocator locator) {
		this(locator.getSchemaName(), locator.getTableName());
	}

	/**
	 * テーブルを一意で特定する指標から、このクラスのインスタンスを生成します。
	 *
	 * @param location テーブルを一意で特定する指標
	 * @return location に対応するインスタンス
	 * @see ResourceLocator#toString()
	 */
	public static ResourceLocator parse(String location) {
		int index = location.indexOf(delimiter);
		if (index == -1) return new ResourceLocator(location);
		return new ResourceLocator(location.substring(0, index), location.substring(index + 1));
	}

	/**
	 * このインスタンスが持つスキーマ名を返します。
	 *
	 * @return スキーマ名
	 */
	public final String getSchemaName() {
		if (schemaName != null) return schemaName;

		//Blendee 初期化以前にインスタンス化されても大丈夫なように、デフォルトスキーマ名は、この時点で取得する
		String[] schemaNames = BlendeeContext.get(BlendeeManager.class).getConfigure().getSchemaNames();
		if (schemaNames.length > 1) throw new IllegalStateException("スキーマ名が複数設定されています");

		return schemaNames[0];
	}

	/**
	 * このインスタンスが持つテーブル名を返します。
	 *
	 * @return テーブル名
	 */
	public final String getTableName() {
		return tableName;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof ResourceLocator && id().equals(((ResourceLocator) o).id());
	}

	@Override
	public int hashCode() {
		return id().hashCode();
	}

	/**
	 * スキーマ名、テーブル名を元に、テーブルを一意で特定する指標となる文字列を返します。
	 *
	 * @return テーブルを一意で特定する指標
	 * @see Initializer#addSchemaName(String)
	 */
	@Override
	public String toString() {
		return getSchemaName() + delimiter + tableName;
	}

	@Override
	public int compareTo(ResourceLocator target) {
		return id().compareTo(target.id());
	}

	/**
	 * このインスタンスが指し示すテーブルが実際に存在するかどうかを検査します。
	 *
	 * @return 存在する場合、 true
	 */
	public boolean exists() {
		return MetadataUtilities.getColumnMetadatas(this).length > 0;
	}

	/**
	 * 使用できるデータベースオブジェクト名かどうかを判定します。
	 *
	 * @param name 判定対象
	 * @return 使用できるかどうか
	 */
	public static boolean checkObjectName(String name) {
		for (int i = 0; i < illegalChars.length; i++) {
			if (name.indexOf(illegalChars[i]) != -1) return false;
		}

		return true;
	}

	/**
	 * 大文字、小文字を意識しないようにする比較用の文字列
	 */
	private String id() {
		return toString().toUpperCase();
	}

	private static void checkObjectName(String type, String name) {
		for (int i = 0; i < illegalChars.length; i++) {
			if (name.indexOf(illegalChars[i]) != -1)
				throw new IllegalArgumentException(type + "には'" + illegalChars[i] + "'は使用できません");
		}
	}
}
