package net.silthus.rccities.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author Silthus
 */
public class QueuedCaptchaCommand extends QueuedCommand {

    private final String captcha;

    public QueuedCaptchaCommand(CommandSender sender, Object object, String method, Object... args) throws NoSuchMethodException {

        super(sender, object, method, args);
        this.captcha = generateCaptchaString();
        sender.sendMessage(ChatColor.RED + "Bitte bestätige den Befehl mit: " + ChatColor.GREEN + "/town confirm " + ChatColor.AQUA + captcha);
    }

    public String getCaptcha() {

        return captcha;
    }

    /**
     * Generate a CAPTCHA String consisting of random lowercase and uppercase letters, and numbers.
     */
    public String generateCaptchaString() {

        int length = 5 + (Math.abs(MathUtil.RANDOM.nextInt()) % 3);

        StringBuilder captchaStringBuffer = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int baseCharNumber = Math.abs(MathUtil.RANDOM.nextInt()) % 62;
            int charNumber;
            if (baseCharNumber < 26) {
                charNumber = 65 + baseCharNumber;
            } else if (baseCharNumber < 52) {
                charNumber = 97 + (baseCharNumber - 26);
            } else {
                charNumber = 48 + (baseCharNumber - 52);
            }
            captchaStringBuffer.append((char) charNumber);
        }

        return captchaStringBuffer.toString();
    }
}
