package tk.freetobuild.mcunified.gui.components;

import java.io.File;

/**
 * Created by liz on 7/10/16.
 */
public class ConfigCheckBoxNode extends CheckBoxNode {
    private final File f;
    private final String path;
    private final String prefix;
    public ConfigCheckBoxNode(File f, String prefix, boolean selected) {
        super(f.getName(), selected);
        this.f = f;
        this.path = prefix+f.getName();
        this.prefix = prefix;
    }

    public File getFile() {
        return f;
    }

    public String getPath() {
        return path;
    }

    public String getPrefix() {
        return prefix;
    }
}
