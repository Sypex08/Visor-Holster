package dev.sypex08.visorholster.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import org.vmstudio.visor.api.client.events.render.RenderPipelineStageVREvent;
import org.vmstudio.visor.api.client.render.RenderPipelineStage;
import org.vmstudio.visor.api.common.eventbus.listener.VREventHandler;
import org.vmstudio.visor.api.common.eventbus.listener.VREventListener;

public class VisorRenderHook implements VREventListener {


    public static final VisorRenderHook INSTANCE = new VisorRenderHook();

    @VREventHandler
    public void renderVisor(RenderPipelineStageVREvent event){
        if (event.getStage() != RenderPipelineStage.AFTER_TRANSLUCENT){
            return;
        }

        if (!event.getRenderPhase().isVRWorld()){
            return;
        }

        if (!event.getRenderPass().isEye()){
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        PoseStack poseStack = event.getPoseStack();

        MultiBufferSource buffers = minecraft.renderBuffers().bufferSource();

        Camera camera = minecraft.gameRenderer.getMainCamera();

        HolsterRenderer.render(poseStack, buffers, camera);

    }
}
