package dev.sypex08.visorholster.fabric.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record HolsterInteractionPayload(boolean mainHand) implements CustomPacketPayload {


    public static final Type<HolsterInteractionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("visor_holster", "holster_interaction"));

    public static final StreamCodec<ByteBuf, HolsterInteractionPayload> CODEC =
            ByteBufCodecs.BOOL.map(HolsterInteractionPayload::new,HolsterInteractionPayload::mainHand);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
