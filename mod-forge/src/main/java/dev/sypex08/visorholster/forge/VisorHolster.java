package dev.sypex08.visorholster.forge;

import org.vmstudio.visor.api.ModLoader;
import org.vmstudio.visor.api.VisorAPI;
import dev.sypex08.visorholster.core.client.HolsterAddonClient;
import dev.sypex08.visorholster.core.server.HolsterAddonServer;
import net.minecraftforge.fml.common.Mod;

@Mod(dev.sypex08.visorholster.core.common.VisorHolster.MOD_ID)
public class VisorHolster {
    public VisorHolster(){
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
