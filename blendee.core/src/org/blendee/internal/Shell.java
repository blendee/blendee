package org.blendee.internal;

/**
 * 内部使用ユーティリティクラス
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public interface Shell {

	void prepare();

	void execute() throws Exception;

	void doFinally();
}
