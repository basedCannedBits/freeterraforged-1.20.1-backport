package raccoonman.reterraforged.fabric.network;

import raccoonman.reterraforged.network.FlowFieldSyncPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class RTFFabricNetworking {

    public static void init() {
        PayloadTypeRegistry.playS2C().register(FlowFieldSyncPayload.TYPE, FlowFieldSyncPayload.CODEC);
    }
}