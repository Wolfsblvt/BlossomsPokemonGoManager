package me.corriekay.pokegoutil.utils;

public class GetBoolStrategy implements GetStrategy{

    @Override
    public Object getJSONElement(FindResult res) {
        return res.getNode().getBoolean(res.getName());
    }

}
