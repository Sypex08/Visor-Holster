package dev.sypex08.visorholster.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.sypex08.visorholster.core.common.VisorHolster;
import dev.sypex08.visorholster.core.common.holster.HolsterState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;


public class HolsterRenderer {

    private static final ResourceLocation WHITE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/white.png");

    public static final RenderType RENDER_TYPE = RenderType.entityTranslucentEmissive(WHITE_TEXTURE);



    private static int RED = 80;
    private static int GREEN = 180;
    private static int BLUE = 255;
    private static int ALPHA = 130;

    public static void setColor(int red, int green, int blue, int alpha){

        RED = red;
        GREEN = green;
        BLUE = blue;
        ALPHA = alpha;
    }

    private HolsterRenderer(){
    }


    public static void renderItem(PoseStack poseStack, MultiBufferSource buffers, int combinedLight, float bodyYaw) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null
                || VisorHolster.holsterManager == null
                || buffers == null) {
            return;
        }

        HolsterState state =
                VisorHolster.holsterManager.get(minecraft.player.getUUID());

        if (state == null || !state.visible) {
            return;
        }


        if (!state.slot.getStack().isEmpty()) {
            poseStack.pushPose();

            poseStack.translate(0.0D, 0.0D, 0.0D);

            poseStack.mulPose(Axis.YP.rotation(-bodyYaw));

            poseStack.scale(0.25f, 0.25f, 0.25f);

            ItemStack stack = state.slot.getStack();
            ItemRenderer renderer = minecraft.getItemRenderer();


            renderer.renderStatic(stack, ItemDisplayContext.FIXED, combinedLight, OverlayTexture.NO_OVERLAY,
                    poseStack, buffers, minecraft.level, 0);


            poseStack.popPose();
        }
    }


    public static void renderHolster(
            PoseStack poseStack,
            MultiBufferSource buffers
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null
                || VisorHolster.holsterManager == null
                || buffers == null) {
            return;
        }

        HolsterState state =
                VisorHolster.holsterManager.get(minecraft.player.getUUID());

        if (state == null || !state.visible) {
            return;
        }

        VertexConsumer consumer =
                buffers.getBuffer(RENDER_TYPE);

        renderSphere(
                poseStack,
                consumer,
                0.125f,
                12,
                24
        );

    }

    private static void renderSphere(
            PoseStack poseStack,
            VertexConsumer consumer,
            float radius,
            int rings,
            int segments
    ){
        PoseStack.Pose pose = poseStack.last();

        for (int ring = 0; ring < rings; ring++){
            double v0 = (double) ring / rings;
            double v1 = (double) (ring + 1) / rings;

            double phi0 = Math.PI * (v0 - 0.5);
            double phi1 = Math.PI * (v1 - 0.5);

            for (int segment = 0; segment < segments; segment++) {
                double u0 = (double) segment / segments;
                double u1 = (double) (segment + 1) / segments;

                double theta0 = Math.PI * 2.0 * u0;
                double theta1 = Math.PI * 2.0 * u1;

                float x00 = (float) (Math.cos(phi0) * Math.cos(theta0));
                float y00 = (float) Math.sin(phi0);
                float z00 = (float) (Math.cos(phi0) * Math.sin(theta0));

                float x10 = (float) (Math.cos(phi0) * Math.cos(theta1));
                float y10 = y00;
                float z10 = (float) (Math.cos(phi0) * Math.sin(theta1));

                float x11 = (float) (Math.cos(phi1) * Math.cos(theta1));
                float y11 = (float) Math.sin(phi1);
                float z11 = (float) (Math.cos(phi1) * Math.sin(theta1));

                float x01 = (float) (Math.cos(phi1) * Math.cos(theta0));
                float y01 = y11;
                float z01 = (float) (Math.cos(phi1) * Math.sin(theta0));

                vertex(
                        consumer,
                        pose,
                        x00 * radius,
                        y00 * radius,
                        z00 * radius,
                        x00,
                        y00,
                        z00,
                        (float) u0,
                        (float) v0
                );

                vertex(
                        consumer,
                        pose,
                        x10 * radius,
                        y10 * radius,
                        z10 * radius,
                        x10,
                        y10,
                        z10,
                        (float) u1,
                        (float) v0
                );

                vertex(
                        consumer,
                        pose,
                        x11 * radius,
                        y11 * radius,
                        z11 * radius,
                        x11,
                        y11,
                        z11,
                        (float) u1,
                        (float) v1
                );

                vertex(
                        consumer,
                        pose,
                        x01 * radius,
                        y01 * radius,
                        z01 * radius,
                        x01,
                        y01,
                        z01,
                        (float) u0,
                        (float) v1
                );
            }
        }
    }

    private static void vertex(
            VertexConsumer consumer,
            PoseStack.Pose pose,
            float x,
            float y,
            float z,
            float normalX,
            float normalY,
            float normalZ,
            float u,
            float v
    ) {
        consumer.addVertex(pose, x, y, z)
                .setColor(RED, GREEN, BLUE, ALPHA)
                .setUv(u, v)
                .setUv1(OverlayTexture.NO_OVERLAY, OverlayTexture.NO_OVERLAY)
                .setUv2(LightTexture.FULL_BRIGHT, LightTexture.FULL_BRIGHT)
                .setNormal(pose, normalX, normalY, normalZ);
    }


}
