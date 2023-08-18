package com.technovision.craftedkingdoms.util;

import com.technovision.craftedkingdoms.exceptions.CKException;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    public static String toSafeString(String text, int maxChars, String context) throws CKException {
        text = text.replace(" ", "_");
        text = text.replace("\"", "");
        text = text.replace("\'", "");
        if (text.length() > maxChars) {
            throw new CKException(context + " must be under "+maxChars+" characters.");
        }
        if (!StringUtils.isAlpha(text)) {
            throw new CKException(context + " can only contain letters [A-Z].");
        }
        return text;
    }

    public static String stringifyType(Material type) {
        return stringifyType(type, false);
    }

    public static String stringifyType(Material type, boolean firstWordOnly) {
        if (firstWordOnly) {
            String firstWord = type.toString().split("_")[0].toLowerCase();
            return capitalizeEachWord(firstWord);
        } else {
            String materialName = type.toString().replace('_', ' ');
            return capitalizeEachWord(materialName);
        }
    }

    public static String stringifyType(EntityType type) {
        String materialName = type.toString().replace('_', ' ');
        return capitalizeEachWord(materialName);
    }

    private static String capitalizeEachWord(String str) {
        String[] words = str.split(" ");
        StringBuilder capitalizedString = new StringBuilder();
        for (String word : words) {
            String first = word.substring(0, 1).toUpperCase();
            String afterFirst = word.substring(1).toLowerCase();
            capitalizedString.append(first).append(afterFirst).append(" ");
        }
        return capitalizedString.toString().trim();  // Remove the last space
    }
}
