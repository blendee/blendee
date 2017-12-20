package org.blendee.support;

import org.blendee.orm.QueryOption;
import org.blendee.orm.RowLockOption;
import org.blendee.sql.Bindable;
import org.blendee.sql.Criteria;

/**
 * 検索条件と並び替え条件を保持した、実際に検索を行うためのクラスです。<br>
 * {@link Query} クラスのインスタンスは、複数のスレッドから同時にアクセスされることを前提としているため、トランザクションごとに、設定された条件はクリアされてしまいます。<br>
 * 一処理内で、同じ条件で複数回検索を実行したい場合を考慮し、検索実行時状態を保存しておくのが、このクラスのインスタンスの役割です。
 * @param <I> Iterator
 * @param <R> Element
 */
public interface Executor<I, R> {

	/**
	 * このインスタンスが持つ検索条件と並び替え条件を使用して、検索を実行します。<br>
	 * 検索結果が、複数件になることを想定しているメソッドです。
	 * @return 検索結果
	 */
	I execute();

	/**
	 * このインスタンスが持つ検索条件と並び替え条件を使用して、検索を実行します。<br>
	 * 検索結果が、複数件になることを想定しているメソッドです。
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @return 検索結果
	 */
	I execute(QueryOption... options);

	/**
	 * このインスタンスが持つ検索条件を使用して、検索を実行します。<br>
	 * 検索条件として、ユニークキーが指定されていることを想定しているメソッドです。
	 * @return {@link Row}
	 * @throws NotUniqueException 検索結果が複数件あった場合
	 */
	R willUnique();

	/**
	 * このインスタンスが持つ検索条件を使用して、検索を実行します。<br>
	 * 検索条件として、ユニークキーが指定されていることを想定しているメソッドです。
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @return {@link Row}
	 * @throws NotUniqueException 検索結果が複数件あった場合
	 */
	R willUnique(QueryOption... options);

	/**
	 * 主キーから Row 一件を選択するメソッドです。<br>
	 * パラメータの数は、主キーを構成するカラム数と同じ必要があります。
	 * @param primaryKeyMembers 主キーの検索値
	 * @return {@link Row}
	 */
	R fetch(String... primaryKeyMembers);

	/**
	 * 主キーから Row 一件を選択するメソッドです。<br>
	 * パラメータの数は、主キーを構成するカラム数と同じ必要があります。
	 * @param primaryKeyMembers 主キーの検索値
	 * @return {@link Row}
	 */
	R fetch(Number... primaryKeyMembers);

	/**
	 * 主キーから Row 一件を選択するメソッドです。<br>
	 * パラメータの数は、主キーを構成するカラム数と同じ必要があります。
	 * @param primaryKeyMembers 主キーの検索値
	 * @return {@link Row}
	 */
	R fetch(Bindable... primaryKeyMembers);

	/**
	 * 主キーから Row 一件を選択するメソッドです。<br>
	 * パラメータの数は、主キーを構成するカラム数と同じ必要があります。
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーの検索値
	 * @return {@link Row}
	 */
	R fetch(QueryOptions options, String... primaryKeyMembers);

	/**
	 * 主キーから Row 一件を選択するメソッドです。<br>
	 * パラメータの数は、主キーを構成するカラム数と同じ必要があります。
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーの検索値
	 * @return {@link Row}
	 */
	R fetch(QueryOptions options, Number... primaryKeyMembers);

	/**
	 * 主キーから {@link Row} 一件を選択するメソッドです。<br>
	 * パラメータの数は、主キーを構成するカラム数と同じ必要があります。
	 * @param options 行ロックオプション {@link RowLockOption} 等
	 * @param primaryKeyMembers 主キーの検索値
	 * @return {@link Row}
	 */
	R fetch(QueryOptions options, Bindable... primaryKeyMembers);

	/**
	 * このインスタンスが持つ検索条件に合致するレコード件数を取得します。
	 * @return 件数
	 */
	int count();

	/**
	 * この検索での条件を返します。
	 * @return WHERE 句
	 */
	Criteria getCriteria();
}
