package dev.sypex08.visorholster.core.common.holster;

public class HolsterState {
    public static final int SLOT_COUNT = 4;

    public final HolsterSlot[] slots = {
            new HolsterSlot(),
            new HolsterSlot(),
            new HolsterSlot(),
            new HolsterSlot()
    };
    public boolean visible = true;
}
