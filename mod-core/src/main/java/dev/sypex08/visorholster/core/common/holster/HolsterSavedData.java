package dev.sypex08.visorholster.core.common.holster;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
//? if >=1.21.11 {
/*import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.datafixers.util.Pair;
*///?}
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.datafix.DataFixTypes;
//? if >=26.1.2 {
/*import net.minecraft.resources.Identifier;
*///?}
import net.minecraft.world.level.saveddata.SavedData;
//? if >=1.21.5 {
/*import net.minecraft.world.level.saveddata.SavedDataType;
*///?}
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

    public static HolsterSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        HolsterSavedData data = create();

        //? if >=1.21.5 {
        /*ListTag players = tag.getListOrEmpty(PLAYERS);

        for (int i = 0; i < players.size(); i++) {
            Optional<CompoundTag> playerTagOptional = players.getCompound(i);

            if (playerTagOptional.isEmpty()) {
                continue;
            }

            CompoundTag playerTag = playerTagOptional.get();
            Optional<int[]> uuidBits = playerTag.getIntArray("uuid");

            if (uuidBits.isEmpty()) {
                continue;
            }

            UUID uuid = UUIDUtil.uuidFromIntArray(uuidBits.get());
            HolsterState state = new HolsterState();
            ListTag slots = playerTag.getListOrEmpty("slots");

            int slotCount = Math.min(
                    slots.size(),
                    HolsterState.SLOT_COUNT
            );

            for (int slot = 0; slot < slotCount; slot++) {
                int slotIndex = slot;
                slots.getCompound(slot).ifPresent(slotTag -> {
                    if (slotTag.contains("stack")) {
                        state.slots[slotIndex].load(
                                registries,
                                slotTag.get("stack")
                        );
                    }
                });
            }

            state.visible = playerTag.getBooleanOr("visible", state.visible);
            data.states.put(uuid, state);
        }
        *///?} else {
        
        ListTag players = tag.getList(
                PLAYERS,
                Tag.TAG_COMPOUND
        );

        for (int i = 0; i < players.size(); i++) {
            CompoundTag playerTag = players.getCompound(i);

            if (!playerTag.hasUUID("uuid")) {
                continue;
            }

            UUID uuid = playerTag.getUUID("uuid");
            HolsterState state = new HolsterState();

            if (playerTag.contains("slots", Tag.TAG_LIST)) {
                ListTag slots = playerTag.getList(
                        "slots",
                        Tag.TAG_COMPOUND
                );

                int slotCount = Math.min(
                        slots.size(),
                        HolsterState.SLOT_COUNT
                );

                for (int slot = 0; slot < slotCount; slot++) {
                    CompoundTag slotTag = slots.getCompound(slot);

                    if (slotTag.contains("stack")) {
                        state.slots[slot].load(
                                registries,
                                slotTag.get("stack")
                        );
                    }
                }
            }

            if (playerTag.contains("visible")) {
                state.visible = playerTag.getBoolean("visible");
            }

            data.states.put(uuid, state);
        }
        //?}

        return data;
    }

    //? if >=1.21.11 {
    /*private static <T> HolsterSavedData load(DynamicOps<T> ops, Tag input) {
        if (!(input instanceof CompoundTag tag)) {
            return create();
        }

        HolsterSavedData data = create();
        ListTag players = tag.getListOrEmpty(PLAYERS);

        for (int i = 0; i < players.size(); i++) {
            Optional<CompoundTag> playerTagOptional = players.getCompound(i);

            if (playerTagOptional.isEmpty()) {
                continue;
            }

            CompoundTag playerTag = playerTagOptional.get();
            Optional<int[]> uuidBits = playerTag.getIntArray("uuid");

            if (uuidBits.isEmpty()) {
                continue;
            }

            UUID uuid = UUIDUtil.uuidFromIntArray(uuidBits.get());
            HolsterState state = new HolsterState();
            ListTag slots = playerTag.getListOrEmpty("slots");

            int slotCount = Math.min(
                    slots.size(),
                    HolsterState.SLOT_COUNT
            );

            for (int slot = 0; slot < slotCount; slot++) {
                int slotIndex = slot;
                slots.getCompound(slot).ifPresent(slotTag -> {
                    if (slotTag.contains("stack")) {
                        state.slots[slotIndex].load(
                                ops,
                                slotTag.get("stack")
                        );
                    }
                });
            }

            state.visible = playerTag.getBooleanOr("visible", state.visible);
            data.states.put(uuid, state);
        }

        return data;
    }
    *///?}



    private CompoundTag serialize(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();

        ListTag players = new ListTag();

        for (Map.Entry<UUID, HolsterState> entry : states.entrySet()) {
            CompoundTag playerTag = new CompoundTag();

            //? if >=1.21.5 {
            /*playerTag.putIntArray("uuid", UUIDUtil.uuidToIntArray(entry.getKey()));
            *///?} else {
            
            playerTag.putUUID("uuid", entry.getKey());
            //?}

            ListTag slots = new ListTag();

            for (int slot = 0; slot < HolsterState.SLOT_COUNT; slot++) {
                CompoundTag slotTag = new CompoundTag();
                slotTag.put(
                        "stack",
                        entry.getValue().slots[slot].save(registries)
                );
                slots.add(slotTag);
            }

            playerTag.put("slots", slots);
            playerTag.putBoolean("visible", entry.getValue().visible);

            players.add(playerTag);
        }

        tag.put(PLAYERS, players);
        return tag;
    }

    //? if >=1.21.5 {
    
    
    //?} elif >=1.20.5 {
    
    @Override
    @NotNull
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag serialized = serialize(registries);
        tag.merge(serialized);
        return tag;
    }
    //?} else {

    /*@Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag serialized = serialize(null);
        tag.merge(serialized);
        return tag;
    }
    *///?}

    public static HolsterSavedData get(MinecraftServer server){
        //? if >=1.21.5 {
        /*return server.overworld().getDataStorage().computeIfAbsent(
                new SavedDataType<HolsterSavedData>(
                        //? if >=26.1.2 {
                        /^Identifier.withDefaultNamespace(DATA_ID),
                        ^///?} else {
                        DATA_ID,
                        //?}
                        //? if >=1.21.11 {
                        /^HolsterSavedData::create,
                        ^///?} else {
                        context -> create(),
                        //?}
                        //? if >=1.21.11 {
                        /^codec(),
                        ^///?} else {
                        context -> codec(context.level().registryAccess()),
                        //?}
                        DataFixTypes.LEVEL
                )
        );
        *///?} elif >=1.20.5 {
        return server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(
                HolsterSavedData::create,
                HolsterSavedData::load,
                DataFixTypes.LEVEL
        ),
                DATA_ID);
        //?} elif >=1.20.2 {
        /*return server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(
                HolsterSavedData::create,
                tag -> HolsterSavedData.load(tag, null),
                DataFixTypes.LEVEL
        ),
                DATA_ID);
        *///?} else {
        /*return server.overworld().getDataStorage().computeIfAbsent(
                tag -> HolsterSavedData.load(tag, null),
                HolsterSavedData::create,
                DATA_ID
        );
        *///?}
    }

    //? if >=1.21.11 {
    /*private static Codec<HolsterSavedData> codec() {
        return Codec.of(
                new Encoder<HolsterSavedData>() {
                    @Override
                    public <T> DataResult<T> encode(
                            HolsterSavedData data,
                            DynamicOps<T> ops,
                            T prefix
                    ) {
                        return DataResult.success(
                                NbtOps.INSTANCE.convertTo(ops, data.serialize(ops))
                        );
                    }
                },
                new Decoder<HolsterSavedData>() {
                    @Override
                    public <T> DataResult<Pair<HolsterSavedData, T>> decode(
                            DynamicOps<T> ops,
                            T input
                    ) {
                        return DataResult.success(
                                Pair.of(
                                        load(ops, ops.convertTo(NbtOps.INSTANCE, input)),
                                        input
                                )
                        );
                    }
                }
        );
    }

    private <T> CompoundTag serialize(DynamicOps<T> ops) {
        CompoundTag tag = new CompoundTag();
        ListTag players = new ListTag();

        for (Map.Entry<UUID, HolsterState> entry : states.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putIntArray("uuid", UUIDUtil.uuidToIntArray(entry.getKey()));

            ListTag slots = new ListTag();
            for (int slot = 0; slot < HolsterState.SLOT_COUNT; slot++) {
                CompoundTag slotTag = new CompoundTag();
                slotTag.put("stack", entry.getValue().slots[slot].save(ops));
                slots.add(slotTag);
            }

            playerTag.put("slots", slots);
            playerTag.putBoolean("visible", entry.getValue().visible);
            players.add(playerTag);
        }

        tag.put(PLAYERS, players);
        return tag;
    }
    *///?} elif >=1.21.5 {
    /*private static Codec<HolsterSavedData> codec(
            HolderLookup.Provider registries
    ) {
        return Codec.PASSTHROUGH.xmap(
                dynamic -> load(
                        (CompoundTag) dynamic.convert(NbtOps.INSTANCE).getValue(),
                        registries
                ),
                data -> new Dynamic<>(
                        NbtOps.INSTANCE,
                        data.serialize(registries)
                )
        );
    }
    *///?}

    public void markChanged(){
        setDirty();
    }
}
