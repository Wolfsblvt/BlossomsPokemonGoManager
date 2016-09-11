package me.corriekay.pokegoutil.gui.controller;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import me.corriekay.pokegoutil.utils.logging.ConsolePrintStream;

/**
 * This controller handles the log printing onto the text area.
 */
public class LogController {

    private static final int defaultMaxLines = 2500;
    private JTextArea textArea;

    private int maxLines;

    /**
     * Instantiate a LogController to handle console output.
     */
    public LogController() {
        this.maxLines = defaultMaxLines;
    }

    /**
     * Adds the line to the text area.
     *
     * @param line line to add
     */
    public void addLine(final String line) {
        if (textArea != null) {
            textArea.append(line);
            trimExcessLines();
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }

    /**
     * Set the text area to empty.
     */
    public void clearAllLines() {
        if (textArea != null) {
            textArea.setText("");
        }
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
     * Get the text area used to append logs.
     *
     * @return text area used to append logs to
     */
    public JTextArea getTextArea() {
        return textArea;
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
     * Get the text area used to append logs to.
     *
     * @param textArea text area used to append logs to
     */
    public void setTextArea(final JTextArea textArea) {
        this.textArea = textArea;
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
