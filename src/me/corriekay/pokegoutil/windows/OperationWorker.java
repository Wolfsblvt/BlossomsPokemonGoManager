package me.corriekay.pokegoutil.windows;

import java.util.function.Consumer;

import javax.swing.SwingWorker;

/**
 * Worker class to call the functions of each operation in a generic way.
 * @param <T> if a consumer this is the type
 */
public final class OperationWorker<T> extends SwingWorker<Void, Void> {
    private Runnable function;
    private Consumer<T> consumer;
    private T consumable;
    
    /**
     * Constructor that receives a function that has none arguments and just call it.
     * @param function with no arguments that will be called in the "doInBackground" method.
     */
    public OperationWorker(final Runnable function) {
        this.function = function;
    }
    
    /**
     * Constructor that receives a function that expect to receive one Object and call it.
     * @param action function that will be called passin the consumable Object to it.
     * @param consumable Object that will be passed to action function.
     */
    public OperationWorker(final Consumer<T> action, final T consumable) {
        this.consumer = action;
        this.consumable = consumable;
    }
    
    @Override
    protected Void doInBackground() {
        if (function != null) { 
            function.run(); 
        } else if (consumer != null) { 
            consumer.accept(consumable); 
        }
        return null;
    }
}
