package net.silthus.rccities;

import com.google.common.base.Strings;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.silthus.ebean.Config;
import net.silthus.ebean.EbeanWrapper;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.commands.PlotCommands;
import net.silthus.rccities.commands.ResidentCommands;
import net.silthus.rccities.commands.TownCommands;
import net.silthus.rccities.flags.city.*;
import net.silthus.rccities.flags.city.admin.InviteCityFlag;
import net.silthus.rccities.flags.plot.*;
import net.silthus.rccities.listener.EntityListener;
import net.silthus.rccities.listener.ResidentListener;
import net.silthus.rccities.listener.UpgradeListener;
import net.silthus.rccities.manager.*;
import net.silthus.rccities.requirements.CityExpRequirement;
import net.silthus.rccities.requirements.CityMoneyRequirement;
import net.silthus.rccities.requirements.CityStaffRequirement;
import net.silthus.rccities.requirements.CityUpgradeLevelRequirement;
import net.silthus.rccities.rewards.CityFlagReward;
import net.silthus.rccities.rewards.CityPlotsReward;
import net.silthus.rccities.rewards.CityRadiusReward;
import net.silthus.rccities.rewards.SubtractMoneyReward;
import net.silthus.rccities.tables.*;
import net.silthus.rccities.upgrades.RCUpgrades;
import net.silthus.rccities.upgrades.RequirementManager;
import net.silthus.rccities.upgrades.api.reward.Reward;
import net.silthus.rccities.upgrades.api.reward.RewardManager;
import net.silthus.rccities.util.QueuedCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import co.aikar.commands.PaperCommandManager;
import io.ebean.Database;
import me.wiefferink.interactivemessenger.processing.Message;
import me.wiefferink.interactivemessenger.source.LanguageManager;
import net.milkbowl.vault.permission.Permission;

import java.io.File;
import java.util.*;

/**
 * @author Philip Urban
 */
@PluginMain
@Getter
public class RCCitiesPlugin extends JavaPlugin {

    private final Map<String, QueuedCommand> queuedCommands = new HashMap<>();
    private Economy economy;
    private Permission permission;
    private Database database;
    private PaperCommandManager commandManager;
    private LanguageManager languageManager;

    private WorldGuard worldGuard;

    private CityManager cityManager;
    private PlotManager plotManager;
    private ResidentManager residentManager;
    private AssignmentManager assignmentManager;
    private FlagManager flagManager;
    private WorldGuardManager worldGuardManager;
    private UpgradeRequestManager upgradeRequestManager;
    private SchematicManager schematicManager;

    private EntityListener entityListener;
    private ResidentListener residentListener;
    private UpgradeListener upgradeListener;
    private RCUpgrades upgrades;

    private RCCitiesPluginConfig pluginConfig;
    private YamlConfiguration upgradeConfiguration;

    private boolean testing = false;

    public static RCCitiesPlugin getPlugin() {
        return (RCCitiesPlugin) Bukkit.getPluginManager().getPlugin("RCCities");
    }

    public RCCitiesPlugin() {
        super();
    }

    public RCCitiesPlugin(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        testing = true;
    }

    @Override
    public void onEnable() {

        if(isTesting()) {
            getLogger().info("RCCities is running in test mode!");
        }

        if (!isTesting() && !setupVault()) {
            getLogger().severe(String.format("[%s] - No Vault dependency found!", getDescription().getName()));
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
            return;
        }

        loadConfig();
        setupLanguageManager();
        setupDatabase();

        upgrades = new RCUpgrades(this);

        RequirementManager.registerRequirementType(CityExpRequirement.class);
        RequirementManager.registerRequirementType(CityMoneyRequirement.class);
        RequirementManager.registerRequirementType(CityStaffRequirement.class);
        RequirementManager.registerRequirementType(CityUpgradeLevelRequirement.class);

        RewardManager.registerRewardType(CityFlagReward.class);
        RewardManager.registerRewardType(CityPlotsReward.class);
        RewardManager.registerRewardType(CityRadiusReward.class);
        RewardManager.registerRewardType(SubtractMoneyReward.class);

        worldGuard = WorldGuard.getInstance();

        reload();

        cityManager = new CityManager(this);
        plotManager = new PlotManager(this);
        residentManager = new ResidentManager(this);
        assignmentManager = new AssignmentManager(this);
        flagManager = new FlagManager(this);
        worldGuardManager = new WorldGuardManager(this);
        upgradeRequestManager = new UpgradeRequestManager(this);
        schematicManager = new SchematicManager(this);

        // city flags
        flagManager.registerCityFlag(PvpCityFlag.class);
        flagManager.registerCityFlag(GreetingsCityFlag.class);
        flagManager.registerCityFlag(InviteCityFlag.class);
        flagManager.registerCityFlag(JoinCostsCityFlag.class);
        flagManager.registerCityFlag(LeafDecayCityFlag.class);
        flagManager.registerCityFlag(MobSpawnCityFlag.class);

        // plot flags
        flagManager.registerPlotFlag(MarkPlotFlag.class);
        flagManager.registerPlotFlag(PvpPlotFlag.class);
        flagManager.registerPlotFlag(TntPlotFlag.class);
        flagManager.registerPlotFlag(MobSpawnPlotFlag.class);
        flagManager.registerPlotFlag(FarmPlotFlag.class);

        flagManager.loadExistingFlags();

        residentManager.reload();

        // create regions if they don't exist
        for (City city : cityManager.getCities()) {
            for (Plot plot : plotManager.getPlots(city)) {
                if (plot.getRegion() == null) {
                    plot.updateRegion(true);
                }
            }
        }

        if (!isTesting()) {
            setupListeners();
            setupCommands();
        }
    }

