package net.silthus.rccities.manager;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.economy.AccountType;
import de.raidcraft.rccities.DatabaseCity;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.flags.CityFlag;
import de.raidcraft.rccities.api.resident.Resident;
import de.raidcraft.rccities.api.resident.Role;
import de.raidcraft.rccities.flags.city.JoinCostsCityFlag;
import de.raidcraft.rccities.tables.TCity;
import de.raidcraft.rcupgrades.api.level.UpgradeLevel;
import de.raidcraft.rcupgrades.api.upgrade.Upgrade;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * @author Philip Urban
 */
public class CityManager {

    private RCCitiesPlugin plugin;
    private Map<String, City> cachedCities = new CaseInsensitiveMap<>();

    public CityManager(RCCitiesPlugin plugin) {

        this.plugin = plugin;
    }

    public City createCity(String cityName, Location location, UUID creator) throws RaidCraftException {

        cityName = cityName.replace(' ', '_');

        if (cityName.length() > 20) {
            throw new RaidCraftException("Der angegebene Stadtname ist zu lange!");
        }
        City city = getCity(cityName);
        if (city != null) {
            throw new RaidCraftException("Es gibt bereits eine Stadt mit diesem Namen!");
        }
        city = new DatabaseCity(cityName, location, creator);

        cachedCities.put(cityName, city);
        return city;
    }

    public void removeFromCache(City city) {

        cachedCities.remove(city.getName());
    }

    public void printCityInfo(String cityName, CommandSender sender) throws RaidCraftException {

        City city = getCity(cityName);
        if (city == null) {
            throw new RaidCraftException("Es wurde keine Stadt mit diesem namen gefunden!");
        }

        String mayorList = "";
        int mayorCount = 0;
        String residentList = "";
        int residentCount = 0;
        for (Resident resident : plugin.getResidentManager().getResidents(city)) {
            if (resident.getRole() == Role.MAYOR || resident.getRole() == Role.ADMIN) {
                if (!mayorList.isEmpty()) mayorList += ChatColor.GRAY + ", ";
                mayorList += resident.getRole().getChatColor() + resident.getName();
                mayorCount++;
            } else {
                if (!residentList.isEmpty()) residentList += ChatColor.GRAY + ", ";
                residentList += resident.getRole().getChatColor() + resident.getName();
                residentCount++;
            }
        }

        double joinCosts = 0;
        CityFlag joinCostsCityFlag = RaidCraft.getComponent(RCCitiesPlugin.class).getFlagManager().getCityFlag(city, JoinCostsCityFlag.class);
        if (joinCostsCityFlag != null) {
            joinCosts = joinCostsCityFlag.getType().convertToMoney(joinCostsCityFlag.getValue());
        }

        sender.sendMessage("*********************************");
        sender.sendMessage(ChatColor.GOLD + "Informationen zur Gilde '" + ChatColor.YELLOW + city.getFriendlyName() + ChatColor.GOLD + "'");
        sender.sendMessage(ChatColor.GOLD + "Beschreibung: " + ChatColor.YELLOW + city.getDescription());
        sender.sendMessage(ChatColor.GOLD + "Gründungsdatum: " + ChatColor.YELLOW + city.getCreationDate().toString());
        sender.sendMessage(ChatColor.GOLD + "Grösse (Plots): " + ChatColor.YELLOW + city.getSize()
                + ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Grösse (Radius): " + ChatColor.YELLOW + city.getMaxRadius());
        sender.sendMessage(ChatColor.GOLD + "Unclaimed Plots: " + ChatColor.YELLOW + city.getPlotCredit());
        sender.sendMessage(ChatColor.GOLD + "Level: " + ChatColor.YELLOW + getCityLevel(city));
        sender.sendMessage(ChatColor.GOLD + "EXP: " + ChatColor.YELLOW + city.getExp());
        sender.sendMessage(ChatColor.GOLD + "Stadtkasse: " + ChatColor.YELLOW + RaidCraft.getEconomy()
                .getFormattedBalance(AccountType.CITY, city.getBankAccountName()));
        sender.sendMessage(ChatColor.GOLD + "Beitrittskosten: " + ChatColor.YELLOW + RaidCraft.getEconomy().getFormattedAmount(joinCosts));
        sender.sendMessage(ChatColor.GOLD + "Bürgermeister (" + mayorCount + "): " + ChatColor.YELLOW + mayorList);
        sender.sendMessage(ChatColor.GOLD + "Einwohner (" + residentCount + "): " + ChatColor.YELLOW + residentList);
        sender.sendMessage("*********************************");
    }

    public Upgrade getMainUpgrade(City city) {

        return city.getUpgrades().getUpgrade("1");
    }

    public String getCityLevel(City city) {

        UpgradeLevel upgradeLevel = getMainUpgrade(city).getHighestUnlockedLevel();
        if (upgradeLevel == null) {
            return "0";
        }
        return String.valueOf(upgradeLevel.getLevel());
    }

    public double getServerJoinCosts(City city) {

        int multiplier = 0;
        for (UpgradeLevel level : getMainUpgrade(city).getLevels()) {
            if (level.isUnlocked()) multiplier++;
        }

        double baseJoinCosts = RaidCraft.getEconomy().parseCurrencyInput(plugin.getConfig().joinCosts);
        return baseJoinCosts + (baseJoinCosts / 2. * multiplier);
    }

    public City getCity(String name) {

        City city = cachedCities.get(name);

        if (city == null) {
            TCity tCity = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TCity.class).where().ieq("name", name).findUnique();
            if (tCity != null) {
                if (Bukkit.getWorld(tCity.getWorld()) != null) {
                    city = new DatabaseCity(tCity);
                    cachedCities.put(tCity.getName(), city);
                }
            }
        }
        // search for name parts
        if (city == null) {
            getCities();
            for (Map.Entry<String, City> entry : cachedCities.entrySet()) {
                if (entry.getKey().toLowerCase().startsWith(name.toLowerCase())) {
                    city = entry.getValue();
                    break;
                }
            }
        }
        return city;
    }

    public Collection<City> getCities() {

        for (TCity tCity : RaidCraft.getDatabase(RCCitiesPlugin.class).find(TCity.class).findList()) {

            if (!cachedCities.containsKey(tCity.getName())) {
                if (Bukkit.getWorld(tCity.getWorld()) == null) continue;
                cachedCities.put(tCity.getName(), new DatabaseCity(tCity));
            }
        }
        return cachedCities.values();
    }

    public void clearCache() {

        cachedCities.clear();
    }
}
