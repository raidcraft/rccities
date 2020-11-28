package net.silthus.rccities.api.flags;

import org.bukkit.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Philip Urban
 */
public enum FlagType {

    STRING("Es wurde Text erwartet!", "Beliebiger Text"),
    INTEGER("Es wurde eine Ganzzahl erwartet!", "Ganzzahl, z.B.: 3, 1337"),
    DOUBLE("Es wurde eine Kommazahl erwartet!", "Kommazahl, z.B.: 2.0, 14.54"),
    BOOLEAN("Es wurde ein Zustand (ja/nein) erwartet!", "Zustand, z.B.: Ja, Nein, On, Off"),
    MONEY("Es wurde ein Geldbetrag erwartet!", "Geldbetrag, Bsp.: 4G, 3S7K");

    private String errorMsg;
    private String typeInfo;
    private static final Pattern CURRENCY_PATTERN = Pattern.compile("^((\\d+)[gG])?\\s?((\\d+)[sS])?\\s?((\\d+)[cCkK]?)?$");

    private FlagType(String errorMsg, String typeInfo) {

        this.errorMsg = errorMsg;
        this.typeInfo = typeInfo;
    }

    public String getErrorMsg() {

        return errorMsg;
    }

    public String getTypeInfo() {

        return typeInfo;
    }

    public boolean validate(String input) {

        if (this == INTEGER) {
            try {
                Integer.valueOf(input);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (this == DOUBLE) {
            try {
                Double.valueOf(input);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (this == BOOLEAN) {
            if (input.equalsIgnoreCase("true")
                    || input.equalsIgnoreCase("allow")
                    || input.equalsIgnoreCase("deny")
                    || input.equalsIgnoreCase("false")
                    || input.equalsIgnoreCase("wahr")
                    || input.equalsIgnoreCase("falsch")
                    || input.equalsIgnoreCase("ja")
                    || input.equalsIgnoreCase("nein")
                    || input.equalsIgnoreCase("yes")
                    || input.equalsIgnoreCase("no")
                    || input.equalsIgnoreCase("an")
                    || input.equalsIgnoreCase("aus")
                    || input.equalsIgnoreCase("on")
                    || input.equalsIgnoreCase("off")
                    || input.equalsIgnoreCase("1")
                    || input.equalsIgnoreCase("0")) {
                return true;
            } else {
                return false;
            }
        } else if (this == MONEY) {
            Matcher matcher = CURRENCY_PATTERN.matcher(input);
            return matcher.matches();
        }

        return true;
    }

    public boolean convertToBoolean(String value) {

        if (value.equalsIgnoreCase("true")
                || value.equalsIgnoreCase("allow")
                || value.equalsIgnoreCase("wahr")
                || value.equalsIgnoreCase("ja")
                || value.equalsIgnoreCase("yes")
                || value.equalsIgnoreCase("an")
                || value.equalsIgnoreCase("on")
                || value.equalsIgnoreCase("1")) {
            return true;
        } else {
            return false;
        }
    }

    public int convertToInteger(String value) {

        int result = 0;
        try {
            result = Integer.valueOf(value);
        } catch (NumberFormatException e) {
        }
        return result;
    }

    public double convertToDouble(String value) {

        double result = 0;
        try {
            result = Double.valueOf(value);
        } catch (NumberFormatException e) {
        }
        return result;
    }

    public double convertToMoney(String input) {

        // lets parse the string for the different money values
        input = ChatColor.stripColor(input).replace("●", "");
        Matcher matcher = CURRENCY_PATTERN.matcher(input);
        double value = 0.0;
        if (matcher.matches()) {
            // lets grap the different groups and check for input
            // group 2 = gold
            // group 4 = silver
            // group 6 = copper
            if (matcher.group(2) != null) {
                try {
                    value += 100 * Integer.parseInt(matcher.group(2));
                } catch (NumberFormatException e) {
                    value = 0;
                }
            }
            if (matcher.group(4) != null) {
                try {
                    value += Integer.parseInt(matcher.group(4));
                } catch (NumberFormatException e) {
                    value = 0;
                }
            }
            if (matcher.group(6) != null) {
                try {
                    value += Integer.parseInt(matcher.group(6)) / 100.0;
                } catch (NumberFormatException e) {
                    value = 0;
                }
            }
        }
        return value;
    }

    public Object convert(String input) {

        Object converted = input;

        if (this == INTEGER) {
            try {
                converted = Integer.valueOf(input);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (this == DOUBLE) {
            try {
                converted = Double.valueOf(input);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (this == BOOLEAN) {
            if (input.equalsIgnoreCase("true")
                    || input.equalsIgnoreCase("wahr")
                    || input.equalsIgnoreCase("ja")
                    || input.equalsIgnoreCase("1")) {
                return true;
            } else if (input.equalsIgnoreCase("false")
                    || input.equalsIgnoreCase("falsch")
                    || input.equalsIgnoreCase("nein")
                    || input.equalsIgnoreCase("0")) {
                return false;
            } else {
                return null;
            }
        }

        return converted;
    }
}
