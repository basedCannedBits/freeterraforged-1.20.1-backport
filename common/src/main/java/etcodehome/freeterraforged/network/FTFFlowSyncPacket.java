package etcodehome.freeterraforged.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;

// a chunk's river flow data, sent server -> client so boats get pushed by current. actual
// networking (SimpleChannel) lives in the forge module, this is just the packet itself
public record FTFFlowSyncPacket(ChunkPos pos, byte[] rawGrid) {

	public void write(FriendlyByteBuf buf) {
		buf.writeLong(this.pos.toLong());
		buf.writeByteArray(this.rawGrid);
	}

	public static FTFFlowSyncPacket read(FriendlyByteBuf buf) {
		return new FTFFlowSyncPacket(new ChunkPos(buf.readLong()), buf.readByteArray(256));
	}
}
