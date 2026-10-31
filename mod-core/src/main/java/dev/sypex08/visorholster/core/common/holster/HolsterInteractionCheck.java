package dev.sypex08.visorholster.core.common.holster;

import dev.sypex08.visorholster.core.client.render.HolsterPosition;
import dev.sypex08.visorholster.core.client.settings.HolsterClientSettings;
import net.minecraft.world.phys.Vec3;
import org.vmstudio.visor.api.client.player.VRLocalPlayer;

import static dev.sypex08.visorholster.core.client.render.HolsterPosition.POSITIONS;

public class HolsterInteractionCheck {

    public int holsterSlot = 0;


    public static int getTouching(Vec3 handWorldPos, Vec3 interpolatedPos, float yaw, VRLocalPlayer vrPlayer){


        double forwardX = -Math.sin(yaw);
        double forwardZ =  Math.cos(yaw);

        double rightX = Math.cos(yaw);
        double rightZ = Math.sin(yaw);

        double interactionRadius = 0.125;

        double bodyHeight = vrPlayer.getActualHeight() - vrPlayer.getActualHeight() * 0.35 ;
        float customBodyHeight = (float)bodyHeight + + (HolsterClientSettings.getHeight() / 100);

        for (int slot = 0; slot < POSITIONS.size(); slot++) {
            HolsterPosition position = POSITIONS.get(slot);

            Vec3 orbWorldPosition = interpolatedPos.add(
                    rightX * position.sideDistance()
                            + forwardX * position.forwardDistance(),
                    customBodyHeight,
                    rightZ * position.sideDistance()
                            + forwardZ * position.forwardDistance()
            );



            if (handWorldPos.closerThan(orbWorldPosition, interactionRadius)) {
                return slot;
            }
        }

        return -1;
    }


}
