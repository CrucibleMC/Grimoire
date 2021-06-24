package io.github.crucible.grimoire.mc1_7_10;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.GrimoireAPI;
import io.github.crucible.grimoire.common.api.lib.Environment;
import io.github.crucible.grimoire.common.events.SubscribeAnnotationWrapper;
import io.github.crucible.grimoire.mc1_7_10.handlers.ChadAnnotationWrapper;
import io.github.crucible.grimoire.mc1_7_10.handlers.ChadOPChecker;
import io.github.crucible.grimoire.mc1_7_10.handlers.ChadPacketDispatcher;
import io.github.crucible.grimoire.mc1_7_10.handlers.ChadVersionHandler;
import io.github.crucible.grimoire.mc1_7_10.test.TestEventHandler;
import io.github.crucible.omniconfig.OmniconfigCore;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import com.google.common.base.Charsets;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;

@IFMLLoadingPlugin.Name("Grimoire")
@IFMLLoadingPlugin.MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(Integer.MIN_VALUE + 1000)
public class GrimoireCoremod implements IFMLLoadingPlugin {

    @SuppressWarnings("deprecation")
    public GrimoireCoremod() {
        GrimoireCore.INSTANCE.getClass(); // Make it construct

        MixinBootstrap.init();
        Mixins.addConfiguration("grimoire/mixins.grimoire.json"); // TODO Register internal GrimmixController for this... or no

        LogManager.getLogger("GrimoireCore").info("Coremod constructed!");
    }

    @Override
    public void injectData(Map<String, Object> data) {
        GrimoireCore.INSTANCE.configure((File) data.get("mcLocation"),
                (Boolean) data.get("runtimeDeobfuscationEnabled"), "mods", "1.7.10",
                FMLLaunchHandler.side() == cpw.mods.fml.relauncher.Side.CLIENT ? Environment.CLIENT : Environment.DEDICATED_SERVER);
        SubscribeAnnotationWrapper.setWrapperFactory(this::createWrapper);
        ChadVersionHandler.init();

        GrimoireAPI.EVENT_BUS.register(new TestEventHandler());

        GrimoireCore.INSTANCE.init();
    }

    private SubscribeAnnotationWrapper createWrapper(Method method) {
        return new ChadAnnotationWrapper(method.getAnnotation(cpw.mods.fml.common.eventhandler.SubscribeEvent.class));
    }

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
