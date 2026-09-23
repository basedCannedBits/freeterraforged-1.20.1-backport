package etcodehome.freeterraforged.world.worldgen.biome.modifier.forge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo;
import net.minecraftforge.server.ServerLifecycleHooks;
import etcodehome.freeterraforged.forge.mixin.MixinBiomeGenerationSettingsPlainsBuilder;

// FTF logic (replacements stored as keys, resolved against the live registry) on Forge 1.20.1 APIs
public record ReplaceModifier(GenerationStep.Decoration step, Optional<HolderSet<Biome>> biomes, Map<ResourceKey<PlacedFeature>, ResourceKey<PlacedFeature>> replacements) implements ForgeBiomeModifier {
	public static final Codec<ReplaceModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(ReplaceModifier::step),
		Biome.LIST_CODEC.optionalFieldOf("biomes").forGetter(ReplaceModifier::biomes),
		Codec.unboundedMap(ResourceKey.codec(Registries.PLACED_FEATURE), ResourceKey.codec(Registries.PLACED_FEATURE)).fieldOf("replacements").forGetter(ReplaceModifier::replacements)
	).apply(instance, ReplaceModifier::new));

	@Override
	public void modify(Holder<Biome> biome, Phase phase, BiomeInfo.Builder builder) {
		if (phase == Phase.AFTER_EVERYTHING) {
			if (builder.getGenerationSettings() instanceof MixinBiomeGenerationSettingsPlainsBuilder builderAccessor) {
				if (this.biomes.isPresent() && !this.biomes.get().contains(biome)) {
					return;
				}

				MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
				HolderLookup.RegistryLookup<PlacedFeature> featureRegistry = server.registryAccess().lookupOrThrow(Registries.PLACED_FEATURE);

				List<List<Holder<PlacedFeature>>> featureSteps = builderAccessor.getFeatures();
				int index = this.step.ordinal();

				while (index >= featureSteps.size()) {
					featureSteps.add(new ArrayList<>());
				}

				List<Holder<PlacedFeature>> replaced = new ArrayList<>(featureSteps.get(index));
				replaced.replaceAll((f) -> f.unwrapKey().map((key) -> {
					ResourceKey<PlacedFeature> replacementKey = this.replacements.get(key);
					if (replacementKey == null) {
						return f;
					}
					return (Holder<PlacedFeature>) featureRegistry.get(replacementKey)
						.orElseThrow(() -> new IllegalStateException("Missing feature: " + replacementKey.location()));
				}).orElse(f));
				featureSteps.set(index, replaced);
			}
		}
	}

	@Override
	public Codec<ReplaceModifier> codec() {
		return CODEC;
	}
}
