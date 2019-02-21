package org.blendee.assist;

import org.blendee.jdbc.Batch;
import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObject;
import org.blendee.orm.PrimaryKey;
import org.blendee.sql.RuntimeIdFactory;
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
	 * @param batch バッチ実行を依頼する {@link Batch}
	 */
	default void update(Batch batch) {
		dataObject().update(batch);
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
	 * @param batch バッチ実行を依頼する {@link Batch}
	 */
	default void insert(Batch batch) {
		new DataAccessHelper().insert(
			batch,
			tablePath(),
			this);
	}

	/**
	 * この {@link Row} が検索された場合 UPDATE 、そうでなければ INSERT を行います。
	 */
	default void save() {
		if (dataObject().selected()) {
			update();
		} else {
			insert();
		}
	}

	/**
	 * この {@link Row} が検索された場合 UPDATE 、そうでなければ INSERT を行います。
	 * @param batch バッチ実行を依頼する {@link Batch}
	 */
	default void save(Batch batch) {
		if (dataObject().selected()) {
			update(batch);
		} else {
			insert(batch);
		}
	}

	/**
	 * この {@link Row} の DELETE を行います。
	 * @return 削除が成功した場合、 true
	 */
	default boolean delete() {
		int result = new DataAccessHelper().delete(
			tablePath(),
			primaryKey().getCriteria(RuntimeIdFactory.getInstance()));
		if (result > 1) throw new IllegalStateException("削除件数が複数件あります。");
		return result == 1;
	}

	/**
	 * この {@link Row} の DELETE をバッチ実行します。
	 * @param batch バッチ実行を依頼する {@link Batch}
	 */
	default void delete(Batch batch) {
		new DataAccessHelper().delete(
			batch,
			tablePath(),
			primaryKey().getCriteria(RuntimeIdFactory.getInstance()));
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
	 * このインスタンスが持つ値が更新されているかどうかを判定します。
	 * @return 更新されている場合、 true
	 */
	default boolean isValueUpdated() {
		return dataObject().isValueUpdated();
	}

	/**
	 * 全てのカラムの更新された値を除去します。<br>
	 * {@link #isValueUpdated()} の戻り値は false に戻ります。
	 */
	default void clearUpdateValues() {
		dataObject().clearUpdateValues();
	}

	/**
	 * このインスタンスの持つPKの値が、NULLかどうかを検査します。
	 * @return このインスタンスが外部結合によるもので、 NULL であれば true
	 */
	default boolean isNullPrimaryKey() {
		return dataObject().isNullPrimaryKey();
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
