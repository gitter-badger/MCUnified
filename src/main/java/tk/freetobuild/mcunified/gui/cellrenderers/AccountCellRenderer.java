package tk.freetobuild.mcunified.gui.cellrenderers;

import tk.freetobuild.mcunified.gui.HeadResolver;
import sk.tomsik68.mclauncher.api.login.IProfile;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDAuthProfile;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Created by liz on 6/22/16.
 */
public class AccountCellRenderer implements ListCellRenderer<Object> {
    @Override
    public Component getListCellRendererComponent(JList jList, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = new JLabel();
        label.setOpaque(isSelected);
        if(value instanceof YDAuthProfile) {
            try {
                label.setIcon(new ImageIcon(HeadResolver.resolve((IProfile)value)));
                label.setText(((YDAuthProfile) value).getDisplayName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if(value!=null) {
                label.setIcon(new ImageIcon(HeadResolver.def));
                label.setText(value.toString());
            }
        }
        if(isSelected)
            label.setBackground(UIManager.getColor("ComboBox.selectionBackground"));
        return label;
    }
}
