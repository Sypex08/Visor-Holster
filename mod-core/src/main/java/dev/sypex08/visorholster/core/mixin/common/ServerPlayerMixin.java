package dev.sypex08.visorholster.core.mixin.common;

import dev.sypex08.visorholster.core.common.VisorHolster;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(at = @At("TAIL"), method = "<init>")
    public void visorholster$init(CallbackInfo ci){
        ServerPlayer player = (ServerPlayer) (Object) this;

        //? if >= 1.21.10{

        /*MinecraftServer server = player.level().getServer();

        *///?} else{
        

        MinecraftServer server = player.getServer();


        //?}

        if (server != null) {
            VisorHolster.holsterManager.onPlayerJoin(server, player.getUUID());
        }
    }

    @Inject(at = @At("TAIL"), method = "disconnect")
    public void visorholster$disconnect(CallbackInfo ci){
        VisorHolster.holsterManager.onPlayerLeave(
                ((ServerPlayer) (Object) this).getUUID()
        );
    }

}
