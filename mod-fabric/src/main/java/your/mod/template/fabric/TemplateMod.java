package your.mod.template.fabric;

import org.vmstudio.visor.api.ModLoader;
import org.vmstudio.visor.api.VisorAPI;
import your.mod.template.core.client.TemplateAddonClient;
import your.mod.template.core.server.TemplateAddonServer;
import net.fabricmc.api.ModInitializer;

public class TemplateMod implements ModInitializer {
    @Override
    public void onInitialize() {
        if(ModLoader.get().isDedicatedServer()){
            VisorAPI.registerAddon(
                    new TemplateAddonServer()
            );
        }else{
            VisorAPI.registerAddon(
                    new TemplateAddonClient()
            );
        }
    }
}
