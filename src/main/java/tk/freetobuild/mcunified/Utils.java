package tk.freetobuild.mcunified;

import java.io.File;

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
}
