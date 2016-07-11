package sk.tomsik68.mclauncher.impl.login.yggdrasil;

import net.minidev.json.JSONObject;
import sk.tomsik68.mclauncher.api.json.IJSONSerializable;

final class YDPartialGameProfile implements IJSONSerializable {
    private final String name, id;
    private final boolean legacy;

    public YDPartialGameProfile(String name, String id) {
        this.name = name;
        this.id = id;
        this.legacy = false;
    }

    public YDPartialGameProfile(JSONObject jsonObj) {
        this(jsonObj.get("name").toString(), jsonObj.get("id").toString());

    }

    String getName() {
        return name;
    }

    String getId() {
        return id;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("name", name);
        return obj;
    }

    boolean isLegacy() {
        return legacy;
    }
}
