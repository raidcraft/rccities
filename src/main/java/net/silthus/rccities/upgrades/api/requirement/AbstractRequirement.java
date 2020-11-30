package net.silthus.rccities.upgrades.api.requirement;

import net.silthus.rccities.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

public abstract class AbstractRequirement<T> implements Requirement<T> {

    protected ConfigurationSection config;

    protected AbstractRequirement() {

    }

    public void load(ConfigurationSection config) {
        this.config = config;
    }
}
