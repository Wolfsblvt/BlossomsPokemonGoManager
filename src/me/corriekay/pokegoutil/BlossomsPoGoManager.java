package me.corriekay.pokegoutil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.util.PokeNames;

import me.corriekay.pokegoutil.controllers.AccountController;
import me.corriekay.pokegoutil.utils.Console;
import me.corriekay.pokegoutil.utils.Utilities;

public class BlossomsPoGoManager {
	
	private static final File file = new File(System.getProperty("user.dir"), "config.json");
	public static JSONObject config;
	private static Console console;
	public static final String VERSION = "0.1.1-Beta";

	public static void main(String[] args) throws Exception {
		Utilities.setNativeLookAndFeel();
		console = new Console("Console", 0, 0, true);
		console.setVisible(false);
		if(!file.exists()) {

			if (!file.createNewFile()) {
				throw new FileAlreadyExistsException(file.getName());
			}
			config = new JSONObject("{\"login\":{},\"options\":{\"lang\":\"en\"}}");
			saveConfig();
		} else {
			config = new JSONObject(Utilities.readFile(file));
		}
		AccountController.initialize(file, config, console);
		AccountController.logOn();
	}
	
	public static void saveConfig() {
        Utilities.saveFile(file, config.toString(4));
	}
	
    public static String getConfigItem(String path, String defaultValue) {
        try {
            ArrayList<String> parts = new ArrayList<String>(Arrays.asList(path.split("\\.")));
            JSONObject current = config;
            for (String item : parts.subList(0, parts.size()-1)) {
                current = config.getJSONObject(item);
            }

            return current.getString(parts.get(parts.size()-1));
        }
        catch (JSONException jsone) {
            System.err.println("Could not fetch config item '" + path + "'! Fallback to default: " + defaultValue);
            return defaultValue;
        }
    }
    
    public static void setConfigItem(String path, String value) {
        try {
            ArrayList<String> parts = new ArrayList<String>(Arrays.asList(path.split("\\.")));
            JSONObject current = config;
            for (String item : parts.subList(0, parts.size()-1)) {
                if (!current.has(item)) {
                    JSONObject newObj = new JSONObject();
                    current.put(item, newObj);
                }
                
                current = current.getJSONObject(item);
            }
            
            current.put(parts.get(parts.size()-1), value);
            BlossomsPoGoManager.saveConfig();
        }
        catch (JSONException jsone) {
            System.out.println("Could not save '" + value + "' to config (" + path + ")!");
        }
    }
	
	public static String getPokemonName(int id) {
		String lang = getConfigItem("options.lang", "en");
		
		Locale locale;
		String[] langar = lang.split("_");
		if (langar.length == 1) {
			locale = new Locale(langar[0]);
		}
		else {
		    locale = new Locale(langar[0], langar[1]);
		}
		
		String name = null;
		try {
			name = new String(PokeNames.getDisplayName(id, locale).getBytes("ISO-8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		name = StringUtils.capitalize(name.toLowerCase());
		return name;
	}
	
	public static String getPokemonName(Pokemon poke) {
		return getPokemonName(poke.getPokemonId().getNumber());
	}

}
