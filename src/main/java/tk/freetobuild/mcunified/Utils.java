package tk.freetobuild.mcunified;

import net.minecraftforge.installer.ForgeArtifact;
import net.minecraftforge.installer.ForgeVersionList;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import tk.freetobuild.mcunified.curse.CurseArtifact;
import tk.freetobuild.mcunified.gui.dialogs.DialogDownloadMod;
import tk.freetobuild.mcunified.gui.workers.ForgeInstallerWorker;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by liz on 6/28/16.
 */
public class Utils {
    public static void recursiveDelete(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if(files!=null)
                for (File f : files) {
                    recursiveDelete(f);
                }
        }
        if(!file.delete())
            Main.logger.severe("Unable to delete file "+file.getPath());
    }
    public static int getFileSize(URL url) {
        URLConnection conn;
        try {
            conn = url.openConnection();
            conn.getInputStream();
            System.out.println(conn.getContentLength());
            return conn.getContentLength();
        } catch (IOException e) {
            return -1;
        }
    }
    private static void extractEntry(File out, ZipFile zf, ZipEntry entry) throws IOException {
        if(entry.isDirectory()) {
            if(!out.mkdirs())
                Main.logger.severe("Unable to create directory "+out.getPath());
        } else {
            if(!out.getParentFile().mkdirs())
                Main.logger.severe("Unable to create directory "+out.getParentFile().getPath());
            Files.copy(zf.getInputStream(entry),out.toPath());
        }
    }
    public static void extractEntryDirectory(File output, ZipFile zf, ZipEntry dir) {
        String name = dir.getName();
        Enumeration<? extends ZipEntry> entries = zf.entries();
        while(entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if(entry.getName().startsWith(name))
                try {
                    extractEntry(new File(output,entry.getName().substring(name.length())),zf,entry);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
    public static void zipDirectory(ZipOutputStream os, File dir, String prefix) throws IOException {
        File[] files = dir.listFiles();
        if(files!=null)
            for(File f : files) {
                if(f.isDirectory()) {
                    ZipEntry entry = new ZipEntry(prefix+f.getName()+"/");
                    os.putNextEntry(entry);
                    os.closeEntry();
                    zipDirectory(os,f,prefix+f.getName()+"/");
                } else {
                    ZipEntry entry = new ZipEntry(prefix+f.getName());
                    os.putNextEntry(entry);
                    Files.copy(f.toPath(),os);
                    os.closeEntry();
                }
            }
    }
    public static String getZipEntryName(ZipEntry entry) {
        return new File(entry.getName()).getName();
    }
    public static JSONObject buildModpack(UnifiedMCInstance instance, String name, String version, String author, String desc, JSONObject config) {
        JSONObject result = instance.getJSONObject();
        result.put("name",name);
        result.put("mcversion",result.get("version"));
        result.put("author",author);
        result.put("description",desc);
        result.put("version",version);
        result.put("config",config);
        JSONArray patches = (JSONArray) result.get("patches");
        patches.stream().map(o->(JSONObject)o).filter(o->o.containsKey("forgeBuild")).forEach(o->result.put("forge",o.get("forgeBuild")));
        result.remove("patches");
        return result;
    }
    public static UnifiedMCInstance installModpack(String name, JSONObject modpack) {
        File dir = new File(Main.baseDir,"instances"+File.separator+name);
        if(!dir.exists())
            if(!dir.mkdirs())
                Main.logger.severe("Unable to create directory "+dir.getPath());
        UnifiedMCInstance result = new UnifiedMCInstance(name,modpack.get("mcversion").toString());
        if(modpack.containsKey("forge")) {
            ForgeVersionList.refreshList();
            ForgeArtifact artifact = ForgeVersionList.getArtifact((int) modpack.get("forge"));
            new ForgeInstallerWorker(artifact, patch -> {
                try {
                    result.addPatch(patch);
                } catch (IOException ignored) {
                }
            }).execute();
        }
        JSONArray mods = (JSONArray) modpack.getOrDefault("mods",new JSONArray());
        DialogDownloadMod dialogDownloadMod = new DialogDownloadMod(result,mods.stream().map(o->new CurseArtifact(o.toString())).collect(Collectors.toList()));
        dialogDownloadMod.setVisible(true);
        return result;
    }
}
