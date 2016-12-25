package jp.ats.blendee.plugin.views.element;

import java.util.LinkedList;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import jp.ats.blendee.plugin.Constants;
import jp.ats.blendee.selector.CommandColumnRepository;
import jp.ats.blendee.sql.Column;
import jp.ats.blendee.sql.Relationship;

abstract class AbstractColumnElement extends PropertySourceElement {

	private static final Image icon = Constants.COLUMN_ICON.createImage();

	private static final Image unmarkIcon = Constants.UNMARK_COLUMN_ICON
		.createImage();

	private static final String markId = "MARK";

	private static final PropertyDescriptor[] descripters = {
		new PropertyDescriptor(markId, "取得した値") };

	final String id;

	final CommandColumnRepository repository;

	final Column column;

	AbstractColumnElement(
		String id,
		CommandColumnRepository repository,
		Column column) {
		this.id = id;
		this.repository = repository;
		this.column = column;
	}

	@Override
	public Image getIcon() {
		return repository.marks(id, column) ? icon : unmarkIcon;
	}

	@Override
	public String getPath() {
		return id + PATH_SEPARETOR + createPathBase(column);
	}

	@Override
	public Element[] getChildren() {
		return Element.EMPTY_ARRAY;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	IPropertyDescriptor[] getMyPropertyDescriptors() {
		return descripters;
	}

	@Override
	Object getMyPropertyValue(Object id) {
		if (markId.equals(id))
			return repository.marks(this.id, column) ? "使用" : "未使用";
		throw new IllegalStateException();
	}

	static PropertyDescriptor getPropertyDescriptor() {
		return descripters[0];
	}

	static String createPathBase(Column column) {
		LinkedList<String> nameBuffer = new LinkedList<>();
		Relationship relationship = column.getRelationship();
		nameBuffer.add(RelationshipElement.createName(relationship));
		while (!relationship.isRoot()) {
			relationship = relationship.getParent();
			nameBuffer.addFirst(RelationshipElement.createName(relationship));
		}
		nameBuffer.add(column.getName());
		return String.join(PATH_SEPARETOR, nameBuffer);
	}
}
