package etcodehome.freeterraforged.mixin.biolith;

import com.terraformersmc.biolith.impl.biome.OverworldBiomePlacement;
import com.terraformersmc.biolith.impl.noise.OpenSimplexNoise2;
import etcodehome.freeterraforged.compat.biolith.BiolithPreviewContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// getLocalNoise (confirmed present, signature (III)D) is what both selectReplacement and selectSubBiome
// ultimately sample from. Redirecting just its two field reads means Biolith's own, real selection logic
// runs unmodified -- it just samples from a preview-seeded noise field instead of the live world's.
@Pseudo
@Mixin(targets = "com.terraformersmc.biolith.impl.biome.OverworldBiomePlacement", remap = false)
public abstract class MixinBiolithOverworldBiomePlacement {
	// Fix (backport): confirmed via a real in-game injection failure that these fields resolve with
	// OverworldBiomePlacement as the bytecode owner, not the declaring class DimensionBiomePlacement
	// as originally guessed. Mixin's field @At target must match the exact owner in the GETFIELD
	// instruction, which javac apparently emits as the accessing (sub)class here, not the declaring one.
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
