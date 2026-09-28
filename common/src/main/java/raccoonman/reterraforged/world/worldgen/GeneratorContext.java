package raccoonman.reterraforged.world.worldgen;

import raccoonman.reterraforged.world.worldgen.cell.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.cell.heightmap.WorldLookup;

// compat shim, see RTFRandomState.java
public class GeneratorContext {
    public WorldLookup lookup;
    public Levels levels;
}
