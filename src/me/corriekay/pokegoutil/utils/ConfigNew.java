package me.corriekay.pokegoutil.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import me.corriekay.pokegoutil.utils.helpers.FileHelper;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Config class that manages saving data to the config.json.
 */
public final class ConfigNew {
    private static final File file = new File(System.getProperty("user.dir"), "config.json");
    private static final ConfigNew cfg = new ConfigNew();

    // Constants
    private static final String CANNOT_FETCH_UNF_STRING = "Could not fetch config item '%s'! Fallback to default: %s%n";
    private static final String CANNOT_SAVE_UNF_STRING = "Could not save '%s' to config (%s)!%n";

    // Class properties
    private JSONObject json;
    private long lastModified = file.lastModified();

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
        cleanUpAndFill();
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
     * Returns the default value as Object, so that it may can added again. Just for internal use, NO TYP CHECKING!
     *
     * @param configKey The config key.
     * @return The Object
     */
    private Object getAsObject(final ConfigKey configKey) {
        Object obj;
        switch (configKey.type) {
            case BOOLEAN:
                obj = getBool(configKey);
                break;
            case STRING:
                obj = getString(configKey);
                break;
            case INTEGER:
                obj = getInt(configKey);
                break;
            case DOUBLE:
                obj = getDouble(configKey);
                break;
            default:
                obj = getJSONObject(configKey);
                break;
        }
        return obj;
    }

    /**
     * Saves given object under the key. Just for internal use, NO TYP CHECKING!
     *
     * @param configKey The config key.
     * @param obj       The object to set.
     */
    private void setFromObject(final ConfigKey configKey, Object obj) {
        switch (configKey.type) {
            case BOOLEAN:
                setBool(configKey, (Boolean) obj);
                break;
            case STRING:
                setString(configKey, (String) obj);
                break;
            case INTEGER:
                setInt(configKey, (Integer) obj);
                break;
            case DOUBLE:
                setDouble(configKey, (Double) obj);
                break;
            default:
                setJSONObject(configKey, new JSONObject(obj));
                break;
        }
    }

    /**
     * Returns the JSONObject for given key. The one in the config, or if it does not exist, the default one.
     *
     * @param configKey The config key.
     * @return The JSONObject under the key, or default value.
     */
    public JSONObject getJSONObject(final ConfigKey configKey) {
        return getJSONObject(configKey, configKey.getDefaultValue());
    }

    /**
     * Returns the JSONObject for given key. The one in the config, or if it does not exist, the given default value.
     *
     * @param configKey    The config key.
     * @param defaultValue The default value to choose if the key does not exist.
     * @return The JSONObject under the key, or default value.
     */
    public JSONObject getJSONObject(final ConfigKey configKey, final JSONObject defaultValue) {
        try {
            final FindResult res = findNode(configKey.keyName, false);
            return res.getNode().getJSONObject(res.getName());
        } catch (final JSONException ignored) {
            //System.out.printf(CANNOT_FETCH_UNF_STRING, configKey.keyName, defaultValue);
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
            if (res.getNode().optJSONObject(res.getName()) != value || value.equals(configKey.getDefaultValue())) {
                res.getNode().put(res.getName(), value);
                saveConfig();
            }
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
            return res.getNode().getBoolean(res.getName());
        } catch (final JSONException ignored) {
            //System.out.printf(CANNOT_FETCH_UNF_STRING, configKey.keyName, defaultValue);
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
            // Set if value is different or if default value should be added
            boolean defaultValue = configKey.getDefaultValue();
            if (res.getNode().optBoolean(res.getName(), defaultValue) != value || value == defaultValue) {
                res.getNode().put(res.getName(), value);
                saveConfig();
            }
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
            final String value = res.getNode().getString(res.getName());
            return StringEscapeUtils.unescapeJson(value);
        } catch (final JSONException ignored) {
            //System.out.printf(CANNOT_FETCH_UNF_STRING, configKey.keyName, defaultValue);
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
            // Set if value is different or if default value should be added
            if (!res.getNode().optString(res.getName(), "." + configKey.getDefaultValue()).equals(value)) {
                res.getNode().put(res.getName(), StringEscapeUtils.escapeJson(value));
                saveConfig();
            }
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
            return res.getNode().getInt(res.getName());
        } catch (final JSONException ignored) {
            //System.out.printf(CANNOT_FETCH_UNF_STRING, configKey.keyName, defaultValue);
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
            // Set if value is different or if default value should be added
            if (res.getNode().optInt(res.getName(), 1 + (int) configKey.getDefaultValue()) != value) {
                res.getNode().put(res.getName(), value);
                saveConfig();
            }
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
            return res.getNode().getDouble(res.getName());
        } catch (final JSONException ignored) {
            //System.out.printf(CANNOT_FETCH_UNF_STRING, configKey.keyName, defaultValue);
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
            if (res.getNode().optDouble(res.getName(), 1 + (double) configKey.getDefaultValue()) != value) {
                res.getNode().put(res.getName(), value);
                saveConfig();
            }
        } catch (final JSONException ignored) {
            System.out.printf(CANNOT_SAVE_UNF_STRING, value, configKey.keyName);
        }
    }

    /**
     * Internal function to find a node with its value.
     *
     * @param path   The path of the node.
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
            System.out.print("Modified config.json externally. Will be reloaded now.");
            // Re-read the file now
            final String content = FileHelper.readFile(file);
            if (content != null) {
                json = new JSONObject(content);
            }
            lastModified = currentModifiedTime;
        }
    }

    /**
     * Removes json nodes that do not belong in the file and fills it with missing values' defaults then.
     */
    private void cleanUpAndFill() {
        ConfigKey[] keys = ConfigKey.values();
        Map<ConfigKey, Object> allConfigs = new HashMap<>(keys.length);

        // Read all config values or take defaults
        for (ConfigKey configKey : keys) {
            Object value = getAsObject(configKey);
            allConfigs.put(configKey, value);
        }

        // Reset the json file and fill it again
        json = new JSONObject();
        for (HashMap.Entry<ConfigKey, Object> entry : allConfigs.entrySet()) {
            setFromObject(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Delete the given key and its value.
     *
     * @param configKey The config key.
     */
    public void delete(final ConfigKey configKey) {
        final FindResult res = findNode(configKey.keyName, false);
        res.getNode().remove(res.getName());
    }

    /**
     * Save the config file.
     */
    public void saveConfig() {
        FileHelper.saveFile(file, json.toString(FileHelper.INDENT));
        lastModified = file.lastModified();
    }

    /**
     * The Result which combines a node with it's value.
     */
    private class FindResult {
        private final JSONObject node;
        private final String name;

        /**
         * Creates a Result object.
         *
         * @param node The node.
         * @param name The value.
         */
        FindResult(final JSONObject node, final String name) {
            this.node = node;
            this.name = name;
        }

        /**
         * Returns the node.
         *
         * @return The node.
         */
        public JSONObject getNode() {
            return this.node;
        }

        /**
         * Returns the name.
         *
         * @return The name.
         */
        public String getName() {
            return this.name;
        }
    }
}
