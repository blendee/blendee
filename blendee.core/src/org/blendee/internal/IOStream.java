package org.blendee.internal;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * サービスを受けるクライアントとの入出力ストリームを保持します。
 * <br>
 * 内部使用ユーティリティクラス
 * @author 千葉 哲嗣
 */
@SuppressWarnings("javadoc")
public interface IOStream extends Closeable {

	InputStream getInputStream() throws IOException;

	OutputStream getOutputStream() throws IOException;
}
