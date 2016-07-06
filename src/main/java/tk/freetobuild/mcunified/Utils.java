package tk.freetobuild.mcunified;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

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
}
