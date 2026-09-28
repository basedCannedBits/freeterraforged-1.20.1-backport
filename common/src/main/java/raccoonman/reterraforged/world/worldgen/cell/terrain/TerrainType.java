package raccoonman.reterraforged.world.worldgen.cell.terrain;

// compat shim, see RTFRandomState.java. SR just grabs the RIVER constant once and does a reference
// equality check against whatever's in Cell.terrain later, so this only needs to be a stable sentinel.
public class TerrainType {
    public static final Object RIVER = new Object();
}
