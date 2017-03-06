package jp.ats.blendee.develop.ormgen;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

/**
 * プロジェクト内で一意となる ID を生成します。
 *
 * @author 千葉 哲嗣
 */
public class IDGenerator {

	/**
	 * UUID を BASE64 エンコードした文字列を生成します。
	 *
	 * @return ID
	 */
	public static String generate() {
		UUID uuid = UUID.randomUUID();
		ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
		buffer.putLong(uuid.getMostSignificantBits());
		buffer.putLong(uuid.getLeastSignificantBits());

		String id = Base64.getUrlEncoder().encodeToString(buffer.array());

		//末尾の == を削除
		return id.substring(0, id.length() - 2);
	}
}
