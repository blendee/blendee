package org.blendee.support;

import org.blendee.jdbc.BatchStatement;
import org.blendee.orm.DataObject;
import org.blendee.orm.PrimaryKey;
import org.blendee.sql.Updatable;
import org.blendee.sql.Updater;

/**
 * 自動生成される Row の共通の振る舞いを定義したインターフェイスです。
 *
 * @author 千葉 哲嗣
 */
public interface Row extends Updatable {

	/**
	 * この Entity が内部で使用している {@link DataObject} を返します。
	 *
	 * @return 内部で使用している {@link DataObject}
	 */
	DataObject getDataObject();

	/**
	 * この Entity の更新用メソッドです。
	 *
	 * @return 更新が成功したかどうか
	 */
	default boolean update() {
		return getDataObject().update();
	}

	/**
	 * この Entity のバッチ更新用メソッドです。
	 *
	 * @param statement バッチ実行を依頼する {@link BatchStatement}
	 */
	default void update(BatchStatement statement) {
		getDataObject().update(statement);
	}

	/**
	 * このレコードの主キーを返します。
	 *
	 * @return 主キー
	 */
	default PrimaryKey getPrimaryKey() {
		return getDataObject().getPrimaryKey();
	}

	@Override
	default void setValuesTo(Updater updater) {
		getDataObject().setValuesTo(updater);
	}

	/**
	 * 引数のカラムが今回の検索に使用されたかを検査します。
	 *
	 * @param columnName 検査対象
	 * @return SELECT されたかどうか
	 */
	default boolean isSelected(String columnName) {
		DataObject data = getDataObject();
		return data.getSelectedValues().isSelected(data.getRelationship().getColumn(columnName));
	}
}
