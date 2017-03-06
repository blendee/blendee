/*--*//*@formatter:off*//*--*/package /*++{0}++*//*--*/jp.ats.blendee.develop/*--*/;

import javax.annotation.Generated;

import jp.ats.blendee.support.BEntity;
import jp.ats.blendee.support.annotation.EntityGetter;
/*--*/import jp.ats.blendee.support.annotation.EntityRelationship;/*--*/
import jp.ats.blendee.support.annotation.EntitySetter;
import jp.ats.blendee.support.annotation.Resource;
/*++{8}++*/
import jp.ats.blendee.jdbc.BlendeeContext;
import jp.ats.blendee.orm.DataObject;
import jp.ats.blendee.selector.SelectorConfigure;
import jp.ats.blendee.selector.ValueExtractor;
import jp.ats.blendee.sql.Binder;
import jp.ats.blendee.sql.Relationship;
import jp.ats.blendee.sql.RelationshipFactory;

/**
 * 自動生成された '{'@link BEntity'}' の実装クラスです。
 *
{6}
 */
@Resource(schema = "{1}", table = "{2}")
@Generated(value = /*++'++*/{/*++'++*/"{9}"/*++'++*/}/*++'++*/)
/*++{7}++*/public class /*++{2}++*//*--*/EntityBase/*--*/
	extends /*++{3}++*//*--*/Object/*--*/
	implements BEntity /*++'++*/{/*++'++*/

	private final DataObject data;

	private final Relationship relationship;

	/**
	 * 登録用コンストラクタです。
	 */
	public /*++{2}++*//*--*/EntityBase/*--*/() /*++'++*/{/*++'++*/
		relationship = BlendeeContext.get(RelationshipFactory.class).getInstance(/*++{2}Constants++*//*--*/Constants/*--*/.RESOURCE_LOCATOR);
		data = new DataObject(relationship);
	/*++'++*/}/*++'++*/

	/**
	 * 参照、更新用コンストラクタです。
	 *
	 * @param data 値を持つ '{'@link DataObject'}'
	 */
	public /*++{2}++*//*--*/EntityBase/*--*/(DataObject data) /*++'++*/{/*++'++*/
		relationship = BlendeeContext.get(RelationshipFactory.class).getInstance(/*++{2}Constants++*//*--*/Constants/*--*/.RESOURCE_LOCATOR);
		this.data = data;
	/*++'++*/}/*++'++*/

	@Override
	public DataObject getDataObject() /*++'++*/{/*++'++*/
		return data;
	/*++'++*/}/*++'++*/

/*++{4}++*/
/*==EntityPropertyAccessorPart==*/
	/**
	 * setter
	 *
{3}
	 *
	 * @param value {2}
	 */
	@EntitySetter(column = "{1}", type = /*++{2}++*//*--*/Object/*--*/.class)
	public void set/*++{0}++*/(/*++{2}++*//*--*/Object/*--*/ value) /*++'++*/{/*++'++*/
		/*++{4}++*/ValueExtractor valueExtractor = BlendeeContext.get(SelectorConfigure.class).getValueExtractors().selectValueExtractor(
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
	@EntityGetter(column = "{1}", type = /*++{2}++*//*--*/Object/*--*/.class, optional = /*++{8}++*//*--*/false/*--*/)
	public /*++{5}++*/ /*--*/String/*--*/get/*++{0}++*/() /*++'++*/{/*++'++*/
		Binder binder = data.getBinder("{1}");
		if (binder == null) return null;
		return /*++{6}++*/(/*++{2}++*//*--*/String/*--*/) binder.getValue()/*++{7}++*/;
	/*++'++*/}/*++'++*/

/*==EntityPropertyAccessorPart==*/
/*++{5}++*/
/*==EntityRelationshipPart==*/
	/**
	 * このレコードが参照しているレコードの Entity を返します。<br>
	 *
	 * 参照先テーブル名 {0}<br>
	 * 外部キー名 {1}<br>
	 * 項目名 {2}<br>
	 *
	 * @return 参照しているレコードの Entity
	 */
	@EntityRelationship(fk = "{1}", referenced = /*++{0}++*//*--*/EntityBase/*--*/.class)
	public /*++{0}++*//*--*/EntityBase/*--*/ /*++get{3}++*//*--*/getRelationship/*--*/() /*++'++*/{/*++'++*/
		return new /*++{0}++*//*--*/EntityBase/*--*/(
			data.getDataObject(/*++{4}Constants++*//*--*/Constants/*--*/./*++{0}++*/_BY_/*++{1}++*/));
	/*++'++*/}/*++'++*/

/*==EntityRelationshipPart==*/
/*++'++*/}/*++'++*/
