package dev.sypex08.visorholster.core.common.network;

import net.minecraft.network.FriendlyByteBuf;
import org.vmstudio.visor.api.common.network.VisorPayloadToServer;

public record HolsterInteractionPayload(boolean mainHand)
        implements VisorPayloadToServer {

    public static final byte PAYLOAD_ID = 1;

    @Override
    public void onWrite(FriendlyByteBuf buffer) {
        buffer.writeBoolean(mainHand);
    }

    @Override
    public byte payloadId() {
        return PAYLOAD_ID;
    }

    public static HolsterInteractionPayload read(
            FriendlyByteBuf buffer
    ) {
        return new HolsterInteractionPayload(
                buffer.readBoolean()
        );
    }
}