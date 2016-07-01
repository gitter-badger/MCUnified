package net.minecraftforge.installer;

import argo.format.PrettyJsonFormatter;
import argo.jdom.*;
import argo.saj.InvalidSyntaxException;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import net.minecraftforge.installer.platform.Platform;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liz on 6/29/16.
 */
public class ForgeArtifact {
    private String branch;
    private int build;
    private String mcversion;
    private String version;
    private Map<String,URI> files = new HashMap<>();
    public ForgeArtifact(JSONObject obj,String baseURI) {
        branch = (String) obj.get("branch");
        build = (int)obj.get("build");
        version = (String) obj.get("version");
        mcversion = (String) obj.get("mcversion");
        JSONArray files = (JSONArray) obj.get("files");
        files.forEach(f -> {
            JSONArray file = (JSONArray) f;
            String fname = file.get(1)+"."+file.get(0);
            String name = String.format("%s-%s",mcversion,version);
            if(branch!=null)
                name+=String.format("-%s",branch);
            try {
                this.files.put((String)file.get(1), new URI(String.format("%1$s%2$s/forge-%2$s-%3$s",baseURI,name,fname)));
            }catch(URISyntaxException ignored) {
            }
        });
    }
    public int getBuild() {
        return build;
    }
    public String getMcversion() {
        return mcversion;
    }
    public boolean hasFile(String file) {
        return files.containsKey(file);
    }
    public URI getFile(String file) {
        return files.get(file);
    }
    public Collection<URI> getFiles() {
        return files.values();
    }
    public String getBranch() {
        return branch;
    }
    public String toString() {
        return String.valueOf(build);
    }
    public JSONObject install(IMonitor monitor) throws ForgeInstallException {
        if(!this.hasFile("installer"))
            throw new ForgeInstallException("There is no installer candidate for build '"+this.getBuild()+"'");
        VersionInfo version = new VersionInfo(this.getFile("installer"));
        File target = Platform.getCurrentPlatform().getWorkingDirectory();
        if (!target.exists())
        {
            throw new ForgeInstallException("There is no minecraft installation at this location!");
        }
        File launcherProfiles = new File(target,"launcher_profiles.json");
        if (!launcherProfiles.exists())
        {
            throw new ForgeInstallException("There is no minecraft launcher profile at this location, you need to run the launcher first!");
        }
        File librariesDir = new File(target, "libraries");
        List<JsonNode> libraries = version.getVersionInfo().getArrayNode("libraries");
        monitor.setMaximum(libraries.size() + 3);
        int progress = 3;
        File targetLibraryFile = version.getLibraryPath(librariesDir);
        List<Artifact> grabbed = Lists.newArrayList();
        List<Artifact> bad = Lists.newArrayList();
        DownloadUtils.downloadInstalledLibraries("clientreq", librariesDir, monitor, libraries, progress, grabbed, bad,new MirrorData(version));
        monitor.close();
        if (bad.size() > 0)
        {
            String list = Joiner.on("\n").join(bad);
            throw new ForgeInstallException("These libraries failed to download. Try again.\n"+list);
        }

        if (!targetLibraryFile.getParentFile().mkdirs() && !targetLibraryFile.getParentFile().isDirectory())
        {
            if (!targetLibraryFile.getParentFile().delete())
            {
                throw new ForgeInstallException("There was a problem with the launcher version data. You will need to clear "+targetLibraryFile.getAbsolutePath()+" manually");
            }
            else
            {
                targetLibraryFile.getParentFile().mkdirs();
            }
        }


        JsonRootNode versionJson = JsonNodeFactories.object(version.getVersionInfo().getFields());
        JSONObject patch = (JSONObject) JSONValue.parse(PrettyJsonFormatter.fieldOrderPreservingPrettyJsonFormatter().format(versionJson));
        patch.remove("id");
        patch.remove("time");
        patch.remove("releaseTime");
        patch.remove("type");
        patch.remove("inheritsFrom");
        patch.remove("jar");
        patch.put("friendlyName","Forge-"+this.version);
        try
        {
            version.extractFile(targetLibraryFile);
        }
        catch (IOException e)
        {
            throw new ForgeInstallException("There was a problem writing the system library file");
        }

        JdomParser parser = new JdomParser();
        JsonRootNode jsonProfileData;

        try
        {
            jsonProfileData = parser.parse(Files.newReader(launcherProfiles, Charsets.UTF_8));
        }
        catch (InvalidSyntaxException e)
        {
            JOptionPane.showMessageDialog(null, "The launcher profile file is corrupted. Re-run the minecraft launcher to fix it!", "Error", JOptionPane.ERROR_MESSAGE);
            throw new ForgeInstallException("There was a problem writing the system library file");
        }
        catch (Exception e)
        {
            throw Throwables.propagate(e);
        }




        HashMap<JsonStringNode, JsonNode> profileCopy = Maps.newHashMap(jsonProfileData.getNode("profiles").getFields());
        HashMap<JsonStringNode, JsonNode> rootCopy = Maps.newHashMap(jsonProfileData.getFields());
        if(profileCopy.containsKey(JsonNodeFactories.string(version.getProfileName())))
        {
            HashMap<JsonStringNode, JsonNode> forgeProfileCopy = Maps.newHashMap(profileCopy.get(JsonNodeFactories.string(version.getProfileName())).getFields());
            forgeProfileCopy.put(JsonNodeFactories.string("name"), JsonNodeFactories.string(version.getProfileName()));
            forgeProfileCopy.put(JsonNodeFactories.string("lastVersionId"), JsonNodeFactories.string(version.getVersionTarget()));
        }
        else
        {
            JsonField[] fields = new JsonField[] {
                    JsonNodeFactories.field("name", JsonNodeFactories.string(version.getProfileName())),
                    JsonNodeFactories.field("lastVersionId", JsonNodeFactories.string(version.getVersionTarget())),
            };
            profileCopy.put(JsonNodeFactories.string(version.getProfileName()), JsonNodeFactories.object(fields));
        }
        JsonRootNode profileJsonCopy = JsonNodeFactories.object(profileCopy);
        rootCopy.put(JsonNodeFactories.string("profiles"), profileJsonCopy);

        jsonProfileData = JsonNodeFactories.object(rootCopy);

        try
        {
            BufferedWriter newWriter = Files.newWriter(launcherProfiles, Charsets.UTF_8);
            PrettyJsonFormatter.fieldOrderPreservingPrettyJsonFormatter().format(jsonProfileData,newWriter);
            newWriter.close();
        }
        catch (Exception e)
        {
            throw new ForgeInstallException("There was a problem writing the launch profile,  is it write protected?");
        }
        return patch;
    }
}
