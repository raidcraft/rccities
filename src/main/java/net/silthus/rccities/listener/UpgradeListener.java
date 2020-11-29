package net.silthus.rccities.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rcupgrades.api.upgrade.Upgrade;
import de.raidcraft.rcupgrades.events.UpgradeUnlockEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Philip Urban
 */
public class UpgradeListener implements Listener {

    @EventHandler
    public void onUnlockUpgrade(UpgradeUnlockEvent event) {

        if (!(event.getObject() instanceof City)) return;

        City city = (City) event.getObject();
        Upgrade mainUpgrade = RaidCraft.getComponent(RCCitiesPlugin.class).getCityManager().getMainUpgrade(city);
        if (mainUpgrade.getLevel(event.getUpgradeLevel().getId()) == null) return;
        int maxLevel = event.getUpgradeLevel().getLevel();
        Bukkit.broadcastMessage(ChatColor.GOLD + "Die Stadt '" + city.getFriendlyName() + "' ist auf Level " + ChatColor.RED + maxLevel + ChatColor.GOLD + " aufgestiegen!");
    }
}
