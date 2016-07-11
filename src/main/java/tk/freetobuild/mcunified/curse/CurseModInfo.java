package tk.freetobuild.mcunified.curse;

import net.minidev.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by liz on 6/30/16.
 */
public class CurseModInfo {
    private final String id;
    private final String name;
    private final String author;
    private final String description;
    private final String icon;
    public CurseModInfo(String id, String name, String author, String description, String icon) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.description = description;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public List<CurseArtifact> getFiles() {
        List<CurseArtifact> artifacts = new ArrayList<>();
        try {

            int i = 1;
            while(true) {
                Document doc = Jsoup.connect(getPage()+"/files?page="+i).userAgent("Mozilla").get();
                artifacts.addAll(doc.select(".project-file-list-item").stream().map(element -> {
                    Element name = element.select(".project-file-name .project-file-name-container a").get(0);
                    Elements release = element.select(".project-file-release-type .release-phase.tip");
                    CurseArtifact.ReleaseType releaseType = CurseArtifact.ReleaseType.ALPHA;
                    if(release.size()>0)
                        releaseType = CurseArtifact.ReleaseType.valueOf(release.get(0).attr("title").toUpperCase());
                    return new CurseArtifact(id,element.select(".project-file-game-version .version-label").text(),name.text(),name.attr("href").substring(name.attr("href").lastIndexOf("/")+1),releaseType);
                }).collect(Collectors.toList()));
                if(doc.select("a[rel=\"next\"]:contains(Next)").size()==0)
                    break;
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return artifacts;
    }
    public String getPage() {
        return "http://minecraft.curseforge.com/projects/"+id;
    }

}
