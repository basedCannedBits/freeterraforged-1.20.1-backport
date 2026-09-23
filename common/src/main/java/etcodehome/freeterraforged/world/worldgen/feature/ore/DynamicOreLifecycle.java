package etcodehome.freeterraforged.world.worldgen.feature.ore;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
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
import etcodehome.freeterraforged.world.worldgen.biome.FTFMultiNoiseBiomeSource;
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
		logBiomeSourceIdentity(generator);
	}

	// Diagnostic (backport): the actual thing that determines FeatureSorter's world-wide feature
	// ordering (and thus the decoration seed for every un-remapped vanilla feature, granite included)
	// is generator.getBiomeSource().possibleBiomes() -- its exact iteration order. This logs what that
	// biome source actually is at runtime: an inline (Either.left) list built by our own code, or a
	// registered preset (Either.right) such as vanilla's own untouched minecraft:overworld -- plus the
	// exact resulting order, so it can be compared directly against what real FTF does.
	private static void logBiomeSourceIdentity(ChunkGenerator generator) {
		if (!(generator.getBiomeSource() instanceof FTFMultiNoiseBiomeSource biomeSource)) {
			FTFCommon.LOGGER.info("Biome source is not a FTFMultiNoiseBiomeSource: {}", generator.getBiomeSource().getClass().getName());
			return;
		}
		var presetKey = biomeSource.freeterraforged$getParameterListPresetKey();
		FTFCommon.LOGGER.info("Biome source parameters: {}", presetKey.isPresent()
				? "Either.right, preset=" + presetKey.get().location()
				: "Either.left (inline list)");

		StringBuilder sb = new StringBuilder();
		int index = 0;
		for (Holder<Biome> holder : generator.getBiomeSource().possibleBiomes()) {
			ResourceLocation id = holder.unwrapKey().map(key -> key.location()).orElse(null);
			sb.append(index).append('=').append(id).append(' ');
			index++;
		}
		FTFCommon.LOGGER.info("Biome source possibleBiomes() order ({} total): {}", index, sb);
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
			List<HolderSet<PlacedFeature>> steps = generator.getBiomeGenerationSettings(biomeHolder).features();
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