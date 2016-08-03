package me.corriekay.pokegoutil;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.net.URI;

import javax.swing.*;

import org.json.JSONObject;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.auth.*;

import me.corriekay.pokegoutil.utils.Console;
import me.corriekay.pokegoutil.utils.Utilities;
import me.corriekay.pokegoutil.windows.PokemonGoMainWindow;
import okhttp3.OkHttpClient;

public class BlossomsPoGoManager {
	
	private static final File file = new File(System.getProperty("user.dir"), "config.json");
	private static JSONObject config;
	private static Console console;
	
	public static void main(String[] args) throws Exception {
		Utilities.setNativeLookAndFeel();
		console = new Console("Console", 0, 0, true);
		console.setVisible(false);
		if(!file.exists()) {
			file.createNewFile();
			config = new JSONObject("{\"login\":{},\"options\":{}}");
			Utilities.saveFile(file, config.toString(4));
		} else {
			config = new JSONObject(Utilities.readFile(file));
		}
		
		boolean logged = false;
		OkHttpClient http;
		CredentialProvider cp = null;
		PokemonGo go = null;
		while(!logged) {
			//BEGIN LOGIN WINDOW
			JSONObject config = BlossomsPoGoManager.config.getJSONObject("login");
			go = null;
			cp = null;
			http = new OkHttpClient();
	
			UIManager.put("OptionPane.noButtonText", "Use Google Auth");
			UIManager.put("OptionPane.yesButtonText", "Use PTC Auth");
			UIManager.put("OptionPane.cancelButtonText", "Exit");
			UIManager.put("OptionPane.okButtonText", "Ok");
			
			JTextField username = new JTextField(config.optString("PTCUsername", null));
			JTextField password = new JPasswordField(config.optString("PTCPassword", null));
	
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
				config.remove("GoogleAuthToken");
				try {
					PtcCredentialProvider provider = new PtcCredentialProvider(http, username.getText(), password.getText());
					cp = provider;
					config.put("PTCUsername", username.getText());
					if(config.optBoolean("SaveAuth", false) || checkSaveAuth()) {
						config.put("PTCPassword", password.getText());
						config.put("SaveAuth", true);
					} else {
						config.remove("PTCPassword");
						config.remove("SaveAuth");
					}
				} catch(Exception e){
					alertFailedLogin();
					continue;
				} 
			} else if (response == JOptionPane.NO_OPTION) {
				//Using Google, remove PTC infos
				config.remove("PTCUsername");
				config.remove("PTCPassword");
				String authCode = config.optString("GoogleAuthToken", null);
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
					JOptionPane.showMessageDialog(null, "Logging in using cached google auth token!", "Google Auth", JOptionPane.PLAIN_MESSAGE);
					refresh = true;
				}
				try {
					GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(http);
					if(refresh) provider.refreshToken(authCode);
					else provider.login(authCode); 
					cp = provider;
					if(config.optBoolean("SaveAuth", false) || checkSaveAuth()){
						if(!refresh) config.put("GoogleAuthToken", provider.getRefreshToken());
						config.put("SaveAuth", true);
					} else {
						config.remove("GoogleAuthToken");
						config.remove("SaveAuth");
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

			go = new PokemonGo(cp, http);
			BlossomsPoGoManager.config.put("login", config);
			Utilities.saveFile(file, BlossomsPoGoManager.config.toString(4));
			logged = true;
		}
		new PokemonGoMainWindow(go, console).start();
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
