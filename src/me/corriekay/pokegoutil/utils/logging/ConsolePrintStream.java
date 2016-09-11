package me.corriekay.pokegoutil.utils.logging;

import java.io.PrintStream;

import me.corriekay.pokegoutil.gui.controller.LogController;

public class ConsolePrintStream extends PrintStream {
    private static ConsolePrintStream consolePrintStream;
    private static PrintStream stdOut;
    private static PrintStream stdErr;

    private ConsolePrintStream(final LogController logController) {
        super(new ConsoleOutStream(logController));
        stdOut = System.out;
        stdErr = System.err;

        setPrintStream(this, this);
    }

    public static void resetPrintStream() {
        setPrintStream(stdOut, stdErr);
    }

    private static void setPrintStream(final PrintStream out, final PrintStream err) {
        System.setOut(out);
        System.setErr(err);
    }

    public static void setup(final LogController logController) {
        consolePrintStream = new ConsolePrintStream(logController);
    }

    public static ConsolePrintStream getConsolePrintStream() {
        return consolePrintStream;
    }
}