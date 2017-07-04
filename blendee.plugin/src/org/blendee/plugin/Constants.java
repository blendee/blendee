package org.blendee.plugin;

import org.eclipse.jface.resource.ImageDescriptor;

public class Constants {

	public static final String TITLE = "Blendee";

	public static final String SCHEMA_NAMES = "schemaNames";

	public static final String OUTPUT_PACKAGE_NAMES = "outputPackageNames";

	public static final String JDBC_DRIVER_CLASS = "jdbcDriverClass";

	public static final String JDBC_URL = "jdbcURL";

	public static final String JDBC_USER = "jdbcUser";

	public static final String JDBC_PASSWORD = "jdbcPassword";

	public static final String COLUMN_REPOSITORY_FILE = "columnRepositoryFile";

	public static final String COLUMN_REPOSITORY_FACTORY_CLASS = "columnRepositoryFactoryClass";

	public static final String TRANSACTION_FACTORY_CLASS = "transactionFactoryClass";

	public static final String METADATA_FACTORY_CLASS = "metadataFactoryClass";

	public static final String ROW_MANAGER_PARENT_CLASS = "rowManagerParentClass";

	public static final String ROW_PARENT_CLASS = "rowParentClass";

	public static final String QUERY_PARENT_CLASS = "queryParentClass";

	public static final String CODE_FORMATTER_CLASS = "codeFormatterClass";

	public static final String USE_NUMBER_CLASS = "useNumberClass";

	public static final String NOT_USE_NULL_GUARD = "notUseNullGuard";

	public static final ImageDescriptor BLENDEE_ICON;

	public static final ImageDescriptor COLLAPSE_ALL_ICON;

	public static final ImageDescriptor DERETE_ALL_EDIT_ICON;

	public static final ImageDescriptor SAVE_ICON;

	public static final ImageDescriptor REFESH_ICON;

	public static final ImageDescriptor UNDO_ICON;

	public static final ImageDescriptor REDO_ICON;

	public static final ImageDescriptor REMOVE_ICON;

	public static final ImageDescriptor PACKAGE_ICON;

	public static final ImageDescriptor CLASS_ICON;

	public static final ImageDescriptor ANCHOR_ICON;

	public static final ImageDescriptor ERROR_ICON;

	public static final ImageDescriptor RELATIONSHIP_ICON;

	public static final ImageDescriptor COLUMN_ICON;

	public static final ImageDescriptor COLUMN_DISABLE_ICON;

	public static final ImageDescriptor UNMARK_COLUMN_ICON;

	public static final ImageDescriptor PRIMARY_KEY_ICON;

	public static final ImageDescriptor FOREIGN_KEY_ICON;

	public static final ImageDescriptor SCHEMA_ICON;

	public static final ImageDescriptor TABLE_ICON;

	public static final ImageDescriptor UNBUILT_TABLE_ICON;

	private static final String ID = "org.blendee.plugin";

	static {
		BLENDEE_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/blendee.png");

		COLLAPSE_ALL_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/collapse_all.gif");

		DERETE_ALL_EDIT_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/delete_all_edit.gif");

		SAVE_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/save.gif");

		REFESH_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/refresh.gif");

		UNDO_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/undo.gif");

		REDO_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/redo.gif");

		REMOVE_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/remove.gif");

		PACKAGE_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/package.gif");

		CLASS_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/class.gif");

		ANCHOR_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/anchor.gif");

		ERROR_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/error.gif");

		RELATIONSHIP_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/relationship.png");

		COLUMN_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/column.png");

		COLUMN_DISABLE_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/column_disable.png");

		UNMARK_COLUMN_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/unmark_column.png");

		PRIMARY_KEY_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/primary_key.png");

		FOREIGN_KEY_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/foreign_key.png");

		SCHEMA_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/schema.gif");

		TABLE_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/table.png");

		UNBUILT_TABLE_ICON = BlendeePlugin.imageDescriptorFromPlugin(ID, "icons/unbuilt_table.png");
	}
}
