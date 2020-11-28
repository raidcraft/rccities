package net.silthus.rccities;

import de.raidcraft.RaidCraft;
import de.raidcraft.rccities.api.city.City;
import de.raidcraft.rccities.api.request.AbstractUpgradeRequest;
import de.raidcraft.rccities.tables.TUpgradeRequest;
import de.raidcraft.rcupgrades.api.level.UpgradeLevel;

import java.sql.Timestamp;

/**
 * @author Philip Urban
 */
public class DatabaseUpgradeRequest extends AbstractUpgradeRequest {

    public DatabaseUpgradeRequest(City city, UpgradeLevel<City> upgradeLevel, String staffInfo) {

        super(city, upgradeLevel, staffInfo);
    }

    public DatabaseUpgradeRequest(TUpgradeRequest tUpgradeRequest) {

        super(tUpgradeRequest.getRCCity(), tUpgradeRequest.getUpgradeLevel(), tUpgradeRequest.getInfo());
        rejected = tUpgradeRequest.isRejected();
        accepted = tUpgradeRequest.isAccepted();
        rejectReason = tUpgradeRequest.getRejectReason();
        rejectDate = tUpgradeRequest.getRejectDate().getTime();
    }

    @Override
    public void save() {

        TUpgradeRequest tUpgradeRequest = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TUpgradeRequest.class).where()
                .eq("city_id", getCity().getId()).ieq("level_identifier", getUpgradeLevel().getId()).findUnique();
        if (tUpgradeRequest == null) {
            tUpgradeRequest = new TUpgradeRequest();
            tUpgradeRequest.setCity(getCity());
            tUpgradeRequest.setInfo(getInfo());
            tUpgradeRequest.setLevelIdentifier(getUpgradeLevel().getId());
            tUpgradeRequest.setRejected(isRejected());
            tUpgradeRequest.setAccepted(isAccepted());
            tUpgradeRequest.setRejectReason(getRejectReason());
            tUpgradeRequest.setRejectDate(new Timestamp(rejectDate));
            RaidCraft.getDatabase(RCCitiesPlugin.class).save(tUpgradeRequest);
        } else {
            tUpgradeRequest.setCity(getCity());
            tUpgradeRequest.setInfo(getInfo());
            tUpgradeRequest.setLevelIdentifier(getUpgradeLevel().getId());
            tUpgradeRequest.setRejected(isRejected());
            tUpgradeRequest.setAccepted(isAccepted());
            tUpgradeRequest.setRejectReason(getRejectReason());
            tUpgradeRequest.setRejectDate(new Timestamp(rejectDate));
            RaidCraft.getDatabase(RCCitiesPlugin.class).update(tUpgradeRequest);
        }
    }

    @Override
    public void delete() {

        TUpgradeRequest tUpgradeRequest = RaidCraft.getDatabase(RCCitiesPlugin.class).find(TUpgradeRequest.class).where()
                .eq("city_id", getCity().getId()).ieq("level_identifier", getUpgradeLevel().getId()).findUnique();
        if (tUpgradeRequest != null) {
            RaidCraft.getDatabase(RCCitiesPlugin.class).delete(tUpgradeRequest);
        }
    }

    @Override
    public void accept() {

        accepted = true;
        save();
    }

    @Override
    public void reactivate() {

        rejected = false;
        save();
    }

    @Override
    public void reject(String reason) {

        accepted = false;
        rejectReason = reason;
        rejected = true;
        rejectDate = System.currentTimeMillis();
        save();
    }
}
