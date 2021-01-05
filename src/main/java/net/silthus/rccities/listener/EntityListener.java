package net.silthus.rccities.listener;

import net.silthus.rccities.RCCitiesPlugin;
import org.bukkit.event.Listener;

/**
 * @author Philip Urban
 */
public class EntityListener implements Listener {

    private final RCCitiesPlugin plugin;

    public EntityListener(RCCitiesPlugin plugin) {
        this.plugin = plugin;
    }

/*    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntitySpwan(CreatureSpawnEvent event) {

        if (!(event.getEntity() instanceof Monster)) return;
        // Allow snowman, irongolems, whitter
        if((event.getEntity() instanceof IronGolem) || (event.getEntity() instanceof Snowman) || (event.getEntity() instanceof Wither)) return;
        // allow CUSTOM spawns, e.g. NPCs
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) return;
        if (plugin.getPlotManager().getPlot(event.getEntity().getLocation()) == null) {
            return;
        }

        event.getEntity().remove();
        event.setCancelled(true);
    }*/
}
