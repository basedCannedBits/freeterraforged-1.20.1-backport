package etcodehome.freeterraforged.compat.biolith;

import java.util.List;

import com.google.common.collect.ImmutableList;

import etcodehome.freeterraforged.platform.ModLoaderUtil;

// Targets Biolith Forge 1.0.1-beta.1, the first (and currently only) Forge build of Biolith for
// Minecraft 1.20.1. Only covers the preset-preview GUI integration (see BiolithPreviewContext) --
// real world generation needs no compat code at all, since Biolith and FTF both just hook into
// vanilla's biome system independently.
public class BiolithCompat {
	public static final List<String> BIOLITH_COMPAT_MIXINS = ImmutableList.of(
		mixinClass("biolith.BiolithDimensionBiomePlacementAccessor"),
		mixinClass("biolith.MixinBiolithOverworldBiomePlacement"),
		mixinClass("biolith.BiolithBiomeCoordinatorAccessor")
	);

	public static boolean isEnabled() {
		return ModLoaderUtil.isLoaded("biolith");
	}

	public static boolean isBiolithMixin(String mixinClassName) {
		return BIOLITH_COMPAT_MIXINS.contains(mixinClassName);
	}

	private static String mixinClass(String className) {
		return "etcodehome.freeterraforged.mixin." + className;
	}
}
