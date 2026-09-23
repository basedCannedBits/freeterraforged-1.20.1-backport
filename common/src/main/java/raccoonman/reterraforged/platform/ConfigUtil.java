package raccoonman.reterraforged.platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import dev.architectury.injectables.annotations.ExpectPlatform;
import raccoonman.reterraforged.RTFCommon;

public class ConfigUtil {
	public static final Path RTF_CONFIG_PATH = getConfigPath().resolve(RTFCommon.MOD_ID);
	public static final Path LEGACY_TF_CONFIG_PATH = getConfigPath().resolve(RTFCommon.LEGACY_TF_MOD_ID);
	public static final Path LEGACY_RTF_CONFIG_PATH = getConfigPath().resolve(RTFCommon.LEGACY_RTF_MOD_ID);
	
	public static Path ftf(String path) {
		return RTF_CONFIG_PATH.resolve(path);
	}
	
	public static Path legacy_tf(String path) {
		return LEGACY_TF_CONFIG_PATH.resolve(path);
	}
	public static Path legacy_rtf(String path) {
		return LEGACY_RTF_CONFIG_PATH.resolve(path);
	}
	
	@ExpectPlatform
	public static Path getConfigPath() {
		throw new IllegalStateException();
	}
	
	static {
		if(!Files.exists(RTF_CONFIG_PATH)) {
			try {
				Files.createDirectory(RTF_CONFIG_PATH);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
