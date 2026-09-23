package raccoonman.reterraforged.data.worldgen.compat.terrablender;

import raccoonman.reterraforged.world.worldgen.densityfunction.CellSampler;
import raccoonman.reterraforged.world.worldgen.densityfunction.RTFDensityFunctions;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;

public class TBNoiseRouterData {
	public static final ResourceKey<DensityFunction> UNIQUENESS =ResourceKey.create(Registries.DENSITY_FUNCTION, ResourceLocation.fromNamespaceAndPath("terrablender","uniqueness"));
	
	public static void bootstrap(BootstrapContext<DensityFunction> ctx) {
		ctx.register(UNIQUENESS, RTFDensityFunctions.cell(CellSampler.Field.BIOME_REGION));
	}
}
