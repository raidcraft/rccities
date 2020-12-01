package net.silthus.rccities.requirements;

import net.silthus.rccities.DatabaseUpgradeRequest;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.request.UpgradeRequest;
import net.silthus.rccities.upgrades.api.level.UpgradeLevel;
import net.silthus.rccities.upgrades.api.requirement.AbstractRequirement;
import net.silthus.rccities.upgrades.api.requirement.RequirementInformation;
import net.silthus.rccities.upgrades.api.upgrade.Upgrade;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

@RequirementInformation(
        value = "city.staff",
        desc = "Staff must approve."
)
public class CityStaffRequirement extends AbstractRequirement<City> {

    protected CityStaffRequirement() {
        super();
    }

    public boolean test(City city) {

        RCCitiesPlugin plugin = RCCitiesPlugin.getPlugin();
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
    public String getDescription(City entity) {
        return "Ein Teammitglied muss das bestätigen";
    }

    @Override
    public String getReason(City entity) {

        return "Ein Teammitglied wird sich in kürze darum kümmern!";
    }
}
