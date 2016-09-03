package me.corriekay.pokegoutil.DATA.models;

import java.util.ArrayList;
import java.util.List;

import me.corriekay.pokegoutil.DATA.enums.OperationError;
import me.corriekay.pokegoutil.GUI.enums.OperationId;

public class BpmOperationResult extends BpmResult {

    private final List<String> successMessageList = new ArrayList<String>();
    private OperationError operationError;
    private OperationId nextOperation;

    public BpmOperationResult() {
        super();
    }

    public BpmOperationResult(final String errorMessage, final OperationError operationError) {
        super(errorMessage);
        this.operationError = operationError;
    }

    public void addSuccessMessage(final String successMessage) {
        successMessageList.add(successMessage);
    }

    public OperationId getNextOperation() {
        return nextOperation;
    }

    public OperationError getOperationError() {
        return operationError;
    }

    public List<String> getSuccessMessageList() {
        return successMessageList;
    }

    public boolean hasNextOperation() {
        return nextOperation != null;
    }

    public void setNextOperation(final OperationId nextOperation) {
        this.nextOperation = nextOperation;
    }

    public void setOperationError(final OperationError operationError) {
        this.operationError = operationError;
    }
}
