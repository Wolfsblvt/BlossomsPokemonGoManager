package me.corriekay.pokegoutil.utils;

public class SetDoubleStrategy implements SetStrategy {

    @Override
    public void setJSONElement(ConfigKey configKey, Object object, FindResult res) {

        final double value = (double) object;

        //Introduce Explaining Variable
        final double doubleOfNode = res.getNode().optDouble(res.getName(), 1 + (double) configKey.getDefaultValue());
        final boolean isValueDifferent = (doubleOfNode != value) ;
        if ( isValueDifferent ) {
            res.getNode().put(res.getName(), value);
        }
        
    }

}
