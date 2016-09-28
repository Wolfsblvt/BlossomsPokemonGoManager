package me.corriekay.pokegoutil.utils.helpers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.pokegoapi.google.common.geometry.S2CellId;
import com.pokegoapi.google.common.geometry.S2LatLng;

import me.corriekay.pokegoutil.data.enums.ExceptionMessages;
import me.corriekay.pokegoutil.utils.StringLiterals;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper class that provides utility functions concerning locations.
 */
public final class LocationHelper {
    private static final Map<Long, String> SAVED_LOCATIONS;
    private static final Gson GSON = new Gson();
    private static final File LOCATION_FILE = new File("locations.json");

    private static boolean shouldSave;

    static {
        SAVED_LOCATIONS = new ConcurrentHashMap<>();
        load();
    }

    /** Prevent initializing this class. */
    private LocationHelper() {
    }

    /**
     * Returns the location as lat long coordinates.
     *
     * @param s2CellId The cell id.
     * @return The location as coordinates.
     */
    public static LatLongLocation getCoordinates(final S2CellId s2CellId) {
        return new LatLongLocation(s2CellId);
    }

    /**
     * Returns a Future that resolves some time in the future after the location is queried from Google API.
     * It returns a string for the location based on the cell ID.
     *
     * @param s2CellId The cell ID
     * @return The location string.
     */
    public static CompletableFuture<String> getLocation(final S2CellId s2CellId) {
        return CompletableFuture.supplyAsync(
            () -> {
                String location = "";
                if (SAVED_LOCATIONS.containsKey(s2CellId.id())) {
                    location = SAVED_LOCATIONS.get(s2CellId.id());
                } else {
                    final LatLongLocation latLng = new LatLongLocation(s2CellId);
                    JSONObject json = null;
                    try {
                        json = queryJsonFromUrl(latLng.toString());

                        final JSONArray matches = json.optJSONArray("results");
                        if (matches != null && matches.length() > 0) {
                            location = matches.getJSONObject(0).getString("formatted_address");
                            SAVED_LOCATIONS.put(s2CellId.id(), location);
                        } else {
                            location = "Error: " + json.getString("status");
                        }
                    } catch (IOException | JSONException e) {
                        location = "Exception: " + e.getMessage();
                    }
                    save();
                }
                return location;
            });

    }

    /**
     * Queries the JSON for location from the Google API.
     *
     * @param latLong A string containing lat and long, like "1.124,1.566"
     * @return The JSON from the server.
     * @throws IOException   io.
     * @throws JSONException json.
     */
    private static JSONObject queryJsonFromUrl(final String latLong) throws IOException, JSONException {
        final String apiUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%s&sensor=true";
        final String formattedUrl = String.format(apiUrl, latLong.replace(" ", "%20"));
        try {
            final URL url = new URL(formattedUrl);
            final String apiResponse = FileHelper.readFile(url.openStream());
            return new JSONObject(apiResponse);
        } catch (IOException e) {
            System.out.println(ExceptionMessages.COULD_NOT_QUERY_LOCATION.with(e));
            return new JSONObject();
        }
    }

    /**
     * Saves the cached locations to location.json file.
     */
    private static void save() {
        // The map gets updated really often maybe, so we delay the save to save a bulk of it
        if (!shouldSave) {
            final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.schedule(() -> {
                // Save the location data to the location.json
                final JSONObject json = new JSONObject(GSON.toJson(SAVED_LOCATIONS));
                FileHelper.saveFile(LOCATION_FILE, json.toString(FileHelper.INDENT));
                shouldSave = false;
            }, 2, TimeUnit.SECONDS);
        }
        shouldSave = true;
    }

    /**
     * Loads saved locations from location.json file.
     */
    private static void load() {
        final Type mapType = new TypeToken<ConcurrentHashMap<Long, String>>() {
        }.getType();
        Map<Long, String> loadedLocations;
        try {
            loadedLocations = GSON.fromJson(FileHelper.readFile(LOCATION_FILE), mapType);
            System.out.println("Saved loaded locations to file.");
        } catch (JsonSyntaxException e) {
            loadedLocations = null;
            System.out.println(ExceptionMessages.COULD_NOT_LOAD_LOCATIONS.with(e));
            FileHelper.deleteFile(LOCATION_FILE, false);
        }
        if (loadedLocations != null) {
            SAVED_LOCATIONS.putAll(loadedLocations);
        }
    }

    /**
     * Class that holds lat long coordinates.
     */
    public static final class LatLongLocation {
        public final double latitude;
        public final double longitude;

        /**
         * Internal constructor to create an object of this class.
         *
         * @param s2CellId The cell.
         */
        private LatLongLocation(final S2CellId s2CellId) {
            final S2LatLng s2LatLng = s2CellId.toLatLng();
            this.latitude = s2LatLng.latDegrees();
            this.longitude = s2LatLng.lngDegrees();
        }

        @Override
        public String toString() {
            return this.latitude + StringLiterals.CONCAT_SEPARATOR + this.longitude;
        }

        /**
         * Formats the long and lat rounded to given decimal places.
         *
         * @param decimals The number of decimal places.
         * @return The formatted string.
         */
        public String toString(final int decimals) {
            final DecimalFormat decimalFormat = new DecimalFormat("#." + StringUtils.repeat("#", decimals));
            return decimalFormat.format(latitude).replace(',', '.')
                + StringLiterals.CONCAT_SEPARATOR
                + decimalFormat.format(longitude).replace(',', '.');
        }
    }
}
