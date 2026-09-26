package etcodehome.freeterraforged.mixin.biolith;

import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

// lets us set biolith's registryManager field ourselves, since the preview never triggers the
// normal server-start code path that would set it
@Pseudo
@Mixin(targets = "com.terraformersmc.biolith.impl.biome.BiomeCoordinator", remap = false)
public interface BiolithBiomeCoordinatorAccessor {
	// has to be a static interface method or the null-receiver call below throws an NPE
	@Accessor("registryManager")
	static void freeterraforged$setRegistryManagerStatic(RegistryAccess.Frozen value) {
		throw new AssertionError("Mixin accessor was not applied");
	}
}
