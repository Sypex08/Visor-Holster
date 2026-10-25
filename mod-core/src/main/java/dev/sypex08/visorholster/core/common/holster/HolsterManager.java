package dev.sypex08.visorholster.core.common.holster;

import dev.sypex08.visorholster.core.common.network.HolsterChannel;
import dev.sypex08.visorholster.core.common.network.HolsterStatePayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HolsterManager {

    private final Map<UUID, HolsterState> clientStates = new HashMap<>();

    public HolsterState get(UUID playerId) {
        return clientStates.computeIfAbsent(playerId, id -> new HolsterState());
    }


    public HolsterState get(ServerPlayer player) {
        MinecraftServer server = player.getServer();

        if (server == null) {
            throw new IllegalStateException("Tried to access data without server");
        }

        HolsterSavedData data = HolsterSavedData.get(server);
        return data.get(player.getUUID());
    }

    public void onPlayerJoin(MinecraftServer server, UUID playerId) {
        HolsterSavedData.get(server).get(playerId);
    }

    @SuppressWarnings("resource")
    public void dropOnDeath(ServerPlayer player){
        if (player == null || player.getServer() == null){
            return;
        }

        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)){
            return;
        }

        MinecraftServer server = player.getServer();
        HolsterSavedData data = HolsterSavedData.get(server);
        HolsterState state = data.get(player.getUUID());

        ItemStack stack = state.slot.take();

        if (stack.isEmpty()){
            return;
        }

        player.drop(stack, true);

        HolsterChannel.get().sendToClient(player,
                HolsterStatePayload.from(ItemStack.EMPTY, server.registryAccess(), state.visible)
        );

        data.markChanged();
    }

    public void onPlayerLeave(UUID playerId) {
        clientStates.remove(playerId);
    }

    public void updateClientState(UUID playerId, ItemStack stack, boolean visible) {
        HolsterState state = get(playerId);

        state.slot.setStack(stack);
        state.visible = visible;
    }

    @SuppressWarnings("resource")
    public void interact(ServerPlayer player, boolean mainHand) {
        if (player == null || player.level().isClientSide()) {
            return;
        }

        MinecraftServer server = player.getServer();

        if (server == null) {
            return;
        }

        HolsterSavedData data = HolsterSavedData.get(server);
        HolsterState state = data.get(player.getUUID());

        InteractionHand hand = mainHand
                ? InteractionHand.MAIN_HAND
                : InteractionHand.OFF_HAND;

        ItemStack heldStack = player.getItemInHand(hand);

        if (state.slot.isEmpty()) {
            if (heldStack.isEmpty()) {
                return;
            }

            ItemStack stackToStore = heldStack.copy();

            if (state.slot.insert(stackToStore)) {
                player.setItemInHand(hand, ItemStack.EMPTY);

                data.markChanged();
            }

            return;
        }

        if (heldStack.isEmpty()) {
            ItemStack stackFromHolster = state.slot.take();

            if (!stackFromHolster.isEmpty()) {
                player.setItemInHand(hand, stackFromHolster);

                data.markChanged();
            }
        }
    }
}