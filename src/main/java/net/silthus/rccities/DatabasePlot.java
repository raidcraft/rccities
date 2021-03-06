package net.silthus.rccities;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.flags.FlagInformation;
import net.silthus.rccities.api.plot.AbstractPlot;
import net.silthus.rccities.api.resident.Resident;
import net.silthus.rccities.tables.TAssignment;
import net.silthus.rccities.tables.TPlot;
import net.silthus.rccities.util.RaidCraftException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Philip Urban
 */
public class DatabasePlot extends AbstractPlot {

    private final Map<UUID, Resident> assignedResidents = new HashMap<>();

    public DatabasePlot(Location location, City city) {

        super(location, city);

        // create schematic
        try {
            RCCitiesPlugin.instance().getSchematicManager().createSchematic(this);
        } catch (RaidCraftException e) {
            RCCitiesPlugin.instance().getLogger().warning(e.getMessage());
        }

        RCCitiesPlugin.instance().getDynmapManager().addPlotAreaMarker(this);
        RCCitiesPlugin.instance().getDynmapManager().addCityMarker(getCity());
    }

    public DatabasePlot(TPlot tPlot) {

        //XXX setter call order is important!!!
        this.id = tPlot.id();

        City city = RCCitiesPlugin.instance().getCityManager().getCity(tPlot.getCity().getName());
        assert city != null : "City of plot is null!";
        this.city = city;

        Location location = new Location(city.getSpawn().getWorld(), tPlot.getX(), 0, tPlot.getZ());
        this.location = location;

        this.region = RCCitiesPlugin.instance().getWorldGuard().getPlatform().getRegionContainer()
                .get(BukkitAdapter.adapt(location.getWorld())).getRegion(getRegionName());
        loadAssignments();
    }

    @Override
    public <T> void setFlag(Class<T> clazz, boolean value) throws RaidCraftException {
        setFlag(clazz, String.valueOf(value));
    }

    @Override
    public <T> void setFlag(Class<T> clazz, double value) throws RaidCraftException {
        setFlag(clazz, String.valueOf(value));
    }

    @Override
    public <T> void setFlag(Class<T> clazz, String value) throws RaidCraftException {
        FlagInformation annotation = clazz.getAnnotation(FlagInformation.class);
        if(annotation == null) {
            throw new RaidCraftException("Class '" + clazz.getName() + "' without FlagInformation annotation");
        }

        setFlag(annotation.name(), value);
    }

    @Override
    public void setFlag(String flagName, String flagValue) throws RaidCraftException {
        setFlag(null, flagName, flagValue);
    }

    @Override
    public void setFlag(Player player, String flagName, String flagValue) throws RaidCraftException {

        RCCitiesPlugin.instance().getFlagManager().setPlotFlag(this, player, flagName, flagValue);
    }

    @Override
    public void removeFlag(String flagName) {

        RCCitiesPlugin.instance().getFlagManager().removePlotFlag(this, flagName);
    }

    @Override
    public void refreshFlags() {

        RCCitiesPlugin.instance().getFlagManager().refreshPlotFlags(this);
    }

    @Override
    public List<Resident> getAssignedResidents() {

        // Without null check there is a NPE!? Why?
        if(assignedResidents == null) return new ArrayList<>();
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

        if(resident == null) return;
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

            Resident resident = RCCitiesPlugin.instance().getResidentManager()
                    .getResident(assignment.getResident().getPlayerId(), getCity());
            if (resident == null) continue;
            assignedResidents.put(resident.getPlayerId(), resident);
        }
    }

    @Override
    public void save() {

        TPlot tPlot = new TPlot(getCity());
        tPlot.setCity(getCity());
        tPlot.setX(getLocation().getBlockX());
        tPlot.setZ(getLocation().getBlockZ());
        tPlot.save();
        this.id = tPlot.id();
    }

    @Override
    public void delete() {

        super.delete();
        RCCitiesPlugin plugin = RCCitiesPlugin.instance();

        // delete from cache
        plugin.getPlotManager().removeFromCache(this);

        plugin.getDynmapManager().removePlotAreaMarker(this);

        // delete plot
        TPlot tPlot = TPlot.find.byId(getId());
        tPlot.delete();
    }
}
