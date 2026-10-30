package dev.sypex08.visorholster.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sypex08.visorholster.core.client.settings.HolsterClientSettings;
import dev.sypex08.visorholster.core.common.holster.HolsterInteractionCheck;
import dev.sypex08.visorholster.core.common.holster.HolsterLayout;
import dev.sypex08.visorholster.core.common.network.HolsterChannel;
import dev.sypex08.visorholster.core.common.network.HolsterInteractionPayload;
import dev.sypex08.visorholster.core.common.network.HolsterStatePayload;
import dev.sypex08.visorholster.core.common.network.HolsterSyncRequestPayload;
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

import java.util.UUID;

import static dev.sypex08.visorholster.core.client.render.HolsterPosition.POSITIONS;


@RegisterVRGameEffect
public class HolsterVRGameEffect extends org.vmstudio.visor.api.client.render.decoration.effects.VRGameEffect{

    private int wasTouching = -1;
    private UUID syncedPlayer;

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

        if (minecraft.player != null) {
            UUID playerId = minecraft.player.getUUID();

            if (!playerId.equals(syncedPlayer)) {
                HolsterChannel.get().sendToServer(
                        new HolsterSyncRequestPayload()
                );

                syncedPlayer = playerId;
            }
        } else {
            syncedPlayer = null;
        }

        if (vrRenderPass == VRRenderPass.worldUpdater() && !minecraft.isPaused()){
            HandType activeHand = VisorAPI.client().getVRLocalPlayer().getActiveHand();

            Vec3 handWorldPosition = renderPose.getHand(activeHand).getPositionVec3();

            int touchingSlot = HolsterInteractionCheck.getTouching(handWorldPosition,InterpolatedPos, yaw, vrPlayer);


            if (touchingSlot != -1 && touchingSlot != wasTouching) {
                HolsterChannel.get().sendToServer(
                        new HolsterInteractionPayload(
                                activeHand == HandType.MAIN,
                                touchingSlot
                        )
                );
            }

            wasTouching = touchingSlot;

        }

        for (int slot = 0; slot < POSITIONS.size(); slot++){

            Vec3 playerRelativePos = HolsterLayout.calcPlayerRelativePos(vrRenderPass, partialTicks, slot);

            poseStack.pushPose();
            poseStack.setIdentity();

            RenderPoseHelper.applyCameraOrientation(vrRenderPass, poseStack);

            poseStack.translate(playerRelativePos.x, playerRelativePos.y, playerRelativePos.z);

            int combinedLight = 0;

            try {

                if (minecraft.isPaused() && !HolsterClientSettings.isPreview()){
                    return;
                }

                HolsterRenderer.renderItem(poseStack,bufferSource, combinedLight, renderPose.getHmd().getYaw(), slot);
                bufferSource.endBatch();

                HolsterRenderer.renderHolster(poseStack, bufferSource);
                bufferSource.endBatch();


            } finally {
                poseStack.popPose();
            }

        }
    }

    @Override
    public @NotNull String getId() {
        return "holster_orb";
    }


}








