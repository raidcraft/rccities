package net.silthus.rccities.upgrades.api.requirement;

import org.bukkit.configuration.ConfigurationSection;

public abstract class AbstractRequirement<T> implements Requirement<T> {

    protected ConfigurationSection config;

    protected AbstractRequirement() {

    }

    public final void load(ConfigurationSection config) {
        this.config = config;
    }
}
