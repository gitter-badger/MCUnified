package tk.freetobuild.mcunified.gui.cellrenderers;

import tk.freetobuild.mcunified.gui.components.CheckBoxNode;
import tk.freetobuild.mcunified.gui.components.ConfigCheckBoxNode;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.io.File;

/**
 * Created by liz on 7/10/16.
 */
public class CheckBoxNodeRenderer implements TreeCellRenderer {
    private JCheckBox leafRenderer = new JCheckBox();
    private ConfigCheckBoxNode config = null;
    private DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();

    Color selectionBorderColor, selectionForeground, selectionBackground,
            textForeground, textBackground;

    public JCheckBox getLeafRenderer() {
        return leafRenderer;
    }

    public ConfigCheckBoxNode getConfig() {
        return config;
    }

    public boolean isConfigNode() {
        return config!=null;
    }

    public CheckBoxNodeRenderer() {
        Font fontValue;
        fontValue = UIManager.getFont("Tree.font");
        if (fontValue != null) {
            leafRenderer.setFont(fontValue);
        }
        Boolean booleanValue = (Boolean) UIManager
                .get("Tree.drawsFocusBorderAroundIcon");
        leafRenderer.setFocusPainted((booleanValue != null)
                && (booleanValue.booleanValue()));

        selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
        selectionForeground = UIManager.getColor("Tree.selectionForeground");
        selectionBackground = UIManager.getColor("Tree.selectionBackground");
        textForeground = UIManager.getColor("Tree.textForeground");
        textBackground = UIManager.getColor("Tree.textBackground");
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {

        Component returnValue;
        if (leaf) {

            String stringValue = tree.convertValueToText(value, selected,
                    expanded, leaf, row, false);
            leafRenderer.setText(stringValue);
            leafRenderer.setSelected(false);

            leafRenderer.setEnabled(tree.isEnabled());

            if (selected) {
                leafRenderer.setForeground(selectionForeground);
                leafRenderer.setBackground(selectionBackground);
            } else {
                leafRenderer.setForeground(textForeground);
                leafRenderer.setBackground(textBackground);
            }

            if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
                Object userObject = ((DefaultMutableTreeNode) value)
                        .getUserObject();
                if (userObject instanceof CheckBoxNode) {
                    CheckBoxNode node = (CheckBoxNode) userObject;
                    leafRenderer.setText(node.getText());
                    leafRenderer.setSelected(node.isSelected());
                    if(userObject instanceof ConfigCheckBoxNode) {
                        config = (ConfigCheckBoxNode)userObject;
                    }
                }
            }
            returnValue = leafRenderer;
        } else {
            returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree,
                    value, selected, expanded, leaf, row, hasFocus);
        }
        return returnValue;
    }
}
