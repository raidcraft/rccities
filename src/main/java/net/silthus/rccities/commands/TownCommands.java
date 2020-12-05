package net.silthus.rccities.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.google.common.base.Strings;
import com.sk89q.minecraft.util.commands.CommandException;
import net.milkbowl.vault.economy.Economy;
import net.silthus.rccities.CityPermissions;
import net.silthus.rccities.DatabasePlot;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.flags.CityFlag;
import net.silthus.rccities.api.flags.FlagInformation;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.api.request.UpgradeRequest;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.api.resident.RolePermission;
import net.silthus.rccities.flags.city.GreetingsCityFlag;
import net.silthus.rccities.flags.city.JoinCostsCityFlag;
import net.silthus.rccities.flags.city.PvpCityFlag;
import net.silthus.rccities.flags.city.admin.InviteCityFlag;
import net.silthus.rccities.upgrades.api.level.UpgradeLevel;
import net.silthus.rccities.upgrades.api.unlockresult.UnlockResult;
import net.silthus.rccities.upgrades.api.upgrade.Upgrade;
import net.silthus.rccities.util.CaseInsensitiveMap;
import net.silthus.rccities.util.QueuedCaptchaCommand;
import net.silthus.rccities.util.QueuedCommand;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
@CommandAlias("town|stadt|rccities|towns|city|gilde")
public class TownCommands extends BaseCommand {

    private final RCCitiesPlugin plugin;
    private final Map<String, City> invites = new CaseInsensitiveMap<>();

    public TownCommands(RCCitiesPlugin plugin) {

        this.plugin = plugin;
    }

    @Default
    @Subcommand("info")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.info")
    public void town(Player player, City city) {

        plugin.getCityManager().printCityInfo(city, player);
    }

