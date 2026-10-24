package dev.sypex08.visorholster.fabric.network;

import dev.sypex08.visorholster.core.common.VisorHolster;
import dev.sypex08.visorholster.core.common.holster.HolsterManager;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class HolsterNetworking {

    public static void register(){
        PayloadTypeRegistry.playC2S().register(HolsterInteractionPayload.TYPE, HolsterInteractionPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(
                HolsterInteractionPayload.TYPE,
                ((payload, context) -> {
                    context.server().execute(() ->{
                        VisorHolster.holsterManager.interact(context.player(), payload.mainHand());
                    });
                })
        );
    }
}
