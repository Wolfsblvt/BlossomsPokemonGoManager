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
    private LogController logController;
    private final String[] testLines;
    private final int numOfLines = 5;

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
        logController = new LogController();
        logController.setTextArea(textArea);
        logController.setMaxLines(numOfLines);
        ConsolePrintStream.setup(logController);
    }

    @Test
    public void empty_TextArea() {
        assertThat("textArea is empty", 0, is(textArea.getText().length()));
    }

    @Test
    public void line_Is_Trimmed_To_Max_Length() {
        printLines();
        printLines();

        final int expectedLength = numOfLines;
        final String textareaLines[] = getLines();
        assertThat("textArea has " + expectedLength + " lines", textareaLines.length, is(expectedLength));

        for (int i = 0; i < numOfLines; i++) {
            assertThat("line contains printed text", textareaLines[i], containsString(testLines[i]));
        }
    }

    @Test
    public void lines_Is_Added_To_TextArea() {
        printLines();

        final int expectedLength = numOfLines;
        final String textareaLines[] = getLines();
        assertThat("textArea has " + expectedLength + " lines", textareaLines.length, is(expectedLength));
        for (int i = 0; i < numOfLines; i++) {
            assertThat("line contains printed text", textareaLines[i], containsString(testLines[i]));
        }
    }

    private void printLines() {
        for (final String s : testLines) {
            System.out.println(s);
        }
    }

    private String[] getLines() {
        return textArea.getText().split("\\r?\\n");
    }
}
