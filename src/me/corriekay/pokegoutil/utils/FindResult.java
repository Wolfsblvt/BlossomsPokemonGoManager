package me.corriekay.pokegoutil.utils;

import org.json.JSONObject;

public class FindResult {
    private final JSONObject node;
    private final String name;

    /**
     * Creates a Result object.
     *
     * @param node The node.
     * @param name The value.
     */
    FindResult(final JSONObject node, final String name) {
        this.node = node;
        this.name = name;
    }

    /**
     * Returns the node.
     *
     * @return The node.
     */
    public JSONObject getNode() {
        return this.node;
    }

    /**
     * Returns the name.
     *
     * @return The name.
     */
    public String getName() {
        return this.name;
    }

}
