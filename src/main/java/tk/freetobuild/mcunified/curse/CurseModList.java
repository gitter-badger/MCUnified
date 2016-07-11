package tk.freetobuild.mcunified.curse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by liz on 6/30/16.
 */
public class CurseModList implements Iterator<List<CurseModInfo>> {
    //region topModConsumer
    private static final Function<Element,CurseModInfo> topModConsumer = e -> {
        String id = e.select(".details .info.name a").get(0).attr("href");
        id = id.substring(id.lastIndexOf("/")+1);
        String icon = "http://static-elerium.cursecdn.com/1-0-6019-17319/Skins/Elerium/images/icons/avatar-flame.png";
        Elements icons = e.select(".avatar a img");
        if(icons.size()>0)
            icon = icons.get(0).attr("src");
        return new CurseModInfo(id, e.select(".details .info.name a").get(0).text(), e.select(".details .info.name .byline a").text(), e.select(".details .description p").get(0).text(),icon);
    };
    //endregion topModConsumer
    //region resultModConsumer
    private static final Function<Element,CurseModInfo> resultModConsumer = e -> {
        String id = e.select("td .results-name a").get(0).attr("href");
        id = id.substring(id.lastIndexOf("/")+1,id.lastIndexOf("?"));
        String icon = "http://static-elerium.cursecdn.com/1-0-6019-17319/Skins/Elerium/images/icons/avatar-flame.png";
        Elements icons = e.select("td .results-image.e-avatar64 img");
        if(icons.size()>0)
            icon = icons.get(0).attr("src");
        return new CurseModInfo(id, e.select("td .results-name a").get(0).text(), e.select(".results-owner a").text(), e.select("td .results-summary").get(0).text(),icon);
    };
    //endregion resultModConsumer
    private String baseSelector;
    private Function<? super Element, ? extends CurseModInfo> func;
    private int currentPage = 0;
    private boolean hasNext = true;
    private String baseURL;
    private List<CurseModInfo> nextList;
    private CurseModList(String baseURL, String baseSelector, Function<? super Element, ? extends CurseModInfo> func) {
        this.baseSelector = baseSelector;
        this.func = func;
        this.baseURL = baseURL;
    }
    public static CurseModList loadTopMods() throws IOException {
        return new CurseModList("http://minecraft.curseforge.com/mc-mods?page=",".project-list-item",topModConsumer);
    }
    public static CurseModList search(String mod) {
        try {
            return new CurseModList("http://minecraft.curseforge.com/search?search="+ URLEncoder.encode(mod,"UTF-8")+"&projects-page=",".results",resultModConsumer);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean hasNext() {
        return (nextList==null||!nextList.isEmpty())&&hasNext;
    }
    private List<CurseModInfo> getMods(int page) {
        try {
            Document doc = Jsoup.connect(baseURL+page).userAgent("Mozilla").get();
            hasNext = doc.select(".b-pagination-item a[rel=\"next\"]:containsOwn(Next)").size()>0;
            Elements elements = doc.select(baseSelector);
            return elements.stream().map(func).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public List<CurseModInfo> next() {
        currentPage++;
        List<CurseModInfo> result;
        if(nextList!=null)
            result =  nextList;
        else
            result = getMods(currentPage);
        nextList = getMods(currentPage+1);
        return result;
    }
}
