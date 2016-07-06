package tk.freetobuild.mcunified.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import tk.freetobuild.mcunified.Utils;
import tk.freetobuild.mcunified.Main;
import tk.freetobuild.mcunified.UnifiedMCInstance;
import tk.freetobuild.mcunified.curse.ForgeMod;
import tk.freetobuild.mcunified.gui.cellrenderers.AccountCellRenderer;
import tk.freetobuild.mcunified.gui.cellrenderers.LabelListRenderer;
import tk.freetobuild.mcunified.gui.components.ServerInfoPanel;
import tk.freetobuild.mcunified.gui.dialogs.*;
import tk.freetobuild.mcunified.gui.workers.ForgeInstallerWorker;
import tk.freetobuild.mcunified.gui.workers.ProgressMonitorListener;
import tk.freetobuild.mcunified.gui.workers.ProgressMonitorWorker;
import net.minecraftforge.installer.ForgeArtifact;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import sk.tomsik68.mclauncher.api.login.IProfile;
import sk.tomsik68.mclauncher.api.servers.ServerInfo;
import sk.tomsik68.mclauncher.impl.common.Platform;
import sk.tomsik68.mclauncher.impl.common.mc.VanillaServerStorage;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDProfileIO;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDServiceAuthenticationException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by liz on 6/22/16.
 */
public class GuiMain {
    private JPanel panelMain;
    private JComboBox comboBox1;
    private JButton launchButton;
    public JList instanceList;
    public JLabel newLabel;
    public JLabel importLabel;
    public JLabel removeLabel;
    public JLabel exportLabel;
    public JTabbedPane tabbedPane1;
    public JLabel aboutLabel;
    public JLabel instanceIcon;
    public JLabel instanceNameLabel;
    public JLabel instanceVersionLabel;
    public JScrollPane instanceServerListContainer;
    public JPanel instanceServerList;
    public JTabbedPane tabbedPane2;
    public JList jarModList;
    public JButton removeJarModButton;
    public JButton addJarModButton;
    public JButton disableJarModButton;
    public JList loaderModList;
    public JButton removeLoaderModButton;
    public JButton disableLoaderModButton;
    public JButton addLoaderModButton;
    public JButton installForgeButton;
    public JButton installButton;
    public JPanel jarModInstallerPanel;
    public JProgressBar statusProgresBar;
    public JLabel statusLabel;
    public JLabel accountsLabel;
    MainFrame parent;

