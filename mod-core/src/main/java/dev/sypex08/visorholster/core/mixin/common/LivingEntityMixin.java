package dev.sypex08.visorholster.core.mixin.common;

import dev.sypex08.visorholster.core.common.VisorHolster;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "dropAllDeathLoot", at = @At("TAIL"))
    private void visorHolster$dropHolsterOnDeath(
            ServerLevel level,
            DamageSource damageSource,
            CallbackInfo ci
    ) {
        if (!((Object) this instanceof ServerPlayer player)) {
            return;
        }

        if (VisorHolster.holsterManager != null) {
            VisorHolster.holsterManager.dropOnDeath(player);
        }
    }
}