package etcodehome.freeterraforged.world.worldgen.biome;

import java.util.Optional;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

public interface FTFMultiNoiseBiomeSource {
    Climate.ParameterList<Holder<Biome>> freeterraforged$getParameters();

    // Diagnostic: empty = inline Either.left (Codec-embedded list, built wherever the caller assembled it).
    // Present = Either.right, referencing a registered MultiNoiseBiomeSourceParameterList by this key
    // (e.g. minecraft:overworld = vanilla's own untouched preset).
    Optional<ResourceKey<MultiNoiseBiomeSourceParameterList>> freeterraforged$getParameterListPresetKey();
}
