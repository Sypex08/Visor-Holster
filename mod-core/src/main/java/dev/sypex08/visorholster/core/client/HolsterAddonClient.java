package dev.sypex08.visorholster.core.client;

import dev.sypex08.visorholster.core.client.gui.HolsterSettingsScreen;
import dev.sypex08.visorholster.core.client.input.HolsterEditInputListener;
import dev.sypex08.visorholster.core.client.settings.HolsterClientSettings;
import dev.sypex08.visorholster.core.common.holster.HolsterState;
import dev.sypex08.visorholster.core.common.network.HolsterChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.vmstudio.visor.api.VisorAPI;
import org.vmstudio.visor.api.common.addon.VisorAddon;
import dev.sypex08.visorholster.core.common.VisorHolster;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class HolsterAddonClient implements VisorAddon {

    @Override
    public void onAddonLoad() {

        VisorHolster.init();
        HolsterClientSettings.load();
    }

    @Override
    public void onAddonRegister() {

        VisorAPI.eventBus().registerListener(
                this,
                new HolsterEditInputListener()
        );

        HolsterChannel.setClientStateHandler(payload -> {
            Minecraft minecraft = Minecraft.getInstance();

            if (minecraft.player == null
                    || minecraft.level == null
                    || VisorHolster.holsterManager == null) {
                return;
            }

            for (int slot = 0; slot < HolsterState.SLOT_COUNT; slot++) {
                ItemStack stack = payload.getItemStack(
                        minecraft.level.registryAccess(),
                        slot
                );

                VisorHolster.holsterManager.updateClientState(
                        minecraft.player.getUUID(),
                        stack,
                        payload.visible(),
                        slot
                );
            }

        });

        HolsterChannel.createChannel(this);
    }

    @Override
    public @Nullable Screen createAddonSettingsScreen(@NotNull Screen backScreen) {
        return new HolsterSettingsScreen(backScreen);
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
