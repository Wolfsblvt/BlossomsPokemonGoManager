package me.corriekay.pokegoutil.utils.helpers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.pokegoapi.google.common.geometry.S2CellId;
import com.pokegoapi.google.common.geometry.S2LatLng;

import me.corriekay.pokegoutil.utils.StringLiterals;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Helper class that provides utility functions concerning locations.
 */
public final class LocationHelper {
    private static final Map<S2CellId, String> SAVED_LOCATIONS;

    static {
        SAVED_LOCATIONS = new HashMap<>(20);
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


    // TODO: Prep
    public static String getLocation(final S2CellId s2CellId) {
        String location;
        if (SAVED_LOCATIONS.containsKey(s2CellId)) {
            location = SAVED_LOCATIONS.get(s2CellId);
        }
        else {
            final S2LatLng latLng = s2CellId.toLatLng();
            JSONObject json = null;
            try {
                json = readJsonFromUrl(latLng.latDegrees(), latLng.lngDegrees());
                location = json.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                System.out.println("Location: " + location);
            } catch (IOException | JSONException e) {
                System.out.println("Error. JSON: " + (json != null ? json : "null"));
                e.printStackTrace();
                location = "";
            }
            SAVED_LOCATIONS.put(s2CellId, location);
        }
        //cell.toString();
        return location;
    }

    private static JSONObject readJsonFromUrl(final double lati, final double longi) throws IOException, JSONException {
        final String apiUrl = "http://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&sensor=true";
        final String formattedUrl = String.format(apiUrl, lati, longi);
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
    public static class LatLongLocation {
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
    }
}
