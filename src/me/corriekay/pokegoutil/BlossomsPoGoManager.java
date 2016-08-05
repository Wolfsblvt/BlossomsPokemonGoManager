package me.corriekay.pokegoutil;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.auth.CredentialProvider;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.util.PokeNames;

import me.corriekay.pokegoutil.utils.Console;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.windows.PokemonGoMainWindow;
import okhttp3.OkHttpClient;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class BlossomsPoGoManager {
	
	private static final File file = new File(System.getProperty("user.dir"), "config.json");
	public static JSONObject config;
	private static Console console;
	private static boolean logged = false;
	private static PokemonGoMainWindow mainWindow = null;
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
		
		logOn();
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

	public static void logOn() throws Exception {
		OkHttpClient http;
		CredentialProvider cp;
		PokemonGo go = null;
		while(!logged) {
			//BEGIN LOGIN WINDOW
			JSONObject loginconf = BlossomsPoGoManager.config.getJSONObject("login");
			go = null;
			cp = null;
			http = new OkHttpClient();
	
			UIManager.put("OptionPane.noButtonText", "Use Google Auth");
			UIManager.put("OptionPane.yesButtonText", "Use PTC Auth");
			UIManager.put("OptionPane.cancelButtonText", "Exit");
			UIManager.put("OptionPane.okButtonText", "Ok");
			
			JTextField username = new JTextField(loginconf.optString("PTCUsername", null));
			JTextField password = new JPasswordField(loginconf.optString("PTCPassword", null));
	
			JPanel panel1 = new JPanel(new BorderLayout());
			panel1.add(new JLabel("PTC Username: "), BorderLayout.LINE_START);
			panel1.add(username, BorderLayout.CENTER);
			JPanel panel2 = new JPanel(new BorderLayout());
			panel2.add(new JLabel("PTC Password: "), BorderLayout.LINE_START);
			panel2.add(password, BorderLayout.CENTER);
			Object[] panel = {panel1, panel2, };
			
			int response = JOptionPane.showConfirmDialog(null, panel, "Login", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if(response == JOptionPane.CANCEL_OPTION) {
				System.exit(0);
			} else if(response == JOptionPane.OK_OPTION) {
				//Using PTC, remove Google infos
				loginconf.remove("GoogleAuthToken");
				try {
					cp = new PtcCredentialProvider(http, username.getText(), password.getText());
					loginconf.put("PTCUsername", username.getText());
					if(loginconf.optBoolean("SaveAuth", false) || checkSaveAuth()) {
						loginconf.put("PTCPassword", password.getText());
						loginconf.put("SaveAuth", true);
					} else {
						loginconf.remove("PTCPassword");
						loginconf.remove("SaveAuth");
					}
				} catch(Exception e){
					alertFailedLogin();
					continue;
				} 
			} else if (response == JOptionPane.NO_OPTION) {
				//Using Google, remove PTC infos
				loginconf.remove("PTCUsername");
				loginconf.remove("PTCPassword");
				String authCode = loginconf.optString("GoogleAuthToken", null);
				boolean refresh = false;
				if(authCode == null) {
					//We need to get the auth code, as we do not have it yet.
					UIManager.put("OptionPane.okButtonText", "Ok");
					JOptionPane.showMessageDialog(null, "You will need to provide a google authentication key to log in. Press OK to continue.", "Google Auth", JOptionPane.PLAIN_MESSAGE);
					//We're gonna try to load the page using the users browser 
					if(Desktop.isDesktopSupported()) {
						JOptionPane.showMessageDialog(null, "A webpage should open up, please allow the permissions, and then copy the code into your clipboard. Press OK to continue", "Google Auth", JOptionPane.PLAIN_MESSAGE);
						Desktop.getDesktop().browse(new URI(GoogleUserCredentialProvider.LOGIN_URL));
					} else {
						UIManager.put("OptionPane.cancelButtonText", "Copy To Clipboard");
						if(JOptionPane.showConfirmDialog(null, "Please copy this link and paste it into a browser.\nThen, allow the permissions, and copy the code into your clipboard.", "Google Auth", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.CANCEL_OPTION){
							StringSelection ss = new StringSelection(GoogleUserCredentialProvider.LOGIN_URL);
							Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
						}
						UIManager.put("OptionPane.cancelButtonText", "Cancel");
					}
					//The user should have the auth code now. Lets get it.
					authCode = JOptionPane.showInputDialog(null, "Please provide the authentication code", "Google Auth", JOptionPane.PLAIN_MESSAGE);
				} else {
					refresh = true;
				}
				try {
					GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(http);
					if(refresh) provider.refreshToken(authCode);
					else provider.login(authCode); 
					cp = provider;
					if(loginconf.optBoolean("SaveAuth", false) || checkSaveAuth()){
						if(!refresh) loginconf.put("GoogleAuthToken", provider.getRefreshToken());
						loginconf.put("SaveAuth", true);
					} else {
						loginconf.remove("GoogleAuthToken");
						loginconf.remove("SaveAuth");
					}
				} catch (Exception e) {
					alertFailedLogin();
					continue;
				}
				
			}
			UIManager.put("OptionPane.noButtonText", "No");
			UIManager.put("OptionPane.yesButtonText", "Yes");
			UIManager.put("OptionPane.okButtonText", "Ok");
			UIManager.put("OptionPane.cancelButtonText", "Cancel");

            if (cp != null)
                go = new PokemonGo(cp, http);
            else
                throw new IllegalStateException();
            BlossomsPoGoManager.config.put("login", loginconf);
            saveConfig();
			logged = true;
		}
		mainWindow = new PokemonGoMainWindow(go, console);
		mainWindow.start();
	}

	// TODO is actually a relog function
	public static void logOff() throws Exception {
		logged = false;
		mainWindow.setVisible(false);
		mainWindow.dispose();
		mainWindow = null;
		logOn();
	}
	
	private static void alertFailedLogin() {
		JOptionPane.showMessageDialog(null, "Unfortunately, your login has failed. Press OK to try again.", "Login Failed", JOptionPane.PLAIN_MESSAGE);
	}
	private static boolean checkSaveAuth() {
		UIManager.put("OptionPane.noButtonText", "No");
		UIManager.put("OptionPane.yesButtonText", "Yes");
		UIManager.put("OptionPane.okButtonText", "Ok");
		UIManager.put("OptionPane.cancelButtonText", "Cancel");
		return JOptionPane.showConfirmDialog(null, "Do you wish to save the password/auth token?\nCaution: These are saved in plain-text.", "Save Authentication?", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
	}
}
