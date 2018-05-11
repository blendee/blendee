package org.blendee.support;

import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObject;
import org.blendee.orm.PrimaryKey;
import org.blendee.sql.Updatable;
import org.blendee.sql.Updater;

/**
 * 自動生成される {@link Row} の共通の振る舞いを定義したインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface Row extends Updatable {

	/**
	 * この Row が内部で使用している {@link DataObject} を返します。
	 * @return 内部で使用している {@link DataObject}
	 */
	DataObject dataObject();

	/**
	 * サブクラスで固有の {@link TablePath} を返します。
	 * @return 固有の {@link TablePath}
	 */
	TablePath tablePath();

	/**
	 * この {@link Row} の更新用メソッドです。
	 * @return 更新が成功したかどうか
	 */
	default boolean update() {
		return dataObject().update();
	}

	/**
	 * この {@link Row} のバッチ更新用メソッドです。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 */
	default void update(BatchStatement statement) {
		dataObject().update(statement);
	}

	/**
	 * この {@link Row} の INSERT を行います。
	 */
	default void insert() {
		new DataAccessHelper().insert(
			tablePath(),
			this);
	}

	/**
	 * この {@link Row} の INSERT をバッチ実行します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 */
	default void insert(BatchStatement statement) {
		new DataAccessHelper().insert(
			statement,
			tablePath(),
			this);
	}

	/**
	 * この {@link Row} の DELETE を行います。
	 * @return 削除が成功した場合、 true
	 */
	default boolean delete() {
		int result = new DataAccessHelper().delete(
			tablePath(),
			primaryKey().getCriteria());
		if (result > 1) throw new IllegalStateException("削除件数が複数件あります。");
		return result == 1;
	}

	/**
	 * この {@link Row} の DELETE をバッチ実行します。
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 */
	default void delete(BatchStatement statement) {
		new DataAccessHelper().delete(
			statement,
			tablePath(),
			primaryKey().getCriteria());
	}

	/**
	 * この {@link Row} の主キーを返します。
	 * @return 主キー
	 */
	default PrimaryKey primaryKey() {
		return dataObject().getPrimaryKey();
	}

	@Override
	default void setValuesTo(Updater updater) {
		dataObject().setValuesTo(updater);
	}

	/**
	 * 引数のカラムが今回の検索に使用されたかを検査します。
	 * @param columnName 検査対象
	 * @return SELECT されたかどうか
	 */
	default boolean isSelected(String columnName) {
		DataObject data = dataObject();
		return data.getSelectedValues().isSelected(data.getRelationship().getColumn(columnName));
	}
}