/*--*//*@formatter:off*//*--*/package /*++{0}++*//*--*/jp.ats.blendee.develop/*--*/;

import javax.annotation.Generated;

import jp.ats.blendee.support.DTO;
import jp.ats.blendee.support.annotation.DTOGetter;
/*--*/import jp.ats.blendee.support.annotation.DTORelationship;/*--*/
import jp.ats.blendee.support.annotation.DTOSetter;
import jp.ats.blendee.support.annotation.Resource;
/*++{8}++*/
import jp.ats.blendee.jdbc.BContext;
import jp.ats.blendee.orm.UpdatableDataObject;
import jp.ats.blendee.selector.SelectorConfigure;
import jp.ats.blendee.selector.ValueExtractor;
import jp.ats.blendee.sql.Binder;
import jp.ats.blendee.sql.Relationship;
import jp.ats.blendee.sql.RelationshipFactory;

/**
 * 自動生成された '{'@link DTO'}' の実装クラスです。
 *
{6}
 */
@Resource(schema = "{1}", table = "{2}")
@Generated(value = /*++'++*/{/*++'++*/"{9}"/*++'++*/}/*++'++*/)
/*++{7}++*/public class /*++{2}++*//*--*/DTOBase/*--*/
	extends /*++{3}++*//*--*/Object/*--*/
	implements DTO /*++'++*/{/*++'++*/

	private final UpdatableDataObject data;

	private final Relationship relationship;

	/**
	 * 登録用コンストラクタです。
	 */
	public /*++{2}++*//*--*/DTOBase/*--*/() /*++'++*/{/*++'++*/
		relationship = BContext.get(RelationshipFactory.class).getInstance(/*++{2}Constants++*//*--*/Constants/*--*/.RESOURCE_LOCATOR);
		data = new UpdatableDataObject(relationship);
	/*++'++*/}/*++'++*/

	/**
	 * 参照、更新用コンストラクタです。
	 *
	 * @param data 値を持つ '{'@link UpdatableDataObject'}'
	 */
	public /*++{2}++*//*--*/DTOBase/*--*/(UpdatableDataObject data) /*++'++*/{/*++'++*/
		relationship = BContext.get(RelationshipFactory.class).getInstance(/*++{2}Constants++*//*--*/Constants/*--*/.RESOURCE_LOCATOR);
		this.data = data;
	/*++'++*/}/*++'++*/

	@Override
	public UpdatableDataObject getDataObject() /*++'++*/{/*++'++*/
		return data;
	/*++'++*/}/*++'++*/

/*++{4}++*/
/*==DTOPropertyAccessorPart==*/
	/**
	 * setter
	 *
{3}
	 *
	 * @param value {2}
	 */
	@DTOSetter(column = "{1}", type = /*++{2}++*//*--*/Object/*--*/.class)
	public void set/*++{1}++*/(/*++{2}++*//*--*/Object/*--*/ value) /*++'++*/{/*++'++*/
		/*++{4}++*/ValueExtractor valueExtractor = BContext.get(SelectorConfigure.class).getValueExtractors().selectValueExtractor(
			relationship.getColumn("{1}").getType());
		data.setValue("{1}", valueExtractor.extractAsBinder(value));
	/*++'++*/}/*++'++*/

	/**
	 * getter
	 *
{3}
	 *
	 * @return {2}
	 */
	@DTOGetter(column = "{1}", type = /*++{2}++*//*--*/Object/*--*/.class, optional = /*++{8}++*//*--*/false/*--*/)
	public /*++{5}++*/ /*--*/String/*--*/get/*++{1}++*/() /*++'++*/{/*++'++*/
		Binder binder = data.getBinder("{1}");
		if (binder == null) return null;
		return /*++{6}++*/(/*++{2}++*//*--*/String/*--*/) binder.getValue()/*++{7}++*/;
	/*++'++*/}/*++'++*/

/*==DTOPropertyAccessorPart==*/
/*++{5}++*/
/*==DTORelationshipPart==*/
	/**
	 * このレコードが参照しているレコードの DTO を返します。<br>
	 *
	 * 参照先テーブル名 {0}<br>
	 * 外部キー名 {1}<br>
	 * 項目名 {2}<br>
	 *
	 * @return 参照しているレコードの DTO
	 */
	@DTORelationship(fk = "{1}", referenced = /*++{0}++*//*--*/DTOBase/*--*/.class)
	public /*++{0}++*//*--*/DTOBase/*--*/ /*++get{3}++*//*--*/getRelationship/*--*/() /*++'++*/{/*++'++*/
		return new /*++{0}++*//*--*/DTOBase/*--*/(
			data.getDataObject(/*++{4}Constants++*//*--*/Constants/*--*/./*++{0}++*/_BY_/*++{1}++*/));
	/*++'++*/}/*++'++*/

/*==DTORelationshipPart==*/
/*++'++*/}/*++'++*/
