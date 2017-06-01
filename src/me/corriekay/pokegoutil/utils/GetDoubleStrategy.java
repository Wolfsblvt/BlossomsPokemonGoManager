package me.corriekay.pokegoutil.utils;

public class GetDoubleStrategy implements GetStrategy {

    @Override
    public Object getJSONElement(final FindResult res) {
        return res.getNode().getDouble(res.getName());
    }

}
