package net.silthus.rccities.util;

import org.bukkit.Location;

public class LocationUtil {

    public static final int CHUNK_BLOCK_WIDTH = 16;

    public static int getChunkX(Location location) {

        return getChunkCoordinate(location.getX());
    }

    public static int getChunkZ(Location location) {

        return getChunkCoordinate(location.getZ());
    }

    private static int getChunkCoordinate(double coordinate) {

        return (int)Math.floor(coordinate / 16D);
    }
}
