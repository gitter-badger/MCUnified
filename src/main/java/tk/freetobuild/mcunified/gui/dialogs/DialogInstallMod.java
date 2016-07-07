package tk.freetobuild.mcunified.gui.dialogs;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import tk.freetobuild.mcunified.UnifiedMCInstance;
import tk.freetobuild.mcunified.curse.CurseModInfo;
import tk.freetobuild.mcunified.curse.CurseModList;
import tk.freetobuild.mcunified.gui.components.ModPanel;
import tk.freetobuild.mcunified.gui.components.PlaceholderTextField;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DialogInstallMod extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    public JTextField searchField;
    public JButton buttonSearch;
    public JPanel modPanel;
    private JScrollPane scrollPane;

    public DialogInstallMod(UnifiedMCInstance instance) {
        $$$setupUI$$$();
        try {
            setTitle("Install Mod");
            setIconImage(ImageIO.read(getClass().getResourceAsStream("/images/head.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonSearch);

        buttonOK.addActionListener(e ->
                onOK());

        buttonCancel.addActionListener(e ->
                onCancel());

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        setPreferredSize(new Dimension(640, 480));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        pack();
        SwingWorker<List<CurseModInfo>, Void> worker = new SwingWorker<List<CurseModInfo>, Void>() {
            @Override
            protected List<CurseModInfo> doInBackground() throws Exception {
                try {
                    return CurseModList.loadTopMods().next();
                } catch (IOException e) {
                    return new ArrayList<>();
                }
            }

            @Override
            protected void done() {
                try {
                    get().forEach(e -> modPanel.add(new ModPanel(e, instance).panel1));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        new DialogLoading("Getting mods...", worker).setVisible(true);
        buttonSearch.addActionListener(e -> onSearch(instance));
    }

    private void onSearch(UnifiedMCInstance instance) {
        modPanel.removeAll();
        modPanel.updateUI();
        SwingWorker<List<CurseModInfo>, Void> worker = new SwingWorker<List<CurseModInfo>, Void>() {
            @Override
            protected List<CurseModInfo> doInBackground() throws Exception {
                List<CurseModInfo> mods = new ArrayList<>();
                CurseModList list = CurseModList.search(searchField.getText());
                if (list != null)
                    while (list.hasNext())
                        mods.addAll(list.next());
                return mods;
            }

            @Override
            protected void done() {
                try {
                    get().forEach(e -> modPanel.add(new ModPanel(e, instance).panel1));
                    modPanel.updateUI();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        new DialogLoading("Getting mods...", worker).setVisible(true);
    }

    private void onOK() {
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void createUIComponents() {
        modPanel = new JPanel(new GridLayout(-1, 1));
        searchField = new PlaceholderTextField();
        ((PlaceholderTextField) searchField).setPlaceholder("Search for a mod...");
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonOK = new JButton();
        buttonOK.setText("Install");
        panel2.add(buttonOK, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Close");
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel4, gbc);
        scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(30);
        panel4.add(scrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane.setViewportView(modPanel);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel3.add(panel5, gbc);
        panel5.add(searchField, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        buttonSearch = new JButton();
        buttonSearch.setText("Search");
        panel5.add(buttonSearch, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
