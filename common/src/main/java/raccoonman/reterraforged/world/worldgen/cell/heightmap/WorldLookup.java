package raccoonman.reterraforged.world.worldgen.cell.heightmap;

import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.terrain.TerrainType;

// compat shim, see RTFRandomState.java. wraps our real WorldLookup and copies the fields SR's
// bridge cares about (height, terrain, riverWaterLevel) into the shim Cell it hands us.
public class WorldLookup {
    private final etcodehome.freeterraforged.world.worldgen.cell.heightmap.WorldLookup real;

    public WorldLookup(etcodehome.freeterraforged.world.worldgen.cell.heightmap.WorldLookup real) {
        this.real = real;
    }

    public boolean applyCell(Cell cell, int x, int z, boolean applyClimate) {
        etcodehome.freeterraforged.world.worldgen.cell.Cell realCell = new etcodehome.freeterraforged.world.worldgen.cell.Cell();
        boolean found = this.real.applyCell(realCell, x, z, applyClimate);

        cell.height = realCell.height;
        cell.riverWaterLevel = realCell.riverWaterLevel;
        cell.terrain = realCell.terrain == etcodehome.freeterraforged.world.worldgen.cell.terrain.TerrainType.RIVER
                ? TerrainType.RIVER
                : null;

        return found;
    }
}
