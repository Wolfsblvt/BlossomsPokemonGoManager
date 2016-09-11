package me.corriekay.pokegoutil.gui.controller;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class LogController {

    private final JTextArea textArea;
    private int maxLines = 2500;

    public LogController(final JTextArea textArea) {
        this.textArea = textArea;
    }

    public void addLine(final String s) {
        textArea.append(s);
        trimExcessLines();
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    public void clearAllLines() {
        textArea.setText("");
    }

    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(final int maxLines) {
        this.maxLines = maxLines + 1;
    }

    private void trimExcessLines() {
        final int numLinesToTrunk = textArea.getLineCount() - maxLines;
        if (numLinesToTrunk > 0) {
            try {
                final int posOfLastLineToTrunk = textArea.getLineEndOffset(numLinesToTrunk - 1);
                textArea.replaceRange("", 0, posOfLastLineToTrunk);
            } catch (final BadLocationException ex) {
                ex.printStackTrace();
            }
        }
    }
}
