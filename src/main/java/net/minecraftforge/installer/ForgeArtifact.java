package net.minecraftforge.installer;

import argo.format.PrettyJsonFormatter;
import argo.jdom.*;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import net.minecraftforge.installer.platform.Platform;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

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
    private final String branch;
    private final int build;
    private final String mcversion;
    private final String version;
    private final Map<String,URI> files = new HashMap<>();
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
    private boolean hasFile() {
        return files.containsKey("installer");
    }
    private URI getFile() {
        return files.get("installer");
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
        if(!this.hasFile())
            throw new ForgeInstallException("There is no installer candidate for build '"+this.getBuild()+"'");
        VersionInfo version = new VersionInfo(this.getFile());
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
        DownloadUtils.downloadInstalledLibraries(librariesDir, monitor, libraries, progress, grabbed, bad,new MirrorData(version));
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
        patch.put("forgeBuild",this.build);
        try
        {
            version.extractFile(targetLibraryFile);
        }
        catch (IOException e)
        {
            throw new ForgeInstallException("There was a problem writing the system library file");
        }
        return patch;
    }
}
