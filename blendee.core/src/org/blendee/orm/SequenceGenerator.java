package org.blendee.orm;

import org.blendee.jdbc.BatchStatement;
import org.blendee.jdbc.TablePath;
import org.blendee.sql.Bindable;
import org.blendee.sql.Condition;
import org.blendee.sql.SQLAdjuster;
import org.blendee.sql.Updatable;

/**
 * 連続した値をもつカラムに対する新規の値を払い出す機能を定義したインターフェイスです。
 * @author 千葉 哲嗣
 * @see DataAccessHelper#insert(TablePath, SequenceGenerator, Updatable, int, SQLAdjuster)
 * @see DataAccessHelper#insert(BatchStatement, TablePath, SequenceGenerator, Updatable, int, SQLAdjuster)
 */
public interface SequenceGenerator {

	/**
	 * この連番が従う上位グループのカラム名を返します。
	 * <br>
	 * 上位グループとは、連番の最大値を求める際に条件として使用されるカラムのことです。
	 * @return 上位グループのカラム名
	 */
	String[] getDependsColumnNames();

	/**
	 * 連番カラム名を返します。
	 * @return 連番カラム名
	 */
	String getTargetColumnName();

	/**
	 * 次の値を生成します。
	 * @param depends 上位グループ条件
	 * @return 次の値
	 * @throws SequenceOverflowException 連番が最大値を超えた場合
	 */
	Bindable next(Condition depends);
}
