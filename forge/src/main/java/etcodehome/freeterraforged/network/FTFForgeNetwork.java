package etcodehome.freeterraforged.network;

import java.util.function.Supplier;

import etcodehome.freeterraforged.FTFCommon;
import etcodehome.freeterraforged.client.network.FTFClientPayloadHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

// forge side of sending FTFFlowSyncPacket, packet itself lives in common
public final class FTFForgeNetwork {
	private FTFForgeNetwork() {
	}

	private static final String PROTOCOL_VERSION = "1";

	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(FTFCommon.MOD_ID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);

	public static void register() {
		int id = 0;
		CHANNEL.registerMessage(
				id,
				etcodehome.freeterraforged.network.FTFFlowSyncPacket.class,
				etcodehome.freeterraforged.network.FTFFlowSyncPacket::write,
				etcodehome.freeterraforged.network.FTFFlowSyncPacket::read,
				FTFForgeNetwork::handle
		);
	}

	private static void handle(etcodehome.freeterraforged.network.FTFFlowSyncPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
				() -> () -> FTFClientPayloadHandler.handleFlowFieldSync(packet)));
		ctx.setPacketHandled(true);
	}

	public static void sendFlowFieldSync(ServerPlayer player, ChunkPos pos, byte[] rawGrid) {
		CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new etcodehome.freeterraforged.network.FTFFlowSyncPacket(pos, rawGrid));
	}
}
