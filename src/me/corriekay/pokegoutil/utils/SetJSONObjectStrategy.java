package me.corriekay.pokegoutil.utils;

import org.json.JSONObject;

public class SetJSONObjectStrategy implements SetStrategy{

    @Override
    public void setJSONElement(ConfigKey configKey, Object object, FindResult res) {

        final JSONObject value = (JSONObject) object;
        
        //Introduce Explaining Variable
        final boolean isValueDifferent = res.getNode().optJSONObject(res.getName()) != value ;
        final boolean isValueEqualToDefalutValue = value.equals(configKey.getDefaultValue());
        if ( isValueDifferent || isValueEqualToDefalutValue ) {
            res.getNode().put(res.getName(), value);
        }
        
    }

}
