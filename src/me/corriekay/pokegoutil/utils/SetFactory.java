package me.corriekay.pokegoutil.utils;

import org.json.JSONObject;

public class SetFactory {
    public static SetStrategy createSetStrategy(ConfigKey configKey){
        SetStrategy mySetStrategy;
        switch (configKey.type) {
            case BOOLEAN:
                mySetStrategy = new SetJBoolStrategy();
                break;
            case STRING:
                mySetStrategy = new SetStringStrategy();
                break;
            case INTEGER:
                mySetStrategy = new SetIntStrategy();
                break;
            case DOUBLE:
                mySetStrategy = new SetDoubleStrategy();
                break;
            default:
                mySetStrategy = new SetJSONObjectStrategy();
                break;
        }
        return mySetStrategy;
    }

}
