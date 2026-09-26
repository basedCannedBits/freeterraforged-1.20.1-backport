package etcodehome.freeterraforged.mixin.biolith;

import com.terraformersmc.biolith.impl.biome.OverworldBiomePlacement;
import com.terraformersmc.biolith.impl.noise.OpenSimplexNoise2;
import etcodehome.freeterraforged.compat.biolith.BiolithPreviewContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// swaps out the noise biolith's getLocalNoise reads from, so its own selection logic just works on
// preview-seeded noise instead of the live world's without us touching that logic at all
@Pseudo
@Mixin(targets = "com.terraformersmc.biolith.impl.biome.OverworldBiomePlacement", remap = false)
public abstract class MixinBiolithOverworldBiomePlacement {
	// owner has to be OverworldBiomePlacement here, not DimensionBiomePlacement where the fields
	// are actually declared. found that out the hard way from a failed injection in-game.
	@Redirect(
		method = "getLocalNoise",
		at = @At(
			value = "FIELD",
			target = "Lcom/terraformersmc/biolith/impl/biome/OverworldBiomePlacement;replacementNoise:Lcom/terraformersmc/biolith/impl/noise/OpenSimplexNoise2;"
		),
		remap = false
	)
	private OpenSimplexNoise2 freeterraforged$previewReplacementNoise(OverworldBiomePlacement placement) {
		OpenSimplexNoise2 original = ((BiolithDimensionBiomePlacementAccessor) placement)
			.freeterraforged$getReplacementNoise();
		return BiolithPreviewContext.replacementNoise(original);
	}

	@Redirect(
		method = "getLocalNoise",
		at = @At(
			value = "FIELD",
			target = "Lcom/terraformersmc/biolith/impl/biome/OverworldBiomePlacement;seedlets:[I"
		),
		remap = false
	)
	private int[] freeterraforged$previewSeedlets(OverworldBiomePlacement placement) {
		int[] original = ((BiolithDimensionBiomePlacementAccessor) placement).freeterraforged$getSeedlets();
		return BiolithPreviewContext.seedlets(original);
	}
}
