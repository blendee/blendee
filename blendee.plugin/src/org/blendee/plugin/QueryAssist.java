package org.blendee.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.blendee.develop.ormgen.IDGenerator;
import org.blendee.support.Query;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;

public class QueryAssist implements IJavaCompletionProposalComputer {

	public QueryAssist() {}

	@Override
	public void sessionStarted() {}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public void sessionEnded() {}

	@Override
	public List<ICompletionProposal> computeCompletionProposals(
		ContentAssistInvocationContext context,
		IProgressMonitor monitor) {
		if (!(context instanceof JavaContentAssistInvocationContext)) {
			return Collections.emptyList();
		}

		List<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();

		JavaContentAssistInvocationContext javaContext = (JavaContentAssistInvocationContext) context;

		ICompilationUnit unit = javaContext.getCompilationUnit();

		int offset = context.getInvocationOffset();

		TargetInfo targetInfo;
		IJavaElement[] elements;
		IType type;
		try {
			targetInfo = searchTargetInfo(unit.getSource().substring(0, offset));

			elements = unit.codeSelect(targetInfo.startPosition, targetInfo.length);

			if (elements.length == 0) return proposalList;

			IJavaElement element = elements[0];
			if (element.getElementType() != IJavaElement.TYPE) return proposalList;

			type = (IType) element;

			boolean result = stream(type.getSuperInterfaceNames())
				.filter(e -> e.equals(Query.class.getSimpleName()))
				.findFirst()
				.map(
					e -> stream(resolve(type, e))
						.findFirst()
						.map(resolved -> String.join(".", resolved))
						.map(joined -> joined.equals(Query.class.getName()))
						.orElse(false))
				.orElse(false);

			if (!result) return proposalList;
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}

		CompletionProposal proposal;

		String replacement = (targetInfo.needsDot ? "." : "") + "of(\"" + IDGenerator.generate() + "\")";
		proposal = new CompletionProposal(
			replacement,
			offset,
			0,
			replacement.length(),
			Constants.BLENDEE_ICON.createImage(),
			type.getElementName() + " を生成",
			null,
			"ランダムな文字列を ID として " + type.getElementName() + " のインスタンスを生成します");
		proposalList.add(proposal);

		return proposalList;
	}

	@Override
	public List<IContextInformation> computeContextInformation(
		ContentAssistInvocationContext context,
		IProgressMonitor monitor) {
		return null;
	}

	private static TargetInfo searchTargetInfo(String prefix) {
		boolean needsDot = true;
		if (prefix.endsWith(".")) {
			prefix = prefix.substring(0, prefix.length() - 1);
			needsDot = false;
		}
		char[] chars = prefix.toCharArray();
		for (int i = chars.length - 1; i >= 0; i--) {
			char c = chars[i];
			if (!Character.isJavaIdentifierPart(c) || !Character.isJavaIdentifierStart(c))
				return new TargetInfo(i + 1, chars.length - i - 1, needsDot);
		}

		return new TargetInfo(0, chars.length, needsDot);
	}

	private static class TargetInfo {

		private final int startPosition;

		private final int length;

		private final boolean needsDot;

		private TargetInfo(int startPosition, int length, boolean needsDot) {
			this.startPosition = startPosition;
			this.length = length;
			this.needsDot = needsDot;
		}
	}

	private static <T> Stream<T> stream(T[] array) {
		return Arrays.asList(array).stream();
	}

	private static String[][] resolve(IType type, String name) {
		try {
			return type.resolveType(name);
		} catch (JavaModelException e) {
			throw new IllegalStateException(e);
		}
	}
}
