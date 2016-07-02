package tk.freetobuild.mcunified.curse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by liz on 6/30/16.
 */
public class CurseArtifact {
    private String modId;
    private ReleaseType releaseType;
    private String name;
    private String fileID;
    private String version;
    public enum ReleaseType {
        RELEASE, ALPHA, BETA
    }
    public CurseArtifact(String modId, String version, String name, String fileID, ReleaseType releaseType) {
        this.modId = modId;
        this.version = version;
        this.releaseType = releaseType;
        this.name = name;
        this.fileID = fileID;
    }
    public String getDownload() {
        return getPage()+"/download";
    }

    public String getPage() {
        try {
            return "http://minecraft.curseforge.com/projects/"+URLEncoder.encode(modId,"UTF-8")+"/files/"+fileID;
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public List<SimpleCurseModInfo> getDependencies() {
        try {
            Document doc = Jsoup.connect(getPage()).userAgent("Mozilla").get();
            return doc.select("h5:contains(Required Library)+ul .project-tag").stream().map(e -> CurseAPI.getSimpleMod("http://minecraft.curseforge.com"+e.select("a").get(0).attr("href"))).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<CurseArtifact> getRecommendedDependencyArtifacts() {
        return getDependencies().stream().map(simpleCurseModInfo -> simpleCurseModInfo.getFiles().stream().filter(curseArtifact -> curseArtifact.getVersion().equals(getVersion())).collect(Collectors.toList()).get(0)).collect(Collectors.toList());
    }
    @Override
    public String toString() {
        return modId+":"+fileID;
    }

    public String getModId() {
        return modId;
    }

    public ReleaseType getReleaseType() {
        return releaseType;
    }

    public String getName() {
        return name;
    }

    public String getFileID() {
        return fileID;
    }

    public String getVersion() { return version; }

    public List<CurseArtifact> getAllDependencies() {
        List<CurseArtifact> dependencies = new ArrayList<>();
        getRecommendedDependencyArtifacts().forEach(dependency -> {
            if(!dependencies.contains(dependency))
                dependencies.add(dependency);
            dependency.getAllDependencies().forEach(d -> {
                if(!dependencies.contains(d))
                    dependencies.add(d);
            });
        });
        return dependencies;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CurseArtifact && obj.toString().equals(toString());
    }
}
