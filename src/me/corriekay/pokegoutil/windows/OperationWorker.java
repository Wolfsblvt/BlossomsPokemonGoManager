package me.corriekay.pokegoutil.windows;

import java.util.function.Consumer;

import javax.swing.SwingWorker;

/**
 * Worker class to call the functions of each operation in a generic way.
 */
public final class OperationWorker extends SwingWorker<Void, Void> {
    private Runnable function;
    private Consumer<String> stringConsumer;
    private String consumable;
    
    /**
     * Constructor that receives a function that has none arguments and just call it.
     * @param function with no arguments that will be called in the "doInBackground" method.
     */
    public OperationWorker(final Runnable function) {
        this.function = function;
    }
    
    /**
     * Constructor that receives a function that expect to receive one String and call it.
     * @param action function that will be called passin the consumable string to it.
     * @param consumable string that will be passed to action function.
     */
    public OperationWorker(final Consumer<String> action, final String consumable) {
        this.stringConsumer = action;
        this.consumable = consumable;
    }
    
    @Override
    protected Void doInBackground() {
        if (function != null) { 
            function.run(); 
        } else if (stringConsumer != null) { 
            stringConsumer.accept(consumable); 
        }
        return null;
    }
}
