package dev.sypex08.visorholster.core.common;

import dev.sypex08.visorholster.core.common.holster.HolsterManager;

public class VisorHolster {
    public static final String MOD_ID = "visor_holster";
    public static final String MOD_NAME = "VisorHolster";

    public static HolsterManager holsterManager;

    public static void init(){

        holsterManager = new HolsterManager();

    }


}
