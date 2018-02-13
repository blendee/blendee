package org.blendee.plugin.views.element;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
			ContextManager.get(BlendeeManager.class).getConnection(),
			fragment.getElementName(),
			parent.getName(),
			plugin.getRowManagerParentClass(),
			plugin.getRowParentClass(),
			plugin.getQueryParentClass(),
			plugin.getCodeFormatter(),
			plugin.useNumberClass(),
			!plugin.notUseNullGuard());

		Relationship relation = ContextManager.get(RelationshipFactory.class).getInstance(path);
		Set<TablePath> tables = new LinkedHashSet<>();
		//自身をセット
		tables.add(relation.getTablePath());
		//最大限テーブルの重複を排除してメモリを節約
		collect(tables, relation);

		tables.forEach(path -> {
			if (!isAvailable(path)) build(generator, fragment, path);
		});
	}

	boolean isAvailable() {
		return isAvailable(path);
	}

	private void collect(Set<TablePath> tables, Relationship relation) {
		for (Relationship child : relation.getRelationships()) {
			tables.add(child.getTablePath());
			collect(tables, child);
		}
	}

	private boolean isAvailable(TablePath path) {
		String typeName = String.join(
			".",
			new String[] {
				BlendeePlugin.getDefault().getOutputPackage(parent.getName()),
				path.getTableName() });
		try {
			if (BlendeePlugin.getDefault().getProject().findType(typeName) != null) return true;
			return false;
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}
	}

	private void build(ORMGenerator generator, IPackageFragment fragment, TablePath path) {
		Relationship relation = ContextManager.get(RelationshipFactory.class).getInstance(path);
		String tableName = path.getTableName();
		try {
			CodeFormatter formatter = ToolFactory.createCodeFormatter(
				BlendeePlugin.getDefault().getProject().getOptions(true));

			createSource(
				ORMGenerator.createRowManagerCompilationUnitName(tableName),
				fragment,
				format(formatter, generator.buildRowManager(relation)));
			createSource(
				ORMGenerator.createRowCompilationUnitName(tableName),
				fragment,
				format(formatter, generator.buildRow(relation)));
			createSource(
				ORMGenerator.createQueryCompilationUnitName(tableName),
				fragment,
				format(formatter, generator.buildQuery(relation)));
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}

		parent.refresh(path);
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
