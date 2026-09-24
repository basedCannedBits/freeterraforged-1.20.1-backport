package etcodehome.freeterraforged.platform.forge;

import etcodehome.freeterraforged.network.FTFForgeNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public final class FTFNetworkUtilImpl {
	private FTFNetworkUtilImpl() {
	}

	public static void sendFlowFieldSync(ServerPlayer player, ChunkPos pos, byte[] rawGrid) {
		FTFForgeNetwork.sendFlowFieldSync(player, pos, rawGrid);
	}
}
