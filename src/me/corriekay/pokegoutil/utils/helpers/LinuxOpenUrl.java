package me.corriekay.pokegoutil.utils.helpers;

import java.io.IOException;

public class LinuxOpenUrl implements OpenUrlStrategy {

    @Override
    public boolean tryOpenUrl(String url) {
        try {
            Runtime runtime = Runtime.getRuntime();
            runtime.exec("xdg-open " + url);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        
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
        System.out.println("Found no chance to open Browser URL. Terminate now.");
        return false;
    }

}
