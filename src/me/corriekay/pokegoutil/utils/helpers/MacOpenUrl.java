package me.corriekay.pokegoutil.utils.helpers;

import java.io.IOException;

public class MacOpenUrl implements OpenUrlStrategy {

    @Override
    public boolean tryOpenUrl(String url) {
        try {
            Runtime rt = Runtime.getRuntime();
            rt.exec("open" + url);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Found no chance to open Browser URL. Terminate now.");
        return false;
    }

}
