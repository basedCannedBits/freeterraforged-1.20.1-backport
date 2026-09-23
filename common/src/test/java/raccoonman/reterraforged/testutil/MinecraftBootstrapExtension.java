package raccoonman.reterraforged.testutil;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;

/**
 * Auto-registered for every test class (see junit-platform.properties). Bootstraps vanilla registries once per
 * test JVM before any test runs; otherwise the first test touching a registry before bootstrap permanently
 * poisons BuiltInRegistries' static initializer and every later test fails with NoClassDefFoundError.
 */
public class MinecraftBootstrapExtension implements BeforeAllCallback {
	@Override
	public void beforeAll(ExtensionContext context) {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
	}
}
