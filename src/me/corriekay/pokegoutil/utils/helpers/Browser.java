package me.corriekay.pokegoutil.utils.helpers;


import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public final class Browser {
    /** Prevent initializing this class. */
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
            try {
                Runtime rt = Runtime.getRuntime();
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Now lets try Mac systems
        if (os.contains("mac")) {
            try {
                Runtime rt = Runtime.getRuntime();
                rt.exec("open" + url);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // That's like the best try for Linux systems
        if (os.contains("nix") || os.contains("nux")) {
            try {
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("xdg-open " + url);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Last chance for Linux now
        if (os.contains("nix") || os.contains("nux")) {
            Runtime rt = Runtime.getRuntime();
            String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
                    "netscape", "opera", "links", "lynx"};

            StringBuilder cmd = new StringBuilder();
            for (int i = 0; i < browsers.length; i++)
                cmd.append(i == 0 ? "" : " || ").append(browsers[i]).append(" \"").append(url).append("\" ");

            try {
                rt.exec(new String[]{"sh", "-c", cmd.toString()});
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Dear lord, everything has failed. No chance here anymore. I don't know what else I can do.
        // Mum, I have failed.
        System.out.println("Found no chance to open Browser URL. Terminate now.");
        return false;
    }
}
