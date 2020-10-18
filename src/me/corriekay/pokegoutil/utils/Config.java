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

public class Config {
    private static final File file = new File(System.getProperty("user.dir"), "config.json");
    private static JSONObject json;
    private static Config cfg = new Config();

    private Config() {
        if (!file.exists()) {

            try {
                if (!file.createNewFile()) {
                    throw new FileAlreadyExistsException(file.getName());
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
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

    public JSONObject getJSONObject(String path, String defaultValue) {
        return getJSONObject(path, new JSONObject(defaultValue));
    }

    public JSONObject getJSONObject(String path, JSONObject defaultValue) {
        try {
            FindResult res = findNode(path, false);

            return res.node().getJSONObject(res.name());
        } catch (JSONException jsone) {
            System.err.println("Could not fetch config item '" + path + "'! Fallback to default: " + defaultValue);
            setJSONObject(path, defaultValue);
            return defaultValue;
        }
    }

    public void setJSONObject(String path, JSONObject value) {
        try {
            FindResult res = findNode(path, true);

            res.node().put(res.name(), value);
            saveConfig();
        } catch (JSONException jsone) {
            System.out.println("Could not save '" + value + "' to config (" + path + ")!");
        }
    }

    public boolean getBool(String path, boolean defaultValue) {
        try {
            FindResult res = findNode(path, false);

            return res.node().getBoolean(res.name());
        } catch (JSONException jsone) {
            System.err.println("Could not fetch config item '" + path + "'! Fallback to default: " + defaultValue);
            setBool(path, defaultValue);
            return defaultValue;
        }
    }

    public void setBool(String path, boolean value) {
        try {
            FindResult res = findNode(path, true);

            res.node().put(res.name(), value);
            saveConfig();
        } catch (JSONException jsone) {
            System.out.println("Could not save '" + value + "' to config (" + path + ")!");
        }
    }

    public String getString(String path, String defaultValue) {
        try {
            FindResult res = findNode(path, false);

            String value = res.node().getString(res.name());
            return StringEscapeUtils.unescapeJson(value);
        } catch (JSONException jsone) {
            System.err.println("Could not fetch config item '" + path + "'! Fallback to default: " + defaultValue);
            setString(path, StringEscapeUtils.escapeJson(defaultValue));
            return defaultValue;
        }
    }

    public void setString(String path, String value) {
        try {
            FindResult res = findNode(path, true);

            res.node().put(res.name(), StringEscapeUtils.escapeJson(value));
            saveConfig();
        } catch (JSONException jsone) {
            System.out.println("Could not save '" + value + "' to config (" + path + ")!");
        }
    }

    public int getInt(String path, int defaultValue) {
        try {
            FindResult res = findNode(path, false);

            return res.node().getInt(res.name());
        } catch (JSONException jsone) {
            System.err.println("Could not fetch config item '" + path + "'! Fallback to default: " + defaultValue);
            setInt(path, defaultValue);
            return defaultValue;
        }
    }

    public void setInt(String path, int value) {
        try {
            FindResult res = findNode(path, true);

            res.node().put(res.name(), value);
            saveConfig();
        } catch (JSONException jsone) {
            System.out.println("Could not save '" + value + "' to config (" + path + ")!");
        }
    }

    public double getDouble(String path, double defaultValue) {
        try {
            FindResult res = findNode(path, false);

            return res.node().getInt(res.name());
        } catch (JSONException jsone) {
            System.err.println("Could not fetch config item '" + path + "'! Fallback to default: " + defaultValue);
            setDouble(path, defaultValue);
            return defaultValue;
        }
    }

    public void setDouble(String path, double value) {
        try {
            FindResult res = findNode(path, true);

            res.node().put(res.name(), value);
            saveConfig();
        } catch (JSONException jsone) {
            System.out.println("Could not save '" + value + "' to config (" + path + ")!");
        }
    }

    private FindResult findNode(String path, boolean create) {
        ArrayList<String> parts = new ArrayList<String>(Arrays.asList(path.split("\\.")));
        JSONObject current = json;
        for (String item : parts.subList(0, parts.size() - 1)) {
            if (!current.has(item) && create) {
                JSONObject newObj = new JSONObject();
                current.put(item, newObj);
            }

            current = current.getJSONObject(item);
        }

        return new FindResult(current, parts.get(parts.size() - 1));
    }

    public void delete(String path) {
        FindResult res = findNode(path, false);
        res.node().remove(res.name());
    }

    public void saveConfig() {
        FileHelper.saveFile(file, json.toString(4));
    }

    private class FindResult {
        private JSONObject node;
        private String name;

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
