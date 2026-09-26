package etcodehome.freeterraforged.mixin.biolith;

import com.terraformersmc.biolith.impl.noise.OpenSimplexNoise2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

// grabs biolith's replacementNoise/seedlets fields off DimensionBiomePlacement so we can read/swap them
@Pseudo
@Mixin(targets = "com.terraformersmc.biolith.impl.biome.DimensionBiomePlacement", remap = false)
public interface BiolithDimensionBiomePlacementAccessor {
	@Accessor("replacementNoise")
	OpenSimplexNoise2 freeterraforged$getReplacementNoise();

	@Accessor("seedlets")
	int[] freeterraforged$getSeedlets();
}
