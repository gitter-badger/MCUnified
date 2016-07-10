package tk.freetobuild.mcunified.gui.components;

import java.util.Vector;

/**
 * Created by liz on 7/10/16.
 */
public class NamedVector extends Vector {
    String name;

    public NamedVector(String name) {
        this.name = name;
    }

    public NamedVector(String name, Object elements[]) {
        this.name = name;
        for (int i = 0, n = elements.length; i < n; i++) {
            add(elements[i]);
        }
    }

    public String toString() {
        return name;
    }
}
