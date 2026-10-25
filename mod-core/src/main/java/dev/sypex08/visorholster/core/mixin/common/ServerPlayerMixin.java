package dev.sypex08.visorholster.core.mixin.common;

import com.mojang.authlib.GameProfile;
import dev.sypex08.visorholster.core.common.VisorHolster;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {

    public ServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    public void visorholster$init(
            MinecraftServer server,
            ServerLevel level,
            GameProfile gameProfile,
            ClientInformation clientInformation,
            CallbackInfo ci
    ) {
        VisorHolster.holsterManager.onPlayerJoin(server, gameProfile.getId());
    }

    @Inject(at = @At("TAIL"), method = "disconnect")
    public void visorholster$disconnect(CallbackInfo ci){
        VisorHolster.holsterManager.onPlayerLeave(uuid);

    }

}
