package dev.sypex08.visorholster.fabric;

import org.vmstudio.visor.api.ModLoader;
import org.vmstudio.visor.api.VisorAPI;
import dev.sypex08.visorholster.core.client.HolsterAddonClient;
import dev.sypex08.visorholster.core.server.HolsterAddonServer;
import net.fabricmc.api.ModInitializer;

public class VisorHolster implements ModInitializer {
    @Override
    public void onInitialize() {
        if(ModLoader.get().isDedicatedServer()){
            VisorAPI.registerAddon(
                    new HolsterAddonServer()
            );
        }else{
            VisorAPI.registerAddon(
                    new VisorHolsterAddonClient()
            );
        }
    }
}
