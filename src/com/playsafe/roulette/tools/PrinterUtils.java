package com.playsafe.roulette.tools;

/**
 *  Util class to assist with formatting.
 */
public class PrinterUtils {
    public static String appendNameSpacing(String value) {
        if (value == null) {
            return "";
        }
        if (value.length() > 20) {
            return value.substring(0, 20);
        }
        int index = value.length();
        while (index <= 20) {
            value += " ";
            index++;
        }
        return value;
    }

    public static String appendSpacing(String value) {
        if (value == null) {
            return "";
        }
        if (value.length() > 10) {
            return value.substring(0, 10);
        }
        int index = value.length();
        while (index <= 10) {
            value += " ";
            index++;
        }
        return value;
    }
}
