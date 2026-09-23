package raccoonman.reterraforged.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import raccoonman.reterraforged.world.worldgen.feature.placement.SurfaceFeatureRescue;

// 1.20.1 backport: FTF used MixinExtras @WrapMethod (try/finally around the original).
// Plain HEAD/RETURN injections give the same begin/finish pairing on normal returns.
@Mixin(PlacedFeature.class)
class MixinPlacedFeature {

	@Inject(method = "placeWithContext", at = @At("HEAD"))
	private void reterraforged$beginSurfaceFeature(PlacementContext context, RandomSource random, BlockPos origin, CallbackInfoReturnable<Boolean> cir) {
		SurfaceFeatureRescue.begin((PlacedFeature)(Object)this, context);
	}

	@Inject(method = "placeWithContext", at = @At("RETURN"))
	private void reterraforged$finishSurfaceFeature(PlacementContext context, RandomSource random, BlockPos origin, CallbackInfoReturnable<Boolean> cir) {
		SurfaceFeatureRescue.finish();
	}
}
