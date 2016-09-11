package me.corriekay.pokegoutil.utils.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import me.corriekay.pokegoutil.gui.controller.LogController;
import me.corriekay.pokegoutil.utils.helpers.DateHelper;

public class ConsoleOutStream extends OutputStream {

    private String logname = "console.log";
    private final String newLine = System.getProperty("line.separator");

    private PrintStream filestream;
    LogController logController;

    public ConsoleOutStream(final LogController logController) {
        this.logController = logController;
        try {
            final File f = new File(System.getProperty("user.dir"), logname);
            if (!f.exists()) {
                f.createNewFile();
            }
            filestream = new PrintStream(new FileOutputStream(f, true));
        } catch (final IOException e) {
            ConsolePrintStream.printException(e);
        }
    }

    private String formatString(final String s) {
        if (s.equals(newLine)) {
            return s;
        }
        return "[" + timestamp() + "]: " + s;
    }

    public void setLogName(final String log) {
        logname = log;
    }

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
