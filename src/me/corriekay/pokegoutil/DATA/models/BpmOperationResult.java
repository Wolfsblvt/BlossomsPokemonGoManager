package me.corriekay.pokegoutil.DATA.models;

import java.util.ArrayList;
import java.util.List;

import me.corriekay.pokegoutil.DATA.enums.OperationError;
import me.corriekay.pokegoutil.GUI.enums.OperationId;

/**
 * An extension of BpmResult. Contains more information relating to an operation task.
 */
public class BpmOperationResult extends BpmResult {

    private final List<String> successMessageList = new ArrayList<String>();
    private OperationError operationError;
    private OperationId nextOperation;

    /**
     * Instantiate a BpmOperationResult. Usually used for success results.
     */
    public BpmOperationResult() {
        super();
    }

    /**
     *
     * Instantiate a BpmOperationResult with an error message and error type.
     *
     * @param errorMessage the error message
     * @param operationError the operation error type
     */
    public BpmOperationResult(final String errorMessage, final OperationError operationError) {
        super(errorMessage);
        this.operationError = operationError;
    }

    /**
     * Adds a success message into the list
     *
     * @param successMessage the success message to be added into the list
     */
    public void addSuccessMessage(final String successMessage) {
        successMessageList.add(successMessage);
    }

    /**
     * Gets the next operation to be called. Used in scenarios such as 'transfer after evolution'.
     *
     * @return the next operation that should be done
     */
    public OperationId getNextOperation() {
        return nextOperation;
    }

    /**
     * Get the type of operation error.
     *
     * @return the type of operation error
     */
    public OperationError getOperationError() {
        return operationError;
    }

    /**
     * Get all the success messages that have been added.
     *
     * @return the list of success messages
     */
    public List<String> getSuccessMessageList() {
        return successMessageList;
    }

    /**
     * Check if there is a next operation
     *
     * @return if there is a next operation
     */
    public boolean hasNextOperation() {
        return nextOperation != null;
    }

    /**
     * Set the next operation to be called. Used in scenarios such as 'transfer after evolution'.
     *
     * @param nextOperation the next operation that should be done
     */
    public void setNextOperation(final OperationId nextOperation) {
        this.nextOperation = nextOperation;
    }

    /**
     * Set the type of operation error.
     *
     * @param operationError the type of operation error
     */
    public void setOperationError(final OperationError operationError) {
        this.operationError = operationError;
    }
}
