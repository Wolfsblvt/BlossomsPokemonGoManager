package me.corriekay.pokegoutil.GUI.enums;

import java.util.HashMap;
import java.util.Map;

public enum OperationId {
    RENAME("Rename","Renaming","Renamed"),
    TRANSFER("Transfer","Transfering","Transfered"),
    POWERUP("Power Up","Powering Up","Powered Up"),
    EVOLVE("Evolve","Evolving","Evolved"),
    FAVORITE("Favorite","Favoriting","Favorited");

    private static final Map<String, OperationId> operationMap = new HashMap<String, OperationId>();

    static {
        for (OperationId id : OperationId.values()) {
            operationMap.put(id.getActionName(), id);
        }
    }

    private String actionName;
    private String actionVerbDuring;
    private String actionVerbFinished;

    private OperationId(String actionName, String actionVerbDuring, String actionVerbFinished) {
        this.actionName = actionName;
        this.actionVerbDuring = actionVerbDuring;
        this.actionVerbFinished = actionVerbFinished;
    }

    public String getActionName() {
        return actionName;
    }

    public String getActionVerbDuring() {
        return actionVerbDuring;
    }

    public String getActionVerbFinished() {
        return actionVerbFinished;
    }

    public static OperationId get(String actionName) {
        return operationMap.get(actionName);
    }
}
