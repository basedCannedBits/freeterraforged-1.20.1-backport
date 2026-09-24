package etcodehome.freeterraforged.world.worldgen.surface.rule;

import java.util.ArrayList;
import java.util.List;

import etcodehome.freeterraforged.world.worldgen.FTFRandomState;
import etcodehome.freeterraforged.world.worldgen.noise.NoiseUtil;
import etcodehome.freeterraforged.world.worldgen.noise.module.Noises;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import etcodehome.freeterraforged.world.worldgen.noise.module.Noise;
import etcodehome.freeterraforged.world.worldgen.surface.FTFSurfaceSystem;

public record StrataRule(ResourceLocation name, Holder<Noise> selector, List<Strata> strata, int iterations) implements SurfaceRules.RuleSource {
	public static final Codec<StrataRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("name").forGetter(StrataRule::name),
			Noise.CODEC.fieldOf("selector").forGetter(StrataRule::selector),
			Strata.CODEC.listOf().fieldOf("strata").forGetter(StrataRule::strata),
			Codec.INT.fieldOf("iterations").forGetter(StrataRule::iterations)
	).apply(instance, StrataRule::new));

	public StrataRule {
		strata = ImmutableList.copyOf(strata);
	}

	@Override
	public Source apply(Context ctx) {
		if(ctx.system instanceof FTFSurfaceSystem ftfSurfaceSystem && (Object) ctx.randomState instanceof FTFRandomState ftfRandomState) {
			return new Source(ctx, ftfRandomState.seed(this.selector.value()), ftfSurfaceSystem.getOrCreateStrata(this.name, this::generateStrata));
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public KeyDispatchDataCodec<StrataRule> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}

	private List<List<Layer>> generateStrata(RandomSource random) {
		List<List<Layer>> layers = new ArrayList<>();
		for(int i = 0; i < this.iterations; i++) {
			List<Layer> layer = new ArrayList<>();
			for(Strata strata : this.strata) {
				layer.addAll(strata.generateLayers(random));
			}
			layers.add(layer);
		}
		return layers;
	}

	public record Strata(TagKey<Block> materials, Holder<Noise> noise, int attempts, int minLayers, int maxLayers, float minDepth, float maxDepth) {
		public static final Codec<Strata> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				TagKey.hashedCodec(Registries.BLOCK).fieldOf("materials").forGetter(Strata::materials),
				Noise.CODEC.fieldOf("noise").forGetter(Strata::noise),
				Codec.INT.fieldOf("attempts").forGetter(Strata::attempts),
				Codec.INT.fieldOf("min_layers").forGetter(Strata::minLayers),
				Codec.INT.fieldOf("max_layers").forGetter(Strata::maxLayers),
				Codec.FLOAT.fieldOf("min_depth").forGetter(Strata::minDepth),
				Codec.FLOAT.fieldOf("max_depth").forGetter(Strata::maxDepth)
		).apply(instance, Strata::new));

		// Diagnostic (backport): print the resolved tag order + the actual seeded draws, once per tag,
		// so we can directly compare Java-17 vs Java-21 runs against real numbers instead of theory.
		private static final java.util.Set<TagKey<Block>> DIAG_LOGGED = java.util.concurrent.ConcurrentHashMap.newKeySet();

		public List<Layer> generateLayers(RandomSource random) {
			int lastIndex = -1;
			int layers = this.minLayers + NoiseUtil.round(random.nextFloat() * (this.maxLayers - this.minLayers));
			List<Layer> result = new ArrayList<>();
			// Fix (backport): measured directly against the real FTF NeoForge 1.21.1 jar (via a standalone
			// probe mod querying the same tag independently) -- NeoForge resolves #freeterraforged:rock to
			// [stone, granite, andesite, diorite], exactly matching the tag JSON's declared order. Forge
			// 1.20.1's getTagOrEmpty() does NOT preserve that order for this tag (measured: it returns
			// [granite, andesite, stone, diorite] instead), even though the vanilla tag-loading source looks
			// like it should. Since the same seed draws the same *index* on both platforms, this reordering
			// was the actual, sole cause of granite/stone (etc.) swapping identity between builds. Soil/
			// sediment/clay were not affected (measured identical on both platforms) so are left untouched.
			List<Holder<Block>> materials = ImmutableList.copyOf(BuiltInRegistries.BLOCK.getTagOrEmpty(this.materials));
			if (this.materials.location().getPath().equals("rock")) {
				List<String> neoforgeOrder = List.of("minecraft:stone", "minecraft:granite", "minecraft:andesite", "minecraft:diorite");
				materials = materials.stream()
						.sorted(java.util.Comparator.comparingInt(holder -> {
							String id = holder.unwrapKey().map(key -> key.location().toString()).orElse("");
							int idx = neoforgeOrder.indexOf(id);
							return idx < 0 ? Integer.MAX_VALUE : idx;
						}))
						.collect(ImmutableList.toImmutableList());
			}

			boolean diag = DIAG_LOGGED.add(this.materials);
			if (diag) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < materials.size(); i++) {
					sb.append(i).append('=').append(materials.get(i).unwrapKey().map(k -> k.location().toString()).orElse("?")).append(' ');
				}
				etcodehome.freeterraforged.FTFCommon.LOGGER.info(
						"StrataDiag tag={} resolvedOrder({} total): {}", this.materials.location(), materials.size(), sb);
			}

			int seed = random.nextInt();
			StringBuilder draws = diag ? new StringBuilder() : null;
			for (int i = 0; i < layers; i++) {
				int attempts = this.attempts;
				int index = random.nextInt(materials.size());
				while (--attempts >= 0 && index == lastIndex) {
					index = random.nextInt(materials.size());
				}
				if (index != lastIndex) {
					lastIndex = index;
					BlockState material = materials.get(index).value().defaultBlockState();
					float depth = this.minDepth + random.nextFloat() * (this.maxDepth - this.minDepth);
					result.add(new Layer(material, Noises.shiftSeed(Noises.mul(this.noise.value(), depth), random.nextInt()), seed));
					if (draws != null) {
						draws.append("idx=").append(index).append('(').append(material).append(") ");
					}
				}
			}
			if (diag) {
				etcodehome.freeterraforged.FTFCommon.LOGGER.info(
						"StrataDiag tag={} layerCount={} drawSeed={} draws: {}", this.materials.location(), layers, seed, draws);
			}
			return result;
		}
	}

	public record Layer(BlockState material, Noise depth, int seed) {
		public float computeDepth(float x, float z) {
			return this.depth.compute(x, z, this.seed);
		}
	}

	private class Source implements SurfaceRules.SurfaceRule {
		private Context surfaceContext;
		private Noise selector;
		private List<List<Layer>> strata;
		private List<Layer> layers;
		private float[] depthBuffer;
		private long lastUpdateXZ;

		public Source(Context surfaceContext, Noise selector, List<List<Layer>> strata) {
			this.surfaceContext = surfaceContext;
			this.selector = selector;
			this.strata = strata;
			this.lastUpdateXZ = Long.MIN_VALUE;
		}

		@Nullable
		@Override
		public BlockState tryApply(int x, int y, int z) {
			if(this.lastUpdateXZ != this.surfaceContext.lastUpdateXZ) {
				this.initBuffer(x, z);
				this.lastUpdateXZ = this.surfaceContext.lastUpdateXZ;
			}

			Layer last = null;
			for(int i = 0; i < this.layers.size(); i++) {
				Layer layer = last = this.layers.get(i);
				if(y > this.depthBuffer[i]) {
					return layer.material();
				}
			}

			return last != null ? last.material() : null;
		}

		private void initBuffer(int x, int z) {
			this.layers = this.selectLayers(x, z);
			int layerCount = this.layers.size();

			if (this.depthBuffer == null || this.depthBuffer.length < layerCount) {
				this.depthBuffer = new float[layerCount];
			}

			int localX = this.surfaceContext.blockX & 0xF;
			int localZ = this.surfaceContext.blockZ & 0xF;
			int height = this.surfaceContext.chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, localX, localZ);

			float sum = 0.0F;
			for(int i = 0; i < layerCount; i++) {
				Layer layer = this.layers.get(i);
				float depth = layer.computeDepth(x, z);
				sum += depth;
				this.depthBuffer[i] = depth;
			}

			int y = height;
			for(int i = 0; i < layerCount; i++) {
				this.depthBuffer[i] = y -= Math.round((this.depthBuffer[i] / sum) * height);
			}
		}

		private static final java.util.concurrent.atomic.AtomicInteger DIAG_COUNT = new java.util.concurrent.atomic.AtomicInteger();

		private List<Layer> selectLayers(int x, int z) {
			float selector = this.selector.compute(x, z, 0);
			int index = (int) (selector * this.strata.size());
			index = Math.min(this.strata.size() - 1, index);
			// Diagnostic (backport): log the raw selector value + chosen realization at a handful of fixed
			// coordinates, so Java-17 vs Java-21 runs can be diffed as raw numbers, not visual guesses.
			if ((x == 0 && z == 0) || (x == 512 && z == 512) || (x == -512 && z == 512)) {
				if (DIAG_COUNT.incrementAndGet() <= 12) {
					List<Layer> chosen = this.strata.get(index);
					StringBuilder sb = new StringBuilder();
					for (Layer l : chosen) {
						sb.append(l.material()).append(' ');
					}
					etcodehome.freeterraforged.FTFCommon.LOGGER.info(
							"StrataDiag pos=({},{}) rawSelectorFloat={} strataListSize={} chosenIndex={} layers: {}",
							x, z, Float.toHexString(selector), this.strata.size(), index, sb);
				}
			}
			return this.strata.get(index);
		}
	}
}