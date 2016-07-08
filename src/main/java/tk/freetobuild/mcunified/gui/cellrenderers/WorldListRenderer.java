package tk.freetobuild.mcunified.gui.cellrenderers;

import tk.freetobuild.mcunified.WorldInstance;

import javax.swing.*;
import java.awt.*;

/**
 * Created by liz on 7/7/16.
 */
public class WorldListRenderer implements ListCellRenderer{

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        WorldInstance world = (WorldInstance)value;
        JLabel result = new JLabel(world.getName(),world.getIcon(),SwingConstants.LEFT);
        result.setOpaque(isSelected);
        if(isSelected)
            result.setBackground(UIManager.getColor("ComboBox.selectionBackground"));
        return result;
    }
}
