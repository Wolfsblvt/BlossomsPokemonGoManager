package me.corriekay.pokegoutil.utils.helpers;


import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;

import me.corriekay.pokegoutil.data.enums.ExceptionMessages;
import me.corriekay.pokegoutil.utils.StringLiterals;

public final class FileHelper {
    public static final Charset CHARSET = Charset.forName("UTF-8");
    public static final int INDENT = 4;

    /** Prevent initializing this class. */
    private FileHelper() {
    }

    private static ClassLoader classLoader = FileHelper.class.getClassLoader();

    /**
     * Deletes the file (which can't be a dir).
     *
     * @param file The file.
     */
    public static void deleteFile(final File file) {
        deleteFile(file, false);
    }

    /**
     * Deletes the file, or all files in the directory. Optionally the directory itself too.
     *
     * @param file      The file.
     * @param deleteDir Deletes the base directory too.
     */
    public static void deleteFile(final File file, final boolean deleteDir) {
        if (file.isDirectory()) {
            for (final File subFile : file.listFiles()) {
                deleteFile(subFile, true);
            }
            if (deleteDir) {
                file.delete();
            }
        } else {
            file.delete();
        }
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

    /**
     * Reads given file. Returns null if file can't be read.
     *
     * @param file The file.
     * @return The file contents.
     */
    public static String readFile(final File file) {
        try {
            return readFileWithExceptions(file);
        } catch (IOException e) {
            System.out.println(ExceptionMessages.COULD_NOT_READ.with(e));
        }
        return null;
    }

    /**
     * Read file from given input stream and returns it.
     *
     * @param inputStream The InputStream of the file.
     * @return The file as string.
     */
    public static String readFile(final InputStream inputStream) {
        String str;
        final StringBuilder buf = new StringBuilder();
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, CHARSET));
            while ((str = reader.readLine()) != null) {
                buf.append(str).append(StringLiterals.NEWLINE);
            }
        } catch (IOException e) {
            System.out.println(ExceptionMessages.COULD_NOT_READ.with(e));
        } finally {
            try {
                inputStream.close();
            } catch (IOException ignore) {
            }
        }
        return buf.toString().trim();
    }

    /**
     * Reads given file. Throws exceptions on error.
     *
     * @param file The file.
     * @return The file contents.
     * @throws IOException File IO exceptions.
     */
    public static String readFileWithExceptions(final File file) throws IOException {
        final BufferedReader in = new BufferedReader(new FileReader(file));
        final StringBuilder sb = new StringBuilder();

        String content = "";
        boolean firstline = true;
        do {
            sb.append(content);
            content = in.readLine();
            if (content != null && !firstline) {
                sb.append(StringLiterals.NEWLINE);
            }
            firstline = false;
        } while (content != null);
        in.close();
        return sb.toString();
    }

    public static void saveFile(File file, String saveme) {
        try {
            file.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(saveme);
            out.close();
        } catch (Exception e) {
            System.out.println("Exception caught trying to save file. Path: " + file.getAbsolutePath());
        }
    }
}
