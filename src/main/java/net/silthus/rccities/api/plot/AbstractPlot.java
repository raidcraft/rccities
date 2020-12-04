package net.silthus.rccities.api.plot;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.api.resident.RolePermission;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.UUID;

/**
 * @author Philip Urban
 */
@Getter
public abstract class AbstractPlot implements Plot {

    protected UUID id;
    protected Location location;
    protected ProtectedRegion region;
    protected City city;

    protected AbstractPlot() {
    }

    protected AbstractPlot(Location location, City city) {

        this.location = new Location(location.getWorld(), location.getChunk().getX() * 16 + 8,
                0, location.getChunk().getZ() * 16 + 8);
        this.city = city;

        save();
        updateRegion(true);
    }

    @Override
    public final String getRegionName() {

        return city.getName().toLowerCase() + "_" + location.getChunk().getX() + "_" + location.getChunk().getZ();
    }

    @Override
    public final void updateRegion(boolean create) {

        // force create region
        if (create) {
            RegionManager regionManager = RCCitiesPlugin.getPlugin().getWorldGuard().getPlatform().getRegionContainer()
                    .get(BukkitAdapter.adapt(location.getWorld()));
            if (regionManager.getRegion(getRegionName()) != null) {
                regionManager.removeRegion(getRegionName());
            }

            Chunk chunk = location.getChunk();
            BlockVector3 vector1 = BlockVector3.at(
                    chunk.getX() * 16,
                    0,
                    chunk.getZ() * 16
            );
            BlockVector3 vector2 = BlockVector3.at(
                    (chunk.getX() * 16) + 15,
                    location.getWorld().getMaxHeight(),
                    (chunk.getZ() * 16) + 15
            );

            ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion(getRegionName(), vector1, vector2);
            regionManager.addRegion(protectedCuboidRegion);
            region = protectedCuboidRegion;
        }

        // update flags, owner and settings (plot flags)
        if (region != null) {

            //TODO maybe we have to set other regions as parent

            // flags
            region.setFlag(Flags.MOB_DAMAGE, StateFlag.State.ALLOW);
            region.setFlag(Flags.MOB_SPAWNING, StateFlag.State.DENY);
            region.setFlag(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
            region.setFlag(Flags.GHAST_FIREBALL, StateFlag.State.DENY);
            region.setFlag(Flags.ENDER_BUILD, StateFlag.State.DENY);
            region.setFlag(Flags.FIRE_SPREAD, StateFlag.State.DENY);
            region.setFlag(Flags.LIGHTNING, StateFlag.State.DENY);
            region.setFlag(Flags.CHEST_ACCESS, StateFlag.State.ALLOW);
            region.setFlag(Flags.PLACE_VEHICLE, StateFlag.State.ALLOW);
            region.setFlag(Flags.DESTROY_VEHICLE, StateFlag.State.ALLOW);
            region.setFlag(Flags.GRASS_SPREAD, StateFlag.State.ALLOW);

            // flags
            refreshFlags();

            // owner
            DefaultDomain defaultDomain = new DefaultDomain();
            for (Resident resident : getAssignedResidents()) {
                defaultDomain.addPlayer(resident.getName());
            }
            // add city staff
            for (Resident resident : getCity().getResidents()) {
                if (!resident.getRole().hasPermission(RolePermission.BUILD_EVERYWHERE)) continue;
                if(resident.getName() == null) {
                    RCCitiesPlugin.getPlugin().getLogger()
                            .info("name of resident is null: " + resident.getId());
                    continue;
                }
                defaultDomain.addPlayer(resident.getName());
            }
            region.setOwners(defaultDomain);
        }
    }

    @Override
    public void delete() {
        RCCitiesPlugin.getPlugin().getWorldGuard().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(location.getWorld())).removeRegion(getRegionName());
    }
}
