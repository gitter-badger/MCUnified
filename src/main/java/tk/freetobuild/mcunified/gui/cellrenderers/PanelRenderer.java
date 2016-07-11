package tk.freetobuild.mcunified.gui.cellrenderers;

import javax.swing.*;
import java.awt.*;

/**
 * Created by liz on 6/30/16.
 */
class PanelRenderer implements ListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        return (JPanel)value;
    }
}
