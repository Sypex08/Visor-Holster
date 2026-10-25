package dev.sypex08.visorholster.core.common.holster;


import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

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
        return stack.saveOptional(registries);
    }

    public void load(HolderLookup.Provider registries, Tag tag){
        stack = tag instanceof CompoundTag c ? ItemStack.parseOptional(registries, c) : ItemStack.EMPTY;
    }


}
