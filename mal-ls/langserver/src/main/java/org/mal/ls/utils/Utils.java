package org.mal.ls.utils;

public class Utils {

    public static String decapitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        char c[] = str.toCharArray();
        c[0] = Character.toLowerCase(c[0]);

        return new String(c);
    }
}