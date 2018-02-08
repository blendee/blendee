package org.blendee.plugin.views.element;

import java.util.LinkedList;
import java.util.List;

import org.blendee.internal.TransactionManager;
import org.blendee.internal.TransactionShell;
import org.blendee.jdbc.BlenConnection;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ContextManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;

public class ORMGeneratorRootElement implements Element {

	private final SchemaElement[] children;

	public ORMGeneratorRootElement(final String[] schemas) {
		final List<SchemaElement> list = new LinkedList<SchemaElement>();
		try {
			TransactionManager.start(new TransactionShell() {

				@Override
				public void execute() throws Exception {
					BlenConnection connection = ContextManager.get(BlendeeManager.class)
						.getConnection();
					for (String schema : schemas) {
						list.add(new SchemaElement(connection, schema));
					}
				}
			});
		} catch (Throwable t) {
			throw new IllegalStateException(t);
		}

		children = list.toArray(new SchemaElement[list.size()]);
	}

	public ORMGeneratorRootElement() {
		children = new SchemaElement[] {};
	}

	@Override
	public void addActionToContextMenu(IMenuManager manager) {}

	@Override
	public void doubleClick() {}

	@Override
	public int getCategory() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Element[] getChildren() {
		return children;
	}

	@Override
	public Image getIcon() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Element getParent() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasChildren() {
		return false;
	}
}
