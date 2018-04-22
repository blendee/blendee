package org.blendee.support;

import org.blendee.jdbc.PreparedStatementComplementer;

public interface Playbackable<T> {

	T playback(PreparedStatementComplementer complementer);
}
