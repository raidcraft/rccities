package net.silthus.rccities.api.plot;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.raidcraft.RaidCraft;
import de.raidcraft.rccities.RCCitiesPlugin;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.resident.Resident;
import de.raidcraft.rccities.api.resident.RolePermission;
import org.bukkit.Chunk;
import org.bukkit.Location;

/**
 * @author Philip Urban
 */
public abstract class AbstractPlot implements Plot {

    protected int id;
    protected Location location;
    protected ProtectedRegion region;
    protected City city;

    protected AbstractPlot() {

    }

    protected AbstractPlot(Location location, City city) {

        Location simpleLocation = new Location(location.getWorld(), location.getChunk().getX() * 16 + 8, 0, location.getChunk().getZ() * 16 + 8);
        this.location = simpleLocation;
        this.city = city;

        save();
        updateRegion(true);
    }

    @Override
    public final int getId() {

        return id;
    }

    @Override
    public final String getRegionName() {

        return city.getName().toLowerCase() + "_" + getId();
    }

    @Override
    public final Location getLocation() {

        return location;
    }

    @Override
    public final ProtectedRegion getRegion() {

        return region;
    }

    @Override
    public final City getCity() {

        return city;
    }

    @Override
    public final void updateRegion(boolean create) {

        // force create region
        if (create) {
            RegionManager regionManager = RaidCraft.getComponent(RCCitiesPlugin.class).getWorldGuard().getRegionManager(location.getWorld());
            if (regionManager.getRegion(getRegionName()) != null) {
                regionManager.removeRegion(getRegionName());
            }

            Chunk chunk = location.getChunk();
            BlockVector vector1 = new BlockVector(
                    chunk.getX() * 16,
                    0,
                    chunk.getZ() * 16
            );
            BlockVector vector2 = new BlockVector(
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
            region.setFlag(DefaultFlag.MOB_DAMAGE, StateFlag.State.ALLOW);
            region.setFlag(DefaultFlag.MOB_SPAWNING, StateFlag.State.DENY);
            region.setFlag(DefaultFlag.CREEPER_EXPLOSION, StateFlag.State.DENY);
            region.setFlag(DefaultFlag.GHAST_FIREBALL, StateFlag.State.DENY);
            region.setFlag(DefaultFlag.ENDER_BUILD, StateFlag.State.DENY);
            region.setFlag(DefaultFlag.FIRE_SPREAD, StateFlag.State.DENY);
            region.setFlag(DefaultFlag.LIGHTNING, StateFlag.State.DENY);
            region.setFlag(DefaultFlag.CHEST_ACCESS, StateFlag.State.ALLOW);
            region.setFlag(DefaultFlag.PLACE_VEHICLE, StateFlag.State.ALLOW);
            region.setFlag(DefaultFlag.DESTROY_VEHICLE, StateFlag.State.ALLOW);
            region.setFlag(DefaultFlag.GRASS_SPREAD, StateFlag.State.ALLOW);

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
                    RaidCraft.getComponent(RCCitiesPlugin.class).getLogger()
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

        RaidCraft.getComponent(RCCitiesPlugin.class).getWorldGuard().getRegionManager(location.getWorld()).removeRegion(getRegionName());
    }
}
