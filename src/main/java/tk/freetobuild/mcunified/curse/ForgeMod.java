package tk.freetobuild.mcunified.curse;


import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.jsoup.helper.StringUtil;
import tk.freetobuild.mcunified.Main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by liz on 6/30/16.
 */
public class ForgeMod {
    private String name;
    private String version;
    private String[] authors;
    private boolean enabled = true;
    private File mod;
    private File original;
    //not implemented: modid, mcversion, url, updateURL, credits, logofile, screenshot, parents, dependencies
    public ForgeMod(File mod) {
        try {
            original = this.mod = mod;
            enabled = !mod.getName().endsWith(".disabled");
            JarFile jar = new JarFile(mod);
            JarEntry info = jar.getJarEntry("mcmod.info");
            if(info!=null) {
                Object json = JSONValue.parse(jar.getInputStream(info));
                JSONArray arr;
                if(json instanceof JSONArray) {
                    arr = (JSONArray)json;
                } else if(json instanceof JSONObject) {
                    arr = (JSONArray)((JSONObject)json).get("modList");
                } else {
                    throw new IllegalArgumentException("Invalid mod");
                }
                JSONObject obj = (JSONObject)arr.get(0);
                name = obj.get("name").toString();
                version = obj.get("version").toString();
                authors = ((JSONArray) obj.get("authorList")).stream().map(s -> (String)s).toArray(String[]::new);
            } else {
                throw new IllegalArgumentException("Invalid mod");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void enable() {
        if(!enabled) {
            if(!mod.renameTo(original))
                Main.logger.severe("Unable to rename file "+mod.getPath());
            mod = original;
            enabled = true;
        }
    }
    public void disable() {
        if(enabled) {
            File newMod = new File(mod.getPath()+".disabled");
            if(!mod.renameTo(original))
                Main.logger.severe("Unable to rename file "+mod.getPath());
            mod = newMod;
            enabled = false;
        }
    }
    public File getFile() {
        return mod;
    }
    public boolean isEnabled() {
        return enabled;
    }
    @Override
    public String toString() {
        if(enabled)
            return name+" v"+version+" by "+ StringUtil.join(Arrays.asList(authors),", ");
        else
            return name+" v"+version+" by "+ StringUtil.join(Arrays.asList(authors),", ")+" (disabled)";
    }
}
