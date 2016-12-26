package jp.ats.blendee.plugin.views.element;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;

import jp.ats.blendee.develop.ORMGenerator;
import jp.ats.blendee.jdbc.BlendeeContext;
import jp.ats.blendee.jdbc.BlendeeManager;
import jp.ats.blendee.jdbc.ResourceLocator;
import jp.ats.blendee.plugin.BlendeePlugin;
import jp.ats.blendee.plugin.Constants;
import jp.ats.blendee.plugin.views.ClassBuilderView;
import jp.ats.blendee.sql.Relationship;
import jp.ats.blendee.sql.RelationshipFactory;

public class TableElement extends PropertySourceElement {

	private static final Image builtIcon = Constants.TABLE_ICON.createImage();

	private static final Image unbuiltIcon = Constants.UNBUILT_TABLE_ICON.createImage();

	private static final TableAction action = new TableAction();

	private final SchemaElement parent;

	private final ResourceLocator locator;

	TableElement(SchemaElement parent, ResourceLocator locator) {
		this.parent = parent;
		this.locator = locator;
	}

	@Override
	public int getCategory() {
		return 0;
	}

	@Override
	public String getName() {
		return locator.getTableName();
	}

	@Override
	public String getPath() {
		return locator.toString();
	}

	@Override
	public Image getIcon() {
		if (isAvailable()) return builtIcon;
		return unbuiltIcon;
	}

	@Override
	public Element getParent() {
		return parent;
	}

	@Override
	public Element[] getChildren() {
		return EMPTY_ARRAY;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public void doubleClick() {
		build();
	}

	@Override
	public void addActionToContextMenu(IMenuManager manager) {
		if (isAvailable()) return;
		action.element = this;
		manager.add(action);
	}

	@Override
	String getType() {
		return "テーブル";
	}

	void build() {
		BlendeePlugin plugin = BlendeePlugin.getDefault();

		String packageName = plugin.getOutputPackage(parent.getName());
		String packagepath = packageName.replace('.', '/');

		IJavaProject project = plugin.getProject();

		IPackageFragment fragment;
		try {
			IJavaElement element = project.findElement(new Path(packagepath));
			if (!(element instanceof IPackageFragment)) {
				IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
				List<IPackageFragmentRoot> srcRoots = new LinkedList<IPackageFragmentRoot>();
				for (IPackageFragmentRoot root : roots) {
					if (root.getKind() != IPackageFragmentRoot.K_SOURCE) continue;
					srcRoots.add(root);
				}

				if (srcRoots.size() != 1)
					throw new IllegalStateException(
						"パッケージ " + packageName + " を作成するためのパッケージルートが複数あります");

				fragment = srcRoots.get(0).createPackageFragment(packageName, false, null);
			} else {
				fragment = (IPackageFragment) element;
			}
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}

		ORMGenerator generator = new ORMGenerator(
			BlendeeContext.get(BlendeeManager.class).getConnection(),
			fragment.getElementName(),
			parent.getName(),
			plugin.getEntityManagerParentClass(),
			plugin.getEntityParentClass(),
			plugin.getQueryParentClass(),
			plugin.getCodeFormatter(),
			plugin.useNumberClass(),
			!plugin.notUseNullGuard(),
			jp.ats.blendee.plugin.ORMGenerator.class.getName());

		Relationship relation = BlendeeContext.get(RelationshipFactory.class).getInstance(locator);
		String tableName = locator.getTableName();
		try {
			CodeFormatter formatter = ToolFactory.createCodeFormatter(project.getOptions(true));

			createSource(
				ORMGenerator.createConstantsCompilationUnitName(tableName),
				fragment,
				format(formatter, generator.buildConstants(relation)));
			createSource(
				ORMGenerator.createEntityManagerCompilationUnitName(tableName),
				fragment,
				format(formatter, generator.buildEntityManager(relation)));
			createSource(
				ORMGenerator.createEntityCompilationUnitName(tableName),
				fragment,
				format(formatter, generator.buildEntity(relation)));
			createSource(
				ORMGenerator.createQueryCompilationUnitName(tableName),
				fragment,
				format(formatter, generator.buildQuery(relation)));
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}

		ClassBuilderView view = BlendeePlugin.getDefault().getClassBuilderView();
		TreeViewer viewer = view.getTreeViewer();
		viewer.refresh(this);
	}

	boolean isAvailable() {
		String typeName = String.join(
			".",
			new String[] {
				BlendeePlugin.getDefault().getOutputPackage(parent.getName()),
				locator.getTableName() });
		try {
			if (BlendeePlugin.getDefault().getProject().findType(typeName) != null) return true;
			return false;
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}
	}

	private static String format(CodeFormatter formatter, String source) {
		Document document = new Document(source);
		try {
			formatter.format(
				CodeFormatter.K_COMPILATION_UNIT,
				source,
				0,
				source.length(),
				0,
				null).apply(document);
		} catch (BadLocationException e) {
			throw new IllegalStateException(e);
		}
		return document.get();
	}

	private static void createSource(String compilationUnitName, IPackageFragment fragment, String source)
		throws JavaModelException {
		ICompilationUnit compilationUnit = fragment.getCompilationUnit(compilationUnitName);
		if (compilationUnit.exists() && source.equals(compilationUnit.getSource())) return;

		fragment.createCompilationUnit(compilationUnitName, source, true, null);
	}

	private static class TableAction extends Action {

		private TableElement element;

		private TableAction() {
			String text = "Data Object クラスを生成する";
			setText(text);
			setToolTipText(text);
			setImageDescriptor(Constants.TABLE_ICON);
		}

		@Override
		public void run() {
			element.build();
		}
	}
}
