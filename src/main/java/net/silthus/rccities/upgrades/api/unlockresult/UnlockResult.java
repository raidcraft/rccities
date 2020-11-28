package net.silthus.rccities.upgrades.api.unlockresult;

/**
 * @author Philip Urban
 */
public class UnlockResult {

    private boolean successful;
    private String longReason;

    public UnlockResult() {}

    public UnlockResult(boolean successful, String shortReason, String longReason) {

        this.successful = successful;
        this.longReason = longReason;
    }

    public void clearReasons() {

        this.longReason = null;
    }

    public void setSuccessful(boolean successful) {

        this.successful = successful;
    }

    public void setLongReason(String longReason) {

        this.longReason = longReason;
    }

    public boolean isSuccessful() {

        return successful;
    }

    public String getLongReason() {

        return longReason;
    }
}
