package tk.freetobuild.mcunified.gui.cellrenderers;

import javax.swing.*;
import java.awt.*;

/**
 * Created by liz on 6/30/16.
 */
public class LabelListRenderer implements ListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel c = (JLabel)value;
        c.setOpaque(false);
        if(isSelected) {
            c.setOpaque(true);
            c.setBackground(UIManager.getColor("ComboBox.selectionBackground"));
        }
        return c;
    }
}
