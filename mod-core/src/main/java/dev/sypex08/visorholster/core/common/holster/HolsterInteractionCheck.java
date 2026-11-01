package dev.sypex08.visorholster.core.common.holster;

import dev.sypex08.visorholster.core.client.settings.HolsterClientSettings;
import net.minecraft.world.phys.Vec3;
import org.vmstudio.visor.api.client.player.VRLocalPlayer;

public class HolsterInteractionCheck {

    private static final double INTERACTION_RADIUS = 0.125;

    public static int getTouching(
            Vec3 handWorldPos,
            Vec3 bodyOrigin,
            float yaw,
            VRLocalPlayer vrPlayer
    ) {
        return getClosestSlot(
                handWorldPos,
                bodyOrigin,
                yaw,
                vrPlayer,
                INTERACTION_RADIUS
        );
    }

    public static int getClosestSlot(
            Vec3 handWorldPos,
            Vec3 bodyOrigin,
            float yaw,
            VRLocalPlayer vrPlayer,
            double radius
    ) {
        if (handWorldPos == null || bodyOrigin == null || vrPlayer == null || radius < 0.0) {
            return -1;
        }

        double closestDistanceSquared = radius * radius;
        int closestSlot = -1;

        for (int slot = 0; slot < HolsterClientSettings.getHolsterPositionCount(); slot++) {
            Vec3 holsterWorldPos = HolsterLayout.calcWorldPosition(
                    slot,
                    bodyOrigin,
                    yaw,
                    vrPlayer
            );
            double distanceSquared = handWorldPos.distanceToSqr(holsterWorldPos);
            if (distanceSquared < closestDistanceSquared) {
                closestDistanceSquared = distanceSquared;
                closestSlot = slot;
            }
        }

        return closestSlot;
    }
}