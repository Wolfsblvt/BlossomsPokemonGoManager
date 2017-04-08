package me.corriekay.pokegoutil.utils.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

import javax.swing.JTextField;

import me.corriekay.pokegoutil.windows.OperationWorker;

/**
 * Helper class to encapsulate methods and fields used by IVTransfer JTextField.
 */
public class IVTransferTextField extends JTextField {

    private static final int DEFAULT_SIZE = 20;
    private static final long serialVersionUID = -1497271459888003626L;
    
    /**
     * Default constructor that initialize the JTextField with the default properties.
     * @param action function that will be called passing the text present in this TextField
     */
    public IVTransferTextField(final Consumer<String> action) {
        super("", DEFAULT_SIZE);
        this.addKeyListener(
                new KeyListener() {
                    @Override
                    public void keyPressed(final KeyEvent event) {
                        if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                            new OperationWorker(action, IVTransferTextField.this.getText()).execute();
                        }
                    }

                    @Override
                    public void keyTyped(final KeyEvent event) {
                        // nothing here
                    }

                    @Override
                    public void keyReleased(final KeyEvent event) {
                        // nothing here
                    }
                });

        new GhostText(this, "Pokemon IV");
    }
}
