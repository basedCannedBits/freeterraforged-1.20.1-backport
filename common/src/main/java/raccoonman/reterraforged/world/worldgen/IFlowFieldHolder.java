package raccoonman.reterraforged.world.worldgen;

// compat shim, see RTFRandomState.java. method name has to be exactly this (SR reflects it literally),
// which happens to already match RTF's own naming convention that FTF kept on its real interface too.
public interface IFlowFieldHolder {
    ChunkFlowField reterraforged$getFlowField();
}
