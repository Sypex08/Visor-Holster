package dev.sypex08.visorholster.neoforge.client;


import com.mojang.blaze3d.vertex.PoseStack;
import dev.sypex08.visorholster.core.client.render.HolsterRenderer;
import dev.sypex08.visorholster.core.common.VisorHolster;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.vmstudio.visor.api.VisorAPI;

@EventBusSubscriber(
        modid = VisorHolster.MOD_ID,
        value = Dist.CLIENT
)

public final class NeoforgeWorldRenderHook {

    private NeoforgeWorldRenderHook(){
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event){



        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES){
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();

        PoseStack poseStack = event.getPoseStack();

        //TODO WRONG ROTATION

        HolsterRenderer.render(poseStack, buffers, event.getCamera());

        buffers.endBatch(HolsterRenderer.RENDER_TYPE);
    }

}
