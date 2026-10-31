package dev.sypex08.visorholster.core.common.holster;

import dev.sypex08.visorholster.core.client.render.HolsterPosition;
import dev.sypex08.visorholster.core.client.settings.HolsterClientSettings;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.vmstudio.visor.api.VisorAPI;
import org.vmstudio.visor.api.client.player.VRLocalPlayer;
import org.vmstudio.visor.api.client.player.pose.PlayerPoseType;
import org.vmstudio.visor.api.client.player.pose.VRPlayerPoseClient;
import org.vmstudio.visor.api.client.render.VRRenderPass;
import org.vmstudio.visor.core.client.render.camera.VRCameraEntitySwap;
import org.vmstudio.visor.core.client.render.helpers.RenderPoseHelper;

import static dev.sypex08.visorholster.core.client.render.HolsterPosition.POSITIONS;

public class HolsterLayout {

    public static Vec3 calcPlayerRelativePos(
            VRRenderPass vrRenderPass,
            float partialTicks,
            int holsterSlot,
            float holsterYaw
    ){

        VRPlayerPoseClient renderPose = VisorAPI.client().getVRLocalPlayer().getPoseData(PlayerPoseType.RENDER);

        Vector3fc cameraPosition3fc = RenderPoseHelper.getCameraPosition(vrRenderPass, renderPose);

        Vec3 cameraPosition = new Vec3(cameraPosition3fc.x(),cameraPosition3fc.y(),cameraPosition3fc.z());

        Vec3 InterpolatedPos = VRCameraEntitySwap.getCameraEntityCache().getInterpolatedPos(partialTicks);

        VRLocalPlayer vrPlayer = VisorAPI.client().getVRLocalPlayer();

        double bodyHeight = vrPlayer.getActualHeight() - vrPlayer.getActualHeight() * 0.35;

        float customBodyHeight = (float) bodyHeight + (HolsterClientSettings.getHeight() / 100);



        double forwardX = -Math.sin(holsterYaw);
        double forwardZ =  Math.cos(holsterYaw);

        double rightX = Math.cos(holsterYaw);
        double rightZ = Math.sin(holsterYaw);

        HolsterPosition holsterPosition = POSITIONS.get(holsterSlot);

        double sideDistance = holsterPosition.sideDistance();
        double forwardDistance = holsterPosition.forwardDistance();


        Vec3 orbWorldPosition = InterpolatedPos.add(
                rightX * sideDistance + forwardX * forwardDistance,
                customBodyHeight,
                rightZ * sideDistance + forwardZ * forwardDistance
        );



        return orbWorldPosition.subtract(cameraPosition);

    }


}