    public void reload() {

        loadConfig();
    }

    private boolean setupVault() {

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();

        RegisteredServiceProvider<Permission> registration = getServer().getServicesManager().getRegistration(Permission.class);
        if (registration == null) {
            return false;
        }
        permission = registration.getProvider();

        return true;
    }

    private void loadConfig() {

        getDataFolder().mkdirs();
        this.pluginConfig = new RCCitiesPluginConfig(new File(getDataFolder(), "config.yml").toPath());
        this.pluginConfig.loadAndSave();

        this.upgradeConfiguration = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "upgrades.yml"));
    }

    private void setupDatabase() {

        this.database = new EbeanWrapper(Config.builder(this)
                .entities(
                        TAssignment.class,
                        TCity.class,
                        TCityFlag.class,
                        TJoinRequest.class,
                        TPlot.class,
                        TPlotFlag.class,
                        TResident.class,
                        TUpgradeRequest.class
                )
                .build()).connect();
    }

    private void setupLanguageManager() {

        languageManager = new LanguageManager(
                this,                                  // The plugin (used to get the languages bundled in the jar file)
                "lang",                           // Folder where the languages are stored
                getConfig().getString("language"),     // The language to use indicated by the plugin user
                "EN",                                  // The default language, expected to be shipped with the plugin and should be complete, fills in gaps in the user-selected language
                getConfig().getStringList("chatPrefix") // Chat prefix to use with Message#prefix(), could of course come from the config file
        );
    }

    private void setupListeners() {

        entityListener = new EntityListener(this);
        Bukkit.getPluginManager().registerEvents(entityListener, this);

        residentListener = new ResidentListener(this);
        Bukkit.getPluginManager().registerEvents(residentListener, this);

        upgradeListener = new UpgradeListener(this);
        Bukkit.getPluginManager().registerEvents(upgradeListener, this);
    }


    private void setupCommands() {

        this.commandManager = new PaperCommandManager(this);

        registerPlotContext(commandManager);
        registerOfflinePlayerContext(commandManager);
        registerCityContext(commandManager);

        commandManager.registerCommand(new PlotCommands(this));
        commandManager.registerCommand(new ResidentCommands(this));
        commandManager.registerCommand(new TownCommands(this));
    }

    private void registerOfflinePlayerContext(PaperCommandManager commandManager) {

        commandManager.getCommandContexts().registerIssuerOnlyContext(OfflinePlayer.class, c -> {
            String playerName = c.popFirstArg();

            OfflinePlayer offlinePlayer;
            if (Strings.isNullOrEmpty(playerName)) {
                offlinePlayer = c.getPlayer();
            } else {
                offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            }

            return offlinePlayer;
        });
    }

    private void registerCityContext(PaperCommandManager commandManager) {

        commandManager.getCommandContexts().registerContext(City.class, c -> {
            String cityName = c.popFirstArg();

            City city;
            if (Strings.isNullOrEmpty(cityName)) {
                Plot plot = getPlotManager().getPlot(c.getPlayer().getLocation().getChunk());
                if (plot == null) {

                    List<Resident> citizenships = getResidentManager().getCitizenships(c.getPlayer().getUniqueId());
                    if(1 != citizenships.size()) {
                        throw new CommandException("Hier befindet sich kein Plot oder du bist Einwohner in mehr als einer Stadt!");
                    }
                    city = citizenships.get(0).getCity();
                } else {
                    city = plot.getCity();
                }
                if (city == null) {
                    throw new CommandException("Es ist ein Fehler aufgetreten. Gebe den Stadtnamen direkt an!");
                }
            } else {
                city = getCityManager().getCity(cityName);
                if (city == null) {
                    throw new CommandException("Es gibt keine Stadt mit diesem Namen!");
                }
            }

            return city;
        });
    }

    private void registerPlotContext(PaperCommandManager commandManager) {

        commandManager.getCommandContexts().registerContext(Plot.class, c -> {
            String plotId = c.popFirstArg();

            Plot plot;
            if (Strings.isNullOrEmpty(plotId)) {
                plot = getPlotManager().getPlot(c.getPlayer().getLocation().getChunk());
                if (plot == null) {
                    throw new CommandException("Hier befindet sich kein Plot!");
                }
            } else {
                plot = getPlotManager().getPlot(UUID.fromString(plotId)); //TODO fix UUID stuff
                if (plot == null) {
                    throw new CommandException("Es gibt kein Plot mit dieser ID!");
                }
            }

            return plot;
        });
    }

    public final void queueCommand(final QueuedCommand command) {

        queuedCommands.put(command.getSender().getName(), command);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {

                queuedCommands.remove(command.getSender().getName());
            }
        }, 600L);
        // 30 second remove delay
    }

    public final Map<String, QueuedCommand> getQueuedCommands() {

        return queuedCommands;
    }

    /**
     * Send a message to a target without a prefix.
     * @param target       The target to send the message to
     * @param key          The key of the language string
     * @param replacements The replacements to insert in the message
     */
    public void messageNoPrefix(Object target, String key, Object... replacements) {
        Message.fromKey(key).replacements(replacements).send(target);
    }

    /**
     * Send a message to a target, prefixed by the default chat prefix.
     * @param target       The target to send the message to
     * @param key          The key of the language string
     * @param replacements The replacements to insert in the message
     */
    public void message(Object target, String key, Object... replacements) {
        Message.fromKey(key).prefix().replacements(replacements).send(target);
    }
}
