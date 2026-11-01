package dev.sypex08.visorholster.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sypex08.visorholster.core.client.input.HolsterEditController;
import dev.sypex08.visorholster.core.client.settings.HolsterClientSettings;
import dev.sypex08.visorholster.core.common.holster.HolsterInteractionCheck;
import dev.sypex08.visorholster.core.common.holster.HolsterLayout;
import dev.sypex08.visorholster.core.common.network.HolsterChannel;
import dev.sypex08.visorholster.core.common.network.HolsterInteractionPayload;
import dev.sypex08.visorholster.core.common.network.HolsterSyncRequestPayload;
import net.minecraft.client.Minecraft;


//? if >=26.1.2{

/*import org.vmstudio.visor.api.compatibility.mcversion.render.McRenderUtils;

*///?} else{


import net.minecraft.client.renderer.LevelRenderer;

//?}

//? if <1.21.9 {
import net.minecraft.client.renderer.MultiBufferSource;
//?}
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
import org.vmstudio.visor.api.compatibility.mcversion.McVersionClientUtils;
import org.vmstudio.visor.api.common.HandType;
import org.vmstudio.visor.api.common.addon.VisorAddon;
//? if >=1.21.9 {
/*import org.vmstudio.visor.api.compatibility.mcversion.render.McFeatureRenderer;
*///?}
import org.vmstudio.visor.core.client.render.camera.VRCameraEntitySwap;
import org.vmstudio.visor.core.client.render.helpers.RenderPoseHelper;

import java.util.UUID;


@RegisterVRGameEffect
public class HolsterVRGameEffect extends org.vmstudio.visor.api.client.render.decoration.effects.VRGameEffect{

    private static final float HOLSTER_YAW_SMOOTHING = 0.12f;
    private static final float LOOK_DOWN_PITCH_THRESHOLD = (float) Math.toRadians(25.0);
    private static final float LOOK_DOWN_YAW_DEAD_ZONE = (float) Math.toRadians(30.0);

    private final int[] wasTouching = {-1, -1};
    private final HolsterEditController editController = new HolsterEditController();
    private UUID syncedPlayer;
    private Object syncedLevel;
    private float holsterYaw;
    private boolean holsterYawInitialized;
    private boolean lookingDown;
    private float lookDownHeadYaw;
    private float lookDownHolsterYaw;

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

        if (!HolsterClientSettings.isEnabled()){
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.level == null) {
            syncedPlayer = null;
            syncedLevel = null;
            wasTouching[0] = -1;
            wasTouching[1] = -1;
            editController.cancelAndSave();
            return;
        }

        //? if <1.21.9 {
        MultiBufferSource.BufferSource bufferSource =
                minecraft.renderBuffers().bufferSource();
        //?}

        VRPlayerPoseClient renderPose = VisorAPI.client().getVRLocalPlayer().getPoseData(PlayerPoseType.RENDER);

        Vector3fc cameraPosition3fc = RenderPoseHelper.getCameraPosition(vrRenderPass, renderPose);

        Vec3 cameraPosition = new Vec3(cameraPosition3fc.x(),cameraPosition3fc.y(),cameraPosition3fc.z());

        Vec3 InterpolatedPos = VRCameraEntitySwap.getCameraEntityCache().getInterpolatedPos(partialTicks);

        var vrPlayer = VisorAPI.client().getVRLocalPlayer();

        if (minecraft.player != null) {
            UUID playerId = minecraft.player.getUUID();

            if (!playerId.equals(syncedPlayer) || minecraft.level != syncedLevel) {
                HolsterChannel.get().sendToServer(
                        new HolsterSyncRequestPayload()
                );

                syncedPlayer = playerId;
                syncedLevel = minecraft.level;
                wasTouching[0] = -1;
                wasTouching[1] = -1;
                editController.cancelAndSave();
                holsterYawInitialized = false;
                lookingDown = false;
            }
        } else {
            syncedPlayer = null;
            syncedLevel = null;
            wasTouching[0] = -1;
            wasTouching[1] = -1;
            editController.cancelAndSave();
            holsterYawInitialized = false;
            lookingDown = false;
        }

        float headYaw = renderPose.getHmd().getYaw();
        float headPitch = renderPose.getHmd().getPitch();
        holsterYaw = updateHolsterYaw(headYaw, headPitch);

