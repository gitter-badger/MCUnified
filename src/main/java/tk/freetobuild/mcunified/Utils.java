package tk.freetobuild.mcunified;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Created by liz on 6/28/16.
 */
public class Utils {
    public static void recursiveDelete(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                recursiveDelete(f);
            }
        }
        file.delete();
    }
    public static int getFileSize(URL url) {
        URLConnection conn = null;
        try {
            conn = url.openConnection();
            conn.getInputStream();
            System.out.println(conn.getContentLength());
            return conn.getContentLength();
        } catch (IOException e) {
            return -1;
        }
    }
    public static void extractEntry(File out, ZipFile zf, ZipEntry entry) throws IOException {
        if(entry.isDirectory()) {
            out.mkdirs();
        } else {
            out.getParentFile().mkdirs();
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
        for(File f : dir.listFiles()) {
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
    public static JSONObject buildModpack(UnifiedMCInstance instance, String name, String version, String author, String desc) {
        JSONObject result = instance.getJSONObject();
        result.put("name",name);
        result.put("version",result.get("version"));
        result.put("author",author);
        result.put("description",desc);
        result.put("version",version);
        result.put("config",new JSONArray());
        JSONArray patches = (JSONArray) result.get("patches");
        patches.stream().map(o->(JSONObject)o).filter(o->o.containsKey("forgeBuild")).forEach(o->result.put("forge",o.get("forgeBuild")));
        result.remove("patches");
        return result;
    }
}
