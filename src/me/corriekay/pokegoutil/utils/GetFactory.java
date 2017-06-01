package me.corriekay.pokegoutil.utils;

public class GetFactory {
    public static GetStrategy createGetStrategy(ConfigKey configKey){
        GetStrategy myGetStrategy;
        switch (configKey.type) {
            case BOOLEAN:
                myGetStrategy = new GetBoolStrategy();
                break;
            case STRING:
                myGetStrategy = new GetStringStrategy();
                break;
            case INTEGER:
                myGetStrategy = new GetIntStrategy();
                break;
            case DOUBLE:
                myGetStrategy = new GetDoubleStrategy();
                break;
            default:
                myGetStrategy = new GetJSONObjectStrategy();
                break;
        }
        return myGetStrategy;
    }
}
