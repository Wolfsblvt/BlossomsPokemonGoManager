package me.corriekay.pokegoutil.utils;

public class SetJBoolStrategy implements SetStrategy{

    @Override
    public void setJSONElement(ConfigKey configKey, Object object, FindResult res) {
        final boolean value = (boolean) object;
        // change boolean to final boolean
        final boolean defaultValue = configKey.getDefaultValue();
        
        //Introduce Explaining Variable
        final boolean isValueDifferent = res.getNode().optBoolean(res.getName(), defaultValue) != value ;
        final boolean isValueEqualToDefalutValue = (value == defaultValue);
        if (isValueDifferent || isValueEqualToDefalutValue) {
            res.getNode().put(res.getName(), value);
        }

    }
}
