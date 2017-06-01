package me.corriekay.pokegoutil.utils;

public class SetIntStrategy implements SetStrategy{

    @Override
    public void setJSONElement(ConfigKey configKey, Object object, FindResult res) {
        final int value = (int) object;
        // Set if value is different or if default value should be added
        
        //Introduce Explaining Variable
        final int intOfNode = res.getNode().optInt(res.getName(), 1 + (int) configKey.getDefaultValue());
        final boolean isValueDifferent = (intOfNode != value) ;
        if ( isValueDifferent ) {
            res.getNode().put(res.getName(), value);
        }
    }
        
}
