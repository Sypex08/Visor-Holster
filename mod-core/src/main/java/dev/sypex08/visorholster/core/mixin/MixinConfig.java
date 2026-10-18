package dev.sypex08.visorholster.core.mixin;

import org.vmstudio.visor.api.ModLoader;
import dev.sypex08.visorholster.core.common.VisorHolster;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MixinConfig implements IMixinConfigPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(VisorHolster.MOD_NAME);

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void onLoad(String mixinPackage) {
    }


    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!ModLoader.get().isModLoaded(VisorHolster.MOD_ID)) {
            LOGGER.info("{} failed to load, canceled applying mixin '{}'",
                    VisorHolster.MOD_NAME, mixinClassName
            );
            return false;
        }


        return true;
    }
}
