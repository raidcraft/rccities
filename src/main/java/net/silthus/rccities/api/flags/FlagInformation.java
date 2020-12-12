package net.silthus.rccities.api.flags;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Silthus
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FlagInformation {

    String name();

    String friendlyName();

    FlagType type();

    FlagRefreshType refreshType() default FlagRefreshType.ON_CHANGE;

    int refreshInterval() default 60;

    int cooldown() default 30;

    boolean adminOnly() default false;

    boolean needsRefresh() default true;

    boolean commandFlag() default true;
}
