package net.silthus.rccities.upgrades.api.requirement;

public interface Requirement<T> {

    String getDescription(T entity);

    String getReason(T entity);

    boolean test(T entity);
}
