package tk.freetobuild.mcunified.gui.components;

/**
 * Created by liz on 7/10/16.
 */
public class CheckBoxNode {
    private String text;

    private boolean selected;

    public CheckBoxNode(String text, boolean selected) {
        this.text = text;
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getText() {
        return text;
    }

    public String toString() {
        return getClass().getName() + "[" + text + "/" + selected + "]";
    }
}
