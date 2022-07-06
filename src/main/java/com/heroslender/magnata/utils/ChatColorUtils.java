package com.heroslender.magnata.utils;

import net.md_5.bungee.api.ChatColor;

public class ChatColorUtils {
    private static final char COLOR_CHAR = '&';
    private static final char MC_COLOR_CHAR = ChatColor.COLOR_CHAR;
    private static final char HEX_CHAR = '#';
    private static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
    private static final String HEX_CODES = "0123456789AaBbCcDdEeFf";

    public static String translateColors(String input) {
        if (!input.contains("&#")) {
            return ChatColor.translateAlternateColorCodes(COLOR_CHAR, input);
        }

        StringBuilder sb = new StringBuilder();
        int lastIndex = 0;
        int startIndex;
        while (true) {
            startIndex = input.indexOf(COLOR_CHAR, lastIndex);
            if (startIndex == -1) {
                break;
            }

            for (int j = lastIndex; j < startIndex; j++) {
                sb.append(input.charAt(j));
            }

            char colorChar = input.charAt(startIndex + 1);
            if (colorChar == HEX_CHAR) {
                boolean isValid = true;
                for (int j = startIndex + 2; j < startIndex + 8; j++) {
                    if (HEX_CODES.indexOf(input.charAt(j)) == -1) {
                        isValid = false;
                        break;
                    }
                }

                if (isValid) {
                    sb.append(MC_COLOR_CHAR).append('x');
                    for (int j = startIndex + 2; j < startIndex + 8; j++) {
                        sb.append(MC_COLOR_CHAR).append(input.charAt(j));
                    }
                }

                lastIndex = startIndex + 8;
            } else if (ALL_CODES.indexOf(colorChar) >= 0) {
                sb.append(MC_COLOR_CHAR).append(colorChar);
                lastIndex = startIndex + 2;
            }

            if (lastIndex < startIndex) {
                lastIndex = startIndex;
            }
        }

        for (int j = lastIndex; j < input.length(); j++) {
            sb.append(input.charAt(j));
        }

        return sb.toString();
    }
}
