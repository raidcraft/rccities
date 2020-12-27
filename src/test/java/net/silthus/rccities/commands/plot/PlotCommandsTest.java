package net.silthus.rccities.commands.plot;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import co.aikar.commands.InvalidCommandArgument;
import net.silthus.rccities.RCCitiesPlugin;
import net.silthus.rccities.api.flags.FlagInformation;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

class PlotCommandsTest {

    private ServerMock server;
    private RCCitiesPlugin plugin;

    @BeforeEach
    void setUp() {

        server = MockBukkit.mock();
        plugin = MockBukkit.load(RCCitiesPlugin.class);
    }

    @AfterEach
    void tearDown() {

        MockBukkit.unmock();
    }

    @Nested
    @DisplayName("setflag(...)")
    class PlotCommands {


        @BeforeEach
        void setUp() {
        }

        @Nested
        @DisplayName("/plot flag")
        class setFlag {

            @Test
            @Disabled
            @DisplayName("Without flag name and value should return list of registered command flags")
            public void withoutFlagNameAndValue() {

                try {
                    server.dispatchCommand(server.getConsoleSender(), "plot flag");
                    assertThat(false).isTrue();
                } catch (InvalidCommandArgument e) {

                    for(FlagInformation info : plugin.getFlagManager().getRegisteredPlotFlagInformationList()) {
                        if(!info.commandFlag()) {
                            assertThat(!e.getMessage().contains(info.name())).isTrue();
                        } else {
                            assertThat(e.getMessage().contains(info.name())).isTrue();
                        }
                    }
                }
            }
        }
    }
}