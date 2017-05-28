package org.blendee.support;

import org.blendee.orm.QueryOption;

/**
 * {@link QueryOption} を複数持つことができるコンテナクラスです。
 *
 * @author 千葉 哲嗣
 */
public class QueryOptions {

	/**
	 * 空のインスタンス
	 */
	public static final QueryOptions EMPTY_OPTIONS = new QueryOptions();

	private final QueryOption[] options;

	/**
	 * 短縮記述用メソッドです。
	 * <br>
	 * import static した場合、短縮して記述できます。
	 *
	 * @param options 複数の {@link QueryOption}
	 * @return instance
	 */
	public static QueryOptions of(QueryOption... options) {
		return new QueryOptions(options);
	}

	static QueryOptions care(QueryOptions options) {
		return options == null ? EMPTY_OPTIONS : options;
	}

	/**
	 * {@link QueryOption} を持つインスタンスを生成します。
	 *
	 * @param options 複数の {@link QueryOption}
	 */
	public QueryOptions(QueryOption... options) {
		this.options = options;
	}

	/**
	 * 保持する {@link QueryOption} を返します。
	 *
	 * @return {@link QueryOption} の配列
	 */
	public QueryOption[] get() {
		return options.clone();
	}
}
