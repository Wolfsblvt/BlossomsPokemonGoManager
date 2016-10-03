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
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.pokegoapi.google.common.geometry.S2CellId;
import com.pokegoapi.google.common.geometry.S2LatLng;

import me.corriekay.pokegoutil.data.enums.ExceptionMessages;
import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
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
    // General constants that are set for this file
    private static final File LOCATION_FILE = new File(System.getProperty("user.dir"), "locations.json");
    private static final int SAVE_DELAY_SECONDS = 5;

    // Internal needed constants
    private static final Map<Long, Location> SAVED_LOCATIONS;
    private static final Gson GSON = new Gson();

    // A switch if locations should be saved
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
    public static CompletableFuture<Location> getLocation(final S2CellId s2CellId) {
        return CompletableFuture.supplyAsync(
            () -> {
                // If we have the location saved, we can return it already
                if (SAVED_LOCATIONS.containsKey(s2CellId.id())) {
                    return SAVED_LOCATIONS.get(s2CellId.id());
                }

                Location location = null;

                final LatLongLocation latLng = new LatLongLocation(s2CellId);
                try {
                    final JSONObject json = queryJsonFromUrl(latLng.toString());
                    final String formattedLocation = formattedLocationFromGoogleResponse(json);
                    final String city = cityFromGoogleResponse(json);
                    if (formattedLocation != null) {
                        // We got the location, so we save it. If city wasn't found, we leave it empty.
                        location = new Location(formattedLocation, city != null ? city : "");
                        SAVED_LOCATIONS.put(s2CellId.id(), location);
                    } else {
                        location = new Location("Error: " + json.optString(GoogleKey.STATUS));
                    }
                } catch (IOException | JSONException e) {
                    location = new Location("Exception: " + e.getMessage());
                }
                save();

                return location;
            });

    }

    /**
     * Checks if the location file exists.
     *
     * @return Weather or not the location file exists.
     */
    public static boolean locationFileExists() {
        return LOCATION_FILE.exists();
    }

    /**
     * Deletes the cached locations, and the location file too.
     * Cache will be started from scratch again.
     */
    public static void deleteCachedLocations() {
        FileHelper.deleteFile(LOCATION_FILE);
        SAVED_LOCATIONS.clear();
    }

    /**
     * Queries the JSON for location from the Google API.
     * It uses the user chosen language to tell Google in which language the city names should be returned.
     *
     * @param latLong A string containing lat and long, like "1.124,1.566"
     * @return The JSON from the server.
     * @throws IOException   io.
     * @throws JSONException json.
     */
    private static JSONObject queryJsonFromUrl(final String latLong) throws IOException, JSONException {
        final String language = ConfigNew.getConfig().getString(ConfigKey.LANGUAGE);
        final String apiUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%s&sensor=true&language=%s";
        final String formattedUrl = String.format(apiUrl, latLong.replace(" ", "%20"), language);
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
     * Gets the formatted location from the google response JSON.
     *
     * @param json The google response JSON.
     * @return The formatted location.
     */
    private static String formattedLocationFromGoogleResponse(final JSONObject json) {
        final JSONArray matches = json.optJSONArray(GoogleKey.RESULTS);
        String formattedLocation = null;
        if (matches != null && matches.length() > 0) {
            formattedLocation = matches.getJSONObject(0).optString(GoogleKey.FORMATTED_ADDRESS);
        }
        return formattedLocation;
    }

    /**
     * Gets the city from the google response JSON.
     *
     * @param json The google response JSON.
     * @return The city.
     */
    private static String cityFromGoogleResponse(final JSONObject json) {
        final JSONArray matches = json.optJSONArray(GoogleKey.RESULTS);
        String city;
        // First try, we check the different locations to find the city one
        city = cityFromGoogleMatchesList(matches, GoogleKey.FORMATTED_ADDRESS);
        // Second try. If we haven't got the city from the different locations,
        // we use the address components of the most detailed location and search for the city there
        if (city == null && matches != null && matches.length() > 0) {
            final JSONArray addressComponents = matches.getJSONObject(0).optJSONArray(GoogleKey.ADDRESS_COMPONENTS);
            city = cityFromGoogleMatchesList(addressComponents, GoogleKey.LONG_NAME);
        }
        return city;
    }

    /**
     * Gets the city from a matches array from the google response.
     * Should ONLY be used inside the cityFromGoogleResponse() function.
     *
     * @param array The matches array from the google response.
     * @param node  The node to return from the matched array element.
     * @return The city if found, else null.
     */
    private static String cityFromGoogleMatchesList(final JSONArray array, final String node) {
        if (array == null || array.length() == 0) {
            return null;
        }

        // We go through all components to see if we find the one we want
        for (int i = 0; i < array.length(); i++) {
            final JSONObject component = array.getJSONObject(i);

            // We go through the types and check if we are in the right component
            final JSONArray types = component.getJSONArray(GoogleKey.TYPES);
            boolean isCity = false;
            for (int typeIndex = 0; typeIndex < types.length(); typeIndex++) {
                final String type = types.optString(typeIndex);
                if ("postal_code".equals(type)) {
                    // We continue with the next component here, we do not want the postal code notation
                    isCity = false;
                    break;
                }
                if ("locality".equals(type)) {
                    isCity = true;
                }
            }
            // If so, we return the searched value
            if (isCity) {
                return component.optString(node);
            }
        }
        return null;
    }

    /**
     * Saves the cached locations to location.json file.
     */
    private static void save() {
        // The map gets updated really often maybe, so we delay the save to save a bulk of it
        if (!shouldSave) {
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {
                // Save the location data to the location.json
                final JSONObject json = new JSONObject(GSON.toJson(SAVED_LOCATIONS));
                FileHelper.saveFile(LOCATION_FILE, json.toString(FileHelper.INDENT));
                System.out.println("Saved queried locations to file.");
                shouldSave = false;
            }, SAVE_DELAY_SECONDS, TimeUnit.SECONDS);
        }
        shouldSave = true;
    }

    /**
     * Loads saved locations from location.json file.
     */
    private static void load() {
        final Type mapType = new TypeToken<ConcurrentHashMap<Long, Location>>() {
        }.getType();
        Map<Long, Location> loadedLocations;
        try {
            loadedLocations = GSON.fromJson(FileHelper.readFile(LOCATION_FILE), mapType);
            System.out.println("Load saved locations from file.");
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

    /**
     * A wrapper to hold location information.
     */
    public static final class Location {
        public final String formattedLocation;
        public final String city;

        /**
         * Internal constructor to create a location.
         *
         * @param formattedLocation The formatted location.
         * @param city              The city.
         */
        private Location(final String formattedLocation, final String city) {
            this.formattedLocation = formattedLocation;
            this.city = city;
        }

        /**
         * Internal constructor to create a location, based on an error.
         *
         * @param error The error.
         */
        private Location(final String error) {
            this.formattedLocation = error;
            this.city = error;
        }
    }

    /**
     * Internal class that holds possible keys for the Google JSON.
     */
    private static final class GoogleKey {
        // Google keys for JSON accessing
        private static final String STATUS = "status";
        private static final String RESULTS = "results";
        private static final String TYPES = "types";
        private static final String FORMATTED_ADDRESS = "formatted_address";
        private static final String ADDRESS_COMPONENTS = "address_components";
        private static final String LONG_NAME = "long_name";
    }
}
