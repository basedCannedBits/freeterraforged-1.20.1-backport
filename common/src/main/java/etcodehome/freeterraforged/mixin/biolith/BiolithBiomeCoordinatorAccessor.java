package etcodehome.freeterraforged.mixin.biolith;

import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

// Static accessor for BiomeCoordinator.registryManager (RegistryAccess.Frozen). Normally only set once
// during real server startup (handleServerStarting), which FTF's preset preview never runs -- it builds
// its own lightweight simulation instead. Without this set, Biolith's own mixin into MultiNoiseBiomeSource
// (writeBiomeEntries -> getBiomeLookupOrThrow) throws NoSuchElementException the moment a preview tries
// to sample a biome. This lets the preview populate it directly with the registries it already has.
//
// Called as ((BiolithBiomeCoordinatorAccessor) (Object) null).freeterraforged$setRegistryManager(...) --
// this looks unusual, but is the standard Mixin idiom for a pure static accessor: the generated
// implementation never dereferences the receiver, since the target field is static.
@Pseudo
@Mixin(targets = "com.terraformersmc.biolith.impl.biome.BiomeCoordinator", remap = false)
public interface BiolithBiomeCoordinatorAccessor {
	// Not declared static: Mixin infers static-ness from the *target field*, not from this method's own
	// modifiers -- a plain abstract interface method is the correct, standard shape (matches every other
	// @Accessor in this project). The generated implementation ignores whatever receiver it's called on.
	@Accessor("registryManager")
	void freeterraforged$setRegistryManagerStatic(RegistryAccess.Frozen value);
}
