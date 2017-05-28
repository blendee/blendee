package org.blendee.selector;

/**
 * {@link ColumnRepository} インスタンスを生成するファクトリインターフェイスです。
 *
 * @author 千葉 哲嗣
 */
@FunctionalInterface
public interface ColumnRepositoryFactory {

	/**
	 * {@link ColumnRepository} を生成し、返します。
	 *
	 * @return 新しい {@link ColumnRepository}
	 */
	ColumnRepository createColumnRepository();
}
