package net.silthus.rccities;

import net.silthus.rccities.api.city.City;
import net.silthus.rccities.api.request.AbstractUpgradeRequest;
import net.silthus.rccities.tables.TUpgradeRequest;
import net.silthus.rccities.upgrades.api.level.UpgradeLevel;

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

        TUpgradeRequest tUpgradeRequest = TUpgradeRequest.find.query().where()
                .eq("city_id", getCity().getId()).ieq("level_identifier", getUpgradeLevel().getId()).findOne();
        if (tUpgradeRequest == null) {
            tUpgradeRequest = new TUpgradeRequest();
            tUpgradeRequest.setCity(getCity());
            tUpgradeRequest.setInfo(getInfo());
            tUpgradeRequest.setLevelIdentifier(getUpgradeLevel().getId());
            tUpgradeRequest.setRejected(isRejected());
            tUpgradeRequest.setAccepted(isAccepted());
            tUpgradeRequest.setRejectReason(getRejectReason());
            tUpgradeRequest.setRejectDate(new Timestamp(rejectDate));
            tUpgradeRequest.save();
        } else {
            tUpgradeRequest.setCity(getCity());
            tUpgradeRequest.setInfo(getInfo());
            tUpgradeRequest.setLevelIdentifier(getUpgradeLevel().getId());
            tUpgradeRequest.setRejected(isRejected());
            tUpgradeRequest.setAccepted(isAccepted());
            tUpgradeRequest.setRejectReason(getRejectReason());
            tUpgradeRequest.setRejectDate(new Timestamp(rejectDate));
            tUpgradeRequest.update();
        }
    }

    @Override
    public void delete() {

        TUpgradeRequest tUpgradeRequest = TUpgradeRequest.find.query()
                .where()
                .eq("city_id", getCity().getId()).ieq("level_identifier", getUpgradeLevel().getId()).findOne();
        if (tUpgradeRequest != null) {
            tUpgradeRequest.delete();
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
