package net.silthus.rccities;

import co.aikar.commands.PaperCommandManager;
import com.sk89q.worldguard.WorldGuard;
import io.ebean.Database;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.milkbowl.vault.permission.Permission;
import net.silthus.ebean.Config;
import net.silthus.ebean.EbeanWrapper;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.commands.CommandSetup;
import net.silthus.rccities.commands.QueuedCommand;
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
import net.silthus.rccities.rewards.SubtractMoneyReward;
import net.silthus.rccities.tables.*;
import net.silthus.rccities.upgrades.RCUpgrades;
import net.silthus.rccities.upgrades.RequirementManager;
import net.silthus.rccities.upgrades.api.reward.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Philip Urban
 */
@PluginMain
public class RCCitiesPlugin extends JavaPlugin {

    private Database database;

    @Getter
    @Accessors(fluent = true)
    private static RCCitiesPlugin instance;
    @Getter
    private final Map<String, QueuedCommand> queuedCommands = new HashMap<>();
    @Getter
    private Permission permission;
    @Getter
    @Setter
    private PaperCommandManager commandManager;
    @Getter
    private WorldGuard worldGuard;
    @Getter
    private CityManager cityManager;
    @Getter
    private PlotManager plotManager;
    @Getter
    private ResidentManager residentManager;
    @Getter
    private FlagManager flagManager;
    @Getter
    private WorldGuardManager worldGuardManager;
    @Getter
    private UpgradeRequestManager upgradeRequestManager;
    @Getter
    private SchematicManager schematicManager;
    @Getter
    private DynmapManager dynmapManager;
    @Getter
    private RCUpgrades upgrades;
    @Getter
    private RCCitiesPluginConfig pluginConfig;
    @Getter
    private YamlConfiguration upgradeConfiguration;
    @Getter
    private boolean testing = false;

    public RCCitiesPlugin() {
        instance = this;
    }

    public RCCitiesPlugin(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        instance = this;
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
        //setupLanguageManager();
        setupDatabase();

        upgrades = new RCUpgrades(this);

        RequirementManager.registerRequirementType(CityExpRequirement.class);
        RequirementManager.registerRequirementType(CityMoneyRequirement.class);
        RequirementManager.registerRequirementType(CityStaffRequirement.class);
        RequirementManager.registerRequirementType(CityUpgradeLevelRequirement.class);

        RewardManager.registerRewardType(CityFlagReward.class);
        RewardManager.registerRewardType(CityPlotsReward.class);
        RewardManager.registerRewardType(SubtractMoneyReward.class);

        worldGuard = WorldGuard.getInstance();
        cityManager = new CityManager(this);
        plotManager = new PlotManager(this);
        residentManager = new ResidentManager(this);
        flagManager = new FlagManager(this);
        worldGuardManager = new WorldGuardManager(this);
        upgradeRequestManager = new UpgradeRequestManager(this);
        schematicManager = new SchematicManager(this);
        dynmapManager = new DynmapManager(this);

        // city flags
        flagManager.registerCityFlag(PvpCityFlag.class);
        flagManager.registerCityFlag(GreetingsCityFlag.class);
        flagManager.registerCityFlag(InviteCityFlag.class);
        flagManager.registerCityFlag(JoinCostsCityFlag.class);
        flagManager.registerCityFlag(LeafDecayCityFlag.class);
        flagManager.registerCityFlag(MobSpawnCityFlag.class);

        // plot flags
        flagManager.registerPlotFlag(MarkPlotFlag.class);
        flagManager.registerPlotFlag(MarkPlotBaseFlag.class);
        flagManager.registerPlotFlag(PvpPlotFlag.class);
        flagManager.registerPlotFlag(TntPlotFlag.class);
        flagManager.registerPlotFlag(MobSpawnPlotFlag.class);
        flagManager.registerPlotFlag(FarmPlotFlag.class);

        // Load existing cities and process
        // required tasks like refreshing city flags
        //
        // Execute this delayed to be sure server is running
        //--------------------------------------------------
        Bukkit.getScheduler().runTaskLater(this, () -> {

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
        }, 20);

        if (!isTesting()) {
            setupListeners();
            CommandSetup commandSetup = new CommandSetup(this);
            commandSetup.setupCommands();
        }
    }

    public void reload() {

        loadConfig();
    }

    private boolean setupVault() {

        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Permission> registration = getServer().getServicesManager()
                .getRegistration(Permission.class);
        if (registration == null) {
            return false;
        }
        permission = registration.getProvider();

        return true;
    }

    private void loadConfig() {

        //noinspection ResultOfMethodCallIgnored
        getDataFolder().mkdirs();
        this.pluginConfig = new RCCitiesPluginConfig(
                new File(getDataFolder(), "config.yml").toPath());
        this.pluginConfig.loadAndSave();

        this.upgradeConfiguration = YamlConfiguration.loadConfiguration(
                new File(getDataFolder(), "upgrades.yml"));
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

    private void setupListeners() {

        EntityListener entityListener = new EntityListener(this);
        Bukkit.getPluginManager().registerEvents(entityListener, this);

        ResidentListener residentListener = new ResidentListener(this);
        Bukkit.getPluginManager().registerEvents(residentListener, this);

        UpgradeListener upgradeListener = new UpgradeListener(this);
        Bukkit.getPluginManager().registerEvents(upgradeListener, this);
    }

    public final void queueCommand(final QueuedCommand command) {

        queuedCommands.put(command.getSender().getName(), command);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {

            if(queuedCommands.containsKey(command.getSender().getName())) {
                command.getSender().sendMessage(ChatColor.RED + "Du hast nicht rechtzeitig bestätigt");
                queuedCommands.remove(command.getSender().getName());
            }
        }, 900L);
        // 45 second remove delay
    }
}