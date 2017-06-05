package me.corriekay.pokegoutil.utils.helpers;


import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/*
interface interfaceBrowser {
    public boolean openUrl(String url);
}
*/
//public final class Browser implements interfaceBrowser{
public final class Browser{
    /** Prevent initializing this class. */
    private static OpenUrlStrategy iOpenUrl = null;
    
    private Browser() {
    }
    /***
     * Opens given URL in users default browser of his operating system.
     * Should work for all operating systems, with fallback.
     *
     * @param url The URL to open in browser
     * @return If opening was successful
     */
    public static boolean openUrl(String url) {
        // We start to check if Desktop API is supported. In that case everything is easy.
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                // Okay, that was easy
                desktop.browse(new URI(url));
                return true;
            } catch (IOException | URISyntaxException | UnsupportedOperationException e) {
                // Something went wrong with desktop, we try running the crossPlatformSolution
                e.printStackTrace();
                return tryCrossPlatformOpenUrl(url);
            }
        } else {

            return false;
        }
    }

    private static boolean tryCrossPlatformOpenUrl(String url) {
        // We need the OS
        String os = System.getProperty("os.name").toLowerCase();
        // Check for windows first
        if (os.contains("win")) {
            iOpenUrl = new WindowOpenUrl();
        }

        // Now lets try Mac systems
        if (os.contains("mac")) {
            iOpenUrl = new MacOpenUrl();
        }

        // That's like the best try for Linux systems
        if (os.contains("nix") || os.contains("nux")) {
            iOpenUrl = new LinuxOpenUrl();
        }

        // Dear lord, everything has failed. No chance here anymore. I don't know what else I can do.
        // Mum, I have failed.
        
        return iOpenUrl.tryOpenUrl(url);
    }
}
