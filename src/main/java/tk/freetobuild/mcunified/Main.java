package tk.freetobuild.mcunified;

import com.bulenkov.darcula.DarculaLaf;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import tk.freetobuild.mcunified.gui.dialogs.DialogSelectPlayer;
import tk.freetobuild.mcunified.gui.HeadResolver;
import tk.freetobuild.mcunified.gui.MainFrame;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import sk.tomsik68.mclauncher.backend.GlobalAuthenticationSystem;
import sk.tomsik68.mclauncher.backend.MinecraftLauncherBackend;
import sk.tomsik68.mclauncher.impl.common.Platform;
import sk.tomsik68.mclauncher.impl.login.yggdrasil.YDLoginService;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Logger;

public class Main {
    public static JSONObject config;
    public static File baseDir;
    public static Logger logger;
    public static MinecraftLauncherBackend backend;
    public static YDLoginService service;
    public static void main(String[] args) {
        logger = Logger.getLogger("MCUnified");
        logger.info("Initializing Launcher");
        service = new YDLoginService();
        backend = new MinecraftLauncherBackend(Platform.getCurrentPlatform().getWorkingDirectory());
        baseDir = new File(Platform.getCurrentPlatform().getWorkingDirectory(), "mcunified");
        logger.info(String.format("Setting base directory to '%s'", baseDir.getAbsolutePath()));
        File configFile = new File(baseDir, "config.json");
        if (!configFile.exists()) {
            logger.info("Config not found saving default");
            configFile.getParentFile().mkdirs();
            saveDefaultConfig();
            logger.info("Default config saved");
        }
        try {
            logger.info("Loading config");
            config = (JSONObject) JSONValue.parse(new FileInputStream(configFile));
            logger.info("Config loaded");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            logger.info("Loading player heads");
            HeadResolver.loadHeads();
            logger.info("Player heads loaded.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            UIManager.setLookAndFeel(new DarculaLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        if(args.length>0) {
            OptionParser parser = new OptionParser();
            parser.accepts("instance").withRequiredArg();
            parser.accepts("server").withRequiredArg();
            parser.accepts("profile").withRequiredArg();
            OptionSet set = parser.parse(args);
            if(set.has("instance")) {
                String instance = set.valueOf("instance").toString();
                File instanceFolder = new File(baseDir, "instances" + File.separator + instance);
                if (!new File(instanceFolder, "instance.json").exists()) {
                    JOptionPane.showMessageDialog(null, "Instance '"+instance+"' Not Found.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        String server = "";
                        if(set.has("server")) {
                            server = set.valueOf("server").toString();
                            if(!server.contains(":")) {
                                server = server+":25565";
                            }
                        }
                        UnifiedMCInstance mcInstance = new UnifiedMCInstance(instanceFolder, (JSONObject) JSONValue.parse(new FileInputStream(new File(instanceFolder, "instance.json"))));
                        if(set.has("profile")) {
                            if(Arrays.asList(GlobalAuthenticationSystem.getProfileNames()).contains(set.valueOf("profile").toString())) {
                                if(server.isEmpty())
                                    mcInstance.launch(set.valueOf("profile").toString());
                                else
                                    mcInstance.launch(set.valueOf("profile").toString(),server);
                            } else {
                                JOptionPane.showMessageDialog(null,"Invalid Login","Error",JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            DialogSelectPlayer dsp = new DialogSelectPlayer();
                            dsp.setInstance(mcInstance);
                            if(!server.isEmpty())
                                dsp.setServer(server);
                            dsp.setVisible(true);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            new MainFrame().setVisible(true);
        }
    }
    private static void saveDefaultConfig() {
        InputStream is = Main.class.getResourceAsStream("/config.json");
        try {
            Files.copy(is,new File(baseDir,"config.json").toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
