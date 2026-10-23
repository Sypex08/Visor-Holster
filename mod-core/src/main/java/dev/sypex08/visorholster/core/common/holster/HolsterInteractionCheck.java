package dev.sypex08.visorholster.core.common.holster;

import net.minecraft.world.phys.Vec3;

public class HolsterInteractionCheck {


    public static boolean isTouching(Vec3 handWorldPos, Vec3 orbWorldPos){

        double interactionRadius = 0.225;

        return handWorldPos.closerThan(orbWorldPos, interactionRadius);


    }


}
