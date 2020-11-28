package net.silthus.rccities;

import de.raidcraft.RaidCraft;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.plot.Plot;
import de.raidcraft.rccities.api.resident.AbstractResident;
import de.raidcraft.rccities.api.resident.Role;
import de.raidcraft.rccities.tables.TResident;

import java.util.UUID;

/**
 * @author Philip Urban
 */
public class DatabaseResident extends AbstractResident {

    public DatabaseResident(UUID playerId, Role profession, City city) {

        super(playerId, profession, city);

        RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager().addPrefixSkill(this);
    }

    public DatabaseResident(TResident tResident) {

        //XXX setter call order is important!!!
        this.id = tResident.getId();

        City city = RaidCraft.getComponent(RCCitiesPlugin.class).getCityManager().getCity(tResident.getCity().getName());
        assert city != null : "City of resident is null!";
        this.city = city;
        this.playerId = tResident.getPlayerId();
        setRole(Role.valueOf(tResident.getProfession()));
    }

    @Override
    public void save() {

        // save new resident
        if (getId() == 0) {
            TResident tResident = new TResident();
            tResident.setCity(getCity());
            tResident.setPlayerId(getPlayerId());
            tResident.setProfession(getRole().name());
            RaidCraft.getDatabase(RCCitiesPlugin.class).save(tResident);
            this.id = tResident.getId();
        }
        // update existing resident
        else {
            TResident tResident = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TResident.class, getId());
            tResident.setProfession(getRole().name());
            RaidCraft.getDatabase(RCCitiesPlugin.class).update(tResident);
        }
    }

    @Override
    public void delete() {

        RCCitiesPlugin plugin = RaidCraft.getComponent(RCCitiesPlugin.class);

        // remove prefix skill
        RaidCraft.getComponent(RCCitiesPlugin.class).getResidentManager().removePrefixSkill(this);

        plugin.getResidentManager().removeFromCache(this);

        TResident tResident = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TResident.class, getId());
        RaidCraft.getDatabase(RCCitiesPlugin.class).delete(tResident);

        for (Plot plot : plugin.getPlotManager().getPlots(city)) {
            plot.removeResident(this);
            plot.updateRegion(false);
        }
    }
}
