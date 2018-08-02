package org.blendee.orm;

import org.blendee.jdbc.Result;
import org.blendee.sql.Relationship;
import org.blendee.sql.ValueExtractors;

/**
 * result に含まれるカラム名から {@link DataObject} を生成するクラスです。
 * @author 千葉 哲嗣
 */
public class ColumnNameDataObjectBuilder {

	/**
	 * カラムに自動付与されたエイリアスではなく、 result に含まれるカラム名から {@link DataObject} を生成します。
	 * @param result 検索結果
	 * @param relationship result に含まれるカラムをもつ {@link Relationship}
	 * @param extractors {@link ValueExtractors}
	 * @return {@link DataObject}
	 */
	public static DataObject build(Result result, Relationship relationship, ValueExtractors extractors) {
		return new DataObject(relationship, result, extractors);
	}
}
