package tk.freetobuild.mcunified.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.sun.corba.se.spi.activation.Server;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDAuthProfile;
import tk.freetobuild.mcunified.Utils;
import tk.freetobuild.mcunified.Main;
import tk.freetobuild.mcunified.UnifiedMCInstance;
import tk.freetobuild.mcunified.WorldInstance;
import tk.freetobuild.mcunified.curse.ForgeMod;
import tk.freetobuild.mcunified.gui.cellrenderers.AccountCellRenderer;
import tk.freetobuild.mcunified.gui.cellrenderers.LabelListRenderer;
import tk.freetobuild.mcunified.gui.cellrenderers.WorldListRenderer;
import tk.freetobuild.mcunified.gui.components.JLabelButton;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

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
    private JList worldList;
    private JSpinner memMinSpinner;
    private JSpinner memMaxSpinner;
    private JButton saveButton;
    private JLabel importWorldButton;
    private JLabel exportWorldButton;
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
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        //endregion launchButton
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
        installButton.addActionListener(e -> new DialogInstallMod((UnifiedMCInstance) instanceList.getSelectedValue()).setVisible(true));
        worldList.setCellRenderer(new WorldListRenderer());
        //endregion installLoaderMod

        saveButton.addActionListener(e -> {
            UnifiedMCInstance instance = (UnifiedMCInstance) instanceList.getSelectedValue();
            instance.setMinMemory(MemoryModel.parseToMB(memMinSpinner.getValue().toString()));
            instance.setMaxMemory(MemoryModel.parseToMB(memMaxSpinner.getValue().toString()));
            try {
                instance.save();
                Main.logger.info("Instance saved!");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
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

    private JLabelButton setupLabelButton(String icon, Runnable onclick) {
        try {
            JLabelButton button = new JLabelButton(new ImageIcon(ImageIO.read(getClass().getResourceAsStream(icon + ".png"))), new ImageIcon(ImageIO.read(getClass().getResourceAsStream(icon + ":hover.png"))));
            button.addActionListener(e -> onclick.run());
            return button;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
                    ServerInfoPanel panel = new ServerInfoPanel(server);
                    panel.panel1.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (e.getClickCount() >= 2 && comboBox1.getSelectedIndex() > 0) {
                                try {
                                    instance.launch(((YDAuthProfile) comboBox1.getSelectedItem()).getName(), server.getIP() + ":" + server.getPort());
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    });
                    instanceServerList.add(panel.panel1);
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
        loadWorlds(instance);
        populateOptions(instance);
    }

    public void populateOptions(UnifiedMCInstance instance) {
        memMaxSpinner.setModel(new MemoryModel(instance.getMaxMemory()));
        memMinSpinner.setModel(new MemoryModel(instance.getMinMemory()));
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
        model.clear();
        File jarMods = new File(instance.getLocation(), "mods");
        if (!jarMods.exists())
            jarMods.mkdirs();
        for (File file : jarMods.listFiles()) {
            try {
                model.addElement(new ForgeMod(file));
            } catch(Exception ex) {

            }
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

    public void loadWorlds(UnifiedMCInstance instance) {
        DefaultListModel model = new DefaultListModel();
        File saves = new File(instance.getLocation(), "saves");
        for (File f : saves.listFiles()) {
            model.addElement(new WorldInstance(f));
        }
        worldList.setModel(model);
    }

    private void createUIComponents() {
        instanceServerList = new JPanel(new GridLayout(-1, 1));
        //region newInstance
        newLabel = setupLabelButton("/images/toolbar/new", () -> new DialogNewInstance(GuiMain.this).setVisible(true));
        //endregion newInstance
        //region import
        importLabel = setupLabelButton("/images/toolbar/import", () -> {
        });
        //endregion newInstance
        //region export
        exportLabel = setupLabelButton("/images/toolbar/export", () -> {
        });
        //endregion export
        //region removeLabel
        removeLabel = setupLabelButton("/images/toolbar/remove", () -> {
            if (!instanceList.isSelectionEmpty()) {
                UnifiedMCInstance mcinstance = (UnifiedMCInstance) instanceList.getSelectedValue();
                Utils.recursiveDelete(mcinstance.getLocation());
                ((DefaultListModel) instanceList.getModel()).removeElement(mcinstance);
            }
        });
        //endregion removeLabel
        //region accounts
        accountsLabel = setupLabelButton("/images/toolbar/accounts", () -> {
            new DialogAccountManager().setVisible(true);
            comboBox1.removeAllItems();
            comboBox1.addItem("Select an account");
            loadProfiles();
        });
        //endregion accounts
        //region aboutLabel
        aboutLabel = setupLabelButton("/images/toolbar/about", () -> new AboutDialog().setVisible(true));
        //endregion aboutLabel
        importWorldButton = setupLabelButton("/images/toolbar/new", this::importWorld);
        exportWorldButton = setupLabelButton("/images/curseinstaller/download", this::compressWorld);
    }

    public void importWorld() {
        UnifiedMCInstance instance = (UnifiedMCInstance) instanceList.getSelectedValue();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select world");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Zip File", "zip"));
        if (fileChooser.showDialog(panelMain, "Select") == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            try {
                ZipFile zf = new ZipFile(f);
                ZipEntry worldFolder = findWorldFolder(zf);
                if (worldFolder != null) {
                    File worldFolderOutput;
                    Utils.extractEntryDirectory(worldFolderOutput = new File(instance.getLocation(), "saves" + File.separator + Utils.getZipEntryName(worldFolder)), zf, worldFolder);
                    Main.logger.info("World Extracted");
                    ((DefaultListModel) worldList.getModel()).addElement(new WorldInstance(worldFolderOutput));
                } else {
                    Main.logger.info("Invalid Zip");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ZipEntry findWorldFolder(ZipFile file) {
        Enumeration<? extends ZipEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().endsWith("level.dat")) {
                return file.getEntry(entry.getName().substring(0, entry.getName().lastIndexOf("/")));
            }
        }
        return null;
    }

    public void compressWorld() {
        if (!worldList.isSelectionEmpty()) {
            WorldInstance world = (WorldInstance) worldList.getSelectedValue();
            File inputFolder = world.getDir();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export world");
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Zip File", "zip"));
            if (fileChooser.showDialog(panelMain, "Export") == JFileChooser.APPROVE_OPTION) {
                File outputFile = fileChooser.getSelectedFile();
                if (!outputFile.getName().endsWith(".zip"))
                    outputFile = new File(outputFile.getPath() + ".zip");
                if (!outputFile.getParentFile().exists())
                    outputFile.getParentFile().mkdirs();
                try {
                    ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputFile));
                    zos.putNextEntry(new ZipEntry(inputFolder.getName() + "/"));
                    zos.closeEntry();
                    Utils.zipDirectory(zos, inputFolder, inputFolder.getName() + "/");
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
        panel8.setLayout(new GridBagLayout());
        tabbedPane1.addTab("Worlds", panel8);
        worldList = new JList();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel8.add(worldList, gbc);
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);
        toolBar1.setOrientation(1);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel8.add(toolBar1, gbc);
        importWorldButton.setText("");
        toolBar1.add(importWorldButton);
        exportWorldButton.setText("");
        toolBar1.add(exportWorldButton);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        tabbedPane1.addTab("Options", panel9);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel9.add(panel10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel10.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Memory"));
        memMinSpinner = new JSpinner();
        panel10.add(memMinSpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel10.add(spacer1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Minimum Memory");
        panel10.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Maximum Memory");
        panel10.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        memMaxSpinner = new JSpinner();
        panel10.add(memMaxSpinner, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel9.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        panel9.add(saveButton, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelMain.add(panel11, BorderLayout.SOUTH);
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridBagLayout());
        panel11.add(panel12, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JSeparator separator3 = new JSeparator();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel12.add(separator3, gbc);
        statusProgresBar = new JProgressBar();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel12.add(statusProgresBar, gbc);
        statusLabel = new JLabel();
        statusLabel.setText("Status");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel12.add(statusLabel, gbc);
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridBagLayout());
        panel11.add(panel13, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comboBox1 = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Select an Account");
        comboBox1.setModel(defaultComboBoxModel1);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel13.add(comboBox1, gbc);
        launchButton = new JButton();
        launchButton.setEnabled(false);
        launchButton.setHorizontalAlignment(0);
        launchButton.setHorizontalTextPosition(11);
        launchButton.setText("Launch");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.EAST;
        panel13.add(launchButton, gbc);
        final JToolBar toolBar2 = new JToolBar();
        toolBar2.setFloatable(false);
        toolBar2.setOrientation(1);
        panelMain.add(toolBar2, BorderLayout.WEST);
        newLabel.setText("");
        newLabel.setToolTipText("New Instance");
        toolBar2.add(newLabel);
        importLabel.setText("");
        importLabel.setToolTipText("Import");
        toolBar2.add(importLabel);
        exportLabel.setText("");
        exportLabel.setToolTipText("Export Instance");
        toolBar2.add(exportLabel);
        removeLabel.setText("");
        removeLabel.setToolTipText("Remove Instance");
        toolBar2.add(removeLabel);
        aboutLabel.setHorizontalAlignment(4);
        aboutLabel.setText("");
        aboutLabel.setToolTipText("About");
        toolBar2.add(aboutLabel);
        accountsLabel.setText("");
        accountsLabel.setToolTipText("Accounts");
        toolBar2.add(accountsLabel);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelMain;
    }
}
