package net.silthus.rccities.requirements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author Silthus
 */
public class ExampleTrigger extends Trigger implements Listener {

    public ExampleTrigger() {

        super("city", "create", "levelup", "member.join");
    }

    @EventHandler
    public void onCityCreate(PlayerInteractEvent event) {

        informListeners("create", event.getPlayer());
    }
}
