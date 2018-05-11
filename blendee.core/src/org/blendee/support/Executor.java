package org.blendee.support;

import java.util.function.Consumer;
import java.util.function.Function;

import org.blendee.jdbc.BlenConnection;
import org.blendee.jdbc.BlenResultSet;
import org.blendee.jdbc.BlenStatement;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ComposedSQL;
import org.blendee.jdbc.PreparedStatementComplementer;
import org.blendee.jdbc.ResultSetIterator;
import org.blendee.sql.Bindable;
import org.blendee.sql.BindableConverter;

/**
 * 検索条件と並び替え条件を保持した、実際に検索を行うためのクラスです。<br>
 * {@link Query} クラスのインスタンスは、複数のスレッドから同時にアクセスされることを前提としているため、トランザクションごとに、設定された条件はクリアされてしまいます。<br>
 * 一処理内で、同じ条件で複数回検索を実行したい場合を考慮し、検索実行時状態を保存しておくのが、このクラスのインスタンスの役割です。
 * @param <I> Iterator
 * @param <R> Row
 * @author 千葉 哲嗣
 */
public interface Executor<I, R> extends ComposedSQL {

	/**
	 * このインスタンスが持つ検索条件と並び替え条件を使用して、検索を実行します。<br>
	 * 検索結果が、複数件になることを想定しているメソッドです。
	 * @return 検索結果
	 */
	I execute();

	/**
	 * このインスタンスが持つ検索条件を使用して、検索を実行します。<br>
	 * 検索条件として、ユニークキーが指定されていることを想定しているメソッドです。
	 * @return {@link Row}
	 * @throws NotUniqueException 検索結果が複数件あった場合
	 */
	R willUnique();

	/**
	 * 主キーから Row 一件を選択するメソッドです。<br>
	 * パラメータの数は、主キーを構成するカラム数と同じ必要があります。
	 * @param primaryKeyMembers 主キーの検索値
	 * @return {@link Row}
	 */
	default R fetch(String... primaryKeyMembers) {
		return fetch(BindableConverter.convert(primaryKeyMembers));
	}

	/**
	 * 主キーから Row 一件を選択するメソッドです。<br>
	 * パラメータの数は、主キーを構成するカラム数と同じ必要があります。
	 * @param primaryKeyMembers 主キーの検索値
	 * @return {@link Row}
	 */
	default R fetch(Number... primaryKeyMembers) {
		return fetch(BindableConverter.convert(primaryKeyMembers));
	}

	/**
	 * 主キーから Row 一件を選択するメソッドです。<br>
	 * パラメータの数は、主キーを構成するカラム数と同じ必要があります。
	 * @param primaryKeyMembers 主キーの検索値
	 * @return {@link Row}
	 */
	R fetch(Bindable... primaryKeyMembers);

	/**
	 * このインスタンスが持つ検索条件に合致するレコード件数を取得します。
	 * @return 件数
	 */
	int count();

	/**
	 * 集合関数を含む検索を実行します。
	 * @param consumer {@link Consumer}
	 */
	default void aggregate(Consumer<BlenResultSet> consumer) {
		BlenConnection connection = BlendeeManager.getConnection();
		try (BlenStatement statement = connection.getStatement(this)) {
			try (BlenResultSet result = statement.executeQuery()) {
				consumer.accept(result);
			}
		}
	}

	/**
	 * 集合関数を含む検索を実行します。
	 * @param function {@link Function}
	 * @return 任意の型の戻り値
	 */
	default <T> T aggregateAndGet(Function<BlenResultSet, T> function) {
		BlenConnection connection = BlendeeManager.getConnection();
		try (BlenStatement statement = connection.getStatement(this)) {
			try (BlenResultSet result = statement.executeQuery()) {
				return function.apply(result);
			}
		}
	}

	/**
	 * 集合関数を含む検索を実行します。
	 * @return {@link ResultSetIterator}
	 */
	default ResultSetIterator aggregate() {
		return new ResultSetIterator(this);
	}

	@Override
	Executor<I, R> reproduce(PreparedStatementComplementer complementer);
}
