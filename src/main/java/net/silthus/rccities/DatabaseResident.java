package net.silthus.rccities;


import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.plot.Plot;
import net.silthus.rccities.api.resident.AbstractResident;
import net.silthus.rccities.api.resident.Role;
import net.silthus.rccities.tables.TResident;

import java.util.UUID;

/**
 * @author Philip Urban
 */
public class DatabaseResident extends AbstractResident {

    public DatabaseResident(UUID playerId, Role profession, City city) {

        super(playerId, profession, city);

        RCCitiesPlugin.getPlugin().getResidentManager().addPrefixSkill(this);
    }

    public DatabaseResident(TResident tResident) {

        //XXX setter call order is important!!!
        this.id = tResident.id();

        City city = RCCitiesPlugin.getPlugin().getCityManager().getCity(tResident.getCity().getName());
        assert city != null : "City of resident is null!";
        this.city = city;
        this.playerId = tResident.getPlayerId();
        setRole(Role.valueOf(tResident.getProfession()));
    }

    @Override
    public void save() {

        // save new resident
        if (getId() == null) {
            TResident tResident = new TResident();
            tResident.setCity(getCity());
            tResident.setPlayerId(getPlayerId());
            tResident.setProfession(getRole().name());
            tResident.save();
            this.id = tResident.id();
        }
        // update existing resident
        else {
            TResident tResident = TResident.find.byId(getId());
            tResident.setProfession(getRole().name());
            tResident.update();
        }
    }

    @Override
    public void delete() {

        RCCitiesPlugin plugin = RCCitiesPlugin.getPlugin();

        // remove prefix skill
        plugin.getResidentManager().removePrefixSkill(this);

        plugin.getResidentManager().removeFromCache(this);

        TResident tResident = TResident.find.byId(getId());
        tResident.delete();

        for (Plot plot : plugin.getPlotManager().getPlots(city)) {
            plot.removeResident(this);
            plot.updateRegion(false);
        }
    }
}
