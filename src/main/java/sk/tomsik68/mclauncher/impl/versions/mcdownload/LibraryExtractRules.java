package sk.tomsik68.mclauncher.impl.versions.mcdownload;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import sk.tomsik68.mclauncher.util.IExtractRules;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

/**
 * Library extraction filter
 */
final class LibraryExtractRules implements IExtractRules {
    private final ArrayList<String> exclude = new ArrayList<>();

    public LibraryExtractRules(JSONObject object) {
        JSONArray excludeArray = (JSONArray) object.get("exclude");
        exclude.addAll(excludeArray.stream().map(Object::toString).collect(Collectors.toList()));
    }

    /**
     *
     * @param entry Entry that is being thinked about right now
     * @return True if entry may be extracted, otherwise false
     */
    @Override
    public boolean accepts(ZipEntry entry) {
        String path = entry.getName();
        if (exclude != null && !exclude.isEmpty()) {
            for (String p : exclude) {
                if (path.startsWith(p))
                    return false;

            }
        }
        return true;
    }

}
