package me.corriekay.pokegoutil.DATA.models;

import me.corriekay.pokegoutil.DATA.enums.OperationError;

public class BPMOperationResult extends BPMResult {
    
    private OperationError operationError;
    public BPMOperationResult() {
        super();
    }

    public BPMOperationResult(String errorMessage, OperationError operationError){
        super(errorMessage);
    }

    public OperationError getOperationError() {
        return operationError;
    }
    
    public void setOperationError(OperationError operationError) {
        this.operationError = operationError;
    }
}
