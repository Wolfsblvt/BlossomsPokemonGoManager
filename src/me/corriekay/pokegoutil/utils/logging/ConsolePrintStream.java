package me.corriekay.pokegoutil.utils.logging;

import java.io.PrintStream;

import me.corriekay.pokegoutil.gui.controller.LogController;

/**
 * Handles redirection of the console out and err.
 */
public class ConsolePrintStream extends PrintStream {
    private static ConsolePrintStream consolePrintStream;
    private static PrintStream stdOut;
    private static PrintStream stdErr;

    static {
        stdOut = System.out;
        stdErr = System.err;
    }

    /**
     * Get the consolePrintSteam that is being used.
     *
     * @return the consolePrintSteam that is being used
     */
    public static ConsolePrintStream getConsolePrintStream() {
        return consolePrintStream;
    }

    /**
     * Prints the exception to the output stream.
     *
     * @param e the exception
     */
    public static void printException(final Exception e) {
        e.printStackTrace(consolePrintStream);
    }

    /**
     * Reset the print streams to the systems default.
     */
    public static void resetPrintStream() {
        setPrintStream(stdOut, stdErr);
    }

    /**
     * Set the out and err stream.
     *
     * @param out out stream
     * @param err err stream
     */
    private static void setPrintStream(final PrintStream out, final PrintStream err) {
        System.setOut(out);
        System.setErr(err);
    }

    /**
     * Creates the consolePrintStream instance with the log controller.
     *
     * @param logController log controller that handles output to the gui
     */
    public static void setup(final LogController logController) {
        consolePrintStream = new ConsolePrintStream(logController);
    }

    /**
     * Instantiate a ConsolePrintStream and set the print stream to this instance.
     *
     * @param logController log controller that handles output to the gui
     */
    private ConsolePrintStream(final LogController logController) {
        super(new ConsoleOutStream(logController));
        setPrintStream(this, this);
    }
}