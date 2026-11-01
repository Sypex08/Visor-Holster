package dev.sypex08.visorholster.core.common.holster;

import dev.sypex08.visorholster.core.common.network.HolsterChannel;
import dev.sypex08.visorholster.core.common.network.HolsterStatePayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
//? if >=26.3 {
/*import net.minecraft.util.Prediction;
*///?}
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
//? if >=1.21.11 {
/*import net.minecraft.world.level.gamerules.GameRules;
*///?} else {
import net.minecraft.world.level.GameRules;
//?}

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static dev.sypex08.visorholster.core.common.holster.HolsterState.SLOT_COUNT;

public class HolsterManager {

    private final Map<UUID, HolsterState> clientStates = new HashMap<>();

    public HolsterState get(UUID playerId) {
        return clientStates.computeIfAbsent(playerId, id -> new HolsterState());
    }


    public HolsterState get(ServerPlayer player) {
        //? if >= 1.21.10{

        /*MinecraftServer server = player.level().getServer();

        *///?} else{
        
        MinecraftServer server = player.getServer();

         //?}


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
    public void dropOnDeath(ServerPlayer player) {

        //? if >= 1.21.10{

        /*MinecraftServer server = player.level().getServer();

        if (player == null || server == null) {
            return;
        }

        *///?} else{
        

        MinecraftServer server =  player.getServer();

        if (player == null || server == null) {
            return;
        }

         //?}

        boolean keepInventory;

        //? if >=1.21.11 {
        /*keepInventory = player.level().getGameRules().get(GameRules.KEEP_INVENTORY);
        *///?} elif >=1.21.6 {
        /*keepInventory = player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        *///?} else {
        keepInventory = player.serverLevel().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY);
        //?}

        if (keepInventory) {
            return;
        }


        HolsterSavedData data = HolsterSavedData.get(server);
        HolsterState state = data.get(player.getUUID());


        for (int slot = 0; slot < SLOT_COUNT; slot++) {

            ItemStack stack = state.slots[slot].take();

            if (stack.isEmpty()){
                continue;
            }

            //? if >=26.3 {
            /*player.drop(stack, true, Prediction.PREDICTED);
            *///?} else {
            player.drop(stack, true);
            //?}

        }

        HolsterChannel.get().sendToClient(player,
                HolsterStatePayload.from(state, server.registryAccess())
        );

        data.markChanged();
    }

    public void onPlayerLeave(UUID playerId) {
        clientStates.remove(playerId);
    }

    public void updateClientState(UUID playerId, ItemStack stack, boolean visible, int holsterSlot) {

        if (holsterSlot < 0
                || holsterSlot >= SLOT_COUNT) {
            return;
        }

        HolsterState state = get(playerId);

        state.slots[holsterSlot].setStack(stack);
        state.visible = visible;
    }

    @SuppressWarnings("resource")
    public void interact(ServerPlayer player, boolean mainHand, int holsterSlot) {

        if (holsterSlot < 0
                || holsterSlot >= SLOT_COUNT) {
            return;
        }

        if (player == null || player.level().isClientSide()) {
            return;
        }

        //? if >= 1.21.10{

        /*MinecraftServer server = player.level().getServer();

        *///?} else{
        
        MinecraftServer server = player.getServer();

         //?}

        if (server == null) {
            return;
        }

        HolsterSavedData data = HolsterSavedData.get(server);
        HolsterState state = data.get(player.getUUID());

        InteractionHand hand = mainHand
                ? InteractionHand.MAIN_HAND
                : InteractionHand.OFF_HAND;

        ItemStack heldStack = player.getItemInHand(hand);

        if (state.slots[holsterSlot].isEmpty()) {
            if (heldStack.isEmpty()) {
                return;
            }

            ItemStack stackToStore = heldStack.copy();

            if (state.slots[holsterSlot].insert(stackToStore)) {
                player.setItemInHand(hand, ItemStack.EMPTY);

                data.markChanged();
            }

            return;
        }

        if (heldStack.isEmpty()) {
            ItemStack stackFromHolster = state.slots[holsterSlot].take();

            if (!stackFromHolster.isEmpty()) {
                player.setItemInHand(hand, stackFromHolster);

                data.markChanged();

                player.level().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.HORSE_SADDLE,
                        SoundSource.PLAYERS,
                        0.6F,
                        1.8F
                );

            }
        }
    }
}
