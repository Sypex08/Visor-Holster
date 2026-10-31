package dev.sypex08.visorholster.core.common.holster;


import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
//? if >=1.21.11 {
/*import com.mojang.serialization.DynamicOps;
*///?}

public class HolsterSlot {
    private ItemStack stack = ItemStack.EMPTY;

    public boolean isEmpty(){ return stack.isEmpty();}

    public boolean insert(ItemStack from) {
        if (!stack.isEmpty() || from.isEmpty()) return false;
        stack = from.split(from.getCount());
        return true;
    }

    public ItemStack take(){
        ItemStack out = stack;
        stack = ItemStack.EMPTY;
        return out;

    }

    public void setStack(ItemStack stack) {
        this.stack = stack.copy();
    }

    public ItemStack getStack() {
        return stack;
    }

    public Tag save(HolderLookup.Provider registries){

        //? if >=1.20.5 {
        return stack.OPTIONAL_CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), stack).getOrThrow();
        //?} else {
        /*return stack.save(new CompoundTag());
        *///?}

    }

    //? if >=1.21.11 {
    /*public <T> Tag save(DynamicOps<T> ops) {
        return stack.OPTIONAL_CODEC.encodeStart(ops, stack)
                .map(encoded -> ops.convertTo(NbtOps.INSTANCE, encoded))
                .getOrThrow();
    }
    *///?}

    public void load(HolderLookup.Provider registries, Tag tag){

        //? if >=1.20.5 {
        this.stack = ItemStack.OPTIONAL_CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag)
                .resultOrPartial()
                .orElse(ItemStack.EMPTY);
        //?} else {
        /*stack = tag instanceof CompoundTag c ? ItemStack.of(c) : ItemStack.EMPTY;
        *///?}


    }

    //? if >=1.21.11 {
    /*public <T> void load(DynamicOps<T> ops, Tag tag) {
        this.stack = ItemStack.OPTIONAL_CODEC.parse(
                        ops,
                        NbtOps.INSTANCE.convertTo(ops, tag)
                )
                .resultOrPartial()
                .orElse(ItemStack.EMPTY);
    }
    *///?}


}
