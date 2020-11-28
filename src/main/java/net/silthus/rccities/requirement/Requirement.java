package net.silthus.rccities.requirement;

public interface Requirement<T> {

    String getName();

    String getDescription();

    RequirementResolver<T> getResolver();

    boolean isMet(T object);

    String getShortReason();

    String getLongReason();
}
