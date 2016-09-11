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

    private static final int numOfLines = 5;
    private JTextArea textArea;
    private final String[] testLines;

    /**
     * Instantiate a LogControllerTest and generate the testLines.
     */
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
     * Get the array of lines in text area.
     *
     * @return array of lines in text area
     */
    private String[] getLines() {
        return textArea.getText().split("\\r?\\n");
    }

    /**
     * Test for number of lines being trimmed when it is more than the max limit.
     */
    @Test
    public void lineIsTrimmedToMaxLength() {
        printLines();
        printLines();

        final int expectedLength = numOfLines;
        final String[] textareaLines = getLines();
        assertThat(textAreaHas(expectedLength), textareaLines.length, is(expectedLength));
    }

    /**
     * Test for test lines are captured in the text area in the right order.
     */
    @Test
    public void linesArePrintedCorrectly() {
        printLines();

        final String[] textareaLines = getLines();

        for (int i = 0; i < numOfLines; i++) {
            assertThat("line contains printed text", textareaLines[i], containsString(testLines[i]));
        }
    }

    /**
     * Test for number of test lines matches number of lines in text area.
     */
    @Test
    public void linesIsAddedToTextArea() {
        printLines();

        final int expectedLength = numOfLines;
        final String[] textareaLines = getLines();
        assertThat(textAreaHas(expectedLength), textareaLines.length, is(expectedLength));
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
     * Helper method for assert reason.
     *
     * @param numOfLines number of lines
     * @return text area has ... lines
     */
    private String textAreaHas(final int numOfLines) {
        return "textArea has " + numOfLines + " lines";
    }
}
