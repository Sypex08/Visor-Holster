package dev.sypex08.visorholster.fabric;

import dev.sypex08.visorholster.core.client.HolsterAddonClient;
import dev.sypex08.visorholster.core.client.render.HolsterVRGameEffect;
import dev.sypex08.visorholster.core.common.VisorHolster;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.vmstudio.visor.api.VisorAPI;
import org.vmstudio.visor.api.common.addon.VisorAddon;

public class VisorHolsterAddonClient extends HolsterAddonClient {

    @Override
    public void onAddonLoad() {
        super.onAddonLoad();

        VisorAPI.addonManager()
                .getRegistries()
                .gameEffects()
                .registerComponent(new HolsterVRGameEffect(this));
    }

    @Override
    public @Nullable String getAddonPackagePath() {
        return null;
    }
}
