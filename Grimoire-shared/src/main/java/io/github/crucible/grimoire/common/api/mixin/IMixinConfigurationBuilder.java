package io.github.crucible.grimoire.common.api.mixin;

import org.spongepowered.asm.mixin.MixinEnvironment;

import io.github.crucible.grimoire.common.api.mixin.IMixinConfiguration.ConfigurationType;
import io.github.crucible.grimoire.common.core.runtimeconfig.MixinConfigBuilder;

public interface IMixinConfigurationBuilder {

    public IMixinConfigurationBuilder targetEnvironment(MixinEnvironment.Phase envPhase);

    public IMixinConfigurationBuilder refmap(String refmap);

    public IMixinConfigurationBuilder required(boolean required);

    public IMixinConfigurationBuilder mixinPackage(String mixinPackage);

    public IMixinConfigurationBuilder commonMixins(String... mixinClasses);

    public IMixinConfigurationBuilder clientMixins(String... mixinClasses);

    public IMixinConfigurationBuilder serverMixins(String... mixinClasses);

    public IMixinConfigurationBuilder priority(int priority);

    public IMixinConfigurationBuilder mixinPriority(int priority);

    public IMixinConfigurationBuilder setSourceFile(boolean setOrNot);

    public IMixinConfigurationBuilder setConfigurationPlugin(String pluginClass);

    public IMixinConfigurationBuilder configurationType(ConfigurationType type);

    public IMixinConfiguration build();

}