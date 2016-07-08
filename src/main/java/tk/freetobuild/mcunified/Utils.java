package tk.freetobuild.mcunified;

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
    public static String getZipEntryName(ZipEntry entry) {
        return new File(entry.getName()).getName();
    }
}
