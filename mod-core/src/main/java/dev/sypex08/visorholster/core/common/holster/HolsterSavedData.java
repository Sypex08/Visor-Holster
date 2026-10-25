package dev.sypex08.visorholster.core.common.holster;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HolsterSavedData extends SavedData {

    private static final String DATA_ID = "visor_holster_data";
    private static final String PLAYERS = "players";

    private final Map<UUID, HolsterState> states = new HashMap<>();

    public static HolsterSavedData create(){
        return new HolsterSavedData();
    }

    public HolsterState get(UUID playerId){
        return states.computeIfAbsent(playerId, id -> new HolsterState());
    }

    public static HolsterSavedData load(CompoundTag tag, HolderLookup.Provider registries){
        HolsterSavedData data = create();

        ListTag players = tag.getList(PLAYERS, Tag.TAG_COMPOUND);

        for (int i = 0; i < players.size(); i++){
            CompoundTag playerTag = players.getCompound(i);

            if (!playerTag.hasUUID("uuid")){
                continue;
            }

            UUID uuid = playerTag.getUUID("uuid");
            HolsterState state = new HolsterState();

            if(playerTag.contains("slot", Tag.TAG_COMPOUND)){
                state.slot.load(registries, playerTag.getCompound("slot"));
            }

            if (playerTag.contains("visible")){
                state.visible = playerTag.getBoolean("visible");
            }

            data.states.put(uuid, state);
        }

        return data;
    }



    @Override
    @NotNull
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {

        ListTag players = new ListTag();

        for (Map.Entry<UUID, HolsterState> entry : states.entrySet()){
            CompoundTag playerTag = new CompoundTag();

            playerTag.putUUID("uuid", entry.getKey());
            playerTag.put("slot", entry.getValue().slot.save(registries));
            playerTag.putBoolean("visible", entry.getValue().visible);

            players.add(playerTag);
        }

        tag.put(PLAYERS, players);
        return tag;
    }

    public static HolsterSavedData get(MinecraftServer server){
        return server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(
                HolsterSavedData::create,
                HolsterSavedData::load,
                DataFixTypes.LEVEL
        ),
                DATA_ID);
    }

    public void markChanged(){
        setDirty();
    }
}
