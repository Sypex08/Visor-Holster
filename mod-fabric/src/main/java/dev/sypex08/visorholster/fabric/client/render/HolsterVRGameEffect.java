package dev.sypex08.visorholster.fabric.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sypex08.visorholster.core.client.render.HolsterRenderer;
import dev.sypex08.visorholster.core.common.holster.HolsterInteractionCheck;
import dev.sypex08.visorholster.fabric.network.HolsterInteractionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3fc;
import org.vmstudio.visor.api.VisorAPI;
import org.vmstudio.visor.api.client.player.pose.PlayerPoseType;
import org.vmstudio.visor.api.client.player.pose.VRPlayerPoseClient;
import org.vmstudio.visor.api.client.render.VRRenderPass;
import org.vmstudio.visor.api.client.render.decoration.VRDecorator;
import org.vmstudio.visor.api.client.render.decoration.annotations.RegisterVRGameEffect;
import org.vmstudio.visor.api.common.HandType;
import org.vmstudio.visor.api.common.addon.VisorAddon;
import org.vmstudio.visor.core.client.render.camera.VRCameraEntitySwap;
import org.vmstudio.visor.core.client.render.helpers.RenderPoseHelper;


@RegisterVRGameEffect
public class HolsterVRGameEffect extends org.vmstudio.visor.api.client.render.decoration.effects.VRGameEffect{

    private boolean wasTouching;

    public HolsterVRGameEffect(@NotNull VisorAddon owner) {
        super(owner);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }

    @Override
    public boolean isVisible(@NotNull VRDecorator vrDecorator) {
        return true;
    }

    @Override
    public void render(@NotNull VRRenderPass vrRenderPass, @NotNull PoseStack poseStack, float partialTicks) {

        Minecraft minecraft = Minecraft.getInstance();

        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        VRPlayerPoseClient renderPose = VisorAPI.client().getVRLocalPlayer().getPoseData(PlayerPoseType.RENDER);

        Vector3fc cameraPosition3fc = RenderPoseHelper.getCameraPosition(vrRenderPass, renderPose);

        Vec3 cameraPosition = new Vec3(cameraPosition3fc.x(),cameraPosition3fc.y(),cameraPosition3fc.z());

        Vec3 InterpolatedPos = VRCameraEntitySwap.getCameraEntityCache().getInterpolatedPos(partialTicks);

        var vrPlayer = VisorAPI.client().getVRLocalPlayer();

        float yaw = renderPose.getBodyYaw();

        double bodyHeight = vrPlayer.getActualHeight() - vrPlayer.getActualHeight() * 0.35;


        double forwardX = -Math.sin(yaw);
        double forwardZ =  Math.cos(yaw);

        double rightX = Math.cos(yaw);
        double rightZ = Math.sin(yaw);


        double sideDistance = 0.0;
        double forwardDistance = 0.50;


        Vec3 orbWorldPosition = InterpolatedPos.add(
                rightX * sideDistance + forwardX * forwardDistance,
                bodyHeight,
                rightZ * sideDistance + forwardZ * forwardDistance
        );


        if (vrRenderPass == VRRenderPass.worldUpdater() && !minecraft.isPaused()){
            HandType activeHand = VisorAPI.client().getVRLocalPlayer().getActiveHand();

            Vec3 handWorldPosition = renderPose.getHand(activeHand).getPositionVec3();

            boolean touching = HolsterInteractionCheck.isTouching(handWorldPosition,orbWorldPosition);


            if(touching && !wasTouching){

                ClientPlayNetworking.send(new HolsterInteractionPayload(activeHand == HandType.MAIN));

            }

            wasTouching = touching;

        }

        Vec3 playerRelativePos = orbWorldPosition.subtract(cameraPosition);

        poseStack.pushPose();
        poseStack.setIdentity();

        RenderPoseHelper.applyCameraOrientation(vrRenderPass, poseStack);

        poseStack.translate(playerRelativePos.x, playerRelativePos.y, playerRelativePos.z);

        BlockPos orbBlockPos = BlockPos.containing(orbWorldPosition);

        int combinedLight = 0;

        if (minecraft.level != null){

            combinedLight = LevelRenderer.getLightColor(minecraft.level,orbBlockPos);
        }



        try {

            if (!minecraft.isPaused()){

                HolsterRenderer.renderItem(poseStack,bufferSource, combinedLight, renderPose.getHmd().getYaw());
                bufferSource.endBatch();

                HolsterRenderer.renderHolster(poseStack, bufferSource);
                bufferSource.endBatch();
            }



        } finally {
            poseStack.popPose();
        }

    }

    @Override
    public @NotNull String getId() {
        return "holster_orb";
    }


}