        if (vrRenderPass == VRRenderPass.worldUpdater()) {
            boolean screenOpen = McVersionClientUtils.screen() != null;
            editController.update(
                    renderPose,
                    InterpolatedPos,
                    holsterYaw,
                    vrPlayer,
                    minecraft.isPaused(),
                    screenOpen
            );

            for (HandType hand : HandType.values()) {
                Vec3 handWorldPosition =
                        renderPose.getHand(hand).getPositionVec3();

                int touchingSlot = HolsterInteractionCheck.getTouching(
                        handWorldPosition,
                        InterpolatedPos,
                        holsterYaw,
                        vrPlayer
                );

                int handIndex = hand.ordinal();
                boolean canInteract = !HolsterClientSettings.isEditMode()
                        && !minecraft.isPaused()
                        && !screenOpen;

                if (canInteract
                        && touchingSlot != -1
                        && touchingSlot != wasTouching[handIndex]) {
                    HolsterChannel.get().sendToServer(
                            new HolsterInteractionPayload(
                                    hand == HandType.MAIN,
                                    touchingSlot
                            )
                    );
                }

                wasTouching[handIndex] = touchingSlot;
            }
        }

        for (int slot = 0; slot < HolsterClientSettings.getHolsterPositionCount(); slot++){

            Vec3 playerRelativePos = HolsterLayout.calcPlayerRelativePos(
                    vrRenderPass,
                    partialTicks,
                    slot,
                    holsterYaw
            );

            poseStack.pushPose();
            poseStack.setIdentity();

            RenderPoseHelper.applyCameraOrientation(vrRenderPass, poseStack);

            poseStack.translate(playerRelativePos.x, playerRelativePos.y, playerRelativePos.z);

            Vec3 orbWorldPosition = cameraPosition.add(playerRelativePos);

            //? >= 26.1.2{

            /*int combinedLight = McRenderUtils.packedLight(
                    minecraft.level,
                    BlockPos.containing(orbWorldPosition)
            );


            *///?} else {
            

            int combinedLight =  LevelRenderer.getLightColor(
                    minecraft.level,
                    BlockPos.containing(orbWorldPosition)
            );

             //?}


            try {

                if (minecraft.isPaused() && !HolsterClientSettings.isPreview()){
                    return;
                }

                HolsterRenderer.renderItem(
                        poseStack,
                        combinedLight,
                        holsterYaw,
                        slot
                        //? if <1.21.9 {
                        , bufferSource
                        //?}
                );
                HolsterRenderer.renderHolster(
                        poseStack
                        //? if <1.21.9 {
                        , bufferSource
                        //?}
                );

            } finally {
                poseStack.popPose();
            }

        }

        //? if >=1.21.9 {
        /*McFeatureRenderer.render();
        *///?} else {
        bufferSource.endBatch();
        //?}
    }

    private float updateHolsterYaw(float headYaw, float headPitch) {
        boolean lookingDownNow = headPitch < -LOOK_DOWN_PITCH_THRESHOLD;

        if (!holsterYawInitialized) {
            holsterYaw = headYaw;
            holsterYawInitialized = true;
            lookingDown = lookingDownNow;
            if (lookingDown) {
                lookDownHeadYaw = headYaw;
                lookDownHolsterYaw = holsterYaw;
            }
            return holsterYaw;
        }

        if (lookingDownNow && !lookingDown) {
            lookDownHeadYaw = headYaw;
            lookDownHolsterYaw = holsterYaw;
        }
        lookingDown = lookingDownNow;

        float targetYaw;
        if (lookingDownNow) {
            float headTurn = wrapAngle(headYaw - lookDownHeadYaw);
            float turnBeyondDeadZone = Math.abs(headTurn) - LOOK_DOWN_YAW_DEAD_ZONE;
            targetYaw = lookDownHolsterYaw;

            if (turnBeyondDeadZone > 0.0f) {
                targetYaw += Math.copySign(turnBeyondDeadZone, headTurn);
            }
        } else {
            targetYaw = headYaw;
        }

        float difference = wrapAngle(targetYaw - holsterYaw);
        holsterYaw += difference * HOLSTER_YAW_SMOOTHING;
        return holsterYaw;
    }

    private static float wrapAngle(float angle) {
        while (angle > Math.PI) {
            angle -= (float) (Math.PI * 2.0);
        }
        while (angle < -Math.PI) {
            angle += (float) (Math.PI * 2.0);
        }
        return angle;
    }

    @Override
    public @NotNull String getId() {
        return "holster_orb";
    }


}








