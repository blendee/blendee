package org.blendee.util.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.blendee.util.SQLProxyBuilder;

/**
 * {@link SQLProxyBuilder} の対象となるインターフェイスであることを表すアノテーションです。
 * @author 千葉 哲嗣
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface SQLProxy {
}
