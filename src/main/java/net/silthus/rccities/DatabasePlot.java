package net.silthus.rccities;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.plot.AbstractPlot;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.tables.TAssignment;
import net.silthus.rccities.tables.TPlot;
import net.silthus.rccities.util.RaidCraftException;
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

    private final Map<UUID, Resident> assignedResidents = new HashMap<>();

    public DatabasePlot(Location location, City city) {

        super(location, city);
    }

    public DatabasePlot(TPlot tPlot) {

        //XXX setter call order is important!!!
        this.id = tPlot.id();

        City city = RCCitiesPlugin.getPlugin().getCityManager().getCity(tPlot.getCity().getName());
        assert city != null : "City of plot is null!";
        this.city = city;

        Location location = new Location(city.getSpawn().getWorld(), tPlot.getX(), 0, tPlot.getZ());
        this.location = location;

        this.region = RCCitiesPlugin.getPlugin().getRegionContainer()
                .get(BukkitAdapter.adapt(location.getWorld())).getRegion(getRegionName());
        loadAssignments();
    }

    @Override
    public void setFlag(Player player, String flagName, String flagValue) throws RaidCraftException {

        RCCitiesPlugin.getPlugin().getFlagManager().setPlotFlag(this, player, flagName, flagValue);
    }

    @Override
    public void removeFlag(String flagName) {

        RCCitiesPlugin.getPlugin().getFlagManager().removePlotFlag(this, flagName);
    }

    @Override
    public void refreshFlags() {

        RCCitiesPlugin.getPlugin().getFlagManager().refreshPlotFlags(this);
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
        tAssignment.save();
        updateRegion(false);
    }

    @Override
    public void removeResident(Resident resident) {

        Resident removedResident = assignedResidents.remove(resident.getPlayerId());
        if (removedResident != null) {
            // delete assignment
            TAssignment assignment = TAssignment.find.query()
                    .where().eq("resident_id", removedResident.getId())
                    .eq("plot_id", getId()).findOne();
            assignment.delete();
            // update region
            updateRegion(false);
        }
    }

    private void loadAssignments() {

        List<TAssignment> assignments = TAssignment.find.query()
                .where().eq("plot_id", getId()).findList();
        for (TAssignment assignment : assignments) {

            Resident resident = RCCitiesPlugin.getPlugin().getResidentManager()
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
        tPlot.save();
        this.id = tPlot.id();
    }

    @Override
    public void delete() {

        super.delete();
        RCCitiesPlugin plugin = RCCitiesPlugin.getPlugin();

        // delete from cache
        plugin.getPlotManager().removeFromCache(this);

        // delete plot
        TPlot tPlot = TPlot.find.byId(getId());
        tPlot.delete();
    }
}
