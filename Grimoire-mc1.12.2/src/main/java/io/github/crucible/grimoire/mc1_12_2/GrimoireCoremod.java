package io.github.crucible.grimoire.mc1_12_2;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.lib.Environment;
import io.github.crucible.grimoire.common.events.SubscribeAnnotationWrapper;
import io.github.crucible.grimoire.mc1_12_2.handlers.IncelAnnotationWrapper;
import io.github.crucible.grimoire.mc1_12_2.handlers.IncelOPChecker;
import io.github.crucible.grimoire.mc1_12_2.handlers.IncelVersionHandler;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

@IFMLLoadingPlugin.Name("Grimoire")
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE + 1000)
public class GrimoireCoremod implements IFMLLoadingPlugin {

    @SuppressWarnings("deprecation")
    public GrimoireCoremod() {
        GrimoireCore.INSTANCE.setCoremodManager(CoreModManager.class);

        MixinBootstrap.init();
        Mixins.addConfiguration("grimoire/mixins.grimoire.json");
    }

    private String getMCVersion() {
        String version = "1.7.10";
        try {
            Field vf = FMLInjectionData.class.getDeclaredField("mccversion");
            vf.setAccessible(true);
            version = (String) vf.get(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        GrimoireCore.logger.info("Loading on Minecraft version: " + version);

        return version;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        GrimoireCore.INSTANCE.configure((File) data.get("mcLocation"), "mods", this.getMCVersion(),
                FMLLaunchHandler.side() == net.minecraftforge.fml.relauncher.Side.CLIENT ? Environment.CLIENT : Environment.DEDICATED_SERVER);
        SubscribeAnnotationWrapper.setWrapperFactory(this::createWrapper);
        IncelVersionHandler.init();
        GrimoireCore.INSTANCE.init();
    }

    private SubscribeAnnotationWrapper createWrapper(Method method) {
        return new IncelAnnotationWrapper(method.getAnnotation(net.minecraftforge.fml.common.eventhandler.SubscribeEvent.class));
    }

    // IFMLLoadingPlugin junk, none of our interest

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
