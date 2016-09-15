package me.corriekay.pokegoutil.utils.version;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.JOptionPane;

import me.corriekay.pokegoutil.utils.ConfigKey;
import me.corriekay.pokegoutil.utils.ConfigNew;
import me.corriekay.pokegoutil.utils.StringLiterals;
import me.corriekay.pokegoutil.utils.helpers.Browser;
import me.corriekay.pokegoutil.utils.version.thirdparty.ComparableVersion;

/**
 * An Updater which checks for a newer stable version on GitHub, of this tool.
 */
public class Updater {

    public static final String VERSION_FILENAME = "version.txt";
    public static final String LATEST_VERSION_URL = "https://raw.githubusercontent.com/wolfsblvt/BlossomsPokemonGoManager/master/src/main/resources/";

    private static Updater instance;

    public final ComparableVersion currentVersion;
    private final ConfigNew config = ConfigNew.getConfig();

    private ComparableVersion latestStable = ComparableVersion.DEFAULT;

    /**
     * Constructor that initializes the Updater and reads the current local version.
     * <p>
     * Can't be called externally, use Updater.getUpdater() instead.
     */
    private Updater() {
        // Read the local version file as version
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(VERSION_FILENAME);
        String versionString = readVersionFile(inputStream);
        currentVersion = new ComparableVersion(versionString);
        System.out.println("Current version: " + versionString);
    }

    /**
     * Gets the instance of the Updater to check for newer versions.
     *
     * @return The Updater.
     */
    public static Updater getUpdater() {
        if (instance == null) {
            instance = new Updater();
        }
        return instance;
    }

    /**
     * Gets the latest stable version saved locally.
     * <p>
     * Run queryLatestVersion() to update the version correctly.
     *
     * @return The latest stable version.
     */
    public ComparableVersion getLatestVersion() {
        return latestStable;
    }

    /**
     * Queries the latest version from GitHub.
     * Tries to save it locally under latestStable.
     */
    public void queryLatestVersion() {
        String callUrl = LATEST_VERSION_URL + VERSION_FILENAME;
        try {
            URL url = new URL(callUrl);
            String latestVersionString = readVersionFile(url.openStream());
            latestStable = new ComparableVersion(latestVersionString);
            System.out.println("Latest version from server: " + latestVersionString);
        } catch (IOException ex) {
            // there was some connection problem, or the file did not exist on the server,
            // or your URL was not in the right format.
            // think about what to do now, and put it here.
            System.out.println("Could not get latest version from Server. Reason: " + ex.toString());
            System.out.println("File URL: " + callUrl);
            System.out.println("If that problem persists, post your issue on GitHub and check for newer versions manually.");
        }
    }

    /**
     * Compares if there is a newer version available.
     *
     * @return If there is a newer version.
     */
    public boolean hasNewerVersion() {
        return latestStable.compareTo(currentVersion) > 0;
    }

    /**
     * Searches the web for a new version, compares it to the local one and does actions based on this.
     * <p>
     * If there is a new version, a popup is shown (Unless the user has skipped it).
     * If nothing, it just continues.
     */
    public void checkForNewVersion() {
        queryLatestVersion();
        if (hasNewerVersion()) {
            String skipVersion = config.getString(ConfigKey.SKIP_VERSION);
            if (!latestStable.toString().equals(skipVersion)) {
                final String versionFoundString = "New version found! Version: ";
                System.out.println(versionFoundString + latestStable.toString());

                // First, we remove the skipped entry from config, cause we got a newer one than the one written down there
                if (skipVersion != null) {
                    config.delete(ConfigKey.SKIP_VERSION);
                }

                String message = "A new version was found on GitHub." + StringLiterals.NEWLINE
                        + "Version: " + latestStable + StringLiterals.NEWLINE
                        + StringLiterals.NEWLINE
                        + "Your current version: " + currentVersion + StringLiterals.NEWLINE
                        + StringLiterals.NEWLINE
                        + "It should be updated." + StringLiterals.NEWLINE
                        + "Click 'Download' to be redirected to GitHub where you can download the new version." + StringLiterals.NEWLINE
                        + "Click 'Later' to be reminded on next program start." + StringLiterals.NEWLINE
                        + "Click 'Ignore' and you won't be notified again until the next version releases.";

                String[] options = new String[]{"Download", "Later", "Ignore this Version"};
                int response = JOptionPane.showOptionDialog(null, message, versionFoundString,
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, options, options[0]);

                switch (response) {
                    case 0: // Download
                        String latestReleaseUrl = "https://github.com/Wolfsblvt/BlossomsPokemonGoManager/releases/latest";
                        Browser.openUrl(latestReleaseUrl);
                        System.exit(0);
                        return;
                    case 1: // Later
                        // We do nothing here
                        return;
                    case 2: // Ignore
                        config.setString(ConfigKey.SKIP_VERSION, latestStable.toString());
                        return;
                    default:
                        // Can't happen
                }

            } else {
                // We silently ignore the latest version. User has decided.
                System.out.println("Latest version " + latestStable.toString() + " found, but ignored.");
            }
        }
    }

    /**
     * Read version file from given input stream and returns the version.
     *
     * @param inputStream The InputStream of the version file.
     * @return The version string.
     */
    private String readVersionFile(InputStream inputStream) {
        String str;
        StringBuilder buf = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((str = reader.readLine()) != null) {
                buf.append(str).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Could not read file:" + e.toString());
        } finally {
            try {
                inputStream.close();
            } catch (Throwable ignore) {
            }
        }
        return buf.toString().trim();
    }
}
