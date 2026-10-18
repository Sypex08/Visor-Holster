package your.mod.template.neoforge;

import org.vmstudio.visor.api.ModLoader;
import org.vmstudio.visor.api.VisorAPI;
import your.mod.template.core.client.TemplateAddonClient;
import your.mod.template.core.common.VisorTemplate;
import your.mod.template.core.server.TemplateAddonServer;
import net.neoforged.fml.common.Mod;

@Mod(VisorTemplate.MOD_ID)
public class TemplateMod {
    public TemplateMod(){
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
