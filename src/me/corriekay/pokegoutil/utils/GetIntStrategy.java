package me.corriekay.pokegoutil.utils;

public class GetIntStrategy implements GetStrategy {

    @Override
    public Object getJSONElement(final FindResult res) {
        return res.getNode().getInt(res.getName());
    }

}
