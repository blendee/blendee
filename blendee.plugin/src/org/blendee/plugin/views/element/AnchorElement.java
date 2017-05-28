package org.blendee.plugin.views.element;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.blendee.internal.U;
import org.blendee.jdbc.BlendeeContext;
import org.blendee.jdbc.ResourceLocator;
import org.blendee.plugin.BlendeePlugin;
import org.blendee.plugin.Constants;
import org.blendee.plugin.TextDialog;
import org.blendee.plugin.views.QueryEditorView;
import org.blendee.selector.CommandColumnRepository;
import org.blendee.sql.Column;
import org.blendee.sql.FromClause;
import org.blendee.sql.QueryBuilder;
import org.blendee.sql.RelationshipFactory;
import org.blendee.sql.SelectClause;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class AnchorElement extends PropertySourceElement implements Comparable<AnchorElement> {

	private static final Image icon = Constants.ANCHOR_ICON.createImage();

	private static final Image invalidIcon = Constants.ERROR_ICON.createImage();

	private static final ChangeModeAction changeModeAction = new ChangeModeAction();

	private static final ChangeLocationAction changeLocationAction = new ChangeLocationAction();

	private static final ChangeIDAction changeIdAction = new ChangeIDAction();

	private static final RemoveAction removeAction = new RemoveAction();

	private static final ViewClassAction viewClassAction = new ViewClassAction();

	private static final ViewSQLAction viewSqlAction = new ViewSQLAction();

	private static final ViewErrorMessagesAction viewErrorMessagesAction = new ViewErrorMessagesAction();

	private static final CorrectErrorAction correctErrorAction = new CorrectErrorAction();

	private static final ClearAction clearMarksAction = new ClearAction();

	private static final IInputValidator idValidator = new IInputValidator() {

		@Override
		public String isValid(String id) {
			if (id.length() == 0) return "未入力です";
			return null;
		}
	};

	private static final String timestampId = "TIMESTAMP";

	private static final String errorId = "ERROR";

	private static final String modeId = "MODE";

	private static final PropertyDescriptor[] descripters = {
		new PropertyDescriptor(timestampId, "利用マーク除去日"),
		new PropertyDescriptor(errorId, "エラー"),
		new PropertyDescriptor(modeId, "表示モード") };

	private final EditorRootElement root;

	private final CommandColumnRepository repository;

	private Element parent;

	private boolean useColumnMode = false;

	private String id;

	private String usingClassName;

	private String[] allUsingClassNames;

	private String name;

	private String anchorModeName;

	private RelationshipElement relationship;

	private Map<Column, ColumnElement> allColumns = new HashMap<>();

	AnchorElement(
		EditorRootElement root,
		String id,
		String usingClassName,
		String[] allUsingClassNames,
		CommandColumnRepository repository) {
		this.root = root;
		this.id = id;
		this.usingClassName = usingClassName;
		this.allUsingClassNames = allUsingClassNames;
		this.repository = repository;
		prepareName();
	}

	@Override
	public int getCategory() {
		return 3;
	}

	@Override
	public String getName() {
		return root.getAnchorMode() ? anchorModeName : name;
	}

	@Override
	public String getPath() {
		return id;
	}

	@Override
	public Image getIcon() {
		if (!isValidAnchor()) return invalidIcon;
		return icon;
	}

	@Override
	public Element getParent() {
		return parent;
	}

	@Override
	public Element[] getChildren() {
		if (useColumnMode) return UseColumnElement.getElements(
			this,
			repository.getColumns(id));

		if (!repository.getResourceLocator(id).exists()) return new Element[0];

		prepareRelationship();
		return new Element[] { relationship };
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public void doubleClick() {
		TreeViewer viewer = BlendeePlugin.getDefault()
			.getQueryEditorView()
			.getTreeViewer();
		viewer.setExpandedState(this, !viewer.getExpandedState(this));
	}

	@Override
	public void addActionToContextMenu(IMenuManager manager) {
		viewClassAction.element = this;
		viewClassAction.setEnabled((allUsingClassNames.length == 1 || !root.getAnchorMode()) && existsParent());
		manager.add(viewClassAction);

		String changeModeActionText;
		if (useColumnMode) {
			changeModeActionText = "ツリー表示";
		} else {
			changeModeActionText = "検索に使用するカラムのみ表示";
		}
		changeModeAction.setText(changeModeActionText);
		changeModeAction.setToolTipText(changeModeActionText);
		if (repository.getColumns(id).length > 0) {
			viewSqlAction.element = this;
			viewSqlAction.setEnabled(true);
			changeModeAction.element = this;
			changeModeAction.setEnabled(true);
		} else {
			viewSqlAction.setEnabled(false);
			if (useColumnMode) {
				changeModeAction.setEnabled(true);
			} else {
				changeModeAction.setEnabled(false);
			}
		}
		manager.add(viewSqlAction);
		manager.add(changeModeAction);

		manager.add(new Separator());

		changeLocationAction.element = this;
		manager.add(changeLocationAction);

		changeIdAction.element = this;
		manager.add(changeIdAction);

		manager.add(new Separator());

		removeAction.element = this;
		manager.add(removeAction);

		manager.add(new Separator());

		if (!isValidAnchor()) {
			viewErrorMessagesAction.element = this;
			viewErrorMessagesAction.setEnabled(true);
		} else {
			viewErrorMessagesAction.setEnabled(false);
		}

		if (repository.getErrorMessages(id).length > 0) {
			correctErrorAction.element = this;
			correctErrorAction.setEnabled(true);
		} else {
			correctErrorAction.setEnabled(false);
		}

		manager.add(viewErrorMessagesAction);
		manager.add(correctErrorAction);

		manager.add(new Separator());

		clearMarksAction.element = this;
		manager.add(clearMarksAction);
	}

	@Override
	public String toString() {
		return id;
	}

	public void refresh() {
		prepareName();
		BlendeePlugin.getDefault()
			.getQueryEditorView()
			.getTreeViewer()
			.refresh(this);
	}

	void setParent(JavaElement parent) {
		this.parent = parent;
		parent.addChild(this);
	}

	@Override
	String getType() {
		return "アンカー";
	}

	void changeMode() {
		useColumnMode = !useColumnMode;
		BlendeePlugin.getDefault()
			.getQueryEditorView()
			.getTreeViewer()
			.refresh(this);
	}

	void selectColumn(Column column) {
		ColumnElement element = allColumns.get(column);
		BlendeePlugin.getDefault()
			.getQueryEditorView()
			.getTreeViewer()
			.setSelection(new StructuredSelection(element));
	}

	String getID() {
		return id;
	}

	CommandColumnRepository getRepository() {
		return repository;
	}

	@Override
	IPropertyDescriptor[] getMyPropertyDescriptors() {
		return descripters;
	}

	@Override
	Object getMyPropertyValue(Object id) {
		if (timestampId.equals(id))
			return DateFormat.getDateTimeInstance().format(
				new Date(repository.getMarkClearedTimestamp(this.id)));

		if (errorId.equals(id)) return (isValidAnchor()) ? "なし" : "あり";

		if (modeId.equals(id))
			return useColumnMode ? "検索に使用するカラム一覧表示" : "ツリー表示";

		throw new IllegalStateException();
	}

	@Override
	public int compareTo(AnchorElement another) {
		return id.compareTo(another.id);
	}

	private boolean isValidAnchor() {
		return repository.getErrorMessages(id).length == 0;
	}

	private boolean existsParent() {
		if (!(parent instanceof JavaElement)) return true;

		return ((JavaElement) parent).exists();
	}

	private void prepareName() {
		anchorModeName = id
			+ " - "
			+ String.join(", ", allUsingClassNames)
			+ " - "
			+ repository.getResourceLocator(id);

		name = id + " - " + repository.getResourceLocator(id);
	}

	private void prepareRelationship() {
		if (relationship != null
			&& !relationship.getRelationship()
				.getResourceLocator()
				.equals(repository.getResourceLocator(id)))
			relationship = null;

		if (relationship != null) return;

		allColumns.clear();

		relationship = new RelationshipElement(
			repository,
			id,
			BlendeeContext.get(RelationshipFactory.class).getInstance(
				repository.getResourceLocator(id)),
			allColumns);
		relationship.setParent(this);
		relationship.setParentForPath(this);
	}

	private void reselect() {
		BlendeePlugin.getDefault()
			.getQueryEditorView()
			.getTreeViewer()
			.setSelection(new StructuredSelection(this));
	}

	private static class ChangeLocationAction extends Action {

		private AnchorElement element;

		private ChangeLocationAction() {
			String text = "テーブルの変更";
			setText(text);
			setToolTipText(text);
			setImageDescriptor(Constants.RELATIONSHIP_ICON);
		}

		@Override
		public void run() {
			ResourceLocator locator = element.repository
				.getResourceLocator(element.id);
			QueryEditorView view = BlendeePlugin.getDefault()
				.getQueryEditorView();
			InputDialog dialog = view.createLocationDialog(
				locator.toString(),
				"（エラーカラムの参照するテーブルを正しい名称に直す目的で変更する場合"
					+ U.LINE_SEPARATOR
					+ "変更を保管するまでエラーカラムの参照するテーブルは変更されません）");
			if (dialog.open() != Window.OK) return;
			element.repository.add(
				element.id,
				ResourceLocator.parse(dialog.getValue()));
			element.prepareName();
			element.relationship = null;
			view.optimizeRepositoryActions(element.repository);
			view.getTreeViewer().refresh(element);
			element.reselect();
		}
	}

	private static class ChangeIDAction extends Action {

		private AnchorElement element;

		private ChangeIDAction() {
			String text = "ID の変更";
			setText(text);
			setToolTipText(text);
			setImageDescriptor(Constants.ANCHOR_ICON);
		}

		@Override
		public void run() {
			InputDialog dialog = new InputDialog(
				null,
				Constants.TITLE,
				"新しい ID を入力してください ",
				element.id,
				idValidator);
			if (dialog.open() == Window.OK) {
				String newId = dialog.getValue();
				if (element.id.equals(newId)) return;
				element.repository.renameID(element.id, newId);
				element.id = newId;
				element.prepareName();
				element.relationship = null;
				QueryEditorView view = BlendeePlugin.getDefault().getQueryEditorView();
				view.refreshTree();
				view.optimizeRepositoryActions(element.repository);
				element.reselect();
			}
		}
	}

	private static class RemoveAction extends Action {

		private AnchorElement element;

		private RemoveAction() {
			String text = "削除";
			setText(text);
			setToolTipText(text);
			setImageDescriptor(Constants.REMOVE_ICON);
		}

		@Override
		public void run() {
			element.repository.remove(element.id);
			QueryEditorView view = BlendeePlugin.getDefault().getQueryEditorView();
			view.optimizeRepositoryActions(element.repository);
			view.refreshTree();
		}
	}

	private static class ViewClassAction extends Action {

		private AnchorElement element;

		private ViewClassAction() {
			String text = "使用クラスを見る";
			setText(text);
			setToolTipText(text);
			setImageDescriptor(null);
		}

		@Override
		public void run() {
			IType type;

			//内部無名クラスの場合は外のクラスに書き換え
			String className = element.usingClassName.replaceFirst("\\$\\d+$", "");

			try {
				type = BlendeePlugin.getDefault().getProject().findType(className);
				if (type == null) return;
			} catch (JavaModelException e) {
				throw new RuntimeException(e);
			}

			IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow()
				.getActivePage();
			try {
				page.showView(JavaUI.ID_PACKAGES);
			} catch (PartInitException e) {
				throw new RuntimeException(e);
			}

			page.findView(JavaUI.ID_PACKAGES)
				.getSite()
				.getSelectionProvider()
				.setSelection(new StructuredSelection(type));
		}
	}

	private static class ChangeModeAction extends Action {

		private AnchorElement element;

		@Override
		public void run() {
			element.changeMode();
			element.reselect();
		}
	}

	private static class ViewSQLAction extends Action {

		private AnchorElement element;

		private ViewSQLAction() {
			String text = "SQL を生成する";
			setText(text);
			setToolTipText(text);
		}

		@Override
		public void run() {
			ResourceLocator locator = element.repository.getResourceLocator(element.id);
			Column[] columns = element.repository.getColumns(element.id);
			QueryBuilder builder = new QueryBuilder(new FromClause(locator));
			SelectClause selectClause = new SelectClause();
			for (int i = 0; i < columns.length; i++) {
				selectClause.add(columns[i]);
			}

			builder.setSelectClause(selectClause);

			new TextDialog("生成されたSQL", builder.toString()).open();
		}
	}

	private static class ViewErrorMessagesAction extends Action {

		private AnchorElement element;

		private ViewErrorMessagesAction() {
			String text = "エラーメッセージを見る";
			setText(text);
			setToolTipText(text);
		}

		@Override
		public void run() {
			List<String> errorMessages = new LinkedList<>();
			errorMessages.addAll(
				Arrays.asList(element.repository.getErrorMessages(element.id)));
			MessageDialog.openError(
				null,
				Constants.TITLE,
				String.join(U.LINE_SEPARATOR, errorMessages));
		}
	}

	private static class CorrectErrorAction extends Action {

		private AnchorElement element;

		private CorrectErrorAction() {
			String text = "エラーのあるカラムを除去する";
			setText(text);
			setToolTipText(text);
		}

		@Override
		public void run() {
			element.repository.correctErrors(element.id);
			QueryEditorView view = BlendeePlugin.getDefault().getQueryEditorView();
			view.optimizeRepositoryActions(element.repository);
			view.getTreeViewer().refresh(element);
			element.reselect();
		}
	}

	private static class ClearAction extends Action {

		private AnchorElement element;

		private ClearAction() {
			String text = "使用マークの除去、使用クラスのクリア";
			setText(text);
			setToolTipText(text);
			setEnabled(true);
		}

		@Override
		public void run() {
			element.repository.clear(element.id, System.currentTimeMillis());
			QueryEditorView view = BlendeePlugin.getDefault().getQueryEditorView();
			view.optimizeRepositoryActions(element.repository);
			view.refreshTree();
			element.reselect();
		}
	}
}
