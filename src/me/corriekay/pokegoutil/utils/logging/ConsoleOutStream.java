package me.corriekay.pokegoutil.utils.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import me.corriekay.pokegoutil.gui.controller.LogController;
import me.corriekay.pokegoutil.utils.helpers.DateHelper;

/**
 * Handles writing event of the console out and err. Write and appends to the log file.
 */
public class ConsoleOutStream extends OutputStream {

    private String logName = "console.log";
    private final String newLine = System.getProperty("line.separator");

    private PrintStream filestream;
    private final LogController logController;

    /**
     * Instantiate a ConsoleOutStream to handle writing of logs.
     *
     * @param logController log controller that handles output to the gui
     */
    public ConsoleOutStream(final LogController logController) {
        super();

        this.logController = logController;
        try {
            final File f = new File(System.getProperty("user.dir"), logName);
            if (!f.exists()) {
                f.createNewFile();
            }
            filestream = new PrintStream(new FileOutputStream(f, true));
        } catch (final IOException e) {
            ConsolePrintStream.printException(e);
        }
    }

    /**
     * Formats the given message with the timestamp appended to the front.
     *
     * @param s the message
     * @return format message with timestamp
     */
    private String formatString(final String s) {
        if (s.equals(newLine)) {
            return s;
        }
        return String.format("[%s]: %s", timestamp(), s);
    }

    /**
     * Set the log file name.
     * 
     * @param logName the log file name
     */
    public void setLogName(final String logName) {
        this.logName = logName;
    }

    /**
     * Helper method to return timestamp string.
     *
     * @return the timestamp string
     */
    private String timestamp() {
        return DateHelper.currentTime();
    }

    @Override
    public void write(final byte[] b) throws IOException {
        final String s = formatString(new String(b, 0, b.length));
        logController.addLine(s);
        filestream.write(s.getBytes());
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        final String s = formatString(new String(b, off, len));
        logController.addLine(s);
        filestream.write(s.getBytes());
    }

    @Override
    public void write(final int i) throws IOException {
        final String s = formatString(String.valueOf((char) i));
        logController.addLine(s);
        filestream.write(s.getBytes());
    }
}
