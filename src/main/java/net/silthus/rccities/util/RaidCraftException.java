package net.silthus.rccities.util;

/**
 * @author Silthus
 */
public class RaidCraftException extends Throwable {

    public RaidCraftException(String message) {

        super(message);
    }

    public RaidCraftException(String message, Throwable cause) {

        super(message, cause);
    }

    public RaidCraftException(Throwable cause) {

        super(cause);
    }

    public RaidCraftException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }
}
