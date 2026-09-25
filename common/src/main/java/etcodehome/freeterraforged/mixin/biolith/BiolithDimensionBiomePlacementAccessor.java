package etcodehome.freeterraforged.mixin.biolith;

import com.terraformersmc.biolith.impl.noise.OpenSimplexNoise2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

// Targets Biolith 1.0.1-beta.1's DimensionBiomePlacement (the base class OverworldBiomePlacement
// extends), where replacementNoise/seedlets are actually declared. @Pseudo since Biolith may not be
// installed at all -- this mixin is only applied when BiolithCompat.isEnabled() says it should be
// (see MixinPlugin), so it's safe even without the target class present.
@Pseudo
@Mixin(targets = "com.terraformersmc.biolith.impl.biome.DimensionBiomePlacement", remap = false)
public interface BiolithDimensionBiomePlacementAccessor {
	@Accessor("replacementNoise")
	OpenSimplexNoise2 freeterraforged$getReplacementNoise();

	@Accessor("seedlets")
	int[] freeterraforged$getSeedlets();
}