    @Subcommand("reload")
    @CommandPermission(CityPermissions.GROUP_ADMIN + ".town.reload")
    public void reload() {

        plugin.reload();


        plugin.getCityManager().clearCache();
        plugin.getPlotManager().clearCache();
        plugin.getResidentManager().reload();
        plugin.getFlagManager().clearCache();

        for (City city : plugin.getCityManager().getCities()) {
            for (Resident resident : plugin.getResidentManager().getResidents(city)) {
                plugin.getResidentManager().addPrefixSkill(resident);
            }
        }

        getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "RCCities wurde neugeladen und alle Caches geleert!");
    }

    @Subcommand("update")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_ADMIN + ".town.update")
    public void update(City city) {

        for (Plot plot : plugin.getPlotManager().getPlots(city)) {
            plot.updateRegion(false);
        }
    getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "Die Plots der Stadt '"
            + city.getFriendlyName() + "' wurden aktualisiert!");
    }

    @Subcommand("create")
    @CommandPermission(CityPermissions.GROUP_ADMIN + ".town.create")
    public void create(Player player, String cityName) {

        // Check if there is already an plot
        if(plugin.getPlotManager().getPlot(player.getLocation().getChunk()) != null) {
            throw new ConditionFailedException("An dieser Stelle befindet sich bereits eine Stadt Plot!");
        }

        // check if here is a wrong region
        if (!plugin.getWorldGuardManager().claimable(player.getLocation())) {
            throw new ConditionFailedException("An dieser Stelle befindet sich bereits eine andere Region!");
        }

        City city;
        try {
            city = plugin.getCityManager().createCity(cityName, player.getLocation(), player.getUniqueId());

            // default flags

            // create initial plot
            Plot plot = new DatabasePlot(player.getLocation(), city);

            // create schematic
            try {
                plugin.getSchematicManager().createSchematic(plot);
            } catch (RaidCraftException e) {
                throw new InvalidCommandArgument(e.getMessage());
            }

            // set flags at the end because of possible errors
            plugin.getFlagManager().setCityFlag(city, player, PvpCityFlag.class, false);       // disable pvp
            plugin.getFlagManager().setCityFlag(city, player, InviteCityFlag.class, false);    // disable invites
            plugin.getFlagManager().setCityFlag(city, player, GreetingsCityFlag.class, true);  // enable greetings
            plugin.getFlagManager().setCityFlag(city, player, JoinCostsCityFlag.class,
                    plugin.getPluginConfig().getJoinCosts());   // default join costs

        } catch (RaidCraftException e) {
            throw new InvalidCommandArgument(e.getMessage());
        }
        Bukkit.broadcastMessage(ChatColor.GOLD + "Es wurde die Stadt '" + city.getFriendlyName() + "' gegründet!");
    }

    @Subcommand("delete")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_ADMIN + ".town.delete")
    public void delete(Player player, City city, CommandFlag flags) {

        boolean restoreSchematics = false;
        if (flags.hasFlag('r')) {
            restoreSchematics = true;
        }

        try {
            if (restoreSchematics) {
                player.sendMessage(ChatColor.YELLOW + "Bei der Löschung der Stadt werden vorhandene Plots "
                        + ChatColor.DARK_RED + "zurückgesetzt" + ChatColor.YELLOW + "!");
            } else {
                player.sendMessage(ChatColor.YELLOW + "Bei der Löschung der Stadt werden vorhandene Plots "
                        + ChatColor.DARK_RED + "NICHT zurückgesetzt" + ChatColor.YELLOW + "!");
            }
            new QueuedCaptchaCommand(player, this, "deleteCity", player, city, restoreSchematics);
        } catch (NoSuchMethodException e) {
            throw new InvalidCommandArgument(e.getMessage());
        }
    }

    @Subcommand("upgrade|levelup")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_ADMIN + ".town.upgrade")
    public void upgrades(Player player, City city, @Optional String action) {

        List<UpgradeRequest> upgradeRequests = plugin.getUpgradeRequestManager().getOpenRequests(city);
        if (upgradeRequests.size() == 0) {
            throw new InvalidCommandArgument("Für diese Stadt liegen keine Upgrade-Anträge vor!");
        }
        // only process first entry
        UpgradeRequest upgradeRequest = upgradeRequests.get(0);

        if (!Strings.isNullOrEmpty(action)) {
            if (action.startsWith("accept")) {
                upgradeRequest.accept();
                UnlockResult unlockResult = upgradeRequest.getUpgradeLevel().tryToUnlock(city);
                if (unlockResult.isSuccessful()) {
                    player.sendMessage(ChatColor.GREEN + " Du hast den Upgrade-Antrag von '"
                            + city.getFriendlyName() + "' angenommen!");
                    upgradeRequest.getUpgradeLevel().setUnlocked(true);
                } else {
                    player.sendMessage(ChatColor.GREEN
                            + " Das Upgrade ist fehlgeschlagen da andere Bedingungen nicht mehr erfüllt sind!");
                }
                return;
            }
            if (action.startsWith("reject")) {
                String reason = action.substring("reject".length()).trim();
                if (Strings.isNullOrEmpty(reason)) {
                    throw new InvalidCommandArgument("Gib bitte noch einen Grund als weiteren Parameter an!");
                }
                upgradeRequest.reject(reason);
                player.sendMessage(ChatColor.GREEN + " Du hast den Upgrade-Antrag von '" + city.getFriendlyName()
                        + "' " + ChatColor.RED + "abgelehnt" + ChatColor.GREEN + "!");
                plugin.getResidentManager()
                        .broadcastCityMessage(city, "Der Upgrade-Antrag wurde abgelehnt, Grund: " + reason);
                return;
            }
            throw new InvalidCommandArgument("Nutze <accept> oder <reject> umd Anträge zu bearbeiten!");
        }

        // show info
        player.sendMessage(ChatColor.GREEN + "Die Stadt '" + city.getFriendlyName() + "' hat das Upgrade '"
                + ChatColor.YELLOW + upgradeRequest.getUpgradeLevel().getName() + ChatColor.GREEN + "' beantragt:");
        player.sendMessage(ChatColor.GREEN + "Info: " + ChatColor.GRAY + upgradeRequest.getInfo());
        if (upgradeRequest.getRejectReason() != null) {
            player.sendMessage(ChatColor.RED + "Achtung: Der letzte Antrag wurde abgelehnt.");
            player.sendMessage(ChatColor.GREEN + "Grund: " + ChatColor.GRAY + upgradeRequest.getRejectReason());
        }
        player.sendMessage(ChatColor.GREEN + "-->" + ChatColor.YELLOW + "/town upgrades "
                + city.getName() + " <accept/reject>");
    }

    @Subcommand("spawn|tp|warp")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.spawn")
    public void spawn(Player player, City city) {

        CommandHelper.checkRolePermissions(player, city, RolePermission.SPAWN_TELEPORT);

        if (!city.getSpawn().getWorld().equals(player.getWorld())) {
            throw new InvalidCommandArgument("Du befindest dich auf der falschen Welt!");
        }

        player.teleport(city.getSpawn());
        player.sendMessage(ChatColor.YELLOW + "Du wurdest zum Stadt-Spawn von teleportiert!");
    }

    @Subcommand("setspwan")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.setspawn")
    public void setSpawn(Player player, City city) {

        CommandHelper.checkRolePermissions(player, city, RolePermission.SET_SPAWN);

        if (!city.getSpawn().getWorld().equals(player.getWorld())) {
            throw new InvalidCommandArgument("Der Spawn muss sich auf der selben Welt wie die Stadt befinden!");
        }

        city.setSpawn(player.getLocation());
        plugin.getResidentManager().broadcastCityMessage(city, "Der Spawn wurde versetzt!");
    }

    @Subcommand("setdescription|setdesc")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.setdesc")
    public void setDescription(Player player, City city, String description) {

        CommandHelper.checkRolePermissions(player, city, RolePermission.SET_DESCRIPTION);

        city.setDescription(description);
        player.sendMessage(ChatColor.GREEN + "Du hast die Beschreibung der Stadt geändert!");
        plugin.getResidentManager().broadcastCityMessage(city, "Die Beschreibung der Stadt wurde geändert!");
    }


    @Subcommand("list")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.list")
    public void list() {

        Collection<City> cities = plugin.getCityManager().getCities();
        getCurrentCommandIssuer().sendMessage(ChatColor.GOLD + "Es gibt derzeit "
                + ChatColor.YELLOW + cities.size() + ChatColor.GOLD + " Städte auf dem Server:");
        String cityList = "";
        for (City city : cities) {
            if (!cityList.isEmpty()) cityList += ChatColor.GOLD + ", ";
            Upgrade mainUpgrade = plugin.getCityManager().getMainUpgrade(city);
            int level = 0;
            if(mainUpgrade != null) {
                UpgradeLevel upgradeLevel = mainUpgrade.getHighestUnlockedLevel();
                if (upgradeLevel != null) {
                    level = upgradeLevel.getLevel();
                }
            }
            cityList += ChatColor.YELLOW + city.getFriendlyName() + " (" + level + ")";
        }
        getCurrentCommandIssuer().sendMessage(cityList);
    }

    @Subcommand("flag")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.flag")
    public void flag(Player player, City city, @Optional String flagName, @Optional String flagValue) {

        if(Strings.isNullOrEmpty(flagName) && Strings.isNullOrEmpty(flagValue)) {
            String flagList = "";
            for(FlagInformation info : plugin.getFlagManager().getRegisteredCityFlagInformationList()) {
                flagList += info.name() + ", ";
            }
            throw new InvalidCommandArgument("Verfügbare Flags: " + flagList);
        }

        CommandHelper.checkRolePermissions(player, city, RolePermission.CITY_FLAG_MODIFICATION);

        try {
            city.setFlag(player, flagName, flagValue);
        } catch (RaidCraftException e) {
            throw new InvalidCommandArgument(e.getMessage());
        }
        player.sendMessage(ChatColor.GREEN + "Du hast erfolgreich die Flag '"
                + ChatColor.YELLOW + flagName.toUpperCase()
                + ChatColor.GREEN + "' auf den Wert '" + ChatColor.YELLOW
                + flagValue.toUpperCase() + ChatColor.GREEN + "' gesetzt!");
    }

    @Subcommand("invite")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.invite")
    public void invite(Player player, City city, OfflinePlayer targetPlayer) {

        if(!targetPlayer.isOnline()) {
            throw new InvalidCommandArgument("Der gewählte Spieler muss online sein!");
        }

        CommandHelper.checkRolePermissions(player, city, RolePermission.INVITE);

        if (player.getName().equalsIgnoreCase(targetPlayer.getName())) {
            throw new InvalidCommandArgument("Du kannst dich nicht selbst in die Stadt einladen!");
        }

        // invite is locked
        CityFlag inviteFlag = plugin.getFlagManager().getCityFlag(city, InviteCityFlag.class);
        if (inviteFlag != null && !inviteFlag.getType().convertToBoolean(inviteFlag.getValue())) {
            throw new InvalidCommandArgument("Deine Stadt darf zurzeit keine neuen Spieler einladen!");
        }

        invites.put(targetPlayer.getName(), city);
        targetPlayer.getPlayer().sendMessage(ChatColor.GOLD + "Du wurdest in die Stadt '"
                + city.getFriendlyName() + "' eingeladen!");
        targetPlayer.getPlayer().sendMessage(ChatColor.GOLD + "Bestätige die Einladung mit '/town accept'");
        player.sendMessage(ChatColor.GREEN + "Du hast " + targetPlayer.getName() + " in die Stadt '"
                + city.getFriendlyName() + "' eingeladen!");
    }

    @Subcommand("accept")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.accept")
    public void accept(Player player) {

        if (!invites.containsKey(player.getName())) {
            throw new InvalidCommandArgument("Du hast keine offenen Einladungen!");
        }

        City city = invites.get(player.getName());
        try {
            plugin.getResidentManager().addResident(city, player);
        } catch (RaidCraftException e) {
            throw new InvalidCommandArgument(e.getMessage());
        }
        Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " ist nun Einwohner von '"
                + city.getFriendlyName() + "'!");
    }

    @Subcommand("leave")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.leave")
    public void leave(Player player, City city, CommandFlag flags) {

        CommandHelper.checkRolePermissions(player, city, RolePermission.LEAVE);

        Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
        if (resident == null) {
            throw new InvalidCommandArgument("Du bist kein Mitglied der Stadt '" + city.getFriendlyName() + "'!");
        }

        if (flags.hasFlag('f')) {
            leaveCity(resident);
        } else {
            try {
                new QueuedCaptchaCommand(player, this, "leaveCity", resident);
            } catch (NoSuchMethodException e) {
                throw new InvalidCommandArgument(e.getMessage());
            }
        }
    }

    @Subcommand("kick")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.kick")
    public void kick(Player player, City city, OfflinePlayer targetPlayer, CommandFlag flags) {

        CommandHelper.checkRolePermissions(player, city, RolePermission.KICK);

        if (!flags.hasAdminFlag(player,'f') && player.getName().equalsIgnoreCase(targetPlayer.getName())) {
            throw new InvalidCommandArgument("Du kannst dich nicht selbst aus der Stadt werfen!");
        }

        Resident resident = plugin.getResidentManager().getResident(targetPlayer.getUniqueId(), city);
        if (resident == null) {
            throw new InvalidCommandArgument(targetPlayer.getName() + " ist kein Mitglied von '"
                    + city.getFriendlyName() + "'!");
        }

        if (!resident.getRole().hasPermission(RolePermission.GET_KICKED)
                && !player.hasMetadata("rccities.town.kick.all")) {
            throw new InvalidCommandArgument("Du kannst diesen Einwohner nicht aus der Stadt werfen!");
        }

        resident.delete();
        Bukkit.broadcastMessage(ChatColor.GOLD + targetPlayer.getName() + " wurde aus der Stadt '"
                + city.getFriendlyName() + "' geworfen!");
    }

    @Subcommand("confirm")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.confirm")
    public void confirm(Player player, @Optional String captcha) {
        if (!plugin.getQueuedCommands().containsKey(player.getName())) {
            throw new InvalidCommandArgument("Es gibt nichts was du aktuell bestätigen kannst!");
        }
        QueuedCommand command = plugin.getQueuedCommands().get(player.getName());
        if (command instanceof QueuedCaptchaCommand) {
            if (Strings.isNullOrEmpty(captcha)) {
                throw new InvalidCommandArgument("Captcha vergessen! /rcconfirm <Captcha>");
            }
            if (!((QueuedCaptchaCommand) command).getCaptcha().equals(captcha)) {
                throw new InvalidCommandArgument("Falscher Captcha Code! Bitte versuche es erneut.");
            }
        }
        command.run();
        plugin.getQueuedCommands().remove(player.getName());
    }

    @Subcommand("deposit")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.deposit")
    public void deposit(Player player, City city, double amount) {

        Economy economy = plugin.getEconomy();

        if(!economy.has(player, amount)) {
            throw new ConditionFailedException("Du hast nicht genügend Geld auf dem Konto");
        }

        Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
        if(resident == null) {
            throw new InvalidCommandArgument("Nur Einwohner können Geld in die Stadtkasse einzahlen!");
        }

        CommandHelper.checkRolePermissions(player, city, RolePermission.DEPOSIT);

        resident.depositCity(amount);

        plugin.getResidentManager().broadcastCityMessage(city, ChatColor.GOLD
                + player.getName() + " hat " + ChatColor.DARK_GREEN + economy.format(amount)
                + ChatColor.GOLD + " in die Stadtkasse eingezahlt!");
    }

    @Subcommand("withdraw")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.withdraw")
    public void withdraw(Player player, City city, double amount) {

        Economy economy = plugin.getEconomy();

        if(!city.hasMoney(amount)) {
            throw new ConditionFailedException("Es ist nicht genug Geld in der Stadtkasse!");
        }

        Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
        if(resident == null) {
            throw new InvalidCommandArgument("Nur Einwohner können Geld aus der Stadtkasse nehmen!");
        }

        CommandHelper.checkRolePermissions(player, city, RolePermission.WITHDRAW);

        resident.withdrawCity(amount);

        plugin.getResidentManager().broadcastCityMessage(city, ChatColor.GOLD
                + player.getName() + " hat " + ChatColor.RED + economy.format(amount)
                + ChatColor.GOLD + " aus der Stadtkasse genommen!");
    }

    /*
     *******************************************************************************************************************
     */

    public void deleteCity(CommandSender sender, City city, boolean restoreSchematics) {

        if (restoreSchematics) {
            try {
                plugin.getSchematicManager().restoreCity(city);
            } catch (RaidCraftException e) {
                sender.sendMessage(ChatColor.RED + "Es ist ein Fehler beim wiederherstellen der Plots aufgetreten! ("
                        + e.getMessage() + ")");
            }
        }

        city.delete();
        Bukkit.broadcastMessage(ChatColor.GOLD + "Die Stadt '" + city.getFriendlyName() + "' wurde gelöscht!");
    }

    public void leaveCity(Resident resident) {

        resident.delete();
        Bukkit.broadcastMessage(ChatColor.GOLD + resident.getName() + " hat die Stadt '"
                + resident.getCity().getFriendlyName() + "' verlassen!");
    }
}
