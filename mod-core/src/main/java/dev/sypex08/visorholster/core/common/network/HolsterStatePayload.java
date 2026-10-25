package dev.sypex08.visorholster.core.common.network;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import org.vmstudio.visor.api.common.network.VisorPayloadToClient;

import java.util.Optional;

public record HolsterStatePayload(Optional<CompoundTag> stackTag, boolean visible) implements VisorPayloadToClient {

    public static final byte PAYLOAD_ID = 0;

    public static HolsterStatePayload from(ItemStack stack, HolderLookup.Provider registries, boolean visible){
        Optional<CompoundTag> tag = stack.isEmpty() ? Optional.empty() : Optional.of((CompoundTag) stack.save(registries));

        return new HolsterStatePayload(tag, visible);
    }


    @Override
    public void onWrite(FriendlyByteBuf buffer) {
        buffer.writeBoolean(stackTag.isPresent());

        if (stackTag.isPresent()){
            buffer.writeNbt(stackTag.get());
        }

        buffer.writeBoolean(visible);

    }

    @Override
    public byte payloadId() {
        return PAYLOAD_ID;
    }

    public static HolsterStatePayload read(FriendlyByteBuf buffer){
        Optional<CompoundTag> stackTag = Optional.empty();

        if (buffer.readBoolean()){
            CompoundTag tag = buffer.readNbt();

            if (tag != null){
                stackTag = Optional.of(tag);
            }
        }

        boolean visible = buffer.readBoolean();

        return new HolsterStatePayload(stackTag, visible);
    }

    public ItemStack getItemStack(HolderLookup.Provider registries){
        return stackTag.map(tag -> ItemStack.parseOptional(registries, tag)).orElse(ItemStack.EMPTY);
    }
}
