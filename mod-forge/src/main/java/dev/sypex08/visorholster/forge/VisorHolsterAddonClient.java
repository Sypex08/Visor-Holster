package dev.sypex08.visorholster.forge;

import dev.sypex08.visorholster.core.client.HolsterAddonClient;
import dev.sypex08.visorholster.core.client.render.HolsterVRGameEffect;
import org.jetbrains.annotations.Nullable;
import org.vmstudio.visor.api.VisorAPI;

public class VisorHolsterAddonClient extends HolsterAddonClient {

    @Override
    public void onAddonLoad(){
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
