package me.corriekay.pokegoutil.utils;

import org.apache.commons.lang3.StringEscapeUtils;

public class GetStringStrategy implements GetStrategy {

    @Override
    public Object getJSONElement(final FindResult res) {
        final String value = res.getNode().getString(res.getName());
        return StringEscapeUtils.unescapeJson(value);
    }

}
