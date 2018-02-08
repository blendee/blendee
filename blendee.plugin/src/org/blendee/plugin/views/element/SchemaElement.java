package org.blendee.plugin.views.element;

import org.blendee.jdbc.BlenConnection;
import org.blendee.jdbc.TablePath;
import org.blendee.plugin.BlendeePlugin;
import org.blendee.plugin.Constants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;

public class SchemaElement extends PropertySourceElement {

	private static final Image icon = Constants.SCHEMA_ICON.createImage();

	private static final AllBuildAction allBuildAction = new AllBuildAction();

	private static final RebuildAction rebuildAction = new RebuildAction();

	private final String name;

	private final TableElement[] children;

	SchemaElement(BlenConnection connection, String name) {
		this.name = name;
		TablePath[] tables = connection.getTables(name);
		children = new TableElement[tables.length];
		for (int i = 0; i < tables.length; i++) {
			children[i] = new TableElement(this, tables[i]);
		}
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
		return name;
	}

	@Override
	public Image getIcon() {
		return icon;
	}

	@Override
	public Element getParent() {
		return null;
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
			.getClassBuilderView()
			.getTreeViewer();
		viewer.setExpandedState(this, !viewer.getExpandedState(this));
	}

	@Override
	public void addActionToContextMenu(IMenuManager manager) {
		allBuildAction.elements = children;
		rebuildAction.elements = children;
		manager.add(allBuildAction);
		manager.add(rebuildAction);
	}

	@Override
	String getType() {
		return "スキーマ";
	}

	private static class AllBuildAction extends Action {

		private TableElement[] elements;

		private AllBuildAction() {
			String text = "すべてのテーブルに Blendee クラスを生成する";
			setText(text);
			setToolTipText(text);
			setImageDescriptor(Constants.SCHEMA_ICON);
		}

		@Override
		public void run() {
			for (TableElement element : elements) {
				element.build();
			}
		}
	}

	private static class RebuildAction extends Action {

		private TableElement[] elements;

		private RebuildAction() {
			String text = "すべての Blendee クラスを再生成する";
			setText(text);
			setToolTipText(text);
			setImageDescriptor(Constants.SCHEMA_ICON);
		}

		@Override
		public void run() {
			for (TableElement element : elements) {
				if (element.isAvailable()) element.build();
			}
		}
	}
}
