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
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.IDocument;
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
	public void sessionEnded() {
		ASTParser parser = ASTParser.newParser(AST.JLS9);
		parser.setSource(unit);

		CompilationUnit unitNode = (CompilationUnit) parser.createAST(null);
		unitNode.recordModifications();

		ASTRewrite rewrite = ASTRewrite.create(unitNode.getAST());

		ASTNode replacement = rewrite.createStringPlaceholder("r -> r.of()", ASTNode.LAMBDA_EXPRESSION);

		unitNode.accept(new ASTVisitor() {

			@Override
			public boolean preVisit2(ASTNode node) {
				int start = node.getStartPosition();
				int length = node.getLength();

				System.out.println(offset + " >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + start);
				System.out.println(node.getClass());
				System.out.println(node);
				System.out.println(offset + " <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< " + (start + length));

				return true;
			}

			@Override
			public boolean visit(MethodInvocation node) {
				System.out.println("----------------------:" + node);
				return true;
			}

			@Override
			public boolean visit(SimpleName node) {
				System.out.println("!!!!!!!!!!!!!!!!!!!!:" + node);
				if (!node.toString().equals("function")) return true;

				System.out.println("####################:" + node);
				System.out.println("####################:" + replacement);
				rewrite.replace(node, replacement, null);

				try {
					rewrite.rewriteAST().apply(document);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return true;
			}
		});
	}

	private ICompilationUnit unit;

	private IDocument document;

	private int offset;

	@Override
	public List<ICompletionProposal> computeCompletionProposals(
		ContentAssistInvocationContext context,
		IProgressMonitor monitor) {
		if (!(context instanceof JavaContentAssistInvocationContext)) {
			return Collections.emptyList();
		}

		List<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();

		JavaContentAssistInvocationContext javaContext = (JavaContentAssistInvocationContext) context;

		unit = javaContext.getCompilationUnit();

		document = javaContext.getDocument();

		offset = context.getInvocationOffset();

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
