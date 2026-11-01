package dev.sypex08.visorholster.core.common.holster;

import dev.sypex08.visorholster.core.client.render.HolsterPosition;
import dev.sypex08.visorholster.core.client.settings.HolsterClientSettings;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.vmstudio.visor.api.client.player.VRLocalPlayer;
import org.vmstudio.visor.api.client.player.pose.PlayerPoseType;
import org.vmstudio.visor.api.client.player.pose.VRPlayerPoseClient;
import org.vmstudio.visor.api.client.render.VRRenderPass;
import org.vmstudio.visor.api.VisorAPI;
import org.vmstudio.visor.core.client.render.camera.VRCameraEntitySwap;
import org.vmstudio.visor.core.client.render.helpers.RenderPoseHelper;

public class HolsterLayout {

    public static Vec3 calcPlayerRelativePos(
            VRRenderPass vrRenderPass,
            float partialTicks,
            int holsterSlot,
            float holsterYaw
    ) {
        VRPlayerPoseClient renderPose = VisorAPI.client()
                .getVRLocalPlayer()
                .getPoseData(PlayerPoseType.RENDER);
        Vector3fc cameraPosition3fc = RenderPoseHelper.getCameraPosition(vrRenderPass, renderPose);
        Vec3 cameraPosition = new Vec3(
                cameraPosition3fc.x(),
                cameraPosition3fc.y(),
                cameraPosition3fc.z()
        );
        Vec3 bodyOrigin = VRCameraEntitySwap.getCameraEntityCache()
                .getInterpolatedPos(partialTicks);
        VRLocalPlayer vrPlayer = VisorAPI.client().getVRLocalPlayer();

        return calcWorldPosition(holsterSlot, bodyOrigin, holsterYaw, vrPlayer)
                .subtract(cameraPosition);
    }

    public static Vec3 calcWorldPosition(
            int holsterSlot,
            Vec3 bodyOrigin,
            float holsterYaw,
            VRLocalPlayer vrPlayer
    ) {
        HolsterPosition position = HolsterClientSettings.getHolsterPosition(holsterSlot);
        double forwardX = -Math.sin(holsterYaw);
        double forwardZ = Math.cos(holsterYaw);
        double rightX = Math.cos(holsterYaw);
        double rightZ = Math.sin(holsterYaw);
        double verticalOffset = vrPlayer.getActualHeight() * position.height()
                + HolsterClientSettings.getHeight() / 100.0;

        return bodyOrigin.add(
                rightX * position.sideDistance() + forwardX * position.forwardDistance(),
                verticalOffset,
                rightZ * position.sideDistance() + forwardZ * position.forwardDistance()
        );
    }

    public static HolsterPosition toHolsterPosition(
            int holsterSlot,
            Vec3 worldPosition,
            Vec3 bodyOrigin,
            float holsterYaw,
            VRLocalPlayer vrPlayer
    ) {
        double playerHeight = vrPlayer.getActualHeight();
        if (!(playerHeight > 0.000001)) {
            return HolsterClientSettings.getHolsterPosition(holsterSlot);
        }

        double deltaX = worldPosition.x - bodyOrigin.x;
        double deltaZ = worldPosition.z - bodyOrigin.z;
        double forwardX = -Math.sin(holsterYaw);
        double forwardZ = Math.cos(holsterYaw);
        double rightX = Math.cos(holsterYaw);
        double rightZ = Math.sin(holsterYaw);
        double sideDistance = deltaX * rightX + deltaZ * rightZ;
        double forwardDistance = deltaX * forwardX + deltaZ * forwardZ;
        double height = (worldPosition.y - bodyOrigin.y
                - HolsterClientSettings.getHeight() / 100.0) / playerHeight;

        return new HolsterPosition(sideDistance, forwardDistance, height);
    }
}