package net.silthus.rccities.util;

import net.silthus.rccities.RCCitiesPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Silthus
 */
public class QueuedCommand {

    private final CommandSender sender;
    private final Object object;
    private final Object[] args;
    private final Method method;

    public QueuedCommand(final CommandSender sender, Object object, String methodName, Object... args) throws NoSuchMethodException {

        this.sender = sender;
        this.object = object;
        this.method = ReflectionUtil.getMethod(object, methodName, args);
        this.args = args;
        RCCitiesPlugin.getPlugin().queueCommand(this);
        if (!(this instanceof QueuedCaptchaCommand)) {
            sender.sendMessage(ChatColor.RED + "Bitte bestätige den Befehl mit: " + ChatColor.GREEN + "/rcconfirm");
        }
    }

    public CommandSender getSender() {

        return sender;
    }

    public void run() {

        try {
            method.setAccessible(true);
            method.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            sender.sendMessage(ChatColor.RED + e.getCause().getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            e.printStackTrace();
        }
    }
}
