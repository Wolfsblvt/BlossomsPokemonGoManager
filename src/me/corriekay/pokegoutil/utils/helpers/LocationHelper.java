package me.corriekay.pokegoutil.utils.helpers;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;

import com.pokegoapi.google.common.geometry.S2CellId;
import com.pokegoapi.google.common.geometry.S2LatLng;

import me.corriekay.pokegoutil.utils.StringLiterals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper class that provides utility functions concerning locations.
 */
public final class LocationHelper {
    private static final Map<S2CellId, String> SAVED_LOCATIONS;

    static {
        SAVED_LOCATIONS = new HashMap<>();
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
                if (SAVED_LOCATIONS.containsKey(s2CellId)) {
                    location = SAVED_LOCATIONS.get(s2CellId);
                } else {
                    final LatLongLocation latLng = new LatLongLocation(s2CellId);
                    JSONObject json = null;
                    try {
                        json = queryJsonFromUrl(latLng.toString());

                        final JSONArray matches = json.optJSONArray("results");
                        if (matches != null && matches.length() > 0) {
                            location = matches.getJSONObject(0).getString("formatted_address");
                        } else {
                            location = "Error: " + json.getString("status");
                        }
                    } catch (IOException | JSONException e) {
                        location = "Exception: " + e.getMessage();
                    }
                    SAVED_LOCATIONS.put(s2CellId, location);
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
        } catch (IOException ex) {
            System.out.println("Could query location. Reason: " + ex.toString());
            return new JSONObject();
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
            return this.latitude + StringLiterals.CONCAT + this.longitude;
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
                + StringLiterals.CONCAT
                + decimalFormat.format(longitude).replace(',', '.');
        }
    }
}
