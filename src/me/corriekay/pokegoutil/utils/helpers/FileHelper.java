package me.corriekay.pokegoutil.utils.helpers;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public final class FileHelper {
    private FileHelper() { /* Prevent initializing this class */ }

    private static ClassLoader classLoader = FileHelper.class.getClassLoader();

    public static void deleteFile(File file, boolean deleteDir) {
        if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                deleteFile(subFile, true);
            }
            if (deleteDir) {
                file.delete();
            }
        } else {
            file.delete();
        }
    }

    public static boolean checkFilename(String checkme, boolean warn) {
        String[] chars = new String[]{"\\", "/", ":", "*", "?", "\"", "<", ">", "|"};

        for (String c : chars) {

            if (checkme.contains(c)) {
                if (warn)
                    JOptionPane.showMessageDialog(null, "A file name can't contain any of the following characters: \\/:*\"<>|");
                return false;
            }
        }
        return true;
    }

    public static boolean checkFileExists(File file) {
        if (file.exists()) {
            JOptionPane.showMessageDialog(null, "This file already exists. Please choose another file name.");
            return true;
        }
        return false;
    }

    public static Image loadImage(String filename) {
        try {
            return ImageIO.read(classLoader.getResourceAsStream(filename));
        } catch (Exception e) {
            System.out.println("UNABLE TO READ IMAGE " + filename);
            return null;
        }
    }

    public static String readResourceFile(String res) {
        try {
            InputStream is = classLoader.getResourceAsStream(res);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            in.lines().forEach(s -> sb.append(s).append("\n"));
            in.close();
            return sb.toString();
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    public static String readFile(String url) {
        return readFile(new File(url));
    }

    public static String readFile(File file) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));

            StringBuilder sb = new StringBuilder();

            String l = "";
            boolean firstline = true;
            do {
                sb.append(l);
                l = in.readLine();
                if (l != null && !firstline) {
                    sb.append("\n");
                }
                firstline = false;
            } while (l != null);
            in.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace(); //TODO tagging for future exception handling/logging
        }
        return null;
    }

    public static void saveFile(File file, String saveme) {
        try {
            file.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(saveme);
            out.close();
        } catch (Exception e) {
            System.out.println("Exception caught trying to save file. Path: " + file.getAbsolutePath());
            e.printStackTrace(); //TODO tagging for future exception handling/logging
        }
    }
}
