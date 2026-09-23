package raccoonman.reterraforged.fabric.network;

import raccoonman.reterraforged.client.network.RTFClientPayloadHandler;
import raccoonman.reterraforged.network.FlowFieldSyncPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class RTFFabricClientNetworking {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(FlowFieldSyncPayload.TYPE, (payload, context) ->
                context.client().execute(() ->
                        RTFClientPayloadHandler.handleFlowFieldSync(payload, context.player())
                )
        );
    }
}