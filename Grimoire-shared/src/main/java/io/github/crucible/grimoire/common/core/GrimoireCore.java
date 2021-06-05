package io.github.crucible.grimoire.common.core;

import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.lib.Side;
import io.github.crucible.grimoire.common.integrations.IntegrationManager;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class GrimoireCore {
    public static final GrimoireCore INSTANCE = new GrimoireCore();
    public static final Logger logger = LogManager.getLogger("Grimoire");
    private static final LaunchClassLoader classLoader = (LaunchClassLoader) GrimoireCore.class.getClassLoader();

    private final GrimmixLoader grimmixLoader;
    private final IntegrationManager grimmixIntegrations;
    private File mcLocation;
    private Side side;

    public GrimoireCore() {
        if (this.isDevEnvironment()) {
            System.setProperty("mixin.debug", "true");
            System.setProperty("mixin.hotSwap", "true");
            System.setProperty("mixin.env.disableRefMap", "true");
            System.setProperty("mixin.debug.export", "true");
            System.setProperty("mixin.checks", "true");
            System.setProperty("mixin.checks.interfaces", "true");
            System.setProperty("mixin.env", "true");
        }

        this.grimmixLoader = GrimmixLoader.INSTANCE;
        this.grimmixIntegrations = new IntegrationManager();
    }

    public void configure(File mcLocation, boolean obfuscated, String mcModFolder, String version, Side onSide) {
        this.side = onSide;
        this.mcLocation = mcLocation;

        this.grimmixLoader.scanForGrimmixes(classLoader,
                new File(mcLocation, mcModFolder),
                new File(mcLocation, mcModFolder + File.separator + version));

        this.grimmixLoader.construct();
        this.grimmixLoader.validate();
        this.grimmixLoader.coreLoad();
    }

    public boolean isDevEnvironment() {
        return Boolean.parseBoolean(System.getProperty("fml.isDevEnvironment"));
    }

    public void loadModMixins() {
        this.grimmixLoader.modLoad();
    }

    public void finish() {
        this.grimmixLoader.finish();
    }

    public IntegrationManager getGrimmixIntegrations() {
        return this.grimmixIntegrations;
    }

    public Side getSide() {
        return this.side;
    }

    public File getMCLocation() {
        return this.mcLocation;
    }

}
