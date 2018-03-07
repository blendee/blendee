package org.blendee.selector;

import org.blendee.jdbc.TablePath;
import org.blendee.sql.Column;

/**
 * ユニークな ID 毎に SELECT 句に使用するカラムを管理する機能を定義したインターフェイスです。
 * @author 千葉 哲嗣
 * @see ColumnRepositoryFactory#createColumnRepository()
 */
public interface ColumnRepository {

	/**
	 * ID に対応するテーブルを返します。<br>
	 * リポジトリに ID が存在しない場合は null を返します。
	 * @param id ID
	 * @return 対応するテーブル
	 */
	TablePath getTablePath(String id);

	/**
	 * ID と、それに対応するテーブルをリポジトリに登録します。<br>
	 * ID を探すヒントとして、使用しているクラスを渡すことができます。
	 * @param id ID
	 * @param path 対応するテーブル
	 * @param usingClassNames id が使用されているクラス
	 */
	void add(String id, TablePath path, String... usingClassNames);

	/**
	 * ID と、それに対応するテーブルをリポジトリから削除します。
	 * @param id 削除する ID
	 */
	void remove(String id);

	/**
	 * ID の文字列を、新しいものに変更します。<br>
	 * ID を探すヒントとして、使用しているクラスを渡すことができます。
	 * @param oldId 旧 ID
	 * @param newId 新 ID
	 * @param usingClassNames id が使用されているクラス
	 */
	void renameID(String oldId, String newId, String... usingClassNames);

	/**
	 * ID が持つ全カラムを返します。
	 * @param id ID
	 * @return ID が持つ全カラム
	 */
	Column[] getColumns(String id);

	/**
	 * パラメータの ID に新たなカラムを追加します。<br>
	 * ID がまだ登録されていない場合、新たに追加されます。<br>
	 * ID を探すヒントとして、使用しているクラスを渡すことができます。
	 * @param id ID
	 * @param column 新しいカラム
	 * @param usingClassNames id が使用されているクラス
	 */
	void addColumn(String id, Column column, String... usingClassNames);

	/**
	 * パラメータの ID から、指定されたカラムを削除します。
	 * @param id ID
	 * @param column 削除するカラム
	 */
	void removeColumn(String id, Column column);

	/**
	 * このリポジトリがパラメータで指定された ID を持つかどうか検査します。
	 * @param id ID
	 * @return ID を持つ場合、 true
	 */
	boolean contains(String id);

	/**
	 * パラメータの ID が、指定されたカラムを持つかどうか検査します。
	 * @param id ID
	 * @param column 検査するカラム
	 * @return ID がカラムを持つ場合、 true
	 */
	boolean containsColumn(String id, Column column);

	/**
	 * パラメータの ID に設定された使用マーク削除時刻を返します。
	 * @param id ID
	 * @return 使用マーク削除時刻
	 */
	long getMarkClearedTimestamp(String id);

	/**
	 * パラメータの ID とカラムに、検索に使用されたことをマークします。
	 * @param id ID
	 * @param column マークするカラム
	 * @param usingClassNames id が使用されているクラス
	 */
	void markColumn(String id, Column column, String... usingClassNames);

	/**
	 * パラメータの ID が持つ全カラムの使用マークと使用クラスを削除し、削除時刻としてパラメータで指定された時刻を設定します。
	 * @param id ID
	 * @param timestamp 削除時刻
	 */
	void clear(String id, long timestamp);

	/**
	 * パラメータの ID とカラムに、使用マークがあるかどうか検査します。
	 * @param id ID
	 * @param column 検査対象カラム
	 * @return 使用マークがある場合、 true
	 */
	boolean marks(String id, Column column);

	/**
	 * パラメータの ID が使用されているクラスを返します。
	 * @param id ID
	 * @return 使用されているクラス名
	 */
	String[] getUsingClassNames(String id);

	/**
	 * コミットします。
	 */
	void commit();

	/**
	 * ロールバックします。
	 */
	void rollback();

	/**
	 * このリポジトリに対する変更があり、コミットが可能かどうかを返します。
	 * @return コミットが可能な場合、 true
	 */
	boolean canCommit();

	/**
	 * このリポジトリが持つ全 ID を返します。
	 * @return 全 ID
	 */
	String[] getIDs();

	/**
	 * パラメータで指定された ID にエラーがある場合、そのエラーメッセージを返します。
	 * @param id ID
	 * @return 発生している全エラーメッセージ
	 */
	String[] getErrorMessages(String id);

	/**
	 * パラメータで指定された ID に発生しているエラーを訂正します。
	 * @param id エラーが発生している ID
	 */
	void correctErrors(String id);
}
