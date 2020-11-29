package net.silthus.rccities;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.silthus.ebean.Config;
import net.silthus.ebean.EbeanWrapper;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.commands.PlotCommands;
import net.silthus.rccities.commands.ResidentCommands;
import net.silthus.rccities.commands.TownCommands;
import net.silthus.rccities.listener.EntityListener;
import net.silthus.rccities.listener.ExpListener;
import net.silthus.rccities.listener.ResidentListener;
import net.silthus.rccities.listener.UpgradeListener;
import net.silthus.rccities.manager.*;
import net.silthus.rccities.tables.*;
import net.silthus.rccities.upgrades.RCUpgrades;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import co.aikar.commands.PaperCommandManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.base.Strings;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldguard.WorldGuard;
import io.ebean.Database;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.Getter;
import me.wiefferink.interactivemessenger.processing.Message;
import me.wiefferink.interactivemessenger.source.LanguageManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip Urban
 */
@PluginMain
@Getter
public class RCCitiesPlugin extends JavaPlugin {

    private Economy economy;
    private Permission permission;
    private Database database;
    private PaperCommandManager commandManager;
    private LanguageManager languageManager;

    private WorldGuardPlugin worldGuard;
    private WorldEditPlugin worldEdit;
    private ConfigurationSection upgradeConfiguration;

    private CityManager cityManager;
    private PlotManager plotManager;
    private ResidentManager residentManager;
    private AssignmentManager assignmentManager;
    private FlagManager flagManager;
    private SchematicManager schematicManager;
    private DynmapManager dynmapManager;
    private WorldGuardManager worldGuardManager;
    private UpgradeRequestManager upgradeRequestManager;

    private EntityListener entityListener;
    private ExpListener expListener;
    private ResidentListener residentListener;
    private UpgradeListener upgradeListener;
    private RCUpgrades upgrades;

    private RCCitiesPluginConfig pluginConfig;

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

        if (!isTesting() && !setupVault()) {
            getLogger().severe(String.format("[%s] - No Vault dependency found!", getDescription().getName()));
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
            return;
        }

        loadConfig();
        setupLanguageManager();
        setupDatabase();
        setupRegionManager();
        if (!isTesting()) {
            setupListeners();
            setupCommands();
        }

        upgrades = new RCUpgrades(this);
        worldGuard = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
        worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");

        reload();

        cityManager = new CityManager(this);
        plotManager = new PlotManager(this);
        residentManager = new ResidentManager(this);
        assignmentManager = new AssignmentManager(this);
        flagManager = new FlagManager(this);
        schematicManager = new SchematicManager(this);
        dynmapManager = new DynmapManager();
        worldGuardManager = new WorldGuardManager(this, worldGuard);
        upgradeRequestManager = new UpgradeRequestManager(this);

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
    }

    public void reload() {

        loadConfig();

        // TODO load upgrade config
        // load upgrade holder
        for (File file : getDataFolder().listFiles()) {
            if (file.getName().equalsIgnoreCase(config.upgradeHolder + ".yml")) {
                upgradeConfiguration = configure(new SimpleConfiguration<>(this, file));
            }
        }
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

        expListener = new EntityListener(this);
        Bukkit.getPluginManager().registerEvents(expListener, this);

        residentListener = new EntityListener(this);
        Bukkit.getPluginManager().registerEvents(residentListener, this);

        upgradeListener = new EntityListener(this);
        Bukkit.getPluginManager().registerEvents(upgradeListener, this);
    }


    private void setupCommands() {

        this.commandManager = new PaperCommandManager(this);

        // TODO?
        registerRegionPlayerContext(commandManager);
        registerRegionContext(commandManager);
        registerRegionsCompletion(commandManager);
        registerWorldGuardRegionCompletion(commandManager);

        commandManager.registerCommand(new PlotCommands(this));
        commandManager.registerCommand(new ResidentCommands(this));
        commandManager.registerCommand(new TownCommands(this));
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









    public class LocalConfiguration extends ConfigurationBase<RCCitiesPlugin> {

        // TODO Update to new config structure
        @Setting("ignored-regions")
        public String[] ignoredRegions = new String[]{"rcmap"};
        @Setting("default-town-radius")
        public int defaultMaxRadius = 64;
        @Setting("initial-plot-credit")
        public int initialPlotCredit = 3;
        @Setting("flag-plot-mark-cost")
        public double flagPlotMarkCost = 0.01;
        @Setting("city-upgrade-holder")
        public String upgradeHolder = "city-upgrade-holder";
        @Setting("join-costs")
        public String joinCosts = "10S";
        @Setting("upgrade-request-reject-cooldown")
        public int upgradeRequestCooldown = 5 * 24 * 60;// 5 days in minutes

        public LocalConfiguration(RCCitiesPlugin plugin) {

            super(plugin, "config.yml");
        }
    }
}
