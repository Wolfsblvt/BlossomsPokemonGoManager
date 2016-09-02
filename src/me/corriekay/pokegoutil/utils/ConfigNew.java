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

public final class ConfigNew {
    private static final File file = new File(System.getProperty("user.dir"), "config.json");
    private static final ConfigNew cfg = new ConfigNew();
    private static JSONObject json;

    // Save file modified time
    private long lastModified = file.lastModified();

    // Constants
    private static final String COULD_NOT_FETCH_STRING_UNFORMATTED = "Could not fetch config item '%s'! Fallback to default: %s%n";
    private static final String COULD_NOT_SAVE_STRING_UNFORMATTED = "Could not save '%s' to config (%s)!%n";

    private ConfigNew() {
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

    public static ConfigNew getConfig() {
        return cfg;
    }

    public JSONObject getJSONObject(final ConfigKey configKey) {
        return getJSONObject(configKey, configKey.getDefaultValue());
    }

    public JSONObject getJSONObject(final ConfigKey configKey, final JSONObject defaultValue) {
        try {
            final FindResult res = findNode(configKey.keyName, false);
            return res.node().getJSONObject(res.name());
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_FETCH_STRING_UNFORMATTED, configKey.keyName, defaultValue);
            setJSONObject(configKey, defaultValue);
            return defaultValue;
        }
    }

    public void setJSONObject(final ConfigKey configKey, final JSONObject value) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            res.node().put(res.name(), value);
            saveConfig();
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_SAVE_STRING_UNFORMATTED, value, configKey.keyName);
        }
    }

    public boolean getBool(final ConfigKey configKey) {
        return getBool(configKey, configKey.getDefaultValue());
    }

    public boolean getBool(final ConfigKey configKey, final boolean defaultValue) {
        try {
            final FindResult res = findNode(configKey.keyName, false);
            return res.node().getBoolean(res.name());
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_FETCH_STRING_UNFORMATTED, configKey.keyName, defaultValue);
            setBool(configKey, defaultValue);
            return defaultValue;
        }
    }

    public void setBool(final ConfigKey configKey, final boolean value) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            res.node().put(res.name(), value);
            saveConfig();
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_SAVE_STRING_UNFORMATTED, value, configKey.keyName);
        }
    }

    public String getString(final ConfigKey configKey) {
        return getString(configKey, configKey.getDefaultValue());
    }

    public String getString(final ConfigKey configKey, final String defaultValue) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            final String value = res.node().getString(res.name());
            return StringEscapeUtils.unescapeJson(value);
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_FETCH_STRING_UNFORMATTED, configKey.keyName, defaultValue);
            setString(configKey, defaultValue);
            return defaultValue;
        }
    }

    public void setString(final ConfigKey configKey, final String value) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            res.node().put(res.name(), StringEscapeUtils.escapeJson(value));
            saveConfig();
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_SAVE_STRING_UNFORMATTED, value, configKey.keyName);
        }
    }

    public int getInt(final ConfigKey configKey) {
        return getInt(configKey, configKey.getDefaultValue());
    }

    public int getInt(final ConfigKey configKey, final int defaultValue) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            return res.node().getInt(res.name());
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_FETCH_STRING_UNFORMATTED, configKey.keyName, defaultValue);
            setInt(configKey, defaultValue);
            return defaultValue;
        }
    }

    public void setInt(final ConfigKey configKey, final int value) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            res.node().put(res.name(), value);
            saveConfig();
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_SAVE_STRING_UNFORMATTED, value, configKey.keyName);
        }
    }

    public double getDouble(final ConfigKey configKey) {
        return getDouble(configKey, configKey.getDefaultValue());
    }

    public double getDouble(final ConfigKey configKey, final double defaultValue) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            return res.node().getDouble(res.name());
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_FETCH_STRING_UNFORMATTED, configKey.keyName, defaultValue);
            setDouble(configKey, defaultValue);
            return defaultValue;
        }
    }

    public void setDouble(final ConfigKey configKey, final double value) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            res.node().put(res.name(), value);
            saveConfig();
        } catch (final JSONException ignored) {
            System.out.printf(COULD_NOT_SAVE_STRING_UNFORMATTED, value, configKey.keyName);
        }
    }

    private FindResult findNode(final String path, final boolean create) {
        checkModified();
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

    private void checkModified() {
        final long currentModifiedTime = file.lastModified();
        if (currentModifiedTime != lastModified) {
            // Re-read the file now
            json = new JSONObject(FileHelper.readFile(file));
            lastModified = currentModifiedTime;
        }
    }

    public void delete(final ConfigKey configKey) {
        final FindResult res = findNode(configKey.keyName, false);
        res.node().remove(res.name());
    }

    public void saveConfig() {
        FileHelper.saveFile(file, json.toString(4));
    }

    private class FindResult {
        private final JSONObject node;
        private final String name;

        FindResult(final JSONObject node, final String name) {
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
