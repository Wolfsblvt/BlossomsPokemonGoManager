package me.corriekay.pokegoutil.gui.controller;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import me.corriekay.pokegoutil.utils.logging.ConsolePrintStream;

/**
 * This controller handles the log printing onto the text area.
 */
public class LogController {

    private final JTextArea textArea;
    private static final int defaultMaxLines = 2500;
    private int maxLines;

    /**
     * Instantiate a LogController to handle console output.
     *
     * @param textArea console output will be appended to this text area
     */
    public LogController(final JTextArea textArea) {
        this.textArea = textArea;
        this.maxLines = defaultMaxLines;
    }

    /**
     * Adds the line to the text area.
     *
     * @param line line to add
     */
    public void addLine(final String line) {
        textArea.append(line);
        trimExcessLines();
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    /**
     * Set the text area to empty.
     */
    public void clearAllLines() {
        textArea.setText("");
    }

    /**
     * Get the max number of lines text area will have.
     *
     * @return max number of lines text area will have
     */
    public int getMaxLines() {
        return maxLines;
    }

    /**
     * Set the max number of lines text area will have.
     *
     * @param maxLines max number of lines text area will have
     */
    public void setMaxLines(final int maxLines) {
        this.maxLines = maxLines + 1;
    }

    /**
     * When text area contains more lines than maxLines, the lines at the beginning will be trimmed off.
     */
    private void trimExcessLines() {
        final int numLinesToTrunk = textArea.getLineCount() - maxLines;
        if (numLinesToTrunk > 0) {
            try {
                final int posOfLastLineToTrunk = textArea.getLineEndOffset(numLinesToTrunk - 1);
                textArea.replaceRange("", 0, posOfLastLineToTrunk);
            } catch (final BadLocationException e) {
                maxLines = Integer.MAX_VALUE;
                System.err.println("Error trimming text area, trimming will be disabled!");
                ConsolePrintStream.printException(e);
            }
        }
    }
}
