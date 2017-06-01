package me.corriekay.pokegoutil.utils;

public class GetJSONObjectStrategy implements GetStrategy {

    @Override
    public Object getJSONElement(final FindResult res) {
        return res.getNode().getJSONObject(res.getName());
    }

}
