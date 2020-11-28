package net.silthus.rccities;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.plot.AbstractPlot;
import de.raidcraft.rccities.api.resident.Resident;
import de.raidcraft.rccities.tables.TAssignment;
import de.raidcraft.rccities.tables.TPlot;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Philip Urban
 */
public class DatabasePlot extends AbstractPlot {

    private Map<UUID, Resident> assignedResidents = new HashMap<>();

    public DatabasePlot(Location location, City city) {

        super(location, city);
    }

    public DatabasePlot(TPlot tPlot) {

        //XXX setter call order is important!!!
        this.id = tPlot.getId();

        City city = RaidCraft.getComponent(RCCitiesPlugin.class).getCityManager().getCity(tPlot.getCity().getName());
        assert city != null : "City of plot is null!";
        this.city = city;

        Location location = new Location(city.getSpawn().getWorld(), tPlot.getX(), 0, tPlot.getZ());
        this.location = location;

        this.region = RaidCraft.getComponent(RCCitiesPlugin.class).getWorldGuard().getRegionManager(location.getWorld()).getRegion(getRegionName());
        loadAssignments();
    }

    @Override
    public void setFlag(Player player, String flagName, String flagValue) throws RaidCraftException {

        RaidCraft.getComponent(RCCitiesPlugin.class).getFlagManager().setPlotFlag(this, player, flagName, flagValue);
    }

    @Override
    public void removeFlag(String flagName) {

        RaidCraft.getComponent(RCCitiesPlugin.class).getFlagManager().removePlotFlag(this, flagName);
    }

    @Override
    public void refreshFlags() {

        RaidCraft.getComponent(RCCitiesPlugin.class).getFlagManager().refreshPlotFlags(this);
    }

    @Override
    public List<Resident> getAssignedResidents() {

        // prevent NPE!?!?!?
        if (assignedResidents == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(assignedResidents.values());
    }

    @Override
    public void assignResident(Resident resident) {

        if (assignedResidents.containsKey(resident.getPlayerId())) return;
        assignedResidents.put(resident.getPlayerId(), resident);
        TAssignment tAssignment = new TAssignment();
        tAssignment.setPlot(this);
        tAssignment.setResident(resident);
        RaidCraft.getDatabase(RCCitiesPlugin.class).save(tAssignment);
        updateRegion(false);
    }

    @Override
    public void removeResident(Resident resident) {

        Resident removedResident = assignedResidents.remove(resident.getPlayerId());
        if (removedResident != null) {
            // delete assignment
            TAssignment assignment = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TAssignment.class)
                    .where().eq("resident_id", removedResident.getId())
                    .eq("plot_id", getId()).findUnique();
            RaidCraft.getDatabase(RCCitiesPlugin.class).delete(assignment);
            // update region
            updateRegion(false);
        }
    }

    private void loadAssignments() {

        List<TAssignment> assignments = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TAssignment.class)
                .where().eq("plot_id", getId()).findList();
        for (TAssignment assignment : assignments) {

            Resident resident = RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager()
                    .getResident(assignment.getResident().getPlayerId(), getCity());
            if (resident == null) continue;
            assignedResidents.put(resident.getPlayerId(), resident);
        }
    }

    @Override
    public void save() {

        TPlot tPlot = new TPlot();
        tPlot.setCity(getCity());
        tPlot.setX(getLocation().getBlockX());
        tPlot.setZ(getLocation().getBlockZ());
        RaidCraft.getDatabase(RCCitiesPlugin.class).save(tPlot);
        this.id = tPlot.getId();
    }

    @Override
    public void delete() {

        super.delete();
        RCCitiesPlugin plugin = RaidCraft.getComponent(RCCitiesPlugin.class);

        // delete from cache
        plugin.getPlotManager().removeFromCache(this);

        // delete plot
        TPlot tPlot = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TPlot.class, getId());
        RaidCraft.getDatabase(RCCitiesPlugin.class).delete(tPlot);
    }
}
