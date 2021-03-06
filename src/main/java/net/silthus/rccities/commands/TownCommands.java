package net.silthus.rccities.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.*;
import com.google.common.base.Strings;
import de.raidcraft.economy.wrapper.Economy;
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
import net.silthus.rccities.flags.city.admin.IgnoreRadiusCityFlag;
import net.silthus.rccities.flags.city.admin.InviteCityFlag;
import net.silthus.rccities.flags.plot.MarkPlotBaseFlag;
import net.silthus.rccities.manager.FlagManager;
import net.silthus.rccities.upgrades.api.level.UpgradeLevel;
import net.silthus.rccities.upgrades.api.unlockresult.UnlockResult;
import net.silthus.rccities.upgrades.api.upgrade.Upgrade;
import net.silthus.rccities.util.CaseInsensitiveMap;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Philip Urban
 */
@CommandAlias("town|stadt|rccities|towns|city|gilde")
public class TownCommands extends BaseCommand {

    private final RCCitiesPlugin plugin;
    private final Map<String, City> invites = new CaseInsensitiveMap<>();
    private final Map<UUID, Long> lastTeleport = new HashMap<>();
    private final Map<UUID, Long> lastForeignTeleport = new HashMap<>();

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

        plugin.getDynmapManager().addCityMarker(city);

        for (Plot plot : plugin.getPlotManager().getPlots(city)) {
            plot.updateRegion(false);
            plugin.getDynmapManager().addPlotAreaMarker(plot);
        }

        plugin.getPlotManager().migrateAllPlots(city);

    getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "Die Plots der Stadt '"
            + city.getFriendlyName() + "' wurden aktualisiert!");
    }

    @Subcommand("create")
    @CommandPermission(CityPermissions.GROUP_ADMIN + ".town.create")
    public void create(Player player, String cityName) {

        // Check if there is already an plot
        if(plugin.getPlotManager().getPlot(player.getLocation()) != null) {
            throw new ConditionFailedException("Hier befindet sich bereits eine Stadt Plot!");
        }

        // check if here is a wrong region
        if (!plugin.getWorldGuardManager().claimable(cityName, player.getLocation())) {
            throw new ConditionFailedException("Hier befindet sich bereits eine andere Region!");
        }

        City city;
        try {
            city = plugin.getCityManager().createCity(cityName, player.getLocation(), player.getUniqueId());

            // Migrate old plot otherwise create new
            if (!plugin.getPlotManager().migrateOldPlot(city, player.getLocation())) {

                // create initial plot
                Plot plot = new DatabasePlot(player.getLocation(), city);

                // Mark plot
                try {
                    plot.setFlag(MarkPlotBaseFlag.class, true);
                } catch (RaidCraftException e) {
                    RCCitiesPlugin.instance().getLogger().warning(e.getMessage());
                }
            }

            // set flags at the end because of possible errors
            FlagManager flagManager = plugin.getFlagManager();
            flagManager.setCityFlag(city, player, PvpCityFlag.class, false);
            flagManager.setCityFlag(city, player, InviteCityFlag.class, true);
            flagManager.setCityFlag(city, player, IgnoreRadiusCityFlag.class, false);
            flagManager.setCityFlag(city, player, GreetingsCityFlag.class, true);
            flagManager.setCityFlag(city, player, JoinCostsCityFlag.class, plugin.getPluginConfig().getJoinCosts());

        } catch (RaidCraftException e) {
            throw new InvalidCommandArgument(e.getMessage());
        }

        plugin.getPlotManager().migrateAllPlots(city);

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
            throw new ConditionFailedException("Für diese Stadt liegen keine Upgrade-Anträge vor!");
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
                    throw new ConditionFailedException("Gib bitte noch einen Grund als weiteren Parameter an!");
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
                + city.getFriendlyName() + " <accept/reject>");
    }

    @Subcommand("spawn|tp|warp")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.spawn")
    public void spawn(Player player, City city) {

        boolean isResident = RCCitiesPlugin.instance().getResidentManager().isResident(player.getUniqueId(), city);

        if(isResident) {
            CommandHelper.checkRolePermissions(player, city, RolePermission.SPAWN_TELEPORT);
        }

        if (!city.getSpawn().getWorld().equals(player.getWorld())) {
            throw new ConditionFailedException("Du befindest dich auf der falschen Welt!");
        }

        double warmupTime = plugin.getPluginConfig().getSpawnTeleportWarmup();
        double cooldownTime = plugin.getPluginConfig().getSpawnTeleportCooldown();

        if(!isResident) {
            cooldownTime = plugin.getPluginConfig().getForeignSpawnTeleportCooldown();
        }

        if(player.hasPermission(CityPermissions.GROUP_ADMIN)) {
            warmupTime = 0.5;
            cooldownTime = 0;
        }

        Map<UUID, Long> lastTeleportCache = lastTeleport;
        if(!isResident) {
            lastTeleportCache = lastForeignTeleport;
        }

        if(lastTeleportCache.get(player.getUniqueId()) != null &&
                System.currentTimeMillis() - lastTeleportCache.get(player.getUniqueId()) < cooldownTime * 1000.) {
            double remainingSeconds = (cooldownTime)
                    - ((double)(System.currentTimeMillis() - lastTeleportCache.get(player.getUniqueId())) / 1000.);
            if(!isResident) {
                throw new ConditionFailedException(ChatColor.RED +
                        plugin.getPluginConfig().getForeignSpawnTeleportCooldownMessage());
            } else {
                throw new ConditionFailedException(ChatColor.RED + "Warte noch "
                        + ((double) Math.round(remainingSeconds * 100.) / 100.) + "s bis zum nächsten Teleport.");
            }
        }

        player.sendMessage(ChatColor.YELLOW + "Du wirst in "
                + warmupTime + "s nach " + city.getFriendlyName() + " teleportiert...");
        lastTeleportCache.put(player.getUniqueId(), System.currentTimeMillis());
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                player.teleport(city.getSpawn());
                player.sendMessage(ChatColor.YELLOW + "Willkommen in " + city.getFriendlyName() + "!");
            }
        }, (long)(warmupTime * 20.));
    }

    @Subcommand("setspawn")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_ADMIN + ".town.setspawn")
    public void setSpawn(Player player, City city) {

        CommandHelper.checkRolePermissions(player, city, RolePermission.SET_SPAWN);

        if(plugin.getPlotManager().getPlot(player.getLocation()) == null) {
            throw new ConditionFailedException("Der Spawn muss sich innerhalb der Stadt befinden!");
        }

        city.setSpawn(player.getLocation());
        plugin.getDynmapManager().addCityMarker(city);
        player.sendMessage(ChatColor.GREEN + "Du hast den Stadt Spawn versetzt!");
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
    @CommandCompletion("@cities @players")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.invite")
    public void invite(Player player, City city, OfflinePlayer targetPlayer) {

        if(!targetPlayer.isOnline()) {
            throw new ConditionFailedException("Der gewählte Spieler muss online sein!");
        }

        CommandHelper.checkRolePermissions(player, city, RolePermission.INVITE);

        if (player.getName().equalsIgnoreCase(targetPlayer.getName())) {
            throw new ConditionFailedException("Du kannst dich nicht selbst in die Stadt einladen!");
        }

        // invite is locked
        CityFlag inviteFlag = plugin.getFlagManager().getCityFlag(city, InviteCityFlag.class);
        if (inviteFlag != null && !inviteFlag.getType().convertToBoolean(inviteFlag.getValue())) {
            throw new ConditionFailedException("Deine Stadt darf zurzeit keine neuen Spieler einladen!");
        }

        Resident resident = plugin.getResidentManager().getResident(targetPlayer.getUniqueId(), city);
        if(resident != null) {
            throw new ConditionFailedException("Der Spieler ist bereits Einwohner dieser Stadt");
        }

        if(plugin.getResidentManager().getCitizenships(targetPlayer.getUniqueId()).size() > 0) {
            throw new ConditionFailedException("Der Spieler ist bereits Einwohner einer anderen Stadt");
        }

        invites.put(targetPlayer.getName(), city);
        targetPlayer.getPlayer().sendMessage(ChatColor.GOLD + "Du wurdest in die Stadt '"
                + city.getFriendlyName() + "' eingeladen!");
        JoinCostsCityFlag flag = (JoinCostsCityFlag)plugin.getFlagManager().getCityFlag(city, JoinCostsCityFlag.class);
        if(flag != null) {
            targetPlayer.getPlayer().sendMessage(ChatColor.GOLD + "Der Beitritt kostet dich " + ChatColor.DARK_RED +
                    + flag.getAmount());
        }
        targetPlayer.getPlayer().sendMessage(ChatColor.GOLD + "Bestätige die Einladung mit '/town accept'");
        player.sendMessage(ChatColor.GREEN + "Du hast " + targetPlayer.getName() + " in die Stadt '"
                + city.getFriendlyName() + "' eingeladen!");
    }

    @Subcommand("accept")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.accept")
    public void accept(Player player) {

        if (!invites.containsKey(player.getName())) {
            throw new ConditionFailedException("Du hast keine offenen Einladungen!");
        }

        if(plugin.getResidentManager().getCitizenships(player.getUniqueId()).size() > 0) {
            throw new ConditionFailedException("Du bist bereits Einwohner einer anderen Stadt");
        }

        City city = invites.get(player.getName());

        JoinCostsCityFlag flag = (JoinCostsCityFlag)plugin.getFlagManager().getCityFlag(city, JoinCostsCityFlag.class);
        if(flag != null) {
            if(!de.raidcraft.economy.wrapper.Economy.get().has(player, flag.getAmount())) {
                throw  new ConditionFailedException("Du benötigst "
                        + de.raidcraft.economy.wrapper.Economy.get().format(flag.getAmount()) + " um der Stadt beizutreten");
            }
        }

        try {
            Resident resident = plugin.getResidentManager().addResident(city, player);
            if(flag != null) {
                resident.depositCity(flag.getAmount());
            }
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
            throw new ConditionFailedException("Du bist kein Mitglied der Stadt '" + city.getFriendlyName() + "'!");
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
    @CommandCompletion("@cities @players")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.kick")
    public void kick(Player player, City city, OfflinePlayer targetPlayer, CommandFlag flags) {

        CommandHelper.checkRolePermissions(player, city, RolePermission.KICK);

        if (!flags.hasAdminFlag(player,'f') && player.getName().equalsIgnoreCase(targetPlayer.getName())) {
            throw new ConditionFailedException("Du kannst dich nicht selbst aus der Stadt werfen!");
        }

        Resident resident = plugin.getResidentManager().getResident(targetPlayer.getUniqueId(), city);
        if (resident == null) {
            throw new ConditionFailedException(targetPlayer.getName() + " ist kein Mitglied von '"
                    + city.getFriendlyName() + "'!");
        }

        if (!resident.getRole().hasPermission(RolePermission.GET_KICKED)
                && !player.hasMetadata("rccities.town.kick.all")) {
            throw new ConditionFailedException("Du kannst diesen Einwohner nicht aus der Stadt werfen!");
        }

        resident.delete();
        Bukkit.broadcastMessage(ChatColor.GOLD + targetPlayer.getName() + " wurde aus der Stadt '"
                + city.getFriendlyName() + "' geworfen!");
    }

    @Subcommand("confirm")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.confirm")
    public void confirm(Player player, @Optional String captcha) {
        if (!plugin.getQueuedCommands().containsKey(player.getName())) {
            throw new ConditionFailedException("Es gibt nichts was du aktuell bestätigen kannst!");
        }
        QueuedCommand command = plugin.getQueuedCommands().get(player.getName());
        if (command instanceof QueuedCaptchaCommand) {
            if (Strings.isNullOrEmpty(captcha)) {
                throw new ConditionFailedException("Captcha vergessen! /rcconfirm <Captcha>");
            }
            if (!((QueuedCaptchaCommand) command).getCaptcha().equals(captcha)) {
                throw new ConditionFailedException("Falscher Captcha Code! Bitte versuche es erneut.");
            }
        }
        command.run();
        plugin.getQueuedCommands().remove(player.getName());
    }

    @Subcommand("deposit")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.deposit")
    public void deposit(Player player, City city, double amount) {

        if(amount < 0) {
            throw new ConditionFailedException("Der Betrag muss größer als 0 sein");
        }

        if(!Economy.get().has(player, amount)) {
            throw new ConditionFailedException("Du hast nicht genügend Geld auf dem Konto");
        }

        Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
        if(resident == null) {
            throw new ConditionFailedException("Nur Einwohner können Geld in die Stadtkasse einzahlen!");
        }

        CommandHelper.checkRolePermissions(player, city, RolePermission.DEPOSIT);

        resident.depositCity(amount);

        plugin.getResidentManager().broadcastCityMessage(city, ChatColor.GOLD
                + player.getName() + " hat " + ChatColor.DARK_GREEN + Economy.get().format(amount)
                + ChatColor.GOLD + " in die Stadtkasse eingezahlt!");
    }

    @Subcommand("withdraw")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_USER + ".town.withdraw")
    public void withdraw(Player player, City city, double amount) {

        if(amount < 0) {
            throw new ConditionFailedException("Der Betrag muss größer als 0 sein");
        }

        if(!city.hasMoney(amount)) {
            throw new ConditionFailedException("Es ist nicht genügend Geld in der Stadtkasse!");
        }

        Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
        if(resident == null) {
            throw new ConditionFailedException("Nur Einwohner können Geld aus der Stadtkasse nehmen!");
        }

        CommandHelper.checkRolePermissions(player, city, RolePermission.WITHDRAW);

        resident.withdrawCity(amount);

        plugin.getResidentManager().broadcastCityMessage(city, ChatColor.GOLD
                + player.getName() + " hat " + ChatColor.RED + Economy.get().format(amount)
                + ChatColor.GOLD + " aus der Stadtkasse genommen!");
    }

    @Subcommand("addplots")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_ADMIN + ".town.addplots")
    public void addPlots(Player player, City city, int amount) {

        if(amount < 0) {
            throw new ConditionFailedException("Die Anzahl der Plots muss größer als 0 sein");
        }

        int plotCredit = city.getPlotCredit() + amount;
        city.setPlotCredit(plotCredit);

        plugin.getResidentManager().broadcastCityMessage(city, ChatColor.GOLD
                + player.getName() + " hat der Stadt " + ChatColor.GREEN + amount
                + ChatColor.GOLD + " Plots hinzugefügt! Die Stadt hat jetzt"
                + ChatColor.GREEN + plotCredit + " Plots.");
    }

    @Subcommand("removeplots")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_ADMIN + ".town.removeplots")
    public void removePlots(Player player, City city, int amount) {

        if(amount < 0) {
            throw new ConditionFailedException("Die Anzahl der Plots muss größer als 0 sein");
        }

        int plotCredit = city.getPlotCredit() - amount;
        if (plotCredit < 0) plotCredit = 0;
        city.setPlotCredit(plotCredit);

        plugin.getResidentManager().broadcastCityMessage(city, ChatColor.GOLD
                + player.getName() + " hat der Stadt " + ChatColor.RED + amount
                + ChatColor.GOLD + " Plots abgezogen! Die Stadt hat jetzt "
                + ChatColor.GREEN + plotCredit + " Plots.");
    }

    @Subcommand("setplots")
    @CommandCompletion("@cities")
    @CommandPermission(CityPermissions.GROUP_ADMIN + ".town.setplots")
    public void setPlots(Player player, City city, int amount) {

        if(amount < 0) {
            throw new ConditionFailedException("Die Anzahl der Plots muss größer als 0 sein");
        }

        city.setPlotCredit(amount);

        plugin.getResidentManager().broadcastCityMessage(city, ChatColor.GOLD
                + player.getName() + " hat die Plots der Stadt auf " + ChatColor.AQUA + amount
                + ChatColor.GOLD + " Plots gesetzt!");
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
