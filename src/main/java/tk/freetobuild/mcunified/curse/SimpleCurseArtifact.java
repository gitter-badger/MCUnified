package tk.freetobuild.mcunified.curse;

/**
 * Created by liz on 7/1/16.
 */
public class SimpleCurseArtifact extends CurseArtifact {
    public SimpleCurseArtifact(String modId, String fileID) {
        super(modId, "", "", fileID, null);
    }
    public SimpleCurseArtifact(String shortHand) {
        this(shortHand.split(":")[0],shortHand.split(":")[1]);
    }
}
