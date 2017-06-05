package me.corriekay.pokegoutil.utils.helpers;

import java.io.IOException;

public class WindowOpenUrl implements OpenUrlStrategy{
    @Override
    public boolean tryOpenUrl(String url){
        try {
            Runtime rt = Runtime.getRuntime();
            rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Found no chance to open Browser URL. Terminate now.");
        return false;
    }
}
