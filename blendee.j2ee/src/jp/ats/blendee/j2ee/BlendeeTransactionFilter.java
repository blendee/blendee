package jp.ats.blendee.j2ee;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import jp.ats.blendee.internal.TransactionManager;
import jp.ats.blendee.internal.TransactionShell;
import jp.ats.blendee.jdbc.BContext;
import jp.ats.blendee.jdbc.BlendeeManager;
import jp.ats.blendee.jdbc.BTransaction;
import jp.ats.blendee.util.Blendee;
import jp.ats.blendee.util.ParsableOptionKey;

/**
 * {@link Filter} の範囲でトランザクションを管理するためのクラスです。
 *
 * @author 千葉 哲嗣
 */
public class BlendeeTransactionFilter implements Filter {

	private static final ThreadLocal<BTransaction> transaction = new ThreadLocal<>();

	/**
	 * 現在のトランザクションを返します。
	 *
	 * @return 現在のトランザクション
	 */
	public static BTransaction getTransaction() {
		return transaction.get();
	}

	@Override
	public void init(final FilterConfig config) throws ServletException {
		new Blendee().start(
			Collections.list(config.getInitParameterNames()).stream().collect(
				Collectors.toConcurrentMap(
					key -> ParsableOptionKey.convert(key),
					key -> ParsableOptionKey.convert(key).parse(config.getInitParameter(key)))));
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
		throws IOException, ServletException {
		if (BContext.get(BlendeeManager.class).hasConnection()) {
			chain.doFilter(request, response);
			return;
		}

		try {
			TransactionManager.start(new TransactionShell() {

				@Override
				public void execute() throws Exception {
					transaction.set(getTransaction());
					chain.doFilter(request, response);
				}
			});
		} catch (Throwable t) {
			Throwable rootCause = ServletUtilities.getRootCause(t);
			throw new ServletException(rootCause);
		} finally {
			transaction.set(null);
		}
	}

	@Override
	public void destroy() {}
}
