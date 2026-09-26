package etcodehome.freeterraforged.compat.biolith;

import com.terraformersmc.biolith.impl.noise.OpenSimplexNoise2;
import net.minecraft.core.RegistryAccess;

// biolith 1.0.1-beta.1 (only forge build for 1.20.1). just swaps the noise biolith uses for
// replacement/sub-biome picks with a preview-seeded one when the preset preview is open, so the
// preview shows what biolith would actually do without us having to reimplement its logic.
public final class BiolithPreviewContext {
	private static final ThreadLocal<State> ACTIVE = new ThreadLocal<>();

	private BiolithPreviewContext() {
	}

	// passes the active preview session from the gui thread to whatever worker thread renders a tile
	public static Object captureState() {
		return ACTIVE.get();
	}

	public static AutoCloseable attach(Object captured) {
		if (!(captured instanceof State state)) {
			return () -> {
			};
		}
		State previous = ACTIVE.get();
		ACTIVE.set(state);
		return () -> {
			if (previous == null) {
				ACTIVE.remove();
			} else {
				ACTIVE.set(previous);
			}
		};
	}

	// biolith's own mixin needs its registry lookup set up before it'll work, normally that only
	// happens on real server start which the preview skips. set it manually here or preview crashes.
	public static void preInitializeBiomeLookup(RegistryAccess registries) {
		if (registries instanceof RegistryAccess.Frozen frozen) {
			etcodehome.freeterraforged.mixin.biolith.BiolithBiomeCoordinatorAccessor
				.freeterraforged$setRegistryManagerStatic(frozen);
		}
	}

	public static AutoCloseable open(long seed) {
		State previous = ACTIVE.get();
		ACTIVE.set(new State(seed));
		return () -> {
			if (previous == null) {
				ACTIVE.remove();
			} else {
				ACTIVE.set(previous);
			}
		};
	}

	public static boolean isActive() {
		return ACTIVE.get() != null;
	}

	public static OpenSimplexNoise2 replacementNoise(OpenSimplexNoise2 original) {
		State state = ACTIVE.get();
		return state != null ? state.replacementNoise : original;
	}

	public static int[] seedlets(int[] original) {
		State state = ACTIVE.get();
		return state != null ? state.seedlets : original;
	}

	private static final class State {
		private final OpenSimplexNoise2 replacementNoise;
		private final int[] seedlets;

		private State(long seed) {
			this.replacementNoise = new OpenSimplexNoise2(seed);
			// 8 seedlets sliced out of the world seed, one byte each
			this.seedlets = new int[8];
			for (int i = 0; i < this.seedlets.length; i++) {
				this.seedlets[i] = (int) ((seed >> (i * 8)) & 255L);
			}
		}
	}
}
