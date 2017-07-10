package org.blendee.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.blendee.jdbc.ColumnMetadata;
import org.blendee.jdbc.TablePath;
import org.blendee.jdbc.TableMetadata;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Blendee Metadata Extension フォーマットの XML ファイルから定義情報を読み込み {@link VirtualSpace} を生成するファクトリクラスです。
 * @author 千葉 哲嗣
 */
class FileVirtualSpaceFactory {

	private FileVirtualSpaceFactory() {}

	/**
	 * パラメータの {@link URL} の指し示す XML ファイルを読み込み {@link VirtualSpace} を生成します。
	 * @param xml XML の URL
	 * @return 新しい {@link VirtualSpace} のインスタンス
	 * @throws IOException XML の読み込みに失敗した場合
	 * @throws ClassNotFoundException XML 内で定義されている項目の型のクラスが存在しない場合
	 */
	static VirtualSpace getInstance(URL xml) throws IOException, ClassNotFoundException {
		try (InputStream stream = xml.openStream()) {
			return getInstance(new BufferedInputStream(stream));
		}
	}

	/**
	 * パラメータの {@link InputStream} から読み込める XML から {@link VirtualSpace} を生成します。
	 * @param xml XML を読み込むストリーム
	 * @return 新しい {@link VirtualSpace} のインスタンス
	 * @throws IOException XML の読み込みに失敗した場合
	 * @throws ClassNotFoundException XML 内で定義されている項目の型のクラスが存在しない場合
	 */
	static VirtualSpace getInstance(InputStream xml) throws IOException, ClassNotFoundException {
		VirtualSpace space = new VirtualSpace();
		NodeList list;
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			list = (NodeList) xpath.evaluate(
				"/blendee-metadata-extension/table",
				new InputSource(xml),
				XPathConstants.NODESET);
			int length = list.getLength();
			for (int i = 0; i < length; i++) {
				Node table = list.item(i);
				space.addTable(processTable(xpath, table));
			}
		} catch (XPathExpressionException e) {
			throw new Error(e);
		}
		return space;
	}

	private static TableSource processTable(XPath xpath, Node table)
		throws IOException, XPathExpressionException, ClassNotFoundException {
		TableMetadata tableMetadata = processTableMetadata(xpath, table);
		String tableName = tableMetadata.getName();

		if (!isJavaIdentifierPart(tableName))
			throw new IllegalStateException("テーブル名 " + tableName + " は使用できない文字を含んでいます");

		TablePath path = new TablePath(tableMetadata.getSchemaName(), tableName);

		ColumnMetadata[] columnMetadatas = processColumnMetadatas(
			path,
			xpath,
			(NodeList) xpath.evaluate("columns/column", table, XPathConstants.NODESET));

		for (ColumnMetadata columnMetadata : columnMetadatas) {
			if (!isJavaIdentifierPart(columnMetadata.getName())) throw new IllegalStateException(
				"テーブル名 " + tableName + " 項目名 " + columnMetadata.getName() + " は使用できない文字を含んでいます");
		}

		PrimaryKeySource pk;
		if (xpath.evaluate("primary-key", table, XPathConstants.NODE) != null) {

			String primaryKeyName = xpath.evaluate("primary-key/@name", table);

			if (!isJavaIdentifierPart(primaryKeyName))
				throw new IllegalStateException("テーブル名 " + tableName + " 主キー名 " + primaryKeyName + " は使用できない文字を含んでいます");

			pk = new PrimaryKeySource(
				primaryKeyName,
				processColumnNames(
					xpath,
					(NodeList) xpath.evaluate("primary-key/column", table, XPathConstants.NODESET)),
				true);
		} else {
			pk = null;
		}

		NodeList foreignKeyNodes = (NodeList) xpath.evaluate("foreign-key", table, XPathConstants.NODESET);
		int foreignKeyNodesLength = foreignKeyNodes.getLength();
		ForeignKeySource[] fks = new ForeignKeySource[foreignKeyNodesLength];
		for (int i = 0; i < foreignKeyNodesLength; i++) {
			Node node = foreignKeyNodes.item(i);

			String foreignKeyName = xpath.evaluate("@name", node);

			if (!isJavaIdentifierPart(foreignKeyName)) throw new IllegalStateException(
				"テーブル名 " + tableName + " 外部キー名 " + foreignKeyName + " は使用できない文字を含んでいます");

			fks[i] = new ForeignKeySource(
				foreignKeyName,
				processColumnNames(xpath, (NodeList) xpath.evaluate("column", node, XPathConstants.NODESET)),
				TablePath.parse(xpath.evaluate("@references", node)));
		}

		return new TableSource(path, tableMetadata, columnMetadatas, pk, fks);
	}

	private static String[] processColumnNames(XPath xpath, NodeList list) throws XPathExpressionException {
		int length = list.getLength();
		String[] result = new String[length];
		for (int i = 0; i < length; i++) {
			result[i] = xpath.evaluate("@name", list.item(i));
		}

		return result;
	}

	private static TableMetadata processTableMetadata(XPath xpath, Node table) throws XPathExpressionException {
		return new VirtualTableMetadata(
			xpath.evaluate("@schema", table),
			xpath.evaluate("@name", table),
			xpath.evaluate("@type", table),
			xpath.evaluate("@remarks", table));
	}

	private static ColumnMetadata[] processColumnMetadatas(TablePath path, XPath xpath, NodeList list)
		throws XPathExpressionException, ClassNotFoundException {
		int length = list.getLength();
		ColumnMetadata[] result = new ColumnMetadata[length];
		for (int i = 0; i < length; i++) {
			Node node = list.item(i);

			//必須
			int type = Integer.parseInt(xpath.evaluate("@type", node));

			//省略可
			String typeName = xpath.evaluate("@type-name", node);

			//省略可
			String sizeValue = xpath.evaluate("@size", node);
			int size = 0;
			if (sizeValue != null && sizeValue.length() > 0) {
				size = Integer.parseInt(sizeValue);
			}

			//省略可
			boolean hasDecimalDigits = Boolean.parseBoolean(xpath.evaluate("@has-decimal-digits", node));

			//hasDecimalDigits = true の場合、必須
			int decimalDigits = 0;
			if (hasDecimalDigits) {
				decimalDigits = Integer.parseInt(xpath.evaluate("@decimal-disits", node));
			}

			//省略可
			String remarks = xpath.evaluate("@remarks", node);

			//省略可
			String defaultValue = xpath.evaluate("@default-value", node);

			//必須
			int ordinalPosition = Integer.parseInt(xpath.evaluate("@ordinal-position", node));

			//省略可
			boolean isNotNull = Boolean.parseBoolean(xpath.evaluate("@is-not-null", node));

			VirtualColumnMetadata metadata = new VirtualColumnMetadata(
				path.getSchemaName(),
				path.getTableName(),
				xpath.evaluate("@name", list.item(i)),
				type,
				typeName,
				size,
				hasDecimalDigits,
				decimalDigits,
				remarks,
				defaultValue,
				ordinalPosition,
				isNotNull);
			result[i] = metadata;
		}

		return result;
	}

	private static boolean isJavaIdentifierPart(String target) {
		char[] targetChars = target.toCharArray();
		if (targetChars.length == 0) return false;
		if (!Character.isJavaIdentifierStart(targetChars[0])) return false;
		for (Character c : targetChars) {
			if (!Character.isJavaIdentifierPart(c)) return false;
		}

		return true;
	}
}
