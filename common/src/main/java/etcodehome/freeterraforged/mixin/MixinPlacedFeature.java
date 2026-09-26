package etcodehome.freeterraforged.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import etcodehome.freeterraforged.world.worldgen.feature.placement.SurfaceFeatureRescue;

// real ftf uses @WrapMethod for this, plain HEAD/RETURN does the same job here
@Mixin(PlacedFeature.class)
class MixinPlacedFeature {

	@Inject(method = "placeWithContext", at = @At("HEAD"))
	private void freeterraforged$beginSurfaceFeature(PlacementContext context, RandomSource random, BlockPos origin, CallbackInfoReturnable<Boolean> cir) {
		SurfaceFeatureRescue.begin((PlacedFeature)(Object)this, context);
	}

	@Inject(method = "placeWithContext", at = @At("RETURN"))
	private void freeterraforged$finishSurfaceFeature(PlacementContext context, RandomSource random, BlockPos origin, CallbackInfoReturnable<Boolean> cir) {
		SurfaceFeatureRescue.finish();
	}
}
