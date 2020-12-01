package net.silthus.rccities.manager;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.silthus.rccities.RCCitiesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

import java.util.*;

/**
 * @author Philip Urban
 */
public class WorldGuardManager implements Listener {

    private final RCCitiesPlugin plugin;
    private final WorldGuardPlugin worldGuard;

    public WorldGuardManager(RCCitiesPlugin plugin, WorldGuardPlugin worldGuard) {

        this.plugin = plugin;
        this.worldGuard = worldGuard;
    }

    public boolean claimable(Location location) {

        BlockVector3 blockVector3 = BukkitAdapter.asBlockVector(location);
        ApplicableRegionSet regions = plugin.getRegionContainer()
                .get(BukkitAdapter.adapt(location.getWorld())).getApplicableRegions(blockVector3);
        if (regions.size() == 0) {
            return true;
        }
        for (ProtectedRegion region : regions) {
            for (String ignoredRegion : plugin.getPluginConfig().getIgnoredRegions()) {
                if (!region.getId().startsWith(ignoredRegion)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void save() {

        for (World world : Bukkit.getServer().getWorlds()) {
            try {
                plugin.getRegionContainer().get(BukkitAdapter.adapt(world)).save();
            } catch (StorageException e) {
                plugin.getLogger().warning(e.getMessage());
            }
        }
    }



    /**
     *  Allow pistons move across regions
     */

//    private static Set<Material> allowedMaterials = new HashSet<>(Arrays.asList(
//            Material.PISTON_BASE,
//            Material.PISTON_EXTENSION,
//            Material.PISTON_MOVING_PIECE,
//            Material.PISTON_STICKY_BASE
//    ));
//
//    private Map<PlaceBlockEvent, Integer> events = new HashMap<>();
//
//    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
//    public void onPlaceBlockLowest(final PlaceBlockEvent event) {
//
//        // we are only interested in block causes
//        if(!(event.getCause().getRootCause() instanceof Block)) return;
//
//        Block block = (Block) event.getCause().getRootCause();
//
//        // process pistons
//        if(allowedMaterials.contains(block.getType())) {
//            events.put(event, event.getBlocks().size());
//        }
//    }
//
//    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
//    public void onPlaceBlockHighest(final PlaceBlockEvent event) {
//
//        // we are only interested in block causes
//        if(!(event.getCause().getRootCause() instanceof Block)) return;
//
//        // get original list size
//        Integer originalSize = events.remove(event);
//
//        // event was not tracked
//        if(originalSize == null) return;
//
//        // we are only interested in cancelled events
//        if(!event.isCancelled()) return;
//
//        RaidCraft.LOGGER.info("[RCCDebug] Cancelled PlaceBlockEvent: " + event.getCause().getRootCause().getClass().getName());
//
//        Block block = (Block) event.getCause().getRootCause();
//
//        RaidCraft.LOGGER.info("[RCCDebug] BlockCause: " + block.getType());
//
//        // process pistons
//        if(allowedMaterials.contains(block.getType())) {
//
//            if(event.getBlocks().size() != originalSize) {
//                RaidCraft.LOGGER.info("[RCCDebug] Original size: " + originalSize + " | Current size: " + event.getBlocks().size());
//                for(int i = 0; i < originalSize - event.getBlocks().size(); i++) {
//                    event.getBlocks().add(block);
//                }
//            }
//            event.setCancelled(false);
//        }
//    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onPistonExtend(BlockPistonExtendEvent event) {

        // allow pistons
        event.setCancelled(false);
    }
}
