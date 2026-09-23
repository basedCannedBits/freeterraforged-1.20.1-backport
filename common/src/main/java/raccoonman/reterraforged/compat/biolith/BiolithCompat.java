package raccoonman.reterraforged.compat.biolith;

import java.util.List;

/**
 * 1.20.1 backport stub: FTF's Biolith compat targets Biolith 3.x (MC 1.21) internals.
 * Original sources are parked in /backport-parked/biolith. Always disabled here.
 */
public class BiolithCompat {
	public static final List<String> BIOLITH_COMPAT_MIXINS = List.of();

	public static boolean isEnabled() {
		return false;
	}

	public static boolean isBiolithMixin(String mixinClassName) {
		return mixinClassName.startsWith("raccoonman.reterraforged.mixin.biolith.");
	}
}
