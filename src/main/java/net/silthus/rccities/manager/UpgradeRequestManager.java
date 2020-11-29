package net.silthus.rccities.manager;

import net.silthus.rccities.DatabaseUpgradeRequest;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.request.UpgradeRequest;
import net.silthus.rccities.tables.TUpgradeRequest;
import net.silthus.rccities.upgrades.api.level.UpgradeLevel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Philip Urban
 */
public class UpgradeRequestManager {

    private RCCitiesPlugin plugin;
    private UpgradeRequestInformTask informTask;

    public UpgradeRequestManager(RCCitiesPlugin plugin) {

        this.plugin = plugin;

        informTask = new UpgradeRequestInformTask(this);
        Bukkit.getScheduler().runTaskTimer(plugin, informTask, 5 * 60 * 20, 5 * 60 * 20);
    }

    public UpgradeRequest getRequest(City city, UpgradeLevel upgradeLevel) {

        TUpgradeRequest tUpgradeRequest = TUpgradeRequest.find.query()
                .where().eq("city_id", city.getId()).ieq("level_identifier", upgradeLevel.getId()).findOne();
        if (tUpgradeRequest == null) {
            return null;
        }
        return new DatabaseUpgradeRequest(tUpgradeRequest);
    }

    public List<UpgradeRequest> getOpenRequests() {

        List<UpgradeRequest> requests = new ArrayList<>();
        List<TUpgradeRequest> tUpgradeRequests = TUpgradeRequest.find.query()
                .where().eq("rejected", false).eq("accepted", false).findList();
        for (TUpgradeRequest tUpgradeRequest : tUpgradeRequests) {
            requests.add(new DatabaseUpgradeRequest(tUpgradeRequest));
        }
        return requests;
    }

    public List<UpgradeRequest> getOpenRequests(City city) {

        List<UpgradeRequest> requests = new ArrayList<>();
        List<TUpgradeRequest> tUpgradeRequests = TUpgradeRequest.find.query()
                .where().eq("city_id", city.getId()).eq("rejected", false).eq("accepted", false).findList();
        for (TUpgradeRequest tUpgradeRequest : tUpgradeRequests) {
            if (tUpgradeRequest.getRCCity() == null) continue;
            requests.add(new DatabaseUpgradeRequest(tUpgradeRequest));
        }
        return requests;
    }

    public class UpgradeRequestInformTask implements Runnable {

        UpgradeRequestManager upgradeRequestManager;

        public UpgradeRequestInformTask(UpgradeRequestManager upgradeRequestManager) {

            this.upgradeRequestManager = upgradeRequestManager;
        }

        @Override
        public void run() {

            List<UpgradeRequest> openRequests = upgradeRequestManager.getOpenRequests();
            if (openRequests.size() == 0) return;

            String list = "";
            for (UpgradeRequest request : openRequests) {
                if (!list.isEmpty()) list += ", ";
                list += request.getCity().getFriendlyName();
            }

            for (Player player : Bukkit.getOnlinePlayers()) {

                if (!player.hasPermission("rccities.upgrades.process")) continue;

                player.sendMessage(ChatColor.GRAY + "Es liegen Upgrade-Anfragen von Städten vor:");
                player.sendMessage(ChatColor.GRAY + list + " (nutze /town upgrade <Stadt>)");
            }
        }
    }
}
