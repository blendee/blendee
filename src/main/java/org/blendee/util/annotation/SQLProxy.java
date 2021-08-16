package org.blendee.util.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.UUID;

import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.JDBCBorrower;
import org.blendee.jdbc.wrapperbase.ResultSetBase;
import org.blendee.util.SQLProxyBuilder;

/**
 * {@link SQLProxyBuilder} の対象となるインターフェイスであることを表すアノテーションです。
 * @author 千葉 哲嗣
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface SQLProxy {

	/**
	 * {@link SQLProxy} の返却値として使用可能な {@link BResultSet} の拡張クラスです。
	 */
	public static class ResultSet extends ResultSetBase {

		private final BResultSet base;

		/**
		 * @param base
		 */
		public ResultSet(BResultSet base) {
			this.base = base;
		}

		/**
		 * {@link UUID} を返します。
		 * @param columnName
		 * @return {@link UUID}
		 */
		public UUID getUUID(String columnName) {
			return (UUID) base.getObject(columnName);
		}

		/**
		 * {@link UUID} を返します。
		 * @param columnIndex
		 * @return {@link UUID}
		 */
		public UUID getUUID(int columnIndex) {
			return (UUID) base.getObject(columnIndex);
		}

		@Override
		public void lend(JDBCBorrower<java.sql.ResultSet> borrower) {
			base.lend(borrower);
		}

		@Override
		protected BResultSet base() {
			return base;
		}
	}
}
