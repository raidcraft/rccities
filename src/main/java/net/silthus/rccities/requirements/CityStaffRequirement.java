package net.silthus.rccities.requirements;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.ReasonableRequirement;
import de.raidcraft.rccities.DatabaseUpgradeRequest;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.request.UpgradeRequest;
import de.raidcraft.rcupgrades.api.level.UpgradeLevel;
import de.raidcraft.rcupgrades.api.upgrade.Upgrade;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author Silthus
 */
public class CityStaffRequirement implements ReasonableRequirement<City> {

    @Override
    @Information(
            value = "city.staff",
            desc = "Informs staff to accept upgrade request.",
            conf = {"upgrade-id: <ID of parent update>",
                    "upgrade-level-id: <ID of affecting update level>",
                    "info: <Info for players about staff requirement"}
    )
    public boolean test(City city, ConfigurationSection config) {

        RCCitiesPlugin plugin = RaidCraft.getComponent(RCCitiesPlugin.class);
        if(city == null) return false;
        Upgrade upgrade = city.getUpgrades().getUpgrade(config.getString("upgrade-id"));
        if (upgrade == null) return false;
        UpgradeLevel upgradeLevel = upgrade.getLevel(config.getString("upgrade-level-id"));
        if (upgradeLevel == null) return false;
        UpgradeRequest request = plugin.getUpgradeRequestManager().getRequest(city, upgradeLevel);

        // new request
        if (request == null) {
            request = new DatabaseUpgradeRequest(city, upgradeLevel, config.getString("info"));
            request.save();

            for (Player player : Bukkit.getOnlinePlayers()) {

                if (!player.hasPermission("rccities.upgrades.process")) continue;

                player.sendMessage(ChatColor.GRAY + "Die Gilde '" + city.getFriendlyName() + "' hat ein Upgrade Antrag gestellt!");
                player.sendMessage(ChatColor.GRAY + "Upgrade-Level: " + config.getString("upgrade-level-id"));
            }
            return false;
        }

        // check old request
        if (request.isAccepted()) {
            request.delete();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getReason(City entity, ConfigurationSection config) {

        return "Ein Teammitglied wird sich in kürze darum kümmern!";
    }
}
