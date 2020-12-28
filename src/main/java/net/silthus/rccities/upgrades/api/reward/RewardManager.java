package net.silthus.rccities.upgrades.api.reward;

import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.util.CaseInsensitiveMap;
import net.silthus.rccities.util.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class RewardManager {

    private static final Map<String, Class<? extends Reward<?>>> rewardClasses = new CaseInsensitiveMap<>();
    private static final Map<Class<? extends Reward<?>>, Constructor<? extends Reward<?>>> constructors = new HashMap<>();

    public static <O> List<Reward<O>> createRewards(ConfigurationSection config) {

        List<Reward<O>> rewards = new ArrayList<>();
        if (config == null || config.getKeys(false) == null) {
            return rewards;
        }

        for (String rewardType : config.getKeys(false)) {
            ConfigurationSection rewardSection = config.getConfigurationSection(rewardType);

            ConfigurationSection args = rewardSection.isConfigurationSection("args")
                    ? rewardSection.getConfigurationSection("args") : new MemoryConfiguration();
            args.set("name", rewardType);

            Class<? extends Reward<?>> rClass = rewardClasses.get(rewardType);
            if (rClass == null) {
                RCCitiesPlugin.instance().getLogger().warning("There are no reward types defined for the type " + rewardType);
                RCCitiesPlugin.instance().getLogger().warning("Available Reward Types are: " + String.join(", ", new ArrayList<>(rewardClasses.keySet())));
                return rewards;
            }
            try {
                final Reward<O> reward = (Reward<O>) constructors.get(rClass).newInstance(rewardSection);
                if (reward instanceof AbstractReward) {
                    ((AbstractReward) reward).load(args);
                }
                rewards.add(reward);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                RCCitiesPlugin.instance().getLogger().warning(e.getMessage());
                e.printStackTrace();
            }
        }
        return rewards;
    }

    public static <T extends Reward<?>> void registerRewardType(Class<T> rClass) {

        if (!rClass.isAnnotationPresent(RewardInformation.class)) {
            RCCitiesPlugin.instance().getLogger().warning("Cannot register " + rClass.getCanonicalName() + " as Reward because it has no Information tag!");
            return;
        }
        for (Constructor<?> constructor : rClass.getDeclaredConstructors()) {
            if (constructor.getParameterTypes()[0].isAssignableFrom(ConfigurationSection.class)) {
                constructor.setAccessible(true);
                constructors.put(rClass, (Constructor<T>) constructor);
                // get the displayName for aliasing
                String name = StringUtils.formatName(rClass.getAnnotation(RewardInformation.class).value());
                rewardClasses.put(name, rClass);
                //RCCitiesPlugin.instance().getLogger().info("Registered Reward Type: " + name);
                break;
            }
        }
    }
}
