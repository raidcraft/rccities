package net.silthus.rccities.listener;

import net.silthus.rccities.RCCities;
import net.silthus.rccities.api.resident.Resident;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

/**
 * Created by Philip on 01.02.2016.
 */

public class ResidentListener implements Listener {

    private final RCCities plugin;

    public ResidentListener(RCCities plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                List<Resident> citizenships = plugin.getResidentManager().getCitizenships(event.getPlayer().getUniqueId());
                if(citizenships == null) return;
                for(Resident resident : citizenships) {
                    plugin.getResidentManager().addPrefixSkill(resident);
                }
            }
        }, 10);
    }
}
