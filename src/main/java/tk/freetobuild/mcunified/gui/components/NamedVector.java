package tk.freetobuild.mcunified.gui.components;

import java.util.Vector;

/**
 * Created by liz on 7/10/16.
 */
public class NamedVector extends Vector<Object> {
    private final String name;

    public NamedVector(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
