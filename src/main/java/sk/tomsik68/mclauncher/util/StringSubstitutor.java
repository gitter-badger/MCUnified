package sk.tomsik68.mclauncher.util;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Replaces certain parts of string
 *
 */
public final class StringSubstitutor {
    private final String template;
    private final HashMap<String, String> variables = new HashMap<>();

    /**
     * Creates a StringSubstitutor that replaces variable in a way like you specify in template.
     */
    public StringSubstitutor() {
        template = "${%s}";
    }

    public String substitute(String s) {
        for (Entry<String, String> variable : variables.entrySet()) {
            s = s.replace(variable.getKey(), variable.getValue());
        }
        return s;
    }

    public void setVariable(String key, String val) {
        variables.put(String.format(template, key), val);
    }
}
