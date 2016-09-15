package me.corriekay.pokegoutil.gui.enums;

import java.util.HashMap;
import java.util.Map;

public enum OperationId {
    RENAME("Rename", "Renaming", "Renamed"),
    TRANSFER("Transfer", "Transfering", "Transfered"),
    POWERUP("Power Up", "Powering Up", "Powered Up"),
    EVOLVE("Evolve", "Evolving", "Evolved"),
    FAVORITE("Favorite", "Favoriting", "Favorited");

    private static final Map<String, OperationId> operationMap = new HashMap<>();

    static {
        for (final OperationId id : OperationId.values()) {
            operationMap.put(id.getActionName(), id);
        }
    }

    private String actionName;
    private String actionVerbDuring;
    private String actionVerbFinished;

    OperationId(final String actionName, final String actionVerbDuring, final String actionVerbFinished) {
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

    public static OperationId get(final String actionName) {
        return operationMap.get(actionName);
    }
}
