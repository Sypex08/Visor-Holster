package dev.sypex08.visorholster.core.common.network;

import net.minecraft.network.FriendlyByteBuf;
import org.vmstudio.visor.api.common.network.VisorPayloadToServer;

public record HolsterInteractionPayload(boolean mainHand, int holsterSlot)
        implements VisorPayloadToServer {

    public static final byte PAYLOAD_ID = 1;

    @Override
    public void onWrite(FriendlyByteBuf buffer) {

        buffer.writeBoolean(mainHand);
        buffer.writeVarInt(holsterSlot);
    }

    @Override
    public byte payloadId() {
        return PAYLOAD_ID;
    }

    public static HolsterInteractionPayload read(
            FriendlyByteBuf buffer
    ) {
        boolean mainHand = buffer.readBoolean();
        int holsterSlot = buffer.readVarInt();

        return new HolsterInteractionPayload(
                mainHand,
                holsterSlot
        );
    }
}