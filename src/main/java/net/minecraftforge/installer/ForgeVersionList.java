package net.minecraftforge.installer;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by liz on 6/29/16.
 */
public class ForgeVersionList {
    private static String versionURL="http://files.minecraftforge.net/maven/net/minecraftforge/forge/json";
    private static Map<String,List<ForgeArtifact>> versions = new HashMap<>();
    private static Map<Integer,ForgeArtifact> builds = new HashMap<>();
    public static void refreshList() {
        versions.clear();
        try {
            JSONObject obj = (JSONObject) JSONValue.parse(new URL(versionURL).openStream());
            String baseURL = (String) obj.get("homepage");
            JSONObject builds = (JSONObject) obj.get("number");
            for(Object o : builds.values()) {
                ForgeArtifact artifact = new ForgeArtifact((JSONObject)o,baseURL);
                if(!versions.containsKey(artifact.getMcversion()))
                    versions.put(artifact.getMcversion(),new ArrayList<>());
                versions.get(artifact.getMcversion()).add(artifact);
                ForgeVersionList.builds.put(artifact.getBuild(),artifact);
            }
            versions.values().forEach(forgeArtifacts -> forgeArtifacts.sort((a1,a2)->{
                if(a1.getBuild()<a2.getBuild())
                    return -1;
                else if(a1.getBuild()>a2.getBuild())
                    return 1;
                return 0;
            }));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<ForgeArtifact> getArtifacts(String version) {
        if(!versions.containsKey(version))
            return new ArrayList<>();
        else
            return versions.get(version);
    }
    public static ForgeArtifact getArtifact(int build) {
        return builds.get(build);
    }
    public static Set<String> getVersions() {
        return versions.keySet();
    }
}
