package tk.freetobuild.mcunified.curse;

import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Created by liz on 6/30/16.
 */
class CurseAPI {
    public static SimpleCurseModInfo getSimpleMod(String url) {
        try {
            String path = Jsoup.connect(url).followRedirects(true).userAgent("Mozilla").execute().url().getPath();
            return new SimpleCurseModInfo(path.substring(path.lastIndexOf("/")+1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
