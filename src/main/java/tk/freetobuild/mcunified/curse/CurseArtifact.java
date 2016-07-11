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
    private final String modId;
    private ReleaseType releaseType;
    private final String name;
    private final String fileID;
    private String version;
    public enum ReleaseType {
        ALPHA
    }
    public CurseArtifact(String modId, String version, String name, String fileID, ReleaseType releaseType) {
        this.modId = modId;
        this.version = version;
        this.releaseType = releaseType;
        this.name = name;
        this.fileID = fileID;
    }
    public CurseArtifact(String shortHand) {
        String[] split = shortHand.split(":");
        this.modId = split[0];
        this.fileID = split[1];
        this.name = split[2];
    }
    public String getDownload() {
        return getPage()+"/download";
    }

    private String getPage() {
        try {
            return "http://minecraft.curseforge.com/projects/"+URLEncoder.encode(modId,"UTF-8")+"/files/"+fileID;
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    private List<SimpleCurseModInfo> getDependencies() {
        try {
            Document doc = Jsoup.connect(getPage()).userAgent("Mozilla").get();
            return doc.select("h5:contains(Required Library)+ul .project-tag").stream().map(e -> CurseAPI.getSimpleMod("http://minecraft.curseforge.com"+e.select("a").get(0).attr("href"))).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<CurseArtifact> getRecommendedDependencyArtifacts() {
        return getDependencies().stream().map(simpleCurseModInfo -> simpleCurseModInfo.getFiles().stream().filter(curseArtifact -> isCompatible(curseArtifact.getVersion())).collect(Collectors.toList()).get(0)).collect(Collectors.toList());
    }
    @Override
    public String toString() {
        return modId+":"+fileID+":"+name;
    }

    public String getModId() {
        return modId;
    }

    public String getName() {
        return name;
    }

    private String getVersion() { return version; }

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
    public boolean isCompatible(String version) {
        String[] strings = version.split("\\.");
        String v1 = strings[0]+"."+strings[1];
        strings = getVersion().split("\\.");
        return v1.equals(strings[0]+"."+strings[1]);
    }
    @Override
    public boolean equals(Object obj) {
        return obj instanceof CurseArtifact && obj.toString().equals(toString());
    }
}
