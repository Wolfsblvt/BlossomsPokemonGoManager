package me.corriekay.pokegoutil.DATA.models;

import java.util.ArrayList;
import java.util.List;

import me.corriekay.pokegoutil.DATA.enums.OperationError;
import me.corriekay.pokegoutil.GUI.enums.OperationID;

public class BPMOperationResult extends BPMResult {

    private List<String> successMessageList = new ArrayList<String>();
    private OperationError operationError;
    private OperationID nextOperation;

    public BPMOperationResult() {
        super();
    }
    
    public BPMOperationResult(String errorMessage, OperationError operationError) {
        super(errorMessage);
        this.operationError = operationError;
    }
    
    public void addSuccessMessage(String successMessage) {
        successMessageList.add(successMessage);
    }
    
    public OperationID getNextOperation() {
        return nextOperation;
    }
    public OperationError getOperationError() {
        return operationError;
    }

    public List<String> getSuccessMessageList() {
        return successMessageList;
    }

    public boolean hasNextOperation(){
        return nextOperation != null;
    }

    public void setNextOperation(OperationID nextOperation) {
        this.nextOperation = nextOperation;
    }

    public void setOperationError(OperationError operationError) {
        this.operationError = operationError;
    }
}