    public GuiMain(MainFrame parent) {
        super();
        this.parent = parent;
        $$$setupUI$$$();
        Main.logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record != null && statusLabel != null && record.getLevel() != null && record.getMessage() != null)
                    statusLabel.setText(String.format("[%s] %s", record.getLevel().getName(), record.getMessage()));
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });
        Main.logger.info("Logger initialized");
        //region instanceServerListContainer
        instanceServerListContainer.setBorder(BorderFactory.createTitledBorder(new JButton().getBorder(), "Servers"));
        //endregion instanceServerListContainer
        //region tabbedPane1
        tabbedPane1.setVisible(false);
        try {
            instanceIcon.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/instance.png")).getScaledInstance(128, 128, Image.SCALE_SMOOTH)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //endregion tabbedPane1
        //region comboBox1
        loadProfiles();
        try {
            comboBox1.setSelectedIndex(Math.max((int) Main.config.get("lastUser"), 0));
        } catch (IllegalArgumentException ignored) {
            comboBox1.setSelectedIndex(0);
        }
        comboBox1.setRenderer(new AccountCellRenderer());
        comboBox1.addItemListener(itemEvent -> {
            Main.config.put("lastUser", comboBox1.getSelectedIndex());
            if (itemEvent.getItem() instanceof IProfile && !instanceList.isSelectionEmpty()) {
                launchButton.setEnabled(true);
            } else {
                launchButton.setEnabled(false);
            }
        });
        //endregion
        //region instanceList
        instanceList.setBorder(BorderFactory.createTitledBorder(new JButton().getBorder(), "Instances"));
        instanceList.setModel(new DefaultListModel());
        addInstances();
        instanceList.addListSelectionListener(e -> {
            if (!instanceList.isSelectionEmpty()) {
                populateInstancePanel((UnifiedMCInstance) instanceList.getSelectedValue());
                tabbedPane1.setVisible(true);
            } else {
                tabbedPane1.setVisible(false);
            }
            if (comboBox1.getSelectedItem() instanceof IProfile && !instanceList.isSelectionEmpty()) {
                launchButton.setEnabled(true);
            } else {
                launchButton.setEnabled(false);
            }
        });
        instanceList.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {
            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_DELETE) {
                    if (!instanceList.isSelectionEmpty()) {
                        UnifiedMCInstance mcinstance = (UnifiedMCInstance) instanceList.getSelectedValue();
                        Utils.recursiveDelete(mcinstance.getLocation());
                        ((DefaultListModel) instanceList.getModel()).removeElement(mcinstance);
                    }
                } else if (keyEvent.getKeyCode() == KeyEvent.VK_INSERT) {
                    new DialogNewInstance(GuiMain.this).setVisible(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
            }
        });
        //endregion instanceList
        //region launchButton
        launchButton.addActionListener(e -> {
            try {
                UnifiedMCInstance instance = (UnifiedMCInstance) instanceList.getSelectedValue();
                instance.launch(((IProfile) comboBox1.getSelectedItem()).getName());
            } catch (YDServiceAuthenticationException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        //endregion launchButton
        //region newInstance
        setupLabelButton(newLabel, "/images/toolbar/plus.png", () -> new DialogNewInstance(GuiMain.this).setVisible(true));
        //endregion newInstance
        //region import
        setupLabelButton(importLabel, "/images/toolbar/import.png", () -> {
        });
        //endregion newInstance
        //region export
        setupLabelButton(exportLabel, "/images/toolbar/export.png", () -> {
        });
        //endregion export
        //region removeLabel
        setupLabelButton(removeLabel, "/images/toolbar/remove.png", () -> {
            if (!instanceList.isSelectionEmpty()) {
                UnifiedMCInstance mcinstance = (UnifiedMCInstance) instanceList.getSelectedValue();
                Utils.recursiveDelete(mcinstance.getLocation());
                ((DefaultListModel) instanceList.getModel()).removeElement(mcinstance);
            }
        });
        //endregion removeLabel
        //region accounts
        setupLabelButton(accountsLabel, "/images/toolbar/accounts.png", () -> {
            new DialogAccountManager().setVisible(true);
            comboBox1.removeAllItems();
            comboBox1.addItem("Select an account");
            loadProfiles();
        });
        //endregion accounts
        //region aboutLabel
        setupLabelButton(aboutLabel, "/images/toolbar/about.png", () -> {
            new AboutDialog().setVisible(true);
        });
        //endregion aboutLabel
        //region installForge
        installForgeButton.addActionListener(e -> {
            if (!instanceList.isSelectionEmpty()) {
                UnifiedMCInstance instance = (UnifiedMCInstance) instanceList.getSelectedValue();
                DialogInstallForge dif;
                (dif = new DialogInstallForge(instance)).setVisible(true);
                ForgeArtifact artifact = dif.getArtifact();
                if (artifact != null) {
                    ForgeInstallerWorker worker = new ForgeInstallerWorker(artifact, patch -> {
                        try {
                            instance.addPatch(patch);
                            JLabel patchLabel = new JLabel(patch.get("friendlyName").toString());
                            patchLabel.setEnabled(false);
                            ((DefaultListModel) jarModList.getModel()).add(0, patchLabel);
                            installForgeButton.setEnabled(false);
                        } catch (IOException ignored) {
                        }
                    });
                    worker.addPropertyChangeListener(new ProgressMonitorListener(Main.logger, statusProgresBar));
                    worker.execute();
                }

            }
        });
        //endregion
        //region removeJarMod
        removeJarModButton.addActionListener(e -> {
            if (!jarModList.isSelectionEmpty()) {
                String name = ((JLabel) jarModList.getSelectedValue()).getText();
                UnifiedMCInstance instance = ((UnifiedMCInstance) instanceList.getSelectedValue());
                File f;
                if (name.endsWith("(disabled)"))
                    f = new File(instance.getLocation(), "jarMods" + File.separator + name.substring(0, name.lastIndexOf(" (disabled)")) + ".disabled");
                else
                    f = new File(instance.getLocation(), "jarMods" + File.separator + name);
                if (f.exists() && f.delete() || instance.removePatch(name)) {
                    ((DefaultListModel) jarModList.getModel()).remove(jarModList.getSelectedIndex());
                    instance.getPatches().forEach(s -> {
                        if (s.startsWith("Forge-"))
                            installForgeButton.setEnabled(true);
                    });
                }
            }
        });
        //endregion removeJarMod
        //region disableJarMod
        disableJarModButton.addActionListener(e -> {
            if (!jarModList.isSelectionEmpty()) {
                UnifiedMCInstance instance = (UnifiedMCInstance) instanceList.getSelectedValue();
                JLabel label = (JLabel) jarModList.getSelectedValue();
                if (label.getText().endsWith(" (disabled)")) {
                    String name = label.getText().substring(0, label.getText().lastIndexOf(" (disabled)"));
                    Main.logger.info("Enabling " + name);
                    File in = new File(instance.getLocation(), "jarMods" + File.separator + name + ".disabled");
                    File out = new File(instance.getLocation(), "jarMods" + File.separator + name);
                    in.renameTo(out);
                    label.setText(name);
                    Main.logger.info("Enabled " + name);
                    jarModList.repaint();
                    disableJarModButton.setText("Disable");
                } else {
                    Main.logger.info("Disabling " + label.getText());
                    File in = new File(instance.getLocation(), "jarMods" + File.separator + label.getText());
                    File out = new File(in.getPath() + ".disabled");
                    in.renameTo(out);
                    Main.logger.info("Disabled " + label.getText());
                    label.setText(label.getText() + " (disabled)");
                    jarModList.repaint();
                    disableJarModButton.setText("Enable");
                }
            }
        });
        //endregion
        //region addJarMod
        addJarModButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Add a Jar Mod");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Jar File", "jar"));
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (fileChooser.showDialog(parent, "Add Mod") == JFileChooser.APPROVE_OPTION) {
                File in = fileChooser.getSelectedFile();
                File out = new File(((UnifiedMCInstance) instanceList.getSelectedValue()).getLocation(), "jarMods" + File.separator + in.getName());
                if (!out.getParentFile().exists())
                    out.getParentFile().mkdirs();
                Main.logger.info("Copying " + in.getName());
                ProgressMonitorWorker worker = new ProgressMonitorWorker(mon -> {
                    mon.setMax((int) (in.length()));
                    byte[] buf = new byte[4096];
                    try {
                        InputStream is = new FileInputStream(in);
                        OutputStream os = new FileOutputStream(out);
                        while (true) {
                            int r = is.read(buf);
                            if (r == -1)
                                break;
                            os.write(buf, 0, r);
                            mon.incrementProgress(r);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }, () -> {
                    Main.logger.info(out.getName() + " copied!");
                    ((DefaultListModel) jarModList.getModel()).addElement(new JLabel(out.getName()));
                    statusProgresBar.setValue(0);
                });
                worker.addPropertyChangeListener(new ProgressMonitorListener(Main.logger, statusProgresBar));
                worker.execute();
            }
        });
        //endregion addJarMod
        //region jarModList
        jarModList.setCellRenderer(new LabelListRenderer());
        jarModList.addListSelectionListener(e -> {
            removeJarModButton.setEnabled(!jarModList.isSelectionEmpty());
            disableJarModButton.setEnabled(!jarModList.isSelectionEmpty() && ((JLabel) jarModList.getSelectedValue()).isEnabled());
            if (!jarModList.isSelectionEmpty()) {
                if (((JLabel) jarModList.getSelectedValue()).getText().endsWith(" (disabled)"))
                    disableJarModButton.setText("Enable");
                else
                    disableJarModButton.setText("Disable");
            }
        });
        //endregion jarModList
        //region addLoaderMod
        addLoaderModButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Add a Mod");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Mod File", "jar", "zip", "liteloder"));
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (fileChooser.showDialog(parent, "Add Mod") == JFileChooser.APPROVE_OPTION) {
                File in = fileChooser.getSelectedFile();
                File out = new File(((UnifiedMCInstance) instanceList.getSelectedValue()).getLocation(), "mods" + File.separator + in.getName());
                if (!out.getParentFile().exists())
                    out.getParentFile().mkdirs();
                Main.logger.info("Copying " + in.getName());
                ProgressMonitorWorker worker = new ProgressMonitorWorker(mon -> {
                    mon.setMax((int) (in.length()));
                    byte[] buf = new byte[4096];
                    try {
                        InputStream is = new FileInputStream(in);
                        OutputStream os = new FileOutputStream(out);
                        while (true) {
                            int r = is.read(buf);
                            if (r == -1)
                                break;
                            os.write(buf, 0, r);
                            mon.incrementProgress(r);
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }, () -> {
                    Main.logger.info(out.getName() + " copied!");
                    ((DefaultListModel) loaderModList.getModel()).addElement(new ForgeMod(out));
                    statusProgresBar.setValue(0);
                });
                worker.addPropertyChangeListener(new ProgressMonitorListener(Main.logger, statusProgresBar));
                worker.execute();
            }
        });
        //endregion addLoaderMod
        //region removeLoaderMod
        removeLoaderModButton.addActionListener(e -> {
            if (!loaderModList.isSelectionEmpty()) {
                ForgeMod mod = (ForgeMod) loaderModList.getSelectedValue();
                UnifiedMCInstance instance = ((UnifiedMCInstance) instanceList.getSelectedValue());
                File f = mod.getFile();
                f.delete();
                ((DefaultListModel) loaderModList.getModel()).removeElementAt(loaderModList.getSelectedIndex());
            }
        });
        //endregion removeLoaderMod
        //region disableLoaderMod
        disableLoaderModButton.addActionListener(e -> {
            ForgeMod mod = (ForgeMod) loaderModList.getSelectedValue();
            if (mod.isEnabled()) {
                mod.disable();
                disableLoaderModButton.setText("Enable");
            } else {
                mod.enable();
                disableLoaderModButton.setText("Disable");
            }
            loaderModList.repaint();
        });
        //endregion disableLoaderMod
        //region loaderModList
        loaderModList.addListSelectionListener(e -> {
            if (!loaderModList.isSelectionEmpty()) {
                removeLoaderModButton.setEnabled(true);
                disableLoaderModButton.setEnabled(true);
                if (loaderModList.getSelectedValue().toString().endsWith(" (disabled)"))
                    disableLoaderModButton.setText("Enable");
                else
                    disableLoaderModButton.setText("Disable");
            } else {
                removeLoaderModButton.setEnabled(false);
                disableLoaderModButton.setEnabled(false);
            }
        });
        //endregion loaderModList
        //region instalLoaderMod
        installButton.addActionListener(e -> {
            new DialogInstallMod((UnifiedMCInstance) instanceList.getSelectedValue()).setVisible(true);
        });
        //endregion installLoaderMod
    }

    private void loadProfiles() {
        try {
            if (new File(Platform.getCurrentPlatform().getWorkingDirectory(), "launcher_profiles.json").exists()) {
                IProfile[] profiles = new YDProfileIO(Platform.getCurrentPlatform().getWorkingDirectory()).read();
                for (IProfile profile : profiles) {
                    comboBox1.addItem(profile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupLabelButton(JLabel label, String icon, Runnable onclick) {

        Border raisedBorder = new JButton().getBorder();
        EmptyBorder emptyBorder = new EmptyBorder(raisedBorder.getBorderInsets(label));
        try {
            label.setIcon(new ImageIcon(ImageIO.read(getClass().getResourceAsStream(icon))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        label.setBorder(emptyBorder);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                label.setBorder(raisedBorder);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                label.setBorder(emptyBorder);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                onclick.run();
            }
        });

    }

    private void populateInstancePanel(UnifiedMCInstance instance) {
        instanceNameLabel.setText(instance.getName());
        instanceVersionLabel.setText(instance.version);
        VanillaServerStorage storage = new VanillaServerStorage(instance);
        instanceServerList.removeAll();
        DefaultListModel model = new DefaultListModel();
        if (new File(instance.getLocation(), "servers.dat").exists()) {
            try {
                for (ServerInfo server : storage.loadServers()) {
                    instanceServerList.add(new ServerInfoPanel(server).panel1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        installForgeButton.setEnabled(true);
        java.util.List<String> patches = instance.getPatches();
        patches.forEach(e -> {
            if (e.startsWith("Forge-"))
                installForgeButton.setEnabled(false);
            JLabel label = new JLabel(e);
            label.setEnabled(false);
            model.addElement(label);
        });
        loadJarMods(model, instance);
        loadLoaderMods(new DefaultListModel(), instance);
    }

    public void loadJarMods(DefaultListModel model, UnifiedMCInstance instance) {
        File jarMods = new File(instance.getLocation(), "jarMods");
        if (!jarMods.exists())
            jarMods.mkdirs();
        for (File file : jarMods.listFiles()) {
            if (file.getName().endsWith(".disabled"))
                model.addElement(new JLabel(file.getName().replaceFirst("[.][^.]+$", "") + " (disabled)"));
            else
                model.addElement(new JLabel(file.getName()));
        }
        jarModList.setModel(model);
    }

    public void loadLoaderMods(DefaultListModel model, UnifiedMCInstance instance) {
        File jarMods = new File(instance.getLocation(), "mods");
        if (!jarMods.exists())
            jarMods.mkdirs();
        for (File file : jarMods.listFiles()) {
            model.addElement(new ForgeMod(file));
        }
        loaderModList.setModel(model);
    }

    public JPanel getPanelMain() {
        return panelMain;
    }

    private void addInstances() {
        File instances = new File(Main.baseDir, "instances");
        if (!instances.exists())
            instances.mkdirs();
        for (File f : instances.listFiles()) {
            if (f.isDirectory()) {
                File config = new File(f, "instance.json");
                if (config.exists()) {
                    try {
                        UnifiedMCInstance instance = new UnifiedMCInstance(f, (JSONObject) JSONValue.parse(new FileInputStream(config)));
                        ((DefaultListModel) instanceList.getModel()).addElement(instance);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void createUIComponents() {
        instanceServerList = new JPanel(new GridLayout(-1, 1));
        //instanceServerList.setCellRenderer(new ServerCellRender());
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
        panelMain = new JPanel();
        panelMain.setLayout(new BorderLayout(0, 0));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setBackground(new Color(-7366757));
        panelMain.add(panel1, BorderLayout.CENTER);
        instanceList = new JList();
        instanceList.setSelectionMode(0);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 50;
        panel1.add(instanceList, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(panel2, gbc);
        tabbedPane1 = new JTabbedPane();
        tabbedPane1.setEnabled(true);
        tabbedPane1.setVisible(true);
        panel2.add(tabbedPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("About", panel3);
        instanceIcon = new JLabel();
        instanceIcon.setText("");
        panel3.add(instanceIcon, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        instanceNameLabel = new JLabel();
        instanceNameLabel.setText("Instance Name");
        panel3.add(instanceNameLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        instanceVersionLabel = new JLabel();
        instanceVersionLabel.setEnabled(false);
        instanceVersionLabel.setHorizontalAlignment(10);
        instanceVersionLabel.setHorizontalTextPosition(11);
        instanceVersionLabel.setText("Version");
        panel3.add(instanceVersionLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        instanceServerListContainer = new JScrollPane();
        instanceServerListContainer.setVerticalScrollBarPolicy(20);
        panel3.add(instanceServerListContainer, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        instanceServerListContainer.setViewportView(instanceServerList);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Mods", panel4);
        tabbedPane2 = new JTabbedPane();
        panel4.add(tabbedPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridBagLayout());
        tabbedPane2.addTab("Jar Mods", panel5);
        jarModList = new JList();
        jarModList.setBackground(new Color(-12302519));
        jarModList.setSelectionMode(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(jarModList, gbc);
        jarModInstallerPanel = new JPanel();
        jarModInstallerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel5.add(jarModInstallerPanel, gbc);
        installForgeButton = new JButton();
        installForgeButton.setHorizontalAlignment(2);
        installForgeButton.setText("Install Forge");
        jarModInstallerPanel.add(installForgeButton);
        addJarModButton = new JButton();
        addJarModButton.setHorizontalAlignment(4);
        addJarModButton.setHorizontalTextPosition(11);
        addJarModButton.setText("Add");
        jarModInstallerPanel.add(addJarModButton);
        disableJarModButton = new JButton();
        disableJarModButton.setEnabled(false);
        disableJarModButton.setText("Disable");
        jarModInstallerPanel.add(disableJarModButton);
        removeJarModButton = new JButton();
        removeJarModButton.setEnabled(false);
        removeJarModButton.setHorizontalAlignment(4);
        removeJarModButton.setText("Remove");
        jarModInstallerPanel.add(removeJarModButton);
        final JSeparator separator1 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel5.add(separator1, gbc);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridBagLayout());
        tabbedPane2.addTab("Loader Mods", panel6);
        loaderModList = new JList();
        loaderModList.setBackground(new Color(-12302519));
        loaderModList.setSelectionMode(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(loaderModList, gbc);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel6.add(panel7, gbc);
        installButton = new JButton();
        installButton.setText("Install");
        panel7.add(installButton);
        addLoaderModButton = new JButton();
        addLoaderModButton.setText("Add");
        panel7.add(addLoaderModButton);
        disableLoaderModButton = new JButton();
        disableLoaderModButton.setText("Disable");
        panel7.add(disableLoaderModButton);
        removeLoaderModButton = new JButton();
        removeLoaderModButton.setText("Remove");
        panel7.add(removeLoaderModButton);
        final JSeparator separator2 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel6.add(separator2, gbc);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Worlds", panel8);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Options", panel9);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(panel10, BorderLayout.SOUTH);
        comboBox1 = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Select an Account");
        comboBox1.setModel(defaultComboBoxModel1);
        panel10.add(comboBox1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        launchButton = new JButton();
        launchButton.setEnabled(false);
        launchButton.setHorizontalAlignment(0);
        launchButton.setHorizontalTextPosition(11);
        launchButton.setText("Launch");
        panel10.add(launchButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridBagLayout());
        panel10.add(panel11, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JSeparator separator3 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel11.add(separator3, gbc);
        statusProgresBar = new JProgressBar();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel11.add(statusProgresBar, gbc);
        statusLabel = new JLabel();
        statusLabel.setText("Status");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel11.add(statusLabel, gbc);
        final JToolBar toolBar1 = new JToolBar();
        panelMain.add(toolBar1, BorderLayout.NORTH);
        newLabel = new JLabel();
        newLabel.setText("");
        newLabel.setToolTipText("New Instance");
        toolBar1.add(newLabel);
        importLabel = new JLabel();
        importLabel.setText("");
        importLabel.setToolTipText("Import");
        toolBar1.add(importLabel);
        exportLabel = new JLabel();
        exportLabel.setText("");
        exportLabel.setToolTipText("Export Instance");
        toolBar1.add(exportLabel);
        removeLabel = new JLabel();
        removeLabel.setText("");
        removeLabel.setToolTipText("Remove Instance");
        toolBar1.add(removeLabel);
        aboutLabel = new JLabel();
        aboutLabel.setHorizontalAlignment(4);
        aboutLabel.setText("");
        aboutLabel.setToolTipText("About");
        toolBar1.add(aboutLabel);
        accountsLabel = new JLabel();
        accountsLabel.setText("");
        accountsLabel.setToolTipText("Accounts");
        toolBar1.add(accountsLabel);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }
}
