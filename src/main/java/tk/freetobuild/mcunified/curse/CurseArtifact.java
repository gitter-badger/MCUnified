package tk.freetobuild.mcunified.curse;

/**
 * Created by liz on 6/30/16.
 */
public class CurseArtifact {
    private String modId;
    private ReleaseType releaseType;
    private String name;
    private String fileID;
    public enum ReleaseType {
        RELEASE, ALPHA, BETA
    }
    public CurseArtifact(String modId, String name, String fileID, ReleaseType releaseType) {
        this.modId = modId;
        this.releaseType = releaseType;
        this.name = name;
        this.fileID = fileID;
    }
    public String getDownload() {
        return "http://minecraft.curseforge.com/projects/"+modId+"/files/"+fileID;
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
}
