package jp.ats.blendee.support;

import jp.ats.blendee.jdbc.BatchStatement;
import jp.ats.blendee.orm.PrimaryKey;
import jp.ats.blendee.orm.UpdatableDataObject;
import jp.ats.blendee.sql.Updatable;
import jp.ats.blendee.sql.Updater;

/**
 * 自動生成される DTO の共通の振る舞いを定義したインターフェイスです。
 *
 * @author 千葉 哲嗣
 */
public interface DTO extends Updatable {

	/**
	 * この DTO が内部で使用している {@link UpdatableDataObject} を返します。
	 *
	 * @return 内部で使用している {@link UpdatableDataObject}
	 */
	UpdatableDataObject getDataObject();

	/**
	 * この DTO の更新用メソッドです。
	 *
	 * @return 更新が成功したかどうか
	 */
	default boolean update() {
		return getDataObject().update();
	}

	/**
	 * この DTO のバッチ更新用メソッドです。
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
	 * このインスタンスにセットされた更新値で、検索結果を上書きします。
	 */
	default void commit() {
		getDataObject().commit();
	}

	/**
	 * このインスタンスにセットされた更新値を捨て、検索結果の値に戻します。
	 */
	default void rollback() {
		getDataObject().rollback();
	}

	/**
	 * 引数のカラムが今回の検索に使用されたかを検査します。
	 *
	 * @param columnName 検査対象
	 * @return SELECT されたかどうか
	 */
	default boolean isSelected(String columnName) {
		UpdatableDataObject data = getDataObject();
		return data.getSelectedValues().isSelected(data.getRelationship().getColumn(columnName));
	}
}
