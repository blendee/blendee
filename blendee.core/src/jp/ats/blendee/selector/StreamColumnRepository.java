package jp.ats.blendee.selector;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jp.ats.blendee.internal.IOStream;
import jp.ats.blendee.internal.U;
import jp.ats.blendee.jdbc.ResourceLocator;

/**
 * {@link IOStream} から定義情報を読み込むことができる {@link ColumnRepository} です。
 *
 * @author 千葉 哲嗣
 */
public class StreamColumnRepository extends AbstractColumnRepository {

	private static final Pattern locationLinePattern = Pattern
		.compile("^id=([^&]+)&location=([^&]*)&using=\\{([^\\}]*)\\}&timestamp=(\\d+)$");

	private static final Pattern columnLinePattern = Pattern.compile("^id=([^&]+)&column=([^&]+)&mark=(true|false)$");

	private final IOStream repository;

	/**
	 * このクラスのインスタンスを生成します。
	 *
	 * @param repository リポジトリのソース
	 */
	public StreamColumnRepository(IOStream repository) {
		this.repository = repository;
		initialize();
	}

	@Override
	void read(Map<String, LocationSource> locationMap) {
		String[] lines;
		try (InputStream input = new BufferedInputStream(repository.getInputStream())) {
			lines = SimpleResourceReader.readLines(input);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} finally {
			U.close(repository);
		}

		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			Matcher locationMatcher = locationLinePattern.matcher(line);
			Matcher columnMatcher = columnLinePattern.matcher(line);
			if (locationMatcher.matches()) {
				LocationSource locationSource = new LocationSource(
					locationMatcher.group(1),
					ResourceLocator.parse(locationMatcher.group(2)),
					Arrays.asList(locationMatcher.group(3).split(","))
						.stream()
						.filter(e -> e.length() > 0)
						.collect(Collectors.toList()),
					Long.parseLong(locationMatcher.group(4)));
				locationMap.put(locationSource.getId(), locationSource);
			} else if (columnMatcher.matches()) {
				LocationSource locationSource = locationMap.get(columnMatcher.group(1));
				if (locationSource == null) throw new IllegalStateException("IDが存在しません " + "[" + line + "]");

				locationSource.add(
					new ColumnSource(
						new ColumnPath(columnMatcher.group(2)),
						Boolean.parseBoolean(columnMatcher.group(3))));
			} else {
				throw new IllegalStateException("不正なフォーマットです " + "[" + line + "]");
			}
		}
	}

	@Override
	void write(Map<String, LocationSource> locationMap) {
		try (PrintWriter writer = new PrintWriter(
			new BufferedWriter(new OutputStreamWriter(repository.getOutputStream())))) {
			String[] ids = getIDs();
			for (String id : ids) {
				LocationSource locationSource = locationMap.get(id);
				writer.println(locationSource);
				for (ColumnSource columnSource : locationSource.getColumnSources())
					writer.println("id=" + id + "&column=" + columnSource);
				writer.println();
				writer.flush();
			}

			writer.flush();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} finally {
			U.close(repository);
		}
	}
}
