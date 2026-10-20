package dev.sypex08.visorholster.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.sypex08.visorholster.core.common.VisorHolster;
import dev.sypex08.visorholster.core.common.holster.HolsterState;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;
import org.vmstudio.visor.api.VisorAPI;
import org.vmstudio.visor.api.common.player.VRBodyPartType;
import org.vmstudio.visor.api.common.player.VisorPlayer;

import static com.mojang.text2speech.Narrator.LOGGER;

public class HolsterRenderer {

    private static final ResourceLocation WHITE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/white.png");

    public static final RenderType RENDER_TYPE = RenderType.entityTranslucent(WHITE_TEXTURE);


    private static final int RED = 80;
    private static final int GREEN = 180;
    private static final int BLUE = 255;
    private static final int ALPHA = 130;

    private HolsterRenderer(){
    }

    public static void render(PoseStack poseStack, MultiBufferSource buffers, Camera camera) {

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || VisorHolster.holsterManager == null) {
            return;
        }

        VisorPlayer VRplayer = VisorAPI.getVisorPlayer(minecraft.player);


        HolsterState state = VisorHolster.holsterManager.get(minecraft.player.getUUID());

        if(!state.visable){
            return;
        }

        float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);

        Vec3 playerPosition = minecraft.player.getPosition(partialTick);

        Vec3 cameraPosition = camera.getPosition(); // might need to be changed to visor camera

        Vector3fc look = null;
        double bodyHeight = 0;

        if(VisorAPI.getVRPlayer(minecraft.player) == null){
            bodyHeight = minecraft.player.getBbHeight() * 0.55;
            look = camera.getLookVector();
        }
        else if(VisorAPI.getVRPlayer(minecraft.player).isVR()){

            VisorPlayer vrPlayer = VisorAPI.getVRPlayer(minecraft.player);

            bodyHeight = vrPlayer.asVR().getActualHeight() - (vrPlayer.asVR().getActualHeight() * 1.45);

            look = VisorAPI.getVRPlayer(minecraft.player).asVR().getPoseData().getPose(VRBodyPartType.HEAD).getDirection();

        }


        double horizalLength = Math.sqrt(look.x() * look.x() + look.z() * look.z());

        double flatX = 0.0;
        double flatZ = 0.0;

        if (horizalLength > 0.0001){
            flatX = look.x() / horizalLength;
            flatZ = look.z() / horizalLength;
        }

        double forwardDistance = 0.25;
        double sideDistance = 0.35;

        double rightX = -flatZ;
        double rightZ = flatX;



        Vec3 sphereWorldPosition =
                playerPosition.add(
                        flatX * forwardDistance + rightX * sideDistance,
                        bodyHeight,
                        flatZ * forwardDistance + rightZ * sideDistance
                );

        Vec3 relativePosition = sphereWorldPosition.subtract(cameraPosition);

        poseStack.pushPose();

        poseStack.translate(relativePosition.x, relativePosition.y, relativePosition.z);

        VertexConsumer consumer =
                buffers.getBuffer(RENDER_TYPE);

        renderSphere(poseStack, consumer, 0.125f, 12, 24);

        poseStack.popPose();

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
