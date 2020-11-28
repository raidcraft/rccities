package net.silthus.rccities.api.request;

/**
 * @author Philip Urban
 */
public interface Request {

    boolean isRejected();

    String getRejectReason();

    void accept();

    void reactivate();

    void reject(String reason);
}
