package etcodehome.freeterraforged.compat.biolith;

import com.terraformersmc.biolith.impl.noise.OpenSimplexNoise2;
import net.minecraft.core.RegistryAccess;

// Real implementation for Biolith Forge 1.0.1-beta.1 (the first, and so far only, Forge-1.20.1-compatible
// release). Deliberately much simpler than FTF's own upstream (Biolith 3.x) compat: that version
// reimplements Biolith's replacement/sub-biome selection logic from scratch, using accessor mixins on
// Biolith's raw request data. In 1.0.1-beta.1, DimensionBiomePlacement already exposes real selection
// methods (selectReplacement/selectSubBiome/getDirectReplacement) that do this correctly on their own --
// so instead of reimplementing the algorithm, this only redirects the *noise input* those methods read
// from (replacementNoise/seedlets) to a value seeded from the preset preview's own seed, when a preview
// is active. That means the preview automatically stays correct even if Biolith changes its own internal
// selection logic later, since we never duplicate it.
public final class BiolithPreviewContext {
	private static final ThreadLocal<State> ACTIVE = new ThreadLocal<>();

	private BiolithPreviewContext() {
	}

	// Used to propagate the active preview session from the GUI thread onto whichever ForkJoinPool
	// worker thread ends up computing a given preview tile.
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

	// No-op for this Biolith version: unlike 3.x, 1.0.1-beta.1's selection methods don't need an
	// early biome-lookup handoff from us -- they use whatever lookup Biolith itself already has.
	public static void preInitializeBiomeLookup(RegistryAccess registries) {
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
			// Matches the derivation FTF's own (3.x-targeting) compat used: 8 single-byte seedlets
			// sliced out of the world seed. This low-level seeding utility is very unlikely to have
			// changed between Biolith versions, since doing so would silently break existing worlds.
			this.seedlets = new int[8];
			for (int i = 0; i < this.seedlets.length; i++) {
				this.seedlets[i] = (int) ((seed >> (i * 8)) & 255L);
			}
		}
	}
}
