package me.corriekay.pokegoutil.utils.ui;

import me.corriekay.pokegoutil.utils.helpers.DateHelper;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Iterator;

@SuppressWarnings("serial")
public class Console extends JFrame {

    public JTextArea ta = new JTextArea();
    public JTextField tf = new JTextField();
    public JScrollPane jsp;
    ArrayDeque<String> lines = new ArrayDeque<>();
    public ConsoleOut out = new ConsoleOut();

    private String logname = "console.log";

    public void setLogName(String log) {
        logname = log;
    }

    public Console(String windowName, int x, int y, boolean smartscroll) {
        super(windowName);
        setSize(new Dimension(677, 340));
        setLocation(x, y);
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        tf.setPreferredSize(new Dimension(670, 25));
        setLayout(new BorderLayout());
        jsp = new JScrollPane(ta);
        if (smartscroll) new SmartScroller(jsp);
        add(jsp, BorderLayout.CENTER);
        add(tf, BorderLayout.SOUTH);
        setResizable(false);

        tf.addActionListener((ActionEvent e) -> {
            out.out(tf.getText());
            tf.setText("");
        });

        setDefaultCharset("UTF8");

        ConsolePrintStream cps = new ConsolePrintStream();
        System.setOut(cps);
        System.setErr(cps);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void setDefaultCharset(final String p_charset) {
        try {
            final Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, Charset.forName(p_charset));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            System.out.println(ex.toString());
        }
    }

    public void clearAllLines() {
        ta.setText("");
        lines.clear();
    }

    private void addLine(String s) {
        lines.add(s);
        while (lines.size() > 2500) {
            lines.pop();
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = lines.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next());
        }
        ta.setText(sb.toString());
        ta.setCaretPosition(ta.getDocument().getLength());
    }

    class ConsolePrintStream extends PrintStream {
        public ConsolePrintStream() {
            super(new ConsoleOutStream());
        }
    }

    class ConsoleOutStream extends OutputStream {

        private PrintStream filestream;

        public ConsoleOutStream() {
            try {
                File f = new File(System.getProperty("user.dir"), logname);
                if (!f.exists()) {
                    f.createNewFile();
                }
                filestream = new PrintStream(new FileOutputStream(f));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void write(int i) throws IOException {
            String s = formatString(String.valueOf((char) i));
            addLine(s);
            filestream.write(s.getBytes());
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            String s = formatString(new String(b, off, len));
            addLine(s);
            filestream.write(s.getBytes());
        }

        @Override
        public void write(byte[] b) throws IOException {
            String s = formatString(new String(b, 0, b.length));
            addLine(s);
            filestream.write(s.getBytes());
        }

        private String timestamp() {
            return DateHelper.currentTime();
        }

        private String formatString(String s) {
            if (s.equals(System.getProperty("line.separator"))) return s;

            return "[" + timestamp() + "]: " + s;
        }
    }

    public static class ConsoleOut {
        public void out(String line) {

        }
    }
}
