package org.blendee.assist;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import org.blendee.jdbc.AutoCloseableIterator;
import org.blendee.jdbc.BConnection;
import org.blendee.jdbc.BResultSet;
import org.blendee.jdbc.BStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.ResultSetIterator;
import org.blendee.sql.Bindable;
import org.blendee.sql.BindableConverter;
import org.blendee.sql.Reproducible;

/**
 * 検索条件と並び替え条件を保持した、実際に検索を行うためのインターフェイスです。<br>
 * {@link SelectStatement} クラスのインスタンスは、複数のスレッドから同時にアクセスされることを前提としているため、トランザクションごとに、設定された条件はクリアされてしまいます。<br>
 * 一処理内で、同じ条件で複数回検索を実行したい場合を考慮し、検索実行時状態を保存しておくのが、このクラスのインスタンスの役割です。
 * @param <I> Iterator
 * @param <R> Row
 * @author 千葉 哲嗣
 */
public interface Query<I extends AutoCloseableIterator<R>, R> extends ComposedSQL, Reproducible<Query<I, R>> {

	/**
	 * このインスタンスが持つ検索条件と並び替え条件を使用して、検索を実行します。<br>
	 * 検索結果が、複数件になることを想定しているメソッドです。
	 * @return 検索結果
	 */
	I execute();

	/**
	 * 検索を実行します。
	 * @param action {@link Consumer}
	 */
	default void execute(Consumer<I> action) {
		try (I iterator = execute()) {
			action.accept(iterator);
		}
	}

	/**
	 * 検索を実行します。
	 * @param action {@link Consumer}
	 * @param <T> 戻り値の型
	 * @return 任意の型の戻り値
	 */
	default <T> T executeAndGet(Function<I, T> action) {
		try (I iterator = execute()) {
			return action.apply(iterator);
		}
	}

	/**
	 * 検索を実行します。
	 * @param action {@link Consumer}
	 */
	default void forEach(Consumer<R> action) {
		try (I iterator = execute()) {
			iterator.forEach(action);
		}
	}

	/**
	 * このインスタンスが持つ検索条件を使用して、検索を実行します。<br>
	 * 検索条件として、ユニークキーが指定されていることを想定しているメソッドです。
	 * @return {@link Row}
	 * @throws NotUniqueException 検索結果が複数件あった場合
	 */
	default Optional<R> willUnique() {
		return Helper.unique(execute());
	}

	/**
	 * 主キーから Row 一件を選択するメソッドです。<br>
	 * パラメータの数は、主キーを構成するカラム数と同じ必要があります。
	 * @param primaryKeyMembers 主キーの検索値
	 * @return {@link Row}
	 */
	default Optional<R> fetch(String... primaryKeyMembers) {
		return fetch(BindableConverter.convert(primaryKeyMembers));
	}

	/**
	 * 主キーから Row 一件を選択するメソッドです。<br>
	 * パラメータの数は、主キーを構成するカラム数と同じ必要があります。
	 * @param primaryKeyMembers 主キーの検索値
	 * @return {@link Row}
	 */
	default Optional<R> fetch(Number... primaryKeyMembers) {
		return fetch(BindableConverter.convert(primaryKeyMembers));
	}

	/**
	 * 主キーから Row 一件を選択するメソッドです。<br>
	 * パラメータの数は、主キーを構成するカラム数と同じ必要があります。
	 * @param primaryKeyMembers 主キーの検索値
	 * @return {@link Row}
	 */
	default Optional<R> fetch(UUID... primaryKeyMembers) {
		return fetch(BindableConverter.convert(primaryKeyMembers));
	}

	/**
	 * 主キーから Row 一件を選択するメソッドです。<br>
	 * パラメータの数は、主キーを構成するカラム数と同じ必要があります。
	 * @param primaryKeyMembers 主キーの検索値
	 * @return {@link Row}
	 */
	Optional<R> fetch(Bindable... primaryKeyMembers);

	/**
	 * このインスタンスが持つ検索条件に合致するレコード件数を取得します。
	 * @return 件数
	 */
	int count();

	/**
	 * {@link #count()} で使用する SQL を返します。
	 * @return カウント用 {@link ComposedSQL}
	 */
	ComposedSQL countSQL();

	/**
	 * 集合関数を含む検索用の SQL を返します。
	 * @return {@link ComposedSQL}
	 */
	ComposedSQL aggregateSQL();

	/**
	 * 検索結果として {@link Row} を使用するモードかどうかを判定します。
	 * @return {@link Row} を使用するモードかどうか
	 */
	boolean rowMode();

	/**
	 * 集合関数を含む検索を実行します。
	 * @param action {@link Consumer}
	 */
	default void aggregate(Consumer<BResultSet> action) {
		BConnection connection = BlendeeManager.getConnection();
		try (BStatement statement = connection.getStatement(aggregateSQL())) {
			try (BResultSet result = statement.executeQuery()) {
				action.accept(result);
			}
		}
	}

	/**
	 * 集合関数を含む検索を実行します。
	 * @param action {@link Function}
	 * @param <T> 戻り値の型
	 * @return 任意の型の戻り値
	 */
	default <T> T aggregateAndGet(Function<BResultSet, T> action) {
		BConnection connection = BlendeeManager.getConnection();
		try (BStatement statement = connection.getStatement(aggregateSQL())) {
			try (BResultSet result = statement.executeQuery()) {
				return action.apply(result);
			}
		}
	}

	/**
	 * 集合関数を含む検索を実行します。
	 * @return {@link ResultSetIterator}
	 */
	default ResultSetIterator aggregate() {
		return new ResultSetIterator(aggregateSQL());
	}
}
