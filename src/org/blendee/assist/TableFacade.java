package org.blendee.assist;

import org.blendee.jdbc.TablePath;
import org.blendee.orm.DataAccessHelper;
import org.blendee.orm.DataObject;

/**
 * 自動生成される TableFacade の共通の振る舞いを定義したインターフェイスです。
 * @author 千葉 哲嗣
 * @param <T> {@link Row}
 */
public interface TableFacade<T extends Row> {

	/**
	 * 空の検索結果
	 */
	static final RowIterator<Row> EMPTY_ITERATOR = new RowIterator<Row>(
		DataAccessHelper.EMPTY_UPDATABLE_DATA_OBJECT_ITERATOR) {

		@Override
		public Row next() {
			throw new UnsupportedOperationException();
		}
	};

	/**
	 * {@link Row} を生成するメソッドです。
	 * @param data {@link Row} の全要素の値を持つ検索結果オブジェクト
	 * @return 生成された {@link Row}
	 */
	T createRow(DataObject data);

	/**
	 * サブクラスで固有の {@link TablePath} を返します。
	 * @return 固有の {@link TablePath}
	 */
	TablePath getTablePath();

	/**
	 * 空の {@link RowIterator} を返します。
	 * @param <T> {@link RowIterator} の要素型
	 * @return {@link RowIterator}
	 */
	@SuppressWarnings("unchecked")
	static <T extends Row> RowIterator<T> getEmptyRowIterator() {
		return (RowIterator<T>) EMPTY_ITERATOR;
	}
}
