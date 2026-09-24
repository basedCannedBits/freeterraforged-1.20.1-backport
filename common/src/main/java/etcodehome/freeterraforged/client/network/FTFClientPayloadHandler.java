package etcodehome.freeterraforged.client.network;

import etcodehome.freeterraforged.network.FTFFlowSyncPacket;
import etcodehome.freeterraforged.world.worldgen.IFlowFieldHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;

public class FTFClientPayloadHandler {

	// Always applies to the client's own view of the world -- there's no other "player" a client-received
	// packet could be about -- so this doesn't need a Player parameter the way the original (1.21.1) version did.
	public static void handleFlowFieldSync(FTFFlowSyncPacket payload) {
		ClientLevel clientLevel = Minecraft.getInstance().level;
		if (clientLevel != null) {
			ChunkAccess chunk = clientLevel.getChunk(payload.pos().x, payload.pos().z, ChunkStatus.FULL, false);
			if (chunk instanceof IFlowFieldHolder holder) {
				holder.freeterraforged$getFlowField().loadRawGrid(payload.rawGrid());
			}
		}
	}
}
