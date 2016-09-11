package me.corriekay.pokegoutil.utils.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import me.corriekay.pokegoutil.gui.controller.LogController;
import me.corriekay.pokegoutil.utils.logging.ConsolePrintStream;

@SuppressWarnings("serial")
public class Console extends JFrame {

    public static class ConsoleOut {
        public void out(final String line) {

        }
    }
    public JTextArea ta = new JTextArea();
    public JTextField tf = new JTextField();
    public JScrollPane jsp;

    public ConsoleOut out = new ConsoleOut();

    public final LogController logController;

    /**
     * Set the default charset for the console, so that characters are displayed correctly.
     *
     * @param charsetName The name of the charset.
     */
    public static void setDefaultCharset(final String charsetName) {
        try {
            final Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, Charset.forName(charsetName));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            System.out.println(ex.toString());
        }
    }



    public Console(final String windowName, final int x, final int y, final boolean smartscroll) {
        super(windowName);
        setSize(new Dimension(677, 340));
        setLocation(x, y);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        tf.setPreferredSize(new Dimension(670, 25));
        setLayout(new BorderLayout());
        jsp = new JScrollPane(ta);
        if (smartscroll) {
            new SmartScroller(jsp);
        }
        add(jsp, BorderLayout.CENTER);
        add(tf, BorderLayout.SOUTH);
        setResizable(false);

        tf.addActionListener((final ActionEvent e) -> {
            out.out(tf.getText());
            tf.setText("");
        });

        setDefaultCharset("UTF8");

        logController = new LogController(ta);
        ConsolePrintStream.setup(logController);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
