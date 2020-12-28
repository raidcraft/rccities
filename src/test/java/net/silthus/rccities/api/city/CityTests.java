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

            assertThat(city.getMoney() == 0).isTrue();

            assertThat(city.depositMoney(100)).isTrue();
            assertThat(city.getMoney() == 100).isTrue();

            assertThat(city.depositMoney(123)).isTrue();
            assertThat(city.getMoney() == 100D + 123).isTrue();

            assertThat(city.withdrawMoney(100)).isTrue();
            assertThat(city.getMoney() == 123).isTrue();
        }

        @Test
        @DisplayName("Withdraw")
        void withdraw() {

            City city = new DatabaseCity("Test City",
                    server.getWorlds().get(0).getSpawnLocation(), player.getUniqueId());

            assertThat(city.getMoney() == 0).isTrue();

            // Withdraw with higher amount than balance should not be possible
            assertThat(city.withdrawMoney(1)).isFalse();

            // Withdraw to zero balance should be possible
            assertThat(city.depositMoney(1)).isTrue();
            assertThat(city.withdrawMoney(1)).isTrue();
            assertThat(city.getMoney() == 0).isTrue();

            // Withdraw with negativ amount should not be possible
            assertThat(city.withdrawMoney(-1)).isFalse();
            assertThat(city.getMoney() == 0).isTrue();

            // Deposit zero amount should not be possible
            assertThat(city.withdrawMoney(0)).isFalse();
            assertThat(city.getMoney() == 0).isTrue();

            // Withdraw with valid amount should be possible
            assertThat(city.depositMoney(100)).isTrue();
            assertThat(city.getMoney() == 100).isTrue();
            assertThat(city.withdrawMoney(60)).isTrue();
            assertThat(city.getMoney() == 40).isTrue();
        }

        @Test
        @DisplayName("Deposit")
        void deposit() {

            City city = new DatabaseCity("Test City",
                    server.getWorlds().get(0).getSpawnLocation(), player.getUniqueId());

            assertThat(city.getMoney() == 0).isTrue();

            // Deposit in empty bank should be possible
            assertThat(city.getMoney() == 0).isTrue();
            assertThat(city.depositMoney(1)).isTrue();
            assertThat(city.getMoney() == 1).isTrue();
            assertThat(city.withdrawMoney(1)).isTrue();
            assertThat(city.getMoney() == 0).isTrue();

            // Deposit with negativ amount should not be possible
            assertThat(city.depositMoney(-1)).isFalse();
            assertThat(city.getMoney() == 0).isTrue();

            // Deposit zero amount should not be possible
            assertThat(city.depositMoney(0)).isFalse();
            assertThat(city.getMoney() == 0).isTrue();

            // Deposit valid amount should be possible
            assertThat(city.depositMoney(100)).isTrue();
            assertThat(city.getMoney() == 100).isTrue();
        }
    }
}
