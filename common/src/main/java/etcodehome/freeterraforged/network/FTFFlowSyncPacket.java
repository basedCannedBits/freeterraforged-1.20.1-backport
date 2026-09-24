package etcodehome.freeterraforged.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;

// Platform-agnostic packet payload: a chunk's river flow-field grid, sent server -> client so boats
// (and the debug overlay) can see current direction/strength. Wiring this onto an actual transport
// (SimpleChannel on Forge) lives in the forge module; this class only knows how to read/write itself.
public record FTFFlowSyncPacket(ChunkPos pos, byte[] rawGrid) {

	public void write(FriendlyByteBuf buf) {
		buf.writeLong(this.pos.toLong());
		buf.writeByteArray(this.rawGrid);
	}

	public static FTFFlowSyncPacket read(FriendlyByteBuf buf) {
		return new FTFFlowSyncPacket(new ChunkPos(buf.readLong()), buf.readByteArray(256));
	}
}
