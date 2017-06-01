package me.corriekay.pokegoutil.utils;

import org.apache.commons.lang3.StringEscapeUtils;

public class SetStringStrategy implements SetStrategy {
    
    @Override
    public void setJSONElement(ConfigKey configKey, Object object, FindResult res) {
        final String value = (String) object;
        
        //Introduce Explaining Variable
        final String stringOfNode = res.getNode().optString(res.getName(), "." + configKey.getDefaultValue());
        final boolean isStringOfNodeEqualToValue = stringOfNode.equals(value) ;
        if (!isStringOfNodeEqualToValue) {
            res.getNode().put(res.getName(), StringEscapeUtils.escapeJson(value));
        }
        
    }

}
