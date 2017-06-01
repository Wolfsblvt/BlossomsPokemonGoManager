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
    public static final String CANNOT_FETCH_UNF_STRING = "Could not fetch config item '%s'! Fallback to default: %s%n";
    public static final String CANNOT_SAVE_UNF_STRING = "Could not save '%s' to config (%s)!%n";
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
            //json = new JSONObject();
            setJson(new JSONObject());
            saveConfig();
        } else {
            //json = new JSONObject(FileHelper.readFile(file));
            setJson(new JSONObject(FileHelper.readFile(file)));
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

    
    
    public Object getAsObject(final ConfigKey configKey) {
        return getAsObject(configKey, configKey.getDefaultValue());
    }
    
    /**
     * Returns the default value as Object, so that it may can added again. Just for internal use, NO TYP CHECKING!
    *
    * @param configKey The config key.
    * @return The Object
    */
    
    public Object getAsObject(final ConfigKey configKey, Object defaultValue) {
        Object obj;
        GetStrategy myGetStrategy;
        myGetStrategy = GetFactory.createGetStrategy(configKey);
        
        final FindResult res = findNode(configKey.keyName, true);
        try {
            obj = myGetStrategy.getJSONElement(res);
        } catch (final JSONException ignored) {
            //System.out.printf(CANNOT_FETCH_UNF_STRING, configKey.keyName, defaultValue);
            SetStrategy mySetStrategy;
            mySetStrategy = SetFactory.createSetStrategy(configKey);
            mySetStrategy.setJSONElement(configKey, defaultValue, res);
            return defaultValue;
        }

        return obj;
    }

    /**
     * Saves given object under the key. Just for internal use, NO TYP CHECKING!
     *
     * @param configKey The config key.
     * @param obj       The object to set.
     */
    public void setFromObject(final ConfigKey configKey, Object obj) {
        SetStrategy mySetStrategy;
        mySetStrategy = SetFactory.createSetStrategy(configKey);
        
        final FindResult res = findNode(configKey.keyName, true);
        try {
            mySetStrategy.setJSONElement(configKey, obj, res);
        } catch (final JSONException ignored) {
            System.out.printf(CANNOT_SAVE_UNF_STRING, obj, configKey.keyName);
        }
        saveConfig();
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
        //JSONObject current = json;
        JSONObject current = getJson();
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
        
        //Introduce Explaining Variable
        final boolean isFileModified = (currentModifiedTime != getLastModified());
        if (isFileModified) {
            System.out.print("Modified config.json externally. Will be reloaded now.");
            // Re-read the file now
            
            // rename
            final String reReadContent = FileHelper.readFile(file);
            if (reReadContent != null) {
                //json = new JSONObject(content);
                setJson(new JSONObject(reReadContent));
            }
            //lastModified = currentModifiedTime;
            setLastModified(currentModifiedTime);
        }
    }

    /**
     * Removes json nodes that do not belong in the file and fills it with missing values' defaults then.
     */
    private void cleanUpAndFill() {

        ConfigKey[] allConfigKeys = ConfigKey.values();
        
        Map<ConfigKey, Object> allConfigs = new HashMap<>(allConfigKeys.length);

        // Read all config values or take defaults
        for (ConfigKey configKey : allConfigKeys) {
            Object value = getAsObject(configKey);
            allConfigs.put(configKey, value);
        }
        // Reset the json file and fill it again
        //json = new JSONObject();
        setJson(new JSONObject());
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
        //FileHelper.saveFile(file, json.toString(FileHelper.INDENT));
        FileHelper.saveFile(file, getJson().toString(FileHelper.INDENT));
        //lastModified = file.lastModified();
        setLastModified(file.lastModified());
    }
    
    /**
     * get json
     */
    public JSONObject getJson(){
        return json;
    }
    
    /**
     *  set json
     */
    public void setJson(JSONObject aJson){
        json = aJson;
    }
    
    /**
     * get lastModified
     */
    public long getLastModified(){
        return lastModified;
    }
    
    /**
     * set lastModified
     */
    public void setLastModified(long modifiedTime){
        lastModified = modifiedTime;
    }
    
    /**
     * The Result which combines a node with it's value.
     */
   
    
}
