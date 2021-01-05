package net.silthus.rccities.util;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import lombok.Getter;
import net.silthus.rccities.RCCitiesPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilTests {

    private ServerMock server;
    private WorldMock world;
    private Player player;
    private RCCitiesPlugin plugin;

    @BeforeEach
    void setUp() {
        this.server = MockBukkit.mock();
        this.plugin = MockBukkit.load(RCCitiesPlugin.class);
        this.player = server.addPlayer();
        world = new WorldMock();
        world.setName("world");
        server.addWorld(world);
    }

    @AfterEach
    void tearDown() {

        MockBukkit.unmock();
    }

    @Nested
    @DisplayName("LocationUtil")
    class LocationUtilTests {

        @Test
        @DisplayName("getChunkX")
        void getChunkX() {

            // Real test locations picked from running server with existing town plot locations
            ChunkLocationSet[] testLocations = {
                    new ChunkLocationSet(0.645, 147.946, 0, 9),
                    new ChunkLocationSet(428.759, 370.885, 26, 23),
                    new ChunkLocationSet(-507.975, 632.259, -32, 39),
                    new ChunkLocationSet(-8426.083, -918.214, -527, -58),
            };

            for(ChunkLocationSet testLocation : testLocations) {

                assertThat(testLocation.chunkX == LocationUtil.getChunkX(testLocation.getLocation())).isTrue();
                assertThat(testLocation.chunkZ == LocationUtil.getChunkZ(testLocation.getLocation())).isTrue();
            }
        }

        @Getter
        private class ChunkLocationSet {

            private Location location;
            private int chunkX;
            private int chunkZ;

            public ChunkLocationSet(double locationX, double locationZ, int chunkX, int chunkZ) {
                this.location = new Location(null, locationX, 0, locationZ);
                this.chunkX = chunkX;
                this.chunkZ = chunkZ;
            }
        }
    }
}
