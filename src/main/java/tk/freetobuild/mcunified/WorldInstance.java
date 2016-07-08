package tk.freetobuild.mcunified;

import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by liz on 7/7/16.
 */
public class WorldInstance {
    private File dir;
    private ImageIcon icon;
    private String name;
    public WorldInstance(File dir) {
        this.dir = dir;
        File ico = new File(dir,"icon.png");
        try {
            if (ico.exists())
                icon = new ImageIcon(ImageIO.read(ico));
            else
                icon = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/images/pack.png")));
        }catch (IOException ignored) {}
        try {
            NBTInputStream nis = new NBTInputStream(new FileInputStream(new File(dir,"level.dat")));
            name = ((CompoundTag)((CompoundTag)nis.readTag()).getValue().get("Data")).getValue().get("LevelName").getValue().toString();
            nis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getDir() {
        return dir;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }
}
