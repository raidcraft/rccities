package net.silthus.rccities.listener;

import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.upgrades.api.upgrade.Upgrade;
import net.silthus.rccities.upgrades.events.UpgradeUnlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Philip Urban
 */
public class UpgradeListener implements Listener {

    private final RCCitiesPlugin plugin;

    public UpgradeListener(RCCitiesPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onUnlockUpgrade(UpgradeUnlockEvent event) {

        if (!(event.getObject() instanceof City)) return;

        City city = (City) event.getObject();
        Upgrade mainUpgrade = plugin.getCityManager().getMainUpgrade(city);
        if (mainUpgrade.getLevel(event.getUpgradeLevel().getId()) == null) return;
        int maxLevel = event.getUpgradeLevel().getLevel();
        Bukkit.broadcastMessage(ChatColor.GOLD + "Die Stadt '" + city.getFriendlyName() + "' ist auf Level " + ChatColor.RED + maxLevel + ChatColor.GOLD + " aufgestiegen!");
    }
}
