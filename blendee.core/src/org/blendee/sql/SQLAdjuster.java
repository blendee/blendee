package org.blendee.sql;

/**
 * SQL 文を各データベース向けに微調整したり拡張する機能を定義したインターフェイスです。
 * @author 千葉 哲嗣
 * @see QueryBuilder#setAdjuster(SQLAdjuster)
 */
@FunctionalInterface
public interface SQLAdjuster {

	/**
	 * 何もしないインスタンス
	 */
	SQLAdjuster DISABLED_ADJUSTER = new SQLAdjuster() {

		@Override
		public String adjustSQL(String sql) {
			return sql;
		}
	};

	/**
	 * Blendee が生成した SELECT 文を、カスタマイズした SELECT 文に変換します。
	 * @param sql 元になる SELECT 文
	 * @return カスタマイズした SELECT 文
	 */
	String adjustSQL(String sql);

	/**
	 * この SQLAdjuster が、句ごとに分割された SELECT 文を組立可能かを返却します。<br>
	 * このメソッドが使用されるのは、 {@link QueryBuilder} が SELECT 文を生成するときです。
	 * @return 組立可能かどうか
	 */
	default boolean canBuildQueryParts() {
		return false;
	};

	/**
	 * Blendee が生成した SELECT 文の各句を、カスタマイズした SELECT 文に組み立てます。<br>
	 * {@link #canBuildQueryParts()} が true を返した時のみ、このメソッドが使用されます。
	 * @param selectClause SELECT 句
	 * @param fromClause FROM 句
	 * @param whereClause WHERE 句
	 * @param groupClause GROUP BY 句
	 * @param havingClause HAVING 句
	 * @param orderClause ORDER BY 句
	 * @return カスタマイズした SELECT 文
	 */
	default String buildQueryParts(
		String selectClause,
		String fromClause,
		String whereClause,
		String groupClause,
		String havingClause,
		String orderClause) {
		throw new UnsupportedOperationException();
	};
}
