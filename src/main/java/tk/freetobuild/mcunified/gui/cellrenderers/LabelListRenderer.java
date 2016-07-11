package tk.freetobuild.mcunified.gui.cellrenderers;

import javax.swing.*;
import java.awt.*;

/**
 * Created by liz on 6/30/16.
 */
public class LabelListRenderer implements ListCellRenderer<JLabel> {
    @Override
    public Component getListCellRendererComponent(JList list, JLabel value, int index, boolean isSelected, boolean cellHasFocus) {
        value.setOpaque(false);
        if(isSelected) {
            value.setOpaque(true);
            value.setBackground(UIManager.getColor("ComboBox.selectionBackground"));
        }
        return value;
    }
}
