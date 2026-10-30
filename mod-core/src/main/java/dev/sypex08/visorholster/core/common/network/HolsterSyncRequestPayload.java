package dev.sypex08.visorholster.core.common.network;

import net.minecraft.network.FriendlyByteBuf;
import org.vmstudio.visor.api.common.network.VisorPayloadToServer;

public class HolsterSyncRequestPayload implements VisorPayloadToServer {
    public static final byte PAYLOAD_ID = 2;

    @Override
    public void onWrite(FriendlyByteBuf buffer) {
    }

    @Override
    public byte payloadId() {
        return PAYLOAD_ID;
    }

    public static VisorPayloadToServer read(
            FriendlyByteBuf buffer
    ) {
        return new HolsterSyncRequestPayload();
    }
}

