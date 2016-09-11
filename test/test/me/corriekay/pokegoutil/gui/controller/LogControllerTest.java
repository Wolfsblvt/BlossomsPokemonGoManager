package test.me.corriekay.pokegoutil.gui.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.swing.JTextArea;

import org.junit.Before;
import org.junit.Test;

import me.corriekay.pokegoutil.gui.controller.LogController;
import me.corriekay.pokegoutil.utils.logging.ConsolePrintStream;

/**
 * Test for LogController.
 */
public class LogControllerTest {

    private JTextArea textArea;
    private final String[] testLines;
    private static final int numOfLines = 5;

    public LogControllerTest() {
        testLines = new String[numOfLines];
        for (int i = 0; i < numOfLines; i++) {
            testLines[i] = "Test Line " + i;
        }
    }

    /**
     * Before every test.
     */
    @Before
    public void beforeTest() {
        textArea = new JTextArea();
        final LogController logController = new LogController();
        logController.setTextArea(textArea);
        logController.setMaxLines(numOfLines);
        ConsolePrintStream.setup(logController);
    }

    /**
     * Test for empty text area.
     */
    @Test
    public void emptyTextArea() {
        assertThat("textArea is empty", 0, is(textArea.getText().length()));
    }

    /**
     * Test for number of lines being trimmed when it is more than the max limit.
     */
    @Test
    public void lineIsTrimmedToMaxLength() {
        printLines();
        printLines();

        final int expectedLength = numOfLines;
        final String textareaLines[] = getLines();
        assertThat("textArea has " + expectedLength + " lines", textareaLines.length, is(expectedLength));

        for (int i = 0; i < numOfLines; i++) {
            assertThat("line contains printed text", textareaLines[i], containsString(testLines[i]));
        }
    }

    /**
     * Test for test lines are captured in the text area.
     */
    @Test
    public void linesIsAddedToTextArea() {
        printLines();

        final int expectedLength = numOfLines;
        final String textareaLines[] = getLines();
        assertThat("textArea has " + expectedLength + " lines", textareaLines.length, is(expectedLength));
        for (int i = 0; i < numOfLines; i++) {
            assertThat("line contains printed text", textareaLines[i], containsString(testLines[i]));
        }
    }

    /**
     * Print the test lines.
     */
    private void printLines() {
        for (final String s : testLines) {
            System.out.println(s);
        }
    }

    /**
     * Get the array of lines in text area.
     *
     * @return array of lines in text area
     */
    private String[] getLines() {
        return textArea.getText().split("\\r?\\n");
    }
}
