package etcodehome.freeterraforged.compat.biolith;

import net.minecraft.core.RegistryAccess;

/** 1.20.1 backport stub (see BiolithCompat). */
public final class BiolithPreviewContext {
	private BiolithPreviewContext() {
	}

	public static Object captureState() {
		return null;
	}

	public static AutoCloseable attach(Object captured) {
		return () -> {
		};
	}

	public static void preInitializeBiomeLookup(RegistryAccess registries) {
	}
}
