package jp.ats.blendee.plugin.popup.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import jp.ats.blendee.internal.U;
import jp.ats.blendee.plugin.BlendeePlugin;
import jp.ats.blendee.plugin.BlendeePlugin.JavaProjectException;
import jp.ats.blendee.plugin.Constants;
import jp.ats.blendee.plugin.views.QueryEditorView;

public class ChangeProjectHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String id = event.getCommand().getId();
		if ("jp.ats.blendee.plugin.restartQueryEditor".equals(id)) {
			Starter.start("jp.ats.blendee.plugin.queryEditorView");
		} else if ("jp.ats.blendee.plugin.restartORMGenerator".equals(id)) {
			Starter.start("jp.ats.blendee.plugin.classBuilderView");
		} else {
			throw new IllegalStateException(id);
		}

		QueryEditorView queryEditor = BlendeePlugin.getDefault()
			.getQueryEditorView();
		if (queryEditor != null && queryEditor.hasEdit()) {
			if (!MessageDialog.openConfirm(
				null,
				Constants.TITLE,
				"定義情報を再度読み込み直します"
					+ U.LINE_SEPARATOR
					+ "（保存されていない変更は失われてしまいます）"))
				return null;
		}

		ISelection selection = HandlerUtil.getActiveMenuSelection(event);

		if (selection == null) return null;

		IStructuredSelection structured = (IStructuredSelection) selection;
		Object element = structured.getFirstElement();
		if (element == null) return null;

		if (!(element instanceof IJavaProject)) return null;

		IJavaProject project = (IJavaProject) element;

		try {
			BlendeePlugin.getDefault().setProjectAndRefresh(project);
		} catch (JavaProjectException e) {
			MessageDialog.openError(
				null,
				Constants.TITLE,
				"設定に問題があります" + U.LINE_SEPARATOR + e.getMessage());
		}

		return null;
	}
}
