package tk.freetobuild.mcunified.curse;

/**
 * Created by liz on 6/30/16.
 */
public class CurseAPI {
    public CurseModInfo getMod(String id) {
        return new SimpleCurseModInfo(id);
    }

}
