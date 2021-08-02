package org.blendee.sql;

import java.util.List;

import org.blendee.jdbc.TablePath;

/**
 * {@link Relationship} の内部の関連探索の方法を解決するプラグインインターフェイスです。
 * @author 千葉 哲嗣
 */
public interface RelationshipResolver {

	/**
	 * これ以上探索してよいかどうか、判定します。
	 * @param relationshipPath root から target に至るまでの全ての {@link TablePath}
	 * @param target 判定対象の {@link TablePath}
	 * @return これ以上探索を続けてよいかどうか
	 */
	boolean canTraverse(List<TablePath> relationshipPath, TablePath target);
}
