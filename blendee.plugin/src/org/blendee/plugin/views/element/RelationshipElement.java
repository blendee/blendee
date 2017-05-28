package org.blendee.plugin.views.element;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.blendee.jdbc.CrossReference;
import org.blendee.jdbc.MetadataUtilities;
import org.blendee.plugin.BlendeePlugin;
import org.blendee.plugin.Constants;
import org.blendee.selector.CommandColumnRepository;
import org.blendee.sql.Column;
import org.blendee.sql.Relationship;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;

public class RelationshipElement extends PropertySourceElement {

	private static final Image icon = Constants.RELATIONSHIP_ICON.createImage();

	private final Relationship relationship;

	private final String name;

	private final Element[] children;

	private Element parent;

	private Element parentForPath;

	RelationshipElement(
		CommandColumnRepository repository,
		String id,
		Relationship relationship,
		Map<Column, ColumnElement> allColumnMap) {
		this.relationship = relationship;
		name = createName(relationship);
		Map<Column, ColumnElement> myColumnMap = new HashMap<>();
		List<ColumnElement> remain = new LinkedList<>();
		Column[] myColumns = relationship.getColumns();
		for (int i = 0; i < myColumns.length; i++) {
			Column column = myColumns[i];
			ColumnElement element = new ColumnElement(
				this,
				repository,
				id,
				column);
			element.setParent(this);
			myColumnMap.put(column, element);
			allColumnMap.put(column, element);
			remain.add(element);
		}

		List<Element> elements = new LinkedList<>();

		Column[] pkColumns = relationship.getPrimaryKeyColumns();
		if (pkColumns.length > 0) {
			ColumnElement[] pkColumnElements = new ColumnElement[pkColumns.length];
			for (int i = 0; i < pkColumns.length; i++) {
				pkColumnElements[i] = myColumnMap.get(pkColumns[i]);
				remain.remove(pkColumnElements[i]);
			}

			elements.add(
				new PrimaryKeyElement(
					this,
					MetadataUtilities.getPrimaryKeyName(
						relationship.getResourceLocator()),
					pkColumnElements));
		}

		Relationship[] relations = relationship.getRelationships();
		for (Relationship element : relations)
			elements.add(
				createForeignKeyElement(
					repository,
					id,
					relationship,
					element,
					myColumnMap,
					allColumnMap,
					remain));

		elements.addAll(remain);
		children = elements.toArray(new Element[elements.size()]);
	}

	@Override
	public int getCategory() {
		return 0;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPath() {
		return parentForPath.getPath() + PATH_SEPARETOR + name;
	}

	@Override
	public Image getIcon() {
		return icon;
	}

	@Override
	public Element getParent() {
		return parent;
	}

	@Override
	public Element[] getChildren() {
		return children.clone();
	}

	@Override
	public boolean hasChildren() {
		return children.length > 0;
	}

	@Override
	public void doubleClick() {
		TreeViewer viewer = BlendeePlugin.getDefault()
			.getQueryEditorView()
			.getTreeViewer();
		viewer.setExpandedState(this, !viewer.getExpandedState(this));
	}

	@Override
	public void addActionToContextMenu(IMenuManager manager) {}

	void setParent(Element parent) {
		this.parent = parent;
	}

	void setParentForPath(Element parentForPath) {
		this.parentForPath = parentForPath;
	}

	Relationship getRelationship() {
		return relationship;
	}

	static String createName(Relationship relationship) {
		return relationship.getResourceLocator()
			+ "("
			+ relationship.getID()
			+ ")";
	}

	private ForeignKeyElement createForeignKeyElement(
		CommandColumnRepository repository,
		String id,
		Relationship parent,
		Relationship child,
		Map<Column, ColumnElement> myColumns,
		Map<Column, ColumnElement> allColumns,
		List<ColumnElement> remain) {
		CrossReference reference = child.getCrossReference();
		String[] fks = reference.getForeignKeyColumnNames();
		String[] pks = reference.getPrimaryKeyColumnNames();
		ForeignKeyColumnElement[] columns = new ForeignKeyColumnElement[fks.length];
		for (int i = 0; i < fks.length; i++) {
			ColumnElement base = myColumns.get(parent.getColumn(fks[i]));
			columns[i] = new ForeignKeyColumnElement(base, pks[i]);
			remain.remove(base);
		}

		return new ForeignKeyElement(
			this,
			reference.getForeignKeyName(),
			new RelationshipElement(repository, id, child, allColumns),
			columns);
	}

	@Override
	String getType() {
		return "テーブル";
	}
}
