package dev.sypex08.visorholster.forge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sypex08.visorholster.core.client.render.HolsterRenderer;
import dev.sypex08.visorholster.core.common.VisorHolster;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.vmstudio.visor.api.VisorAPI;


@Mod.EventBusSubscriber(
        modid = VisorHolster.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)

public final class ForgeWorldRenderHook {

    private ForgeWorldRenderHook() {
    }

    @SubscribeEvent
    public static void onRenderLevelStage(
            RenderLevelStageEvent event
    ){
        Minecraft minecraft = Minecraft.getInstance();

        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES){
            return;
        }

        if (VisorAPI.getVRPlayer(minecraft.player) != null){
            return;
        }


        MultiBufferSource.BufferSource buffers =
                minecraft.renderBuffers().bufferSource();

        PoseStack poseStack = new PoseStack();


        HolsterRenderer.render(poseStack, buffers);

        //TODO ADD positioning (maybe)

        buffers.endBatch(HolsterRenderer.RENDER_TYPE);
    }






}

