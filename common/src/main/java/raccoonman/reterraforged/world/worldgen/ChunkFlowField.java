package raccoonman.reterraforged.world.worldgen;

// compat shim, see RTFRandomState.java. thin wrapper around our real per-chunk flow grid.
public class ChunkFlowField {
    private final etcodehome.freeterraforged.world.worldgen.ChunkFlowField real;

    public ChunkFlowField(etcodehome.freeterraforged.world.worldgen.ChunkFlowField real) {
        this.real = real;
    }

    public boolean hasRivers() {
        return this.real.hasRivers();
    }

    public byte[] getRawGrid() {
        return this.real.getRawGrid();
    }
}
