package me.corriekay.pokegoutil.utils.helpers;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * A class that provides some usability functions for collections.
 */
public final class CollectionHelper {
    /** Prevent initializing this class. */
    private CollectionHelper() {
    }

    /**
     * Function that provides the empty list needed for data.
     *
     * @param <T>     The type of the list.
     * @param ignored The class type.
     * @return The list.
     */
    public static <T> ArrayList<T> provideArrayList(final Class<T> ignored) {
        return new ArrayList<>();
    }
}
