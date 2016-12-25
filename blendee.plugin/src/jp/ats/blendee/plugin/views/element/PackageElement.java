package jp.ats.blendee.plugin.views.element;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;

import jp.ats.blendee.plugin.Constants;

public class PackageElement extends JavaElement {

	static final Image icon = Constants.PACKAGE_ICON.createImage();

	PackageElement(EditorRootElement root, String path) {
		super(root, path);
	}

	@Override
	public int getCategory() {
		return 1;
	}

	@Override
	public Image getIcon() {
		return icon;
	}

	@Override
	public void addActionToContextMenu(IMenuManager manager) {}

	@Override
	String getType() {
		return "パッケージ";
	}

	@Override
	boolean exists() {
		return true;
	}
}
