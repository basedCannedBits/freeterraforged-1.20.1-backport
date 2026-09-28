package raccoonman.reterraforged.world.worldgen;

// not RTF's code. this whole raccoonman.reterraforged.world.worldgen package is a compat shim so
// Streams Reflowing's RTF-detection reflection (Class.forName("raccoonman.reterraforged...")) finds
// something here and pulls real terrain/river data from our generator instead of falling back to
// slow generic height sampling. see MixinRandomState / MixinChunkAccess for where this gets wired in.
public interface RTFRandomState {
    GeneratorContext generatorContext();
}
