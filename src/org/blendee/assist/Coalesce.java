package org.blendee.assist;

import java.util.LinkedList;
import java.util.List;

class Coalesce {

	static String createTemplate(int columns) {
		List<String> list = new LinkedList<>();
		for (int i = 0; i < columns; i++) {
			list.add("{" + i + "}");
		}

		return "COALESCE(" + String.join(", ", list) + ")";
	}
}
