package org.blendee.jdbc;

@SuppressWarnings("javadoc")
public enum StoredIdentifier {

	UPPER_CASE {

		@Override
		public String regularize(String name) {
			return name.toUpperCase();
		}
	},

	LOWER_CASE {

		@Override
		public String regularize(String name) {
			return name.toLowerCase();
		}
	},

	MIXED_CASE {

		@Override
		public String regularize(String name) {
			return name;
		}
	};

	public static StoredIdentifier getInstance(
		boolean storesUpperCaseIdentifiers,
		boolean storesLowerCaseIdentifiers) {
		if (storesUpperCaseIdentifiers) return UPPER_CASE;
		if (storesLowerCaseIdentifiers) return LOWER_CASE;
		return MIXED_CASE;
	}

	public abstract String regularize(String name);
}
