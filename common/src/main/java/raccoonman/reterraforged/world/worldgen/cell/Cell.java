package raccoonman.reterraforged.world.worldgen.cell;

// compat shim, see RTFRandomState.java. only the 3 fields SR's bridge reflects on: height, terrain, riverWaterLevel.
// needs a public no-arg ctor (SR instantiates this via reflection too) - the implicit default one covers that.
public class Cell {
    public float height;
    public Object terrain;
    public float riverWaterLevel;
}
