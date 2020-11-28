package net.silthus.rccities.api.request;

/**
 * @author Philip Urban
 */
public abstract class AbstractRequest implements Request {

    protected boolean rejected;
    protected String rejectReason;

    @Override
    public boolean isRejected() {

        return rejected;
    }

    @Override
    public String getRejectReason() {

        return rejectReason;
    }
}
