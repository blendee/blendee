package org.blendee.plugin;

import java.util.Map;

import org.blendee.jdbc.ContextManager;
import org.blendee.jdbc.OptionKey;
import org.blendee.util.Blendee;

/**
 * Blendee 全体を対象とする、簡易操作クラスです。
 * @author 千葉 哲嗣
 */
public class BlendeeStarter {

	/**
	 * Blendee を使用可能な状態にします。
	 * @param initValues Blendee を初期化するための値
	 */
	public static void start(
		JavaProjectClassLoader loader,
		Map<OptionKey<?>, ?> initValues)
		throws Exception {
		PluginDriverTransactionFactory.setClassLoader(loader);
		PluginAnnotationMetadataFactory.setClassLoader(loader);

		ContextManager.newStrategy();
		ContextManager.updateStrategy();

		Blendee blendee = new Blendee();

		blendee.setDefaultTransactionFactoryClass(
			PluginDriverTransactionFactory.class);
		blendee.setDefaultMetadataFactoryClass(
			PluginAnnotationMetadataFactory.class);

		//過去のキャッシュがあるかもしれないのでクリアしておく
		Blendee.clearCache();

		blendee.start(initValues);
	}
}
