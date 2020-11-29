package net.silthus.rccities.requirementsapi.reward;

/**
 * @author Philip Urban
 */
public interface Reward<T> {

    String getName();

    String getDescription();

    void reward(T object);
}
