package dev.sypex08.visorholster.core.client.input;

import dev.sypex08.visorholster.core.client.render.HolsterPosition;
import dev.sypex08.visorholster.core.client.settings.HolsterClientSettings;
import dev.sypex08.visorholster.core.common.holster.HolsterInteractionCheck;
import dev.sypex08.visorholster.core.common.holster.HolsterLayout;
import net.minecraft.world.phys.Vec3;
import org.vmstudio.visor.api.VisorAPI;
import org.vmstudio.visor.api.client.input.action.framework.VRActionButton;
import org.vmstudio.visor.api.client.player.VRLocalPlayer;
import org.vmstudio.visor.api.client.player.pose.VRPlayerPoseClient;
import org.vmstudio.visor.api.common.HandType;

public final class HolsterEditController {

    private static final double GRAB_RADIUS = 0.22;

    private final boolean[] wasPressed = new boolean[HandType.values().length];
    private int draggingSlot = -1;
    private HandType draggingHand;
    private Vec3 handOffset = Vec3.ZERO;

    public void update(
            VRPlayerPoseClient pose,
            Vec3 bodyOrigin,
            float holsterYaw,
            VRLocalPlayer vrPlayer,
            boolean paused,
            boolean screenOpen
    ) {
        boolean[] pressedNow = new boolean[HandType.values().length];
        for (HandType hand : HandType.values()) {
            VRActionButton action = VisorAPI.client()
                    .getInputManager()
                    .getActionRightMouse(hand);
            pressedNow[hand.ordinal()] = action != null && action.isPressed();
        }

        if (!HolsterClientSettings.isEditMode() || paused || screenOpen || vrPlayer == null) {
            finishDrag();
            System.arraycopy(pressedNow, 0, wasPressed, 0, wasPressed.length);
            return;
        }

        if (draggingSlot >= 0) {
            if (!pressedNow[draggingHand.ordinal()]) {
                finishDrag();
            } else {
                updateDraggedPosition(
                        pose,
                        bodyOrigin,
                        holsterYaw,
                        vrPlayer
                );
            }
        } else {
            for (HandType hand : HandType.values()) {
                int handIndex = hand.ordinal();
                if (pressedNow[handIndex] && !wasPressed[handIndex]) {
                    startDrag(
                            hand,
                            pose.getHand(hand).getPositionVec3(),
                            bodyOrigin,
                            holsterYaw,
                            vrPlayer
                    );
                    if (draggingSlot >= 0) {
                        updateDraggedPosition(
                                pose,
                                bodyOrigin,
                                holsterYaw,
                                vrPlayer
                        );
                        break;
                    }
                }
            }
        }

        System.arraycopy(pressedNow, 0, wasPressed, 0, wasPressed.length);
    }

    public void cancelAndSave() {
        finishDrag();
        for (int i = 0; i < wasPressed.length; i++) {
            wasPressed[i] = false;
        }
    }

    private void startDrag(
            HandType hand,
            Vec3 handWorldPos,
            Vec3 bodyOrigin,
            float holsterYaw,
            VRLocalPlayer vrPlayer
    ) {
        int slot = HolsterInteractionCheck.getClosestSlot(
                handWorldPos,
                bodyOrigin,
                holsterYaw,
                vrPlayer,
                GRAB_RADIUS
        );
        if (slot < 0) {
            return;
        }

        Vec3 holsterWorldPos = HolsterLayout.calcWorldPosition(
                slot,
                bodyOrigin,
                holsterYaw,
                vrPlayer
        );
        draggingSlot = slot;
        draggingHand = hand;
        handOffset = holsterWorldPos.subtract(handWorldPos);
        VisorAPI.client().getInputManager().triggerHapticPulseClick(hand);
    }

    private void updateDraggedPosition(
            VRPlayerPoseClient pose,
            Vec3 bodyOrigin,
            float holsterYaw,
            VRLocalPlayer vrPlayer
    ) {
        Vec3 handWorldPos = pose.getHand(draggingHand).getPositionVec3();
        Vec3 draggedWorldPos = handWorldPos.add(handOffset);
        HolsterPosition position = HolsterLayout.toHolsterPosition(
                draggingSlot,
                draggedWorldPos,
                bodyOrigin,
                holsterYaw,
                vrPlayer
        );
        HolsterClientSettings.setHolsterPosition(draggingSlot, position);
    }

    private void finishDrag() {
        if (draggingSlot < 0) {
            return;
        }

        HolsterClientSettings.save();
        draggingSlot = -1;
        draggingHand = null;
        handOffset = Vec3.ZERO;
    }
}