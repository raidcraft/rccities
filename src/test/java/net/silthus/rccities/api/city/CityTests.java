package net.silthus.rccities.api.city;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import net.silthus.rccities.DatabaseCity;
import net.silthus.rccities.RCCitiesPlugin;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

public class CityTests {

    private ServerMock server;
    private Player player;
    private RCCitiesPlugin plugin;

    @BeforeEach
    void setUp() {
        this.server = MockBukkit.mock();
        this.plugin = MockBukkit.load(RCCitiesPlugin.class);
        this.player = server.addPlayer();
    }

    @AfterEach
    void tearDown() {

        MockBukkit.unmock();
    }

    @Nested
    @DisplayName("Money")
    class Money {

        @Test
        @DisplayName("Get current balance")
        void balance() {

            City city = new DatabaseCity("Test City",
                    server.getWorlds().get(0).getSpawnLocation(), player.getUniqueId());

            assertThat(city.getMoney() == 0D).isTrue();

            city.depositMoney(100D);
            assertThat(city.getMoney() == 100D).isTrue();

            city.depositMoney(123D);
            assertThat(city.getMoney() == 123D).isTrue();
        }

        @Test
        @DisplayName("Withdraw")
        void withdraw() {

        }

        @Test
        @DisplayName("Deposit")
        void deposit() {

        }
    }
}
