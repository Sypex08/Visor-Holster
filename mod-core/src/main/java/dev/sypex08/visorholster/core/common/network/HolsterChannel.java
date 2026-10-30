package dev.sypex08.visorholster.core.common.network;

import dev.sypex08.visorholster.core.client.render.HolsterPosition;
import dev.sypex08.visorholster.core.common.VisorHolster;
import dev.sypex08.visorholster.core.common.holster.HolsterState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.vmstudio.visor.api.common.addon.VisorAddon;
import org.vmstudio.visor.api.common.network.VisorChannel;
import org.vmstudio.visor.api.common.network.VisorNetwork;
import org.vmstudio.visor.api.common.network.VisorPayloadToServer;
import org.vmstudio.visor.api.compatibility.mcversion.McVersionUtils;

import java.util.function.Consumer;

import static dev.sypex08.visorholster.core.client.render.HolsterPosition.POSITIONS;

public final class HolsterChannel {

    public static final ResourceLocation ID = McVersionUtils.newResourceLoc(VisorHolster.MOD_ID, "channel");

    public static final int NETWORK_VERSION = 3;

    private static VisorChannel INSTANCE;

    private static Consumer<HolsterStatePayload> clientStateHandler = payload -> {};

    private HolsterChannel() {
    }

    public static void createChannel(@NotNull VisorAddon owner) {
        if (INSTANCE != null) {
            return;
        }

        INSTANCE = VisorChannel.builder(owner, ID, NETWORK_VERSION)
                .toServer(
                        HolsterChannel::readToServer,
                        (payload, sender, responseSender) -> {

                            if (payload instanceof HolsterSyncRequestPayload) {
                                if (sender.getServer() == null) {
                                    return;
                                }

                                HolsterState state = VisorHolster.holsterManager.get(sender);

                                responseSender.accept(
                                        HolsterStatePayload.from(state, sender.getServer().registryAccess())
                                );
                                return;
                            }

                            if (!(payload instanceof HolsterInteractionPayload interaction)) {
                                return;
                            }

                            if (VisorHolster.holsterManager == null) {
                                return;
                            }

                            VisorHolster.holsterManager.interact(
                                    sender,
                                    interaction.mainHand(),
                                    interaction.holsterSlot()
                            );

                            HolsterState state =
                                    VisorHolster.holsterManager.get(sender);

                            if (sender.getServer() == null) {
                                return;
                            }

                            responseSender.accept(
                                    HolsterStatePayload.from(
                                            state,
                                            sender.getServer().registryAccess()
                                    )
                            );

                        }
                )
                .toClient(
                        HolsterChannel::readToClient,
                        payload -> clientStateHandler.accept(payload)
                )
                .build();

        VisorNetwork.registerChannel(INSTANCE);
    }

    public static void setClientStateHandler(
            Consumer<HolsterStatePayload> handler
    ) {
        clientStateHandler = handler;
    }

    public static VisorChannel get() {
        if (INSTANCE == null) {
            throw new IllegalStateException(
                    "HolsterChannel was not initialized"
            );
        }

        return INSTANCE;
    }

    private static VisorPayloadToServer readToServer(
            byte payloadId,
            FriendlyByteBuf buffer
    ) {
        return switch (payloadId) {
            case HolsterSyncRequestPayload.PAYLOAD_ID ->
                    HolsterSyncRequestPayload.read(buffer);

            case HolsterInteractionPayload.PAYLOAD_ID ->
                    HolsterInteractionPayload.read(buffer);

            default -> null;
        };
    }

    private static HolsterStatePayload  readToClient(
            byte payloadId,
            FriendlyByteBuf buffer
    ) {
        return switch (payloadId) {
            case HolsterStatePayload.PAYLOAD_ID ->
                    HolsterStatePayload.read(buffer);

            default -> null;
        };
    }
}
