package etcodehome.freeterraforged.client.network;

import etcodehome.freeterraforged.network.FTFFlowSyncPacket;
import etcodehome.freeterraforged.world.worldgen.IFlowFieldHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;

public class FTFClientPayloadHandler {

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
