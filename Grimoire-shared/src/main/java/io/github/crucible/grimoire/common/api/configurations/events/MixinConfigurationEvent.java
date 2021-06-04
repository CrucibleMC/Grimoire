package io.github.crucible.grimoire.common.api.configurations.events;


import io.github.crucible.grimoire.common.api.configurations.IMixinConfiguration;
import io.github.crucible.grimoire.common.api.events.CancelableEvent;
import io.github.crucible.grimoire.common.api.grimmix.IGrimmix;

import java.util.Optional;

public class MixinConfigurationEvent extends CancelableEvent {
    public final IMixinConfiguration configuration;
    public final Optional<IGrimmix> owner;

    public MixinConfigurationEvent(IGrimmix owner, IMixinConfiguration configuration) {
        this.configuration = configuration;
        this.owner = owner != null ? Optional.of(owner) : Optional.empty();
    }

}