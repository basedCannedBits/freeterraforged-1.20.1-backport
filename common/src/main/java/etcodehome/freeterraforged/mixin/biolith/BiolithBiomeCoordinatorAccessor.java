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
	// Must be a *static* interface method here (unlike every other @Accessor in this project): a normal
	// (non-static) interface method compiles its call site to invokeinterface, which the JVM null-checks
	// unconditionally before dispatch -- regardless of whether the generated implementation ever uses the
	// receiver. That's exactly what broke the previous version (NPE on a deliberately-null receiver).
	// A static interface method compiles to invokestatic instead: no receiver, no null check possible.
	// Java requires a body on any interface static method syntactically; Mixin's weaver discards this
	// placeholder entirely and substitutes the real static field-set at class-load time.
	@Accessor("registryManager")
	static void freeterraforged$setRegistryManagerStatic(RegistryAccess.Frozen value) {
		throw new AssertionError("Mixin accessor was not applied");
	}
}
