/*--*//*@formatter:off*//*--*/package /*++{0}++*//*--*/jp.ats.blendee.develop/*--*/;

import javax.annotation.Generated;

import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.orm.DataAccessHelper;
import jp.ats.blendee.orm.DataObjectIterator;
import jp.ats.blendee.orm.QueryOption;
import jp.ats.blendee.orm.RowLockOption;
import jp.ats.blendee.orm.DataObject;
import jp.ats.blendee.selector.Optimizer;
import jp.ats.blendee.selector.SimpleOptimizer;
import jp.ats.blendee.sql.Condition;
import jp.ats.blendee.sql.OrderByClause;
import jp.ats.blendee.support.BEntityManager;
import jp.ats.blendee.support.EntityIterator;

/**
 * 自動生成された '{'@link BEntityManager'}' の実装クラスです。
 *
{3}
 */
@Generated(value = /*++'++*/{/*++'++*/"{4}"/*++'++*/}/*++'++*/)
public class /*++{1}Manager++*//*--*/ManagerBase/*--*/
	extends /*++{2}++*//*--*/Object/*--*/
	implements BEntityManager</*++{1}++*//*--*/EntityBase/*--*/> /*++'++*/{/*++'++*/

	private final DataAccessHelper helper = new DataAccessHelper(false);

	/**
	 * パラメータの条件にマッチするレコードを検索し、 '{'@link {1}Iterator'}' として返します。
	 * <br>
	 * '{'@link Optimizer} には '{'@link SimpleOptimizer'}' が使用されます。
	 *
	 * @param condition WHERE 句となる条件
	 * @param order  ORDER 句
	 * @param options 行ロックオプション '{'@link RowLockOption'}' 等
	 * @return '{'@link EntityIterator'}'
	 */
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ select(
		Condition condition,
		OrderByClause order,
		QueryOption... options) /*++'++*/{/*++'++*/
		return select(
			new SimpleOptimizer(getResourceLocator()),
			condition,
			order,
			options);
	/*++'++*/}/*++'++*/

	/**
	 * パラメータの条件にマッチするレコードを検索し、 '{'@link {1}Iterator'}' として返します。
	 * <br>
	 * '{'@link Optimizer} には '{'@link SimpleOptimizer'}' が使用されます。
	 * <br>
	 * '{'@link RowLockOption'}' には '{'@link RowLockOption#NONE'}' が使用されます。
	 *
	 * @param condition WHERE 句となる条件
	 * @param order  ORDER 句
	 * @return '{'@link EntityIterator'}'
	 */
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ select(
		Condition condition,
		OrderByClause order) /*++'++*/{/*++'++*/
		return select(
			condition,
			order,
			null,
			RowLockOption.NONE);
	/*++'++*/}/*++'++*/

	/**
	 * パラメータの条件にマッチするレコードを検索し、 '{'@link {1}Iterator'}' として返します。
	 *
	 * @param optimizer SELECT 句を制御する '{'@link Optimizer'}'
	 * @param condition WHERE 句となる条件
	 * @param order  ORDER 句
	 * @param options 行ロックオプション '{'@link RowLockOption'}' 等
	 * @return '{'@link EntityIterator'}'
	 */
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ select(
		Optimizer optimizer,
		Condition condition,
		OrderByClause order,
		QueryOption... options) /*++'++*/{/*++'++*/
		return new /*++{1}Iterator++*//*--*/IteratorBase/*--*/(helper.getDataObjects(
			optimizer,
			condition,
			order,
			options));
	/*++'++*/}/*++'++*/

	/**
	 * パラメータの条件にマッチするレコードを検索し、 '{'@link {1}Iterator'}' として返します。
	 * <br>
	 * '{'@link RowLockOption'}' には '{'@link RowLockOption#NONE'}' が使用されます。
	 *
	 * @param optimizer SELECT 句を制御する '{'@link Optimizer'}'
	 * @param condition WHERE 句となる条件
	 * @param order  ORDER 句
	 * @return '{'@link EntityIterator'}'
	 */
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ select(
		Optimizer optimizer,
		Condition condition,
		OrderByClause order) /*++'++*/{/*++'++*/
		return select(
			optimizer,
			condition,
			order,
			null,
			RowLockOption.NONE);
	/*++'++*/}/*++'++*/

	@Override
	public /*++{1}++*//*--*/EntityBase/*--*/ createEntity(DataObject data) /*++'++*/{/*++'++*/
		return new /*++{1}++*//*--*/EntityBase/*--*/(data);
	/*++'++*/}/*++'++*/

	@Override
	public ResourceLocator getResourceLocator() /*++'++*/{/*++'++*/
		return /*++{1}Constants++*//*--*/Constants/*--*/.RESOURCE_LOCATOR;
	/*++'++*/}/*++'++*/

	@Override
	public DataAccessHelper getDataAccessHelper() /*++'++*/{/*++'++*/
		return helper;
	/*++'++*/}/*++'++*/

	/**
	 * '{'@link {1}Manager'}' が使用する Iterator クラスです。
	 */
	public class /*++{1}Iterator++*//*--*/IteratorBase/*--*/
		extends /*++EntityIterator<{1}>++*//*--*/EntityIterator<EntityBase>/*--*/ /*++'++*/{/*++'++*/

		/**
		 * 唯一のコンストラクタです。
		 *
		 * @param iterator 
		 */
		private /*++{1}Iterator++*//*--*/IteratorBase/*--*/(
			DataObjectIterator iterator) /*++'++*/{/*++'++*/
			super(iterator);
		/*++'++*/}/*++'++*/

		@Override
		public /*++{1}++*//*--*/EntityBase/*--*/ next() /*++'++*/{/*++'++*/
			return createEntity(nextDataObject());
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/
/*++'++*/}/*++'++*/
