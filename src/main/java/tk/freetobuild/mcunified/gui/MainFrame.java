package tk.freetobuild.mcunified.gui;

import tk.freetobuild.mcunified.Utils;
import tk.freetobuild.mcunified.Main;
import tk.freetobuild.mcunified.UnifiedMCInstance;
import tk.freetobuild.mcunified.gui.dialogs.DialogNewInstance;
import net.minidev.json.JSONValue;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * Created by liz on 6/22/16.
 */
public class MainFrame extends JFrame {
    public MainFrame() {
        super("MCUnified - Alpha 0.0.1");
        try {
            setIconImage(ImageIO.read(getClass().getResourceAsStream("/images/head.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setContentPane((Main.gui = new GuiMain(this)).getPanelMain());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        //buildMenuBar();
        setPreferredSize(new Dimension(640,480));
        pack();
    }
    private void buildMenuBar() {
        //region Instance
        JMenu instance = new JMenu("Instance");
        instance.setDisplayedMnemonicIndex(0);

        //region new Instance
        JMenu importInstance = new JMenu("Import");
        JMenuItem newInstance = new JMenuItem("New");
        newInstance.addActionListener(actionEvent -> new DialogNewInstance(Main.gui).setVisible(true));
        JMenuItem technicInstance = new JMenuItem("Technic Pack");
        JMenuItem ftbInstance = new JMenuItem("Feed The Beast Pack");
        JMenuItem atInstance = new JMenuItem("ATLauncher Pack");
        JMenuItem cursePack = new JMenuItem("Curse Pack");
        importInstance.add(technicInstance);
        importInstance.add(ftbInstance);
        importInstance.add(atInstance);
        importInstance.add(cursePack);

        JMenuItem deleteInstance = new JMenuItem("Delete");
        deleteInstance.addActionListener(e -> {
            if(!Main.gui.instanceList.isSelectionEmpty()) {
                UnifiedMCInstance mcinstance = Main.gui.instanceList.getSelectedValue();
                Utils.recursiveDelete(mcinstance.getLocation());
                ((DefaultListModel)Main.gui.instanceList.getModel()).removeElement(mcinstance);
            }
        });
        //endregion Instance
        JMenuBar menu = new JMenuBar();
        instance.add(newInstance);
        instance.add(importInstance);
        instance.add(deleteInstance);
        menu.add(instance);
        setJMenuBar(menu);
    }

    @Override
    public void dispose() {
        super.dispose();
        try {
            PrintWriter writer = new PrintWriter(new File(Main.baseDir,"config.json"));
            writer.print(JSONValue.toJSONString(Main.config));
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Main.logger.info("Config saved");
    }
}
