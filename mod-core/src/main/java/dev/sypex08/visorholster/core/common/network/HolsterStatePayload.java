package dev.sypex08.visorholster.core.common.network;

import dev.sypex08.visorholster.core.common.holster.HolsterState;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.vmstudio.visor.api.common.network.VisorPayloadToClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record HolsterStatePayload(List<Optional<CompoundTag>> stackTags, boolean visible) implements VisorPayloadToClient {

    public static final byte PAYLOAD_ID = 0;

    public static HolsterStatePayload from(HolsterState state, HolderLookup.Provider registries) {
        List<Optional<CompoundTag>> stackTags = new ArrayList<>(HolsterState.SLOT_COUNT);

        for (int slot = 0; slot < HolsterState.SLOT_COUNT; slot++) {
            ItemStack stack = state.slots[slot].getStack();

            //? if >=1.20.5 {
            stackTags.add(
                    stack.isEmpty() ? Optional.empty() : Optional.of((CompoundTag) stack.OPTIONAL_CODEC
                            .encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), stack)
                            .getOrThrow())
            );
            //?} else {
            /*stackTags.add(
                    stack.isEmpty() ? Optional.empty() : Optional.of(stack.save(new CompoundTag()))
            );
            *///?}
        }

        return new HolsterStatePayload(
                List.copyOf(stackTags),
                state.visible
        );
    }


    @Override
    public void onWrite(FriendlyByteBuf buffer) {
        for (int slot = 0; slot < HolsterState.SLOT_COUNT; slot++) {
            Optional<CompoundTag> stackTag = stackTags.get(slot);

            buffer.writeBoolean(stackTag.isPresent());

            if (stackTag.isPresent()) {
                buffer.writeNbt(stackTag.get());
            }
        }

        buffer.writeBoolean(visible);
    }

    @Override
    public byte payloadId() {
        return PAYLOAD_ID;
    }

    public static HolsterStatePayload read(FriendlyByteBuf buffer) {
        List<Optional<CompoundTag>> stackTags = new ArrayList<>(HolsterState.SLOT_COUNT);

        for (int slot = 0; slot < HolsterState.SLOT_COUNT; slot++) {
            Optional<CompoundTag> stackTag = Optional.empty();

            if (buffer.readBoolean()) {
                CompoundTag tag = buffer.readNbt();

                if (tag != null) {
                    stackTag = Optional.of(tag);
                }
            }

            stackTags.add(stackTag);
        }

        boolean visible = buffer.readBoolean();

        return new HolsterStatePayload(
                List.copyOf(stackTags),
                visible
        );
    }

    public ItemStack getItemStack(HolderLookup.Provider registries, int slot) {
        //? if >=1.20.5 {
        return stackTags.get(slot).flatMap(tag -> ItemStack.OPTIONAL_CODEC
                        .parse(registries.createSerializationContext(NbtOps.INSTANCE), tag)
                        .result()).orElse(ItemStack.EMPTY);
        //?} else {
        
        /*return stackTags.get(slot).map(ItemStack::of)
                .orElse(ItemStack.EMPTY);
        *///?}
    }
}
