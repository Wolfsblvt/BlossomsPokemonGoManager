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


/**
 * Config class that manages saving data to the config.json.
 */
public final class ConfigNew {
    private static final File file = new File(System.getProperty("user.dir"), "config.json");
    private static final ConfigNew cfg = new ConfigNew();
    private JSONObject json;

    // Save file modified time
    private long lastModified = file.lastModified();

    // Constants
    private static final String CANNOT_FETCH_UNF_STRING = "Could not fetch config item '%s'! Fallback to default: %s%n";
    private static final String CANNOT_SAVE_UNF_STRING = "Could not save '%s' to config (%s)!%n";

    /**
     * Constructor that reads the config file.
     */
    private ConfigNew() {
        if (!file.exists()) {

            try {
                if (!file.createNewFile()) {
                    throw new FileAlreadyExistsException(file.getName());
                }
            } catch (final IOException e) {
                System.out.println(e.toString());
            }
            json = new JSONObject();
            saveConfig();
        } else {
            json = new JSONObject(FileHelper.readFile(file));
        }
    }

    /**
     * Returns the config object that can read the config.
     *
     * @return The Config object.
     */
    public static ConfigNew getConfig() {
        return cfg;
    }

    /**
     * Returns the JSONObject for given key. The one in the config, or if it does not exist, the default one.
     *
     * @param configKey The config key.
     * @return The JSONObject under the key, or default value.
     */
    public JSONObject getJsonObject(final ConfigKey configKey) {
        return getJsonObject(configKey, configKey.getDefaultValue());
    }

    /**
     * Returns the JSONObject for given key. The one in the config, or if it does not exist, the given default value.
     *
     * @param configKey    The config key.
     * @param defaultValue The default value to choose if the key does not exist.
     * @return The JSONObject under the key, or default value.
     */
    public JSONObject getJsonObject(final ConfigKey configKey, final JSONObject defaultValue) {
        try {
            FindResult res = findNode(configKey.keyName, false);
            return res.node().getJSONObject(res.name());
        } catch (final JSONException ignored) {
            System.out.printf(CANNOT_FETCH_UNF_STRING, configKey.keyName, defaultValue);
            setJSONObject(configKey, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Saves the given JSONObject under given key.
     *
     * @param configKey The config key.
     * @param value     The value to save.
     */
    public void setJSONObject(final ConfigKey configKey, final JSONObject value) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            res.node().put(res.name(), value);
            saveConfig();
        } catch (final JSONException ignored) {
            System.out.printf(CANNOT_SAVE_UNF_STRING, value, configKey.keyName);
        }
    }

    /**
     * Returns the Boolean for given key. The one in the config, or if it does not exist, the default one.
     *
     * @param configKey The config key.
     * @return The Boolean under the key, or default value.
     */
    public boolean getBool(final ConfigKey configKey) {
        return getBool(configKey, configKey.getDefaultValue());
    }

    /**
     * Returns the Boolean for given key. The one in the config, or if it does not exist, the given default value.
     *
     * @param configKey    The config key.
     * @param defaultValue The default value to choose if the key does not exist.
     * @return The Boolean under the key, or default value.
     */
    public boolean getBool(final ConfigKey configKey, final boolean defaultValue) {
        try {
            final FindResult res = findNode(configKey.keyName, false);
            return res.node().getBoolean(res.name());
        } catch (final JSONException ignored) {
            System.out.printf(CANNOT_FETCH_UNF_STRING, configKey.keyName, defaultValue);
            setBool(configKey, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Saves the given Boolean under given key.
     *
     * @param configKey The config key.
     * @param value     The value to save.
     */
    public void setBool(final ConfigKey configKey, final boolean value) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            res.node().put(res.name(), value);
            saveConfig();
        } catch (final JSONException ignored) {
            System.out.printf(CANNOT_SAVE_UNF_STRING, value, configKey.keyName);
        }
    }

    /**
     * Returns the String for given key. The one in the config, or if it does not exist, the default one.
     *
     * @param configKey The config key.
     * @return The String under the key, or default value.
     */
    public String getString(final ConfigKey configKey) {
        return getString(configKey, configKey.getDefaultValue());
    }

    /**
     * Returns the String for given key. The one in the config, or if it does not exist, the given default value.
     *
     * @param configKey    The config key.
     * @param defaultValue The default value to choose if the key does not exist.
     * @return The String under the key, or default value.
     */
    public String getString(final ConfigKey configKey, final String defaultValue) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            final String value = res.node().getString(res.name());
            return StringEscapeUtils.unescapeJson(value);
        } catch (final JSONException ignored) {
            System.out.printf(CANNOT_FETCH_UNF_STRING, configKey.keyName, defaultValue);
            setString(configKey, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Saves the given String under given key.
     *
     * @param configKey The config key.
     * @param value     The value to save.
     */
    public void setString(final ConfigKey configKey, final String value) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            res.node().put(res.name(), StringEscapeUtils.escapeJson(value));
            saveConfig();
        } catch (final JSONException ignored) {
            System.out.printf(CANNOT_SAVE_UNF_STRING, value, configKey.keyName);
        }
    }

    /**
     * Returns the Int for given key. The one in the config, or if it does not exist, the default one.
     *
     * @param configKey The config key.
     * @return The Int under the key, or default value.
     */
    public int getInt(final ConfigKey configKey) {
        return getInt(configKey, configKey.getDefaultValue());
    }

    /**
     * Returns the Int for given key. The one in the config, or if it does not exist, the given default value.
     *
     * @param configKey    The config key.
     * @param defaultValue The default value to choose if the key does not exist.
     * @return The Int under the key, or default value.
     */
    public int getInt(final ConfigKey configKey, final int defaultValue) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            return res.node().getInt(res.name());
        } catch (final JSONException ignored) {
            System.out.printf(CANNOT_FETCH_UNF_STRING, configKey.keyName, defaultValue);
            setInt(configKey, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Saves the given Int under given key.
     *
     * @param configKey The config key.
     * @param value     The value to save.
     */
    public void setInt(final ConfigKey configKey, final int value) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            res.node().put(res.name(), value);
            saveConfig();
        } catch (final JSONException ignored) {
            System.out.printf(CANNOT_SAVE_UNF_STRING, value, configKey.keyName);
        }
    }

