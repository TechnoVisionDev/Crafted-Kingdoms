package com.technovision.craftedkingdoms.util;

public class StringUtils {

    public static boolean isAlpha(String s) {
        if (s == null) {
            return false;
        }
        for (char c : s.toCharArray()) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

}
