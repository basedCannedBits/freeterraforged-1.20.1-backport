package etcodehome.freeterraforged.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

public final class FTFNetworkUtil {
	private FTFNetworkUtil() {
	}

	@ExpectPlatform
	public static void sendFlowFieldSync(ServerPlayer player, ChunkPos pos, byte[] rawGrid) {
		throw new AssertionError();
	}
}
