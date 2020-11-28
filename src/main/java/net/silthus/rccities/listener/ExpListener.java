package net.silthus.rccities.listener;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.events.RCPlayerGainExpEvent;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.resident.Resident;
import de.raidcraft.rccities.api.resident.Role;
import de.raidcraft.rccities.api.resident.RolePermission;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class ExpListener implements Listener {

    private static ExpSaver expSaverTask = new ExpSaver();

    static {

        Bukkit.getScheduler().runTaskTimer(RaidCraft.getComponent(RCCitiesPlugin.class), expSaverTask, 60 * 20, 60 * 20);
    }

    @EventHandler(ignoreCancelled = true)
    public void onExpGain(RCPlayerGainExpEvent event) {

        if (event.getGainedExp() < 0) return;

        RCCitiesPlugin plugin = RaidCraft.getComponent(RCCitiesPlugin.class);
        List<Resident> residents = plugin.getResidentManager()
                .getCitizenships(event.getPlayer().getUniqueId(), false);
        if (residents == null) return;
        boolean slave = false;
        Resident resident = null;
        for (Resident res : residents) {
            if (res.getRole() == Role.SLAVE) {
                slave = true;
                resident = res;
            }
            if (!slave && res.getRole().hasPermission(RolePermission.COLLECT_EXP)) {
                resident = res;
            }
        }
        if (resident != null) {
            expSaverTask.addExp(resident.getCity(), event.getGainedExp());
        }
    }

    public static class ExpSaver implements Runnable {

        private Map<City, Integer> gainedExp = new HashMap<>();

        @Override
        public void run() {

            for (Map.Entry<City, Integer> entry : new HashMap<>(gainedExp).entrySet()) {

                entry.getKey().addExp(entry.getValue());
                gainedExp.remove(entry.getKey());
            }
        }

        public void addExp(City city, int exp) {

            if (!gainedExp.containsKey(city)) gainedExp.put(city, 0);
            gainedExp.put(city, gainedExp.get(city) + exp);
        }
    }
}
