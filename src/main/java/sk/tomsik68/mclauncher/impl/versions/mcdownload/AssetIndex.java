package sk.tomsik68.mclauncher.impl.versions.mcdownload;

import net.minidev.json.JSONObject;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

final class AssetIndex {
    private final boolean virtual;
    private final Set<Asset> objects = new HashSet<>();
    private final String name;

    AssetIndex(String name, JSONObject json) {
        this.name = name;
        virtual = json.containsKey("virtual") && Boolean.parseBoolean(json.get("virtual").toString());
        JSONObject objsObj = (JSONObject) json.get("objects");
        objects.addAll(objsObj.entrySet().stream().map(objectEntry -> new Asset((JSONObject) objectEntry.getValue(), objectEntry.getKey())).collect(Collectors.toList()));
    }

    Set<Asset> getAssets() {
        return objects;
    }

    boolean isVirtual() {
        return virtual;
    }

    String getName(){ return name; }

}
