package dev.sypex08.visorholster.core.client;

import dev.sypex08.visorholster.core.common.network.HolsterChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.vmstudio.visor.api.common.addon.VisorAddon;
import dev.sypex08.visorholster.core.common.VisorHolster;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class HolsterAddonClient implements VisorAddon {

    @Override
    public void onAddonLoad() {

        VisorHolster.init();
    }

    @Override
    public void onAddonRegister() {
        HolsterChannel.setClientStateHandler(payload -> {
            Minecraft minecraft = Minecraft.getInstance();

            if (minecraft.player == null
                    || minecraft.level == null
                    || VisorHolster.holsterManager == null) {
                return;
            }

            ItemStack stack = payload.getItemStack(
                    minecraft.level.registryAccess()
            );

            VisorHolster.holsterManager.updateClientState(
                    minecraft.player.getUUID(),
                    stack,
                    payload.visible()
            );
        });

        HolsterChannel.createChannel(this);
    }

    @Override
    public @Nullable String getAddonPackagePath() {
        return "dev.sypex08.visorholster.core.client";
    }

    @Override
    public @NotNull String getAddonId() {
        return VisorHolster.MOD_ID;
    }
    @Override
    public @NotNull Component getAddonName() {
        return Component.literal(VisorHolster.MOD_NAME);
    }
    @Override
    public String getModId() {
        return VisorHolster.MOD_ID;
    }
}
