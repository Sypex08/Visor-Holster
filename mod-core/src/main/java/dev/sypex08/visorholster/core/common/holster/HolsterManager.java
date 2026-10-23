package dev.sypex08.visorholster.core.common.holster;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HolsterManager {

    private final Map<UUID, HolsterState> states = new HashMap<>();

    public HolsterState get(UUID id) { return states.computeIfAbsent(id, k -> new HolsterState());}
    public void onPlayerJoin(UUID id, HolsterState state){ states.put(id, state);}
    public void onPlayerLeave(UUID id) { states.remove(id);}

    //server side

    public void interact(ServerPlayer player, InteractionHand hand){

        HolsterState state = get(player.getUUID());
        HolsterSlot slot = state.slot;

        ItemStack heldStack = player.getItemInHand(hand);

        if(slot.isEmpty() && !heldStack.isEmpty()){
            slot.insert(heldStack);
        } else if (!slot.isEmpty() && heldStack.isEmpty()) {
            player.setItemInHand(hand, slot.take());

        }


    }





}
