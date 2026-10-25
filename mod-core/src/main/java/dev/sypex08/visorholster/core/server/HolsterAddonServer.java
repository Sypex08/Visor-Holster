package dev.sypex08.visorholster.core.server;

import dev.sypex08.visorholster.core.common.network.HolsterChannel;
import org.vmstudio.visor.api.common.addon.VisorAddon;
import dev.sypex08.visorholster.core.common.VisorHolster;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HolsterAddonServer implements VisorAddon {


    @Override
    public void onAddonLoad() {

        VisorHolster.init();

    }

    @Override
    public void onAddonRegister() {
        HolsterChannel.createChannel(this);
    }

    @Override
    public @Nullable String getAddonPackagePath() {
        return "dev.sypex08.visorholster.core.server";
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
