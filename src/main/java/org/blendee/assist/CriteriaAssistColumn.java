package org.blendee.assist;

import org.blendee.sql.Column;

/**
 * {@link Column} を内部に保持するものを表すインターフェイスです。
 * @author 千葉 哲嗣
 * @param <O> {@link LogicalOperators}
 */
public interface CriteriaAssistColumn<O extends LogicalOperators<?>> extends AssistColumn {

}