    /**
     * Returns the Double for given key. The one in the config, or if it does not exist, the default one.
     *
     * @param configKey The config key.
     * @return The Double under the key, or default value.
     */
    public double getDouble(final ConfigKey configKey) {
        return getDouble(configKey, configKey.getDefaultValue());
    }

    /**
     * Returns the Double for given key. The one in the config, or if it does not exist, the given default value.
     *
     * @param configKey    The config key.
     * @param defaultValue The default value to choose if the key does not exist.
     * @return The Double under the key, or default value.
     */
    public double getDouble(final ConfigKey configKey, final double defaultValue) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            return res.node().getDouble(res.name());
        } catch (final JSONException ignored) {
            System.out.printf(CANNOT_FETCH_UNF_STRING, configKey.keyName, defaultValue);
            setDouble(configKey, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Saves the given Double under given key.
     *
     * @param configKey The config key.
     * @param value     The value to save.
     */
    public void setDouble(final ConfigKey configKey, final double value) {
        try {
            final FindResult res = findNode(configKey.keyName, true);
            res.node().put(res.name(), value);
            saveConfig();
        } catch (final JSONException ignored) {
            System.out.printf(CANNOT_SAVE_UNF_STRING, value, configKey.keyName);
        }
    }

    /**
     * Internal function to find a node with its value.
     * @param path The path of the node.
     * @param create Weather or not the node should be created if it doesn't exist.
     * @return The Result for searching the node.
     */
    private FindResult findNode(final String path, final boolean create) {
        checkModified();
        final ArrayList<String> parts = new ArrayList<>(Arrays.asList(path.split("\\.")));
        JSONObject current = json;
        for (final String item : parts.subList(0, parts.size() - 1)) {
            if (!current.has(item) && create) {
                current.put(item, new JSONObject());
            }
            current = current.getJSONObject(item);
        }

        return new FindResult(current, parts.get(parts.size() - 1));
    }

    /**
     * Check if the file was modified.
     * (Different "lastModified" time)
     */
    private void checkModified() {
        final long currentModifiedTime = file.lastModified();
        if (currentModifiedTime != lastModified) {
            // Re-read the file now
            json = new JSONObject(FileHelper.readFile(file));
            lastModified = currentModifiedTime;
        }
    }

    /**
     * Delete the given key and its value.
     *
     * @param configKey The config key.
     */
    public void delete(final ConfigKey configKey) {
        final FindResult res = findNode(configKey.keyName, false);
        res.node().remove(res.name());
    }

    /**
     * Save the config file.
     */
    public void saveConfig() {
        final int indentFactor = 4;
        FileHelper.saveFile(file, json.toString(indentFactor));
    }

    private class FindResult {
        private final JSONObject vNode;
        private final String vName;

        FindResult(final JSONObject vNode, final String vName) {
            this.vNode = vNode;
            this.vName = vName;
        }

        public JSONObject node() {
            return this.vNode;
        }

        public String name() {
            return this.vName;
        }
    }
}
