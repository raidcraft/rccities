package net.silthus.rccities.upgrades;

import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.upgrades.api.requirement.AbstractRequirement;
import net.silthus.rccities.upgrades.api.requirement.Requirement;
import net.silthus.rccities.upgrades.api.requirement.RequirementInformation;
import net.silthus.rccities.util.CaseInsensitiveMap;
import net.silthus.rccities.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequirementManager {

    private static final Map<String, Class<? extends Requirement<?>>> requirementClasses = new CaseInsensitiveMap<>();
    private static final Map<Class<? extends Requirement<?>>, Constructor<? extends Requirement<?>>> constructors = new HashMap<>();

    private RequirementManager() {

    }

    @SuppressWarnings("unchecked")
    public static <O> List<Requirement<O>> createRequirements(ConfigurationSection config) {

        List<Requirement<O>> requirements = new ArrayList<>();
        if (config == null) {
            return requirements;
        }

        /*
         * requirements:
         *      some.requirement.key:
         *          args:
         *              info: Some description
         *              req-specific-arg: value
         */

        for (String reqName : config.getKeys(false)) {
            //            key = StringUtils.formatName(key);
            if (requirementClasses.containsKey(reqName)) {

                Class<? extends Requirement<?>> rClass = requirementClasses.get(reqName);
                    try {
                        final ConfigurationSection section = config.getConfigurationSection(reqName);
                        if (section == null) {
                            RCCitiesPlugin.getPlugin().getLogger().warning("Requirement '" + reqName + "' must be a section");
                            continue;
                        }
                        ConfigurationSection args = section.getConfigurationSection("args");
                        if(args == null) {
                            RCCitiesPlugin.getPlugin().getLogger().warning("No arguments for '" + reqName + "' found");
                            continue;
                        }
                        final Requirement<O> requirement = (Requirement<O>) constructors.get(rClass).newInstance();
                        if (requirement instanceof AbstractRequirement) {
                            // load the config one tick later to allow all processing to take place
                            // this helps to avoid stack overflow errors when a skill requires itself
                            Bukkit.getScheduler().runTaskLater(RCCitiesPlugin.getPlugin(),
                                    () -> ((AbstractRequirement) requirement).load(args), 1L);
                        }
                        requirements.add(requirement);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        RCCitiesPlugin.getPlugin().getLogger().warning(e.getMessage());
                        e.printStackTrace();
                    }
            } else {
                RCCitiesPlugin.getPlugin().getLogger().warning("There are no requirement types defined for the type " + reqName);
                RCCitiesPlugin.getPlugin().getLogger().warning("Available Requirement Types are: " + String.join(", ", new ArrayList<>(requirementClasses.keySet())));
            }
        }
        return requirements;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Requirement<?>> void registerRequirementType(Class<T> rClass) {

        if (!rClass.isAnnotationPresent(RequirementInformation.class)) {
            RCCitiesPlugin.getPlugin().getLogger().warning("Cannot register " + rClass.getCanonicalName() + " as Requirement because it has no Information tag!");
            return;
        }
        for (Constructor<?> constructor : rClass.getDeclaredConstructors()) {
            constructor.setAccessible(true);
            constructors.put(rClass, (Constructor<T>) constructor);
            // get the displayName for aliasing
            String name = StringUtils.formatName(rClass.getAnnotation(RequirementInformation.class).value());
            requirementClasses.put(name, rClass);
            //RCCitiesPlugin.getPlugin().getLogger().info("Registered Requirement Type: " + name);
            break;
        }
    }
}
