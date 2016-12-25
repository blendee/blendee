/*--*//*@formatter:off*//*--*/package /*++{0}++*//*--*/jp.ats.blendee.develop/*--*/;

import javax.annotation.Generated;

import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.orm.DataAccessHelper;
import jp.ats.blendee.orm.DataObjectIterator;
import jp.ats.blendee.orm.QueryOption;
import jp.ats.blendee.orm.RowLockOption;
import jp.ats.blendee.orm.UpdatableDataObject;
import jp.ats.blendee.selector.Optimizer;
import jp.ats.blendee.selector.SimpleOptimizer;
import jp.ats.blendee.sql.Condition;
import jp.ats.blendee.sql.OrderByClause;
import jp.ats.blendee.support.DAO;
import jp.ats.blendee.support.DTOIterator;

/**
 * 自動生成された '{'@link DAO'}' の実装クラスです。
 *
{3}
 */
@Generated(value = /*++'++*/{/*++'++*/"{4}"/*++'++*/}/*++'++*/)
public class /*++{1}DAO++*//*--*/DAOBase/*--*/
	extends /*++{2}++*//*--*/Object/*--*/
	implements DAO</*++{1}++*//*--*/DTOBase/*--*/> /*++'++*/{/*++'++*/

	private final DataAccessHelper helper = new DataAccessHelper(false);

	/**
	 * パラメータの条件にマッチするレコードを検索し、 '{'@link {1}Iterator'}' として返します。
	 * <br>
	 * '{'@link Optimizer} には '{'@link SimpleOptimizer'}' が使用されます。
	 *
	 * @param condition WHERE 句となる条件
	 * @param order  ORDER 句
	 * @param options 行ロックオプション '{'@link RowLockOption'}' 等
	 * @return '{'@link DTOIterator'}'
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
	 * @return '{'@link DTOIterator'}'
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
	 * @return '{'@link DTOIterator'}'
	 */
	public /*++{1}Iterator++*//*--*/IteratorBase/*--*/ select(
		Optimizer optimizer,
		Condition condition,
		OrderByClause order,
		QueryOption... options) /*++'++*/{/*++'++*/
		return new /*++{1}Iterator++*//*--*/IteratorBase/*--*/(helper.getUpdatableDataObjects(
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
	 * @return '{'@link DTOIterator'}'
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
	public /*++{1}++*//*--*/DTOBase/*--*/ createDTO(UpdatableDataObject data) /*++'++*/{/*++'++*/
		return new /*++{1}++*//*--*/DTOBase/*--*/(data);
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
	 * '{'@link {1}DAO'}' が使用する Iterator クラスです。
	 */
	public class /*++{1}Iterator++*//*--*/IteratorBase/*--*/
		extends /*++DTOIterator<{1}>++*//*--*/DTOIterator<DTOBase>/*--*/ /*++'++*/{/*++'++*/

		/**
		 * 唯一のコンストラクタです。
		 *
		 * @param iterator 
		 */
		private /*++{1}Iterator++*//*--*/IteratorBase/*--*/(
			DataObjectIterator<UpdatableDataObject> iterator) /*++'++*/{/*++'++*/
			super(iterator);
		/*++'++*/}/*++'++*/

		@Override
		public /*++{1}++*//*--*/DTOBase/*--*/ next() /*++'++*/{/*++'++*/
			return createDTO(nextDataObject());
		/*++'++*/}/*++'++*/
	/*++'++*/}/*++'++*/
/*++'++*/}/*++'++*/
