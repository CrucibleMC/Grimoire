package io.github.crucible.grimoire.common.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixins;

import com.google.common.base.Preconditions;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.GrimoireAPI;
import io.github.crucible.grimoire.common.api.events.configurations.MixinConfigLoadEvent;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;
import io.github.crucible.grimoire.common.api.mixin.ConfigurationType;
import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration;
import io.github.crucible.grimoire.common.core.runtimeconfig.ConfigBuildingManager;

public class MixinConfiguration implements IMixinConfiguration {
    private static final List<MixinConfiguration> unclaimedConfigurations = new ArrayList<>();
    private static boolean permitConfig = false;

    protected final Optional<IGrimmix> owner;
    protected final String classpath;
    protected final ConfigurationType configType;
    protected final boolean isRuntimeGenerated;

    protected boolean isLoaded = false;
    protected boolean isValid = true;

    public MixinConfiguration(IGrimmix owner, ConfigurationType type, String classpath, boolean isRuntimeGenerated) {
        this.owner = Optional.ofNullable(owner);
        this.classpath = Preconditions.checkNotNull(classpath);
        this.configType = Preconditions.checkNotNull(type);
        this.isRuntimeGenerated = isRuntimeGenerated;

        if (this.owner.isPresent()) {
            List<IMixinConfiguration> ownerConfigurations = ((GrimmixContainer) this.owner.get()).ownedConfigurations;

            if (!ownerConfigurations.contains(this)) {
                ownerConfigurations.add(this);
            } else {
                this.duplicateException();
            }
        } else {
            if (!unclaimedConfigurations.contains(this)) {
                unclaimedConfigurations.add(this);
            } else {
                this.duplicateException();
            }
        }
    }

    public static List<IMixinConfiguration> getUnclaimedConfigurations() {
        return Collections.unmodifiableList(unclaimedConfigurations);
    }

    public static List<IMixinConfiguration> prepareUnclaimedConfigurations(ConfigurationType ofType) {
        List<IMixinConfiguration> configList = new ArrayList<>();

        for (IMixinConfiguration config : unclaimedConfigurations) {
            if (config.getConfigurationType() == ofType) {
                configList.add(config);
            }
        }

        return configList;
    }

    @Override
    public Optional<IGrimmix> getOwner() {
        return this.owner;
    }

    @Override
    public String getClasspath() {
        return this.classpath;
    }

    @Override
    public ConfigurationType getConfigurationType() {
        return this.configType;
    }

    @Override
    public boolean isLoaded() {
        return this.isLoaded;
    }

    @Override
    public boolean isValid() {
        return this.isValid;
    }

    private void duplicateException() {
        throw new RuntimeException("Tried to register duplicate mixin configuration: " + this.classpath);
    }

    private void invalidate() {
        if (this.isLoaded())
            throw new IllegalStateException("Cannot invalidate Mixin configuration " + this.classpath + "; already loaded!");

        this.isValid = false;

        if (this.owner.isPresent()) {
            ((GrimmixContainer) this.owner.get()).ownedConfigurations.remove(this);
        } else {
            unclaimedConfigurations.remove(this);
        }
    }

    @Override
    public boolean isRuntimeGenerated() {
        return this.isRuntimeGenerated;
    }

    @Override
    public boolean canLoad() {
        return this.isLoaded || !this.isRuntimeGenerated() || ConfigBuildingManager.areRuntimeConfigsGenerated();
    }

    public void load() {
        if (this.canLoad() && this.isValid) {
            MixinConfigLoadEvent event = new MixinConfigLoadEvent(this.owner.orElse(null), this);
            GrimoireAPI.EVENT_BUS.post(event);

            if (event.isCanceled()) {
                this.invalidate();
                return;
            }

            GrimoireCore.logger.info("Loading configuration {}", this.classpath);
            this.isLoaded = true;
            permitConfig = true;

            Mixins.addConfiguration(this.classpath);

            permitConfig = false;
        } else {
            String error = "Cannot load mixin configuration " + this.classpath + "; ";

            if (this.isLoaded) {
                error += "already loaded!";
            } else if (this.isRuntimeGenerated && !ConfigBuildingManager.areRuntimeConfigsGenerated()) {
                error += "it is a runtime-generated configuration, and RuntimeMixinConfigs.jar was not generated yet!";
            }  else if (!this.isValid) {
                error += "configuration is invalid!";
            }

            throw new IllegalStateException(error);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MixinConfiguration) {
            MixinConfiguration another = (MixinConfiguration) obj;
            return this.classpath.equals(another.classpath);
        } else
            return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.classpath.hashCode();
    }
}
