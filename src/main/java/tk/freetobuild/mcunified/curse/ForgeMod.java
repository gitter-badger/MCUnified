package tk.freetobuild.mcunified.curse;


import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.jsoup.helper.StringUtil;

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
    public String version;
    private String description;
    String[] authors;
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
                JSONObject obj = (JSONObject)((JSONArray) JSONValue.parse(jar.getInputStream(info))).get(0);
                name = obj.get("name").toString();
                version = obj.get("version").toString();
                description = obj.get("description").toString();
                authors = ((JSONArray) obj.get("authorList")).stream().map(s -> (String)s).toArray(String[]::new);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void enable() {
        if(!enabled) {
            mod.renameTo(original);
            mod = original;
            enabled = true;
        }
    }
    public void disable() {
        if(enabled) {
            File newMod = new File(mod.getPath()+".disabled");
            mod.renameTo(newMod);
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
