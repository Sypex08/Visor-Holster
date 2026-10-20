package dev.sypex08.visorholster.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sypex08.visorholster.core.client.render.HolsterRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.renderer.MultiBufferSource;


public class FabricWorldRenderHook{

    private FabricWorldRenderHook(){
    }

    public static void register(){
        WorldRenderEvents.AFTER_TRANSLUCENT.register(
                FabricWorldRenderHook::renderFabric
        );
    }

    private static void renderFabric(WorldRenderContext context){

        PoseStack poseStack = context.matrixStack();

        MultiBufferSource buffers = context.consumers();

        if ( poseStack == null || buffers == null || context.camera() == null){
            return;
        }

      //  HolsterRenderer.render(poseStack, buffers, context.camera());

    }


}
