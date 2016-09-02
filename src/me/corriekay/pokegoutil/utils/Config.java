package me.corriekay.pokegoutil.utils;

import me.corriekay.pokegoutil.utils.helpers.FileHelper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Arrays;

@Deprecated
public class Config {
    private static final File file = new File(System.getProperty("user.dir"), "config.json");
    private static final Config cfg = new Config();
    private static JSONObject json;

    // Constants
    private static final String COULD_NOT_FETCH_STRING_UNFORMATTED = "Could not fetch config item '%s'! Fallback to default: %s%n";
    private static final String COULD_NOT_SAVE_STRING_UNFORMATTED = "Could not save '%s' to config (%s)!%n";

    private Config() {
        if (!file.exists()) {

            try {
                if (!file.createNewFile()) {
                    throw new FileAlreadyExistsException(file.getName());
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
            json = new JSONObject();
            saveConfig();
        } else {
            json = new JSONObject(FileHelper.readFile(file));
        }
    }

    public static Config getConfig() {
        return cfg;
    }

    public JSONObject getJSONObject(String path, JSONObject defaultValue) {
        try {
            final FindResult res = findNode(path, false);
            return res.node().getJSONObject(res.name());
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_FETCH_STRING_UNFORMATTED, path, defaultValue);
            setJSONObject(path, defaultValue);
            return defaultValue;
        }
    }

    public void setJSONObject(String path, JSONObject value) {
        try {
            final FindResult res = findNode(path, true);
            res.node().put(res.name(), value);
            saveConfig();
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_SAVE_STRING_UNFORMATTED, value, path);
        }
    }

    public boolean getBool(String path, boolean defaultValue) {
        try {
            final FindResult res = findNode(path, false);
            return res.node().getBoolean(res.name());
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_FETCH_STRING_UNFORMATTED, path, defaultValue);
            setBool(path, defaultValue);
            return defaultValue;
        }
    }

    public void setBool(String path, boolean value) {
        try {
            final FindResult res = findNode(path, true);
            res.node().put(res.name(), value);
            saveConfig();
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_SAVE_STRING_UNFORMATTED, value, path);
        }
    }

    public String getString(String path, String defaultValue) {
        try {
            final FindResult res = findNode(path, false);
            final String value = res.node().getString(res.name());
            return StringEscapeUtils.unescapeJson(value);
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_FETCH_STRING_UNFORMATTED, path, defaultValue);
            setString(path, StringEscapeUtils.escapeJson(defaultValue));
            return defaultValue;
        }
    }

    public void setString(String path, String value) {
        try {
            final FindResult res = findNode(path, true);
            res.node().put(res.name(), StringEscapeUtils.escapeJson(value));
            saveConfig();
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_SAVE_STRING_UNFORMATTED, value, path);
        }
    }

    public int getInt(String path, int defaultValue) {
        try {
            final FindResult res = findNode(path, false);
            return res.node().getInt(res.name());
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_FETCH_STRING_UNFORMATTED, path, defaultValue);
            setInt(path, defaultValue);
            return defaultValue;
        }
    }

    public void setInt(String path, int value) {
        try {
            final FindResult res = findNode(path, true);
            res.node().put(res.name(), value);
            saveConfig();
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_SAVE_STRING_UNFORMATTED, value, path);
        }
    }

    public double getDouble(String path, double defaultValue) {
        try {
            final FindResult res = findNode(path, false);
            return res.node().getInt(res.name());
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_FETCH_STRING_UNFORMATTED, path, defaultValue);
            setDouble(path, defaultValue);
            return defaultValue;
        }
    }

    public void setDouble(String path, double value) {
        try {
            final FindResult res = findNode(path, true);
            res.node().put(res.name(), value);
            saveConfig();
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_SAVE_STRING_UNFORMATTED, value, path);
        }
    }

    private FindResult findNode(String path, boolean create) {
        final ArrayList<String> parts = new ArrayList<>(Arrays.asList(path.split("\\.")));
        JSONObject current = json;
        for (String item : parts.subList(0, parts.size() - 1)) {
            if (!current.has(item) && create) {
                current.put(item, new JSONObject());
            }
            current = current.getJSONObject(item);
        }

        return new FindResult(current, parts.get(parts.size() - 1));
    }

    public void delete(String path) {
        final FindResult res = findNode(path, false);
        res.node().remove(res.name());
    }

    public void saveConfig() {
        FileHelper.saveFile(file, json.toString(4));
    }

    private class FindResult {
        private final JSONObject node;
        private final String name;

        FindResult(JSONObject node, String name) {
            this.node = node;
            this.name = name;
        }

        public JSONObject node() {
            return this.node;
        }

        public String name() {
            return this.name;
        }
    }
}
