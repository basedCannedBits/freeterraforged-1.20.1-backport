package etcodehome.freeterraforged.compat.biolith;

import java.util.List;

import com.google.common.collect.ImmutableList;

import etcodehome.freeterraforged.platform.ModLoaderUtil;

// only for biolith forge 1.0.1-beta.1 (the only forge build for 1.20.1). just the preview screen --
// real world gen doesn't need any compat, biolith and ftf don't step on each other there
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
