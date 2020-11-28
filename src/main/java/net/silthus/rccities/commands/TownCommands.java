package net.silthus.rccities.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.commands.QueuedCaptchaCommand;
import de.raidcraft.rccities.DatabasePlot;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.flags.CityFlag;
import de.raidcraft.rccities.api.flags.FlagInformation;
import de.raidcraft.rccities.api.plot.Plot;
import de.raidcraft.rccities.api.request.UpgradeRequest;
import de.raidcraft.rccities.api.resident.Resident;
import de.raidcraft.rccities.api.resident.RolePermission;
import de.raidcraft.rccities.flags.city.GreetingsCityFlag;
import de.raidcraft.rccities.flags.city.JoinCostsCityFlag;
import de.raidcraft.rccities.flags.city.PvpCityFlag;
import de.raidcraft.rccities.flags.city.admin.InviteCityFlag;
import de.raidcraft.rcupgrades.api.level.UpgradeLevel;
import de.raidcraft.rcupgrades.api.unlockresult.UnlockResult;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.CommandUtil;
import de.raidcraft.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class TownCommands {

    private RCCitiesPlugin plugin;

    public TownCommands(RCCitiesPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"gilde", "guild", "rccities", "town", "towns", "city"},
            desc = "Town commands"
    )
    @NestedCommand(value = NestedCommands.class, executeBody = true)
    public void town(CommandContext args, CommandSender sender) throws CommandException {

        if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
        Player player = (Player) sender;

        List<Resident> citizenships = plugin.getResidentManager().getCitizenships(player.getUniqueId());
        if (citizenships == null || citizenships.size() > 1) {
            throw new CommandException("Mehrere Gilden gefunden: Nutze /gilde info <Gildenname>!");
        }
        try {
            plugin.getCityManager().printCityInfo(citizenships.get(0).getCity().getName(), sender);
        } catch (RaidCraftException e) {
            throw new CommandException(e.getMessage());
        }
    }

    public static class NestedCommands {

        private final RCCitiesPlugin plugin;
        private Map<String, City> invites = new CaseInsensitiveMap<>();

        public NestedCommands(RCCitiesPlugin plugin) {

            this.plugin = plugin;
        }

        @Command(
                aliases = {"reload"},
                desc = "Reloads the plugin"
        )
        @CommandPermissions("rccities.town.reload")
        public void reload(CommandContext args, CommandSender sender) {

            plugin.reload();


            plugin.getCityManager().clearCache();
            plugin.getPlotManager().clearCache();
            plugin.getResidentManager().reload();
            plugin.getFlagManager().clearCache();

            for (City city : plugin.getCityManager().getCities()) {
                plugin.getDynmapManager().addCityMarker(city);
                for (Resident resident : plugin.getResidentManager().getResidents(city)) {
                    plugin.getResidentManager().addPrefixSkill(resident);
                }
            }

            sender.sendMessage(ChatColor.GREEN + "RCCities wurde neugeladen und alle Caches geleert!");
        }

        @Command(
                aliases = {"update"},
                desc = "Repairs town plots",
                usage = "[Gilde]",
                min = 1
        )
        @CommandPermissions("rccities.town.update")
        public void update(CommandContext args, CommandSender sender) throws CommandException {

            City city = plugin.getCityManager().getCity(args.getString(0));
            if (city == null) {
                throw new CommandException("Es wurde keine Gilde mit diesem namen gefunden!");
            }

            for (Plot plot : plugin.getPlotManager().getPlots(city)) {
                plot.updateRegion(false);
            }
            sender.sendMessage(ChatColor.GREEN + "Die Plots der Gilde '" + city.getFriendlyName() + "' wurden aktualisiert!");
        }

        @Command(
                aliases = {"create"},
                desc = "Create a new city",
                min = 1,
                usage = "<Gilde>"
        )
        @CommandPermissions("rccities.town.create")
        public void create(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
            Player player = (Player) sender;

            City city;
            try {
                city = plugin.getCityManager().createCity(args.getJoinedStrings(0), player.getLocation(), player.getUniqueId());

                // default flags

                // create initial plot
                Plot plot = new DatabasePlot(player.getLocation(), city);

                // create schematic
                try {
                    plugin.getSchematicManager().createSchematic(plot);
                } catch (RaidCraftException e) {
                    throw new CommandException(e.getMessage());
                }

                // set flags at the end because of possible errors
                plugin.getFlagManager().setCityFlag(city, player, PvpCityFlag.class, false);        // disable pvp
                plugin.getFlagManager().setCityFlag(city, player, InviteCityFlag.class, false);     // disable invites
                plugin.getFlagManager().setCityFlag(city, player, GreetingsCityFlag.class, true);   // enable greetings
                plugin.getFlagManager().setCityFlag(city, player, JoinCostsCityFlag.class, plugin.getConfig().joinCosts);   // default join costs

                plugin.getDynmapManager().addCityMarker(city);

            } catch (RaidCraftException e) {
                throw new CommandException(e.getMessage());
            }
            Bukkit.broadcastMessage(ChatColor.GOLD + "Es wurde die Gilde '" + city.getFriendlyName() + "' gegründet!");
        }

        @Command(
                aliases = {"delete"},
                desc = "Delete an existing city",
                min = 1,
                flags = "r",
                usage = "<Gilde>"
        )
        @CommandPermissions("rccities.town.delete")
        public void delete(CommandContext args, CommandSender sender) throws CommandException {

            City city = plugin.getCityManager().getCity(args.getString(0));
            if (city == null) {
                throw new CommandException("Es wurde keine Gilde mit diesem namen gefunden!");
            }

            boolean restoreSchematics = true;
            if (args.hasFlag('r')) {
                restoreSchematics = false;
            }

            try {
                if (restoreSchematics) {
                    sender.sendMessage(ChatColor.YELLOW + "Bei der Löschung der Gilde werden vorhandene Plots " + ChatColor.DARK_RED + "zurückgesetzt" + ChatColor.YELLOW + "!");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "Bei der Löschung der Gilde werden vorhandene Plots " + ChatColor.DARK_RED + "NICHT zurückgesetzt" + ChatColor.YELLOW + "!");
                }
                new QueuedCaptchaCommand(sender, this, "deleteCity", sender, city, restoreSchematics);
            } catch (NoSuchMethodException e) {
                throw new CommandException(e.getMessage());
            }
        }

        @Command(
                aliases = {"upgrade", "upgrades", "up"},
                desc = "Shows or accept guild upgrades",
                usage = "<Gilde> [accept/reject] [reason]",
                min = 1
        )
        @CommandPermissions("rccities.upgrades.process")
        public void upgrades(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
            Player player = (Player) sender;

            City city;
            city = plugin.getCityManager().getCity(args.getString(0));
            if (city == null) {
                throw new CommandException("Es gibt keine Gilde mit dem Name '" + args.getString(0) + "'!");
            }

            List<UpgradeRequest> upgradeRequests = plugin.getUpgradeRequestManager().getOpenRequests(city);
            if (upgradeRequests.size() == 0) {
                throw new CommandException("Für diese Gilde liegen keine Upgrade-Anträge vor!");
            }
            // only process first entry
            UpgradeRequest upgradeRequest = upgradeRequests.get(0);

            if (args.argsLength() > 1) {
                String option = args.getString(1);
                String reason = null;
                if (args.argsLength() > 2) {
                    reason = args.getJoinedStrings(2);
                }
                if (option.equalsIgnoreCase("accept")) {
                    upgradeRequest.accept();
                    UnlockResult unlockResult = upgradeRequest.getUpgradeLevel().tryToUnlock(city);
                    if (unlockResult.isSuccessful()) {
                        sender.sendMessage(ChatColor.GREEN + " Du hast den Upgrade-Antrag von '" + city.getFriendlyName() + "' angenommen!");
                        upgradeRequest.getUpgradeLevel().setUnlocked(true);
                    } else {
                        sender.sendMessage(ChatColor.GREEN + " Das Upgrade ist fehlgeschlagen da andere Bedingungen nicht mehr erfüllt sind!");
                    }
                    return;
                }
                if (option.equalsIgnoreCase("reject")) {
                    if (reason == null) {
                        throw new CommandException("Gib bitte noch einen Grund als weiteren Parameter an!");
                    }
                    upgradeRequest.reject(reason);
                    sender.sendMessage(ChatColor.GREEN + " Du hast den Upgrade-Antrag von '" + city.getFriendlyName() + "' " + ChatColor.RED + "abgelehnt" + ChatColor.GREEN + "!");
                    plugin.getResidentManager().broadcastCityMessage(city, "Der Upgrade-Antrag wurde abgelehnt, Grund: " + reason);
                    return;
                }
                throw new CommandException("Parameter nicht erkannt. Nutze <accept> oder <reject> umd Anträge zu bearbeiten!");
            }

            // show info
            sender.sendMessage(ChatColor.GREEN + "Die Gilde '" + city.getFriendlyName() + "' hat das Upgrade '"
                    + ChatColor.YELLOW + upgradeRequest.getUpgradeLevel().getName() + ChatColor.GREEN + "' beantragt:");
            sender.sendMessage(ChatColor.GREEN + "Info: " + ChatColor.GRAY + upgradeRequest.getInfo());
            if (upgradeRequest.getRejectReason() != null) {
                sender.sendMessage(ChatColor.RED + "Achtung: Der letzte Antrag wurde abgelehnt.");
                sender.sendMessage(ChatColor.GREEN + "Grund: " + ChatColor.GRAY + upgradeRequest.getRejectReason());
            }
            sender.sendMessage(ChatColor.GREEN + "-->" + ChatColor.YELLOW + "/gilde upgrades " + city.getName() + " <accept/reject>");
        }

        @Command(
                aliases = {"spawn", "tp", "warp"},
                desc = "Teleport to town spawn",
                usage = "[Gilde]"
        )
        @CommandPermissions("rccities.town.spawn")
        public void spawn(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
            Player player = (Player) sender;

            City city;
            if (args.argsLength() > 0) {
                city = plugin.getCityManager().getCity(args.getString(0));
                if (city == null) {
                    throw new CommandException("Es gibt keine Gilde mit dem Name '" + args.getString(0) + "'!");
                }
                if (!player.hasPermission("rccities.town.spawn.all")) {
                    Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
                    if (resident == null || !resident.getRole().hasPermission(RolePermission.SPAWN_TELEPORT)) {
                        throw new CommandException("Du darfst dich nicht zum Spawn der Gilde '" + city.getFriendlyName() + "' porten!");
                    }
                }
            } else {
                List<Resident> citizenships = plugin.getResidentManager().getCitizenships(player.getUniqueId(), RolePermission.SPAWN_TELEPORT);

                if (citizenships == null || citizenships.size() == 0) {
                    throw new CommandException("Du besitzt in keiner Gilde das Recht dich zum Spawn zu porten!");
                }
                if (citizenships.size() > 1) {
                    throw new CommandException("Du besitzt in mehreren Gilden das Recht dich zum Spawn zu porten! Gebe die gewünschte Gilde als Parameter an.");
                }
                city = citizenships.get(0).getCity();
            }

            if (!city.getSpawn().getWorld().equals(player.getWorld())) {
                throw new CommandException("Du befindest dich auf der falschen Welt!");
            }

            player.teleport(city.getSpawn());
            player.sendMessage(ChatColor.YELLOW + "Du wurdest zum Spawn von '" + city.getFriendlyName() + "' teleportiert!");
        }

        @Command(
                aliases = {"setspawn"},
                desc = "Redefine the town spawn location",
                usage = "[Gilde]"
        )
        @CommandPermissions("rccities.town.setspawn")
        public void setSpawn(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
            Player player = (Player) sender;

            City city;
            if (args.argsLength() > 0) {
                city = plugin.getCityManager().getCity(args.getString(0));
                if (city == null) {
                    throw new CommandException("Es gibt keine Gilde mit dem Name '" + args.getString(0) + "'!");
                }
                if (!player.hasPermission("rccities.setspawn.all")) {
                    Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
                    if (resident == null || !resident.getRole().hasPermission(RolePermission.SET_SPAWN)) {
                        throw new CommandException("Du darfst von der Gilde '" + city.getFriendlyName() + "' den Spawn nicht versetzen!");
                    }
                }
            } else {
                List<Resident> citizenships = plugin.getResidentManager().getCitizenships(player.getUniqueId(), RolePermission.SET_SPAWN);

                if (citizenships == null || citizenships.size() == 0) {
                    throw new CommandException("Du besitzt in keiner Gilde das Recht den Spawn zu versetzen!");
                }
                if (citizenships.size() > 1) {
                    throw new CommandException("Du besitzt in mehreren Gilden das Recht den Spawn zu verändern! Gebe die gewünschte Gilde als Parameter an.");
                }
                city = citizenships.get(0).getCity();
            }

            if (!city.getSpawn().getWorld().equals(player.getWorld())) {
                throw new CommandException("Der Spawn muss sich auf der selben Welt wie die Gilde befinden!");
            }

            city.setSpawn(player.getLocation());
            plugin.getResidentManager().broadcastCityMessage(city, "Der Spawn wurde versetzt!");
        }

        @Command(
                aliases = {"setdescription", "setdesc"},
                desc = "Change city description",
                min = 2,
                usage = "<Gilde> <Beschreibung>"
        )
        @CommandPermissions("rccities.town.setdescription")
        public void setDescription(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
            Player player = (Player) sender;

            City city = plugin.getCityManager().getCity(args.getString(0));
            if (city == null) {
                throw new CommandException("Es gibt keine Gilde mit dem Name '" + args.getString(0) + "'!");
            }
            if (!player.hasPermission("rccities.setspawn.all")) {
                Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
                if (resident == null || !resident.getRole().hasPermission(RolePermission.SET_DESCRIPTION)) {
                    throw new CommandException("Du darfst von der Gilde '" + city.getFriendlyName() + "' die Beschreibung nicht ändern!");
                }
            }
            String description = args.getJoinedStrings(1);

            city.setDescription(description);
            player.sendMessage(ChatColor.GREEN + "Du hast die Beschreibung der Gilde '" + city.getFriendlyName() + "' geändert!");
            plugin.getResidentManager().broadcastCityMessage(city, "Die Beschreibung der Gilde wurde geändert!");
        }

        @Command(
                aliases = {"info"},
                desc = "Shows city info",
                min = 1,
                usage = "<Gilde>"
        )
        @CommandPermissions("rccities.town.info")
        public void info(CommandContext args, CommandSender sender) throws CommandException {

            try {
                plugin.getCityManager().printCityInfo(args.getString(0), sender);
            } catch (RaidCraftException e) {
                throw new CommandException(e.getMessage());
            }
        }

        @Command(
                aliases = {"list"},
                desc = "List all existing cities"
        )
        @CommandPermissions("rccities.town.list")
        public void list(CommandContext args, CommandSender sender) throws CommandException {

            Collection<City> cities = plugin.getCityManager().getCities();
            sender.sendMessage(ChatColor.GOLD + "Es gibt derzeit " + ChatColor.YELLOW + cities.size() + ChatColor.GOLD + " Gilden auf dem Server:");
            String cityList = "";
            for (City city : cities) {
                if (!cityList.isEmpty()) cityList += ChatColor.GOLD + ", ";
                UpgradeLevel upgradeLevel = plugin.getCityManager().getMainUpgrade(city).getHighestUnlockedLevel();
                int level = 0;
                if (upgradeLevel != null) {
                    level = upgradeLevel.getLevel();
                }
                cityList += ChatColor.YELLOW + city.getFriendlyName() + " (" + level + ")";
            }
            sender.sendMessage(cityList);
        }

        @Command(
                aliases = {"flag"},
                desc = "Change city flag",
                usage = "<Gilde> <Flag> [Parameter]"
        )
        @CommandPermissions("rccities.town.flag")
        public void flag(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");

            if(args.argsLength() < 2) {
                String flagList = "";
                for(FlagInformation info : plugin.getFlagManager().getRegisteredCityFlagInformationList()) {
                    flagList += info.name() + ", ";
                }
                throw new CommandException("Verfügbare Flags: " + flagList);
            }

            Player player = (Player) sender;

            City city;
            String flagValue = null;
            String flagName = args.getString(1);
            if (args.argsLength() > 2) {
                flagValue = args.getString(2);
            }
            city = plugin.getCityManager().getCity(args.getString(0));
            if (city == null) {
                throw new CommandException("Es gibt keine Gilde mit dem Name '" + args.getString(0) + "'!");
            }
            if (!player.hasPermission("rccities.town.flag.all")) {
                Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
                if (resident == null || !resident.getRole().hasPermission(RolePermission.CITY_FLAG_MODIFICATION)) {
                    throw new CommandException("Du darfst von der Gilde '" + city.getFriendlyName() + "' keine Flags ändern!");
                }
            }

            try {
                city.setFlag(player, flagName, flagValue);
            } catch (RaidCraftException e) {
                throw new CommandException(e.getMessage());
            }
            player.sendMessage(ChatColor.GREEN + "Du hast erfolgreich die Flag '" + ChatColor.YELLOW + flagName.toUpperCase()
                    + ChatColor.GREEN + "' auf den Wert '" + ChatColor.YELLOW + flagValue.toUpperCase() + ChatColor.GREEN + "' gesetzt!");
        }

        @Command(
                aliases = {"invite"},
                desc = "Invites an player as resident",
                min = 1,
                usage = "[Gilde] <Spielername>"
        )
        @CommandPermissions("rccities.town.invite")
        public void invite(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
            Player player = (Player) sender;

            City city;
            Player targetPlayer;
            if (args.argsLength() > 1) {
                targetPlayer = CommandUtil.grabPlayer(args.getString(1));
                if (targetPlayer == null) {
                    throw new CommandException("Der gewählte Spieler muss online sein!");
                }
                city = plugin.getCityManager().getCity(args.getString(0));
                if (city == null) {
                    throw new CommandException("Es gibt keine Gilde mit dem Name '" + args.getString(0) + "'!");
                }
                if (!player.hasPermission("rccities.town.invite.all")) {
                    Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
                    if (resident == null || !resident.getRole().hasPermission(RolePermission.INVITE)) {
                        throw new CommandException("Du darfst in die Gilde '" + city.getFriendlyName() + "' keine Bürger einladen!");
                    }
                }
            } else {
                targetPlayer = CommandUtil.grabPlayer(args.getString(0));
                if (targetPlayer == null) {
                    throw new CommandException("Der gewählte Spieler muss online sein!");
                }

                List<Resident> citizenships = plugin.getResidentManager().getCitizenships(player.getUniqueId(), RolePermission.INVITE);

                if (citizenships == null) {
                    throw new CommandException("Du besitzt in keiner Gilde das Recht Spieler einzuladen!");
                }
                if (citizenships.size() > 1) {
                    throw new CommandException("Du besitzt in mehreren Gilden das Recht Spieler einzuladen! Gebe die gewünschte Gilde als Parameter an.");
                }
                city = citizenships.get(0).getCity();
            }

            if (player.getName().equalsIgnoreCase(targetPlayer.getName())) {
                throw new CommandException("Du kannst dich nicht selbst in die Gilde einladen!");
            }

            // invite is locked
            CityFlag inviteFlag = plugin.getFlagManager().getCityFlag(city, InviteCityFlag.class);
            if (inviteFlag != null && !inviteFlag.getType().convertToBoolean(inviteFlag.getValue())) {
                throw new CommandException("Deine Gilde darf zurzeit keine neuen Spieler einladen!");
            }

            invites.put(targetPlayer.getName(), city);
            targetPlayer.sendMessage(ChatColor.GOLD + "Du wurdest in die Gilde '" + city.getFriendlyName() + "' eingeladen!");
            targetPlayer.sendMessage(ChatColor.GOLD + "Bestätige die Einladung mit '/gilde accept'");
            player.sendMessage(ChatColor.GREEN + "Du hast " + targetPlayer.getName() + " in die Gilde '" + city.getFriendlyName() + "' eingeladen!");
        }

        @Command(
                aliases = {"accept"},
                desc = "Accept an invite"
        )
        @CommandPermissions("rccities.town.invite")
        public void accept(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
            Player player = (Player) sender;

            if (!invites.containsKey(player.getName())) {
                throw new CommandException("Du hast keine offenen Einladungen!");
            }

            City city = invites.get(player.getName());
            try {
                plugin.getResidentManager().addResident(city, player);
            } catch (RaidCraftException e) {
                throw new CommandException(e.getMessage());
            }
            Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + " ist nun Einwohner von '" + city.getFriendlyName() + "'!");
        }

        @Command(
                aliases = {"leave"},
                desc = "Leaves a city",
                flags = "f",
                usage = "[Gilde]"
        )
        @CommandPermissions("rccities.town.leave")
        public void leave(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
            Player player = (Player) sender;

            City city;
            if (args.argsLength() > 0) {
                city = plugin.getCityManager().getCity(args.getString(0));
                if (city == null) {
                    throw new CommandException("Es gibt keine Gilde mit dem Name '" + args.getString(0) + "'!");
                }
                if (!player.hasPermission("rccities.town.leave.all")) {
                    Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
                    if (resident == null) {
                        throw new CommandException("Du bist kein Einwohner der Gilde '" + city.getFriendlyName() + "'!");
                    } else if (!resident.getRole().hasPermission(RolePermission.LEAVE)) {
                        throw new CommandException("Du darfst die Gilde '" + city.getFriendlyName() + "' nicht verlassen!");
                    }
                }
            } else {
                List<Resident> citizenships = plugin.getResidentManager().getCitizenships(player.getUniqueId(), RolePermission.LEAVE);

                if (citizenships == null) {
                    throw new CommandException("Du besitzt in keiner Gilde das Recht diese zu verlassen!");
                }
                if (citizenships.size() > 1) {
                    throw new CommandException("Du bist Bürger von mehreren Gilden. Gebe die gewünschte Gilde als Parameter an.");
                }
                city = citizenships.get(0).getCity();
            }

            Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
            if (resident == null) {
                throw new CommandException("Du bist kein Mitglied der Gilde '" + city.getFriendlyName() + "'!");
            }

            if (args.hasFlag('f')) {
                leaveCity(resident);
            } else {
                try {
                    new QueuedCaptchaCommand(sender, this, "leaveCity", resident);
                } catch (NoSuchMethodException e) {
                    throw new CommandException(e.getMessage());
                }
            }
        }

        @Command(
                aliases = {"kick"},
                desc = "Kicks a resident",
                flags = "f",
                usage = "[Gilde] <Spieler>",
                min = 1
        )
        @CommandPermissions("rccities.town.kick")
        public void kick(CommandContext args, CommandSender sender) throws CommandException {

            if (sender instanceof ConsoleCommandSender) throw new CommandException("Player required!");
            Player player = (Player) sender;

            City city;
            String target;
            if (args.argsLength() > 1) {
                target = args.getString(1);
                city = plugin.getCityManager().getCity(args.getString(0));
                if (city == null) {
                    throw new CommandException("Es gibt keine Gilde mit dem Name '" + args.getString(0) + "'!");
                }
                if (!player.hasPermission("rccities.town.kick.all")) {
                    Resident resident = plugin.getResidentManager().getResident(player.getUniqueId(), city);
                    if (resident == null || !resident.getRole().hasPermission(RolePermission.KICK)) {
                        throw new CommandException("Du darfst keine Bürger aus der Gilde '" + city.getFriendlyName() + "' werfen!");
                    }
                }
            } else {
                target = args.getString(0);
                List<Resident> citizenships = plugin.getResidentManager().getCitizenships(player.getUniqueId(), RolePermission.KICK);
                if (citizenships == null) {
                    throw new CommandException("Du besitzt in keiner Gilde das Recht Spieler rauszuwerfen!");
                }
                if (citizenships.size() > 1) {
                    throw new CommandException("Du besitzt in mehreren Gilden das Recht Spieler rauszuschmeissen! Gebe die gewünschte Gilde als Parameter an.");
                }
                city = citizenships.get(0).getCity();
            }

            if (!args.hasFlag('f') && player.getName().equalsIgnoreCase(target)) {
                throw new CommandException("Du kannst dich nicht selbst aus der Gilde werfen!");
            }

            Resident resident = plugin.getResidentManager().getResident(UUIDUtil.convertPlayer(target), city);
            if (resident == null) {
                throw new CommandException(target + " ist kein Mitglied von '" + city.getFriendlyName() + "'!");
            }

            if (!resident.getRole().hasPermission(RolePermission.GET_KICKED) && !player.hasMetadata("rccities.town.kick.all")) {
                throw new CommandException("Du kannst diesen Einwohner nicht aus der Gilde werfen!");
            }

            resident.delete();
            Bukkit.broadcastMessage(ChatColor.GOLD + target + " wurde aus der Gilde '" + city.getFriendlyName() + "' geworfen!");
        }

        /*
         ***********************************************************************************************************************************
         */

        public void deleteCity(CommandSender sender, City city, boolean restoreSchematics) {

            if (restoreSchematics) {
                try {
                    plugin.getSchematicManager().restoreCity(city);
                } catch (RaidCraftException e) {
                    sender.sendMessage(ChatColor.RED + "Es ist ein Fehler beim wiederherstellen der Plots aufgetreten! (" + e.getMessage() + ")");
                }
            }

            city.delete();
            Bukkit.broadcastMessage(ChatColor.GOLD + "Die Gilde '" + city.getFriendlyName() + "' wurde gelöscht!");
        }

        public void leaveCity(Resident resident) {

            resident.delete();
            Bukkit.broadcastMessage(ChatColor.GOLD + resident.getName() + " hat die Gilde '" + resident.getCity().getFriendlyName() + "' verlassen!");
        }
    }

}
