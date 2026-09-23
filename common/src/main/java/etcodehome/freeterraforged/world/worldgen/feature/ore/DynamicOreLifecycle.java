package etcodehome.freeterraforged.world.worldgen.feature.ore;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import etcodehome.freeterraforged.FTFCommon;
import etcodehome.freeterraforged.server.FTFMinecraftServer;
import etcodehome.freeterraforged.world.worldgen.FTFRandomState;
import etcodehome.freeterraforged.world.worldgen.feature.ore.DynamicOrePlan.VerticalFrame;

public final class DynamicOreLifecycle {

	private DynamicOreLifecycle() {
	}

	public static void onLevelLoad(ServerLevel level) {
		if (Level.OVERWORLD.equals(level.dimension())) {
			refresh(level);
		}
	}

	public static void onServerStarted(MinecraftServer server) {
		if (server instanceof FTFMinecraftServer owner
				&& owner.getDynamicOrePlan().verticalFrame().isEmpty()) {
			refresh(server);
		}
	}

	public static void refresh(MinecraftServer server) {
		if (!(server instanceof FTFMinecraftServer owner)) {
			return;
		}
		ServerLevel overworld = server.getLevel(Level.OVERWORLD);
		if (overworld == null) {
			owner.publishDynamicOrePlan(DynamicOrePlan.empty());
			return;
		}
		refresh(overworld);
	}

	private static void refresh(ServerLevel overworld) {
		MinecraftServer server = overworld.getServer();
		if (!(server instanceof FTFMinecraftServer owner)) {
			return;
		}
		if (!((Object)overworld.getChunkSource().randomState() instanceof FTFRandomState randomState)
				|| randomState.generatorContext() == null) {
			owner.publishDynamicOrePlan(DynamicOrePlan.empty());
			return;
		}

		ChunkGenerator generator = overworld.getChunkSource().getGenerator();
		DynamicOrePlan plan = new DynamicOrePlanner().build(
				server.registryAccess(),
				generator,
				generator.getBiomeSource().possibleBiomes(),
				new VerticalFrame(
						overworld.getMinBuildHeight(),
						overworld.getMaxBuildHeight() - 1,
						generator.getSeaLevel()
				)
		);
		owner.publishDynamicOrePlan(plan);
		FTFCommon.LOGGER.info("Dynamic ore contract inventory: {}", plan.summary());
		plan.failures().forEach(failure -> FTFCommon.LOGGER.warn(
				"Dynamic ore contract inspection failure: {}", failure
		));
		logUndergroundOresOrder(server, generator);
	}

	// Diagnostic (backport): dump each biome's UNDERGROUND_ORES feature list, in order, so it can be diffed
	// index-for-index against the real NeoForge FTF build. Feature placement RNG is derived partly from a
	// feature's index within this list, so an ordering mismatch here (not a height-range mismatch) would explain
	// vanilla ore-blob features like granite/andesite/diorite landing in different spots between builds.
	private static void logUndergroundOresOrder(MinecraftServer server, ChunkGenerator generator) {
		Registry<PlacedFeature> placedFeatures = server.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
		Registry<Biome> biomes = server.registryAccess().registryOrThrow(Registries.BIOME);
		int step = GenerationStep.Decoration.UNDERGROUND_ORES.ordinal();
		for (Holder<Biome> biomeHolder : generator.getBiomeSource().possibleBiomes()) {
			ResourceLocation biomeId = biomeHolder.unwrapKey().map(key -> key.location()).orElse(null);
			if (biomeId == null) {
				continue;
			}
			List<List<Holder<PlacedFeature>>> steps = generator.getBiomeGenerationSettings(biomeHolder).features();
			if (step >= steps.size()) {
				continue;
			}
			StringBuilder sb = new StringBuilder();
			int index = 0;
			for (Holder<PlacedFeature> feature : steps.get(step)) {
				ResourceLocation featureId = feature.unwrapKey()
						.map(key -> key.location())
						.orElseGet(() -> placedFeatures.getKey(feature.value()));
				sb.append(index).append('=').append(featureId).append(' ');
				index++;
			}
			FTFCommon.LOGGER.info("Dynamic ore underground_ores order for {}: {}", biomeId, sb);
		}
	}
}