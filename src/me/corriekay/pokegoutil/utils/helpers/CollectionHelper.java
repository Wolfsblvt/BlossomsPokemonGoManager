package me.corriekay.pokegoutil.utils.helpers;

import java.util.ArrayList;
import java.util.Set;

public final class CollectionHelper {
    /** Prevent initializing this class. */
    private CollectionHelper() {
    }

    public static String joinStringArray(String[] args) {
        return joinStringArray(args, "");
    }

    public static String joinArrayList(ArrayList<String> args) {
        return joinArrayList(args, "");
    }

    public static String joinStringSet(Set<String> args) {
        return joinStringSet(args, "");
    }

    public static String joinStringArray(String[] args, String delimiter) {
        return joinStringArray(args, delimiter, 0);
    }

    public static String joinArrayList(ArrayList<String> args, String delimiter) {
        return joinArrayList(args, delimiter, 0);
    }

    public static String joinStringSet(Set<String> args, String delimiter) {
        return joinStringSet(args, delimiter, 0);
    }

    public static String joinStringArray(String[] args, String delimiter, int startingIndex) {
        StringBuilder s = new StringBuilder();
        for (int i = startingIndex; i < args.length; i++) {
            s.append(args[i]);
            if (!(i + 1 >= args.length)) {
                s.append(delimiter);
            }
        }
        return s.toString();
    }

    public static String joinArrayList(ArrayList<String> args, String delimiter, int startingIndex) {
        StringBuilder s = new StringBuilder();
        for (int i = startingIndex; i < args.size(); i++) {
            s.append(args.get(i));
            if (!(i + 1 >= args.size())) {
                s.append(delimiter);
            }
        }
        return s.toString();
    }

    public static String joinStringSet(Set<String> args, String delimiter, int startingIndex) {
        int count = startingIndex;
        StringBuilder s = new StringBuilder();
        for (String string : args) {
            if (count >= startingIndex) {
                s.append(string);
                if (count + 1 < args.size()) {
                    s.append(delimiter);
                }
            }
            count++;
        }
        return s.toString();
    }

    public static String getStringFromIndex(int index, String[] args) {
        String s = "";
        for (int i = index; i < args.length; i++) {
            s += args[i] + " ";
        }
        return s.trim();
    }
}
