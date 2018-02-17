package org.blendee.plugin.views.element;

import java.util.LinkedList;
import java.util.List;

import org.blendee.develop.ormgen.ORMGenerator;
import org.blendee.jdbc.BlendeeManager;
import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.TablePath;
import org.blendee.plugin.BlendeePlugin;
import org.blendee.plugin.Constants;
import org.blendee.sql.Relationship;
import org.blendee.sql.RelationshipFactory;
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
import org.eclipse.swt.graphics.Image;

public class TableElement extends PropertySourceElement {

	private static final Image builtIcon = Constants.TABLE_ICON.createImage();

	private static final Image unbuiltIcon = Constants.UNBUILT_TABLE_ICON.createImage();

	private static final TableAction action = new TableAction();

	private final SchemaElement parent;

	private final TablePath path;

	TableElement(SchemaElement parent, TablePath path) {
		this.parent = parent;
		this.path = path;
	}

	@Override
	public int getCategory() {
		return 0;
	}

	@Override
	public String getName() {
		return path.getTableName();
	}

	@Override
	public String getPath() {
		return path.toString();
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

	private IPackageFragmentRoot packageRoot() {
		IJavaProject project = BlendeePlugin.getDefault().getProject();
		try {
			IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
			List<IPackageFragmentRoot> srcRoots = new LinkedList<IPackageFragmentRoot>();
			for (IPackageFragmentRoot root : roots) {
				if (root.getKind() != IPackageFragmentRoot.K_SOURCE) continue;
				srcRoots.add(root);
			}

			if (srcRoots.size() != 1)
				throw new IllegalStateException(
					"パッケージを作成するためのパッケージルートが複数あります");

			return srcRoots.get(0);
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}
	}

	void build() {
		BlendeePlugin plugin = BlendeePlugin.getDefault();

		String packageName = plugin.getOutputPackage(parent.getName());

		IPackageFragmentRoot fragmentRoot = packageRoot();

		IPackageFragment baseFragment = getPackage(fragmentRoot, packageName);

		ORMGenerator generator = new ORMGenerator(
			ContextManager.get(BlendeeManager.class).getConnection(),
			baseFragment.getElementName(),
			parent.getName(),
			plugin.getRowManagerParentClass(),
			plugin.getRowParentClass(),
			plugin.getQueryParentClass(),
			plugin.getCodeFormatter(),
			plugin.useNumberClass(),
			!plugin.notUseNullGuard());

		RelationshipFactory factory = ContextManager.get(RelationshipFactory.class);
		Relationship relation = factory.getInstance(path);
		LinkedList<TablePath> tables = new LinkedList<>();
		//自身をセット
		tables.add(relation.getTablePath());

		IPackageFragment rowPackage = getPackage(fragmentRoot, packageName + ".row");
		IPackageFragment managerPackage = getPackage(fragmentRoot, packageName + ".manager");
		IPackageFragment queryPackage = getPackage(fragmentRoot, packageName + ".query");

		while (tables.size() > 0) {
			TablePath targetPath = tables.pop();
			Relationship target = factory.getInstance(targetPath);

			build(generator, rowPackage, managerPackage, queryPackage, target);

			collect(tables, target);

			//大量のテーブルを一度に実行したときのための節約クリア
			//Metadataはキャッシュを使用しているので、同じテーブルを処理してもDBから再取得はしない
			factory.clearCache();
		}
	}

	boolean isAvailable() {
		return isAvailable(path);
	}

	private void collect(List<TablePath> tables, Relationship relation) {
		for (Relationship child : relation.getRelationships()) {
			TablePath childPath = child.getTablePath();
			if (!isAvailable(childPath)) tables.add(childPath);
		}
	}

	private IPackageFragment getPackage(IPackageFragmentRoot fragmentRoot, String packageName) {
		IPackageFragment fragment = findPackage(packageName);
		if (fragment != null) return fragment;

		try {
			return fragmentRoot.createPackageFragment(packageName, false, null);
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}
	}

	private IPackageFragment findPackage(String packageName) {
		String packagePath = packageName.replace('.', '/');
		try {
			IJavaElement element = BlendeePlugin.getDefault().getProject().findElement(new Path(packagePath));
			if (element instanceof IPackageFragment) return (IPackageFragment) element;
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}

		return null;
	}

	private boolean isAvailable(TablePath path) {
		String typeName = String.join(
			".",
			new String[] {
				BlendeePlugin.getDefault().getOutputPackage(parent.getName()),
				"row",
				path.getTableName() });
		try {
			if (BlendeePlugin.getDefault().getProject().findType(typeName) != null) return true;
			return false;
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}
	}

	private void build(
		ORMGenerator generator,
		IPackageFragment rowPackage,
		IPackageFragment managerPackage,
		IPackageFragment queryPackage,
		Relationship relation) {
		TablePath path = relation.getTablePath();
		String tableName = path.getTableName();
		try {
			CodeFormatter formatter = ToolFactory.createCodeFormatter(
				BlendeePlugin.getDefault().getProject().getOptions(true));

			createSource(
				ORMGenerator.createRowCompilationUnitName(tableName),
				rowPackage,
				format(formatter, generator.buildRow(relation)));
			createSource(
				ORMGenerator.createRowManagerCompilationUnitName(tableName),
				managerPackage,
				format(formatter, generator.buildRowManager(relation)));
			createSource(
				ORMGenerator.createQueryCompilationUnitName(tableName),
				queryPackage,
				format(formatter, generator.buildQuery(relation)));
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}

		parent.refresh(path);
		Thread.yield();
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
