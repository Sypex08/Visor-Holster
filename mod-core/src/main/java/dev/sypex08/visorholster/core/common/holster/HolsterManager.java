package dev.sypex08.visorholster.core.common.holster;

import dev.sypex08.visorholster.core.common.VisorHolster;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.vmstudio.visor.api.VisorAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HolsterManager {

    private final Map<UUID, HolsterState> states = new HashMap<>();

    public HolsterState get(UUID playerId) {
        return states.computeIfAbsent(
                playerId,
                id -> new HolsterState()
        );
    }

    public HolsterState get(ServerPlayer player) {
        return get(player.getUUID());
    }

    public void onPlayerJoin(UUID playerId) {
        states.putIfAbsent(playerId, new HolsterState());
    }

    public void onPlayerLeave(UUID playerId) {
        states.remove(playerId);
    }

    public void interact(ServerPlayer player, boolean mainHand) {
        if (player == null || player.level().isClientSide()) {
            return;
        }

        InteractionHand hand = mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;

        HolsterState state = get(player);
        ItemStack heldStack = player.getItemInHand(hand);

        if (state.slot.isEmpty()) {
            if (heldStack.isEmpty()) {
                return;
            }

            ItemStack stackToStore = heldStack.copy();
            stackToStore.setCount(heldStack.getCount());

            if (state.slot.insert(stackToStore)) {
                heldStack.shrink(heldStack.getCount());
                player.setItemInHand(hand, heldStack);
            }

            return;
        }

        if (heldStack.isEmpty()) {
            player.setItemInHand(hand, state.slot.take());
        }
    }
}