package tk.freetobuild.mcunified.gui;

import tk.freetobuild.mcunified.Main;
import sk.tomsik68.mclauncher.api.login.IProfile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * Created by liz on 6/22/16.
 */
public class HeadResolver {
    private static final HashMap<String,Image> heads = new HashMap<>();
    private static final List<String> invalidPlayers = new ArrayList<>();
    public static Image def;
    public static void loadHeads() throws IOException {
        def = ImageIO.read(HeadResolver.class.getResourceAsStream("/images/head.png")).getScaledInstance(32,32,Image.SCALE_SMOOTH);
    }
    public static Image resolve(IProfile profile) throws MalformedURLException {
        if(heads.containsKey(profile.getName()))
            return heads.get(profile.getName());
        else if(invalidPlayers.contains(profile.getName()))
            return def;
        URL remoteHead = new URL(profile.getSkinURL());
        try {
            BufferedImage head = ImageIO.read(remoteHead).getSubimage(8,8,8,8);
            heads.put(profile.getName(),head.getScaledInstance(32,32,Image.SCALE_SMOOTH));
            return head.getScaledInstance(32,32,Image.SCALE_SMOOTH);
        } catch (IOException e) {
            Main.logger.severe(String.format("Unable to find head for player '%s'",profile.getName()));
            invalidPlayers.add(profile.getName());
            return def;
        }
    }
}
