package tk.freetobuild.mcunified;

import tk.freetobuild.mcunified.gui.workers.ProgressMonitorListener;
import tk.freetobuild.mcunified.gui.workers.ProgressMonitorWorker;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import sk.tomsik68.mclauncher.api.common.mc.MinecraftInstance;
import sk.tomsik68.mclauncher.api.servers.ServerInfo;
import sk.tomsik68.mclauncher.backend.DefaultLaunchSettings;
import sk.tomsik68.mclauncher.backend.GlobalAuthenticationSystem;
import sk.tomsik68.mclauncher.impl.versions.mcdownload.MCDownloadVersion;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by liz on 6/27/16.
 */
public class UnifiedMCInstance extends MinecraftInstance {
    public String version;
    private MCDownloadVersion mcversion;
    private String name;
    private JSONArray patches = new JSONArray();
    public JSONArray mods = new JSONArray();
    private UnifiedMCInstance(File f, String version) {
        super(f);
        name = f.getName();
        this.version = version;
        try {
            mcversion = (MCDownloadVersion) Main.backend.findVersion(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public UnifiedMCInstance(File f, JSONObject obj) {
        this(f,obj.get("version").toString());
        loadPatches((JSONArray) obj.get("patches"));
        this.mods = (JSONArray) obj.getOrDefault("mods",new JSONArray());
    }
    private void loadPatches(JSONArray patches) {
        patches.forEach(obj -> {
            JSONObject json = (JSONObject) obj;
            mcversion.patch(json);
            this.patches.add(json);
            Main.logger.info(String.format("Loaded patch '%s'",json.getOrDefault("friendlyName","unknown")));
        });
    }
    public void addPatch(JSONObject patch) throws IOException {
        mcversion.patch(patch);
        patches.add(patch);
        save();
    }
    public UnifiedMCInstance(String name,String version) {
        this(new File(Main.baseDir,"instances"+File.separator+name),version);
    }
    @Override
    public String toString() {
        return getName();
    }
    public String getName() {
        return name;
    }
    public JSONObject getJSONObject() {
        JSONObject output = new JSONObject();
        output.put("version",version);
        output.put("patches",patches);
        output.put("mods",mods);
        return output;
    }
    public void save() throws IOException {
        FileWriter writer = new FileWriter(new File(getLocation(),"instance.json"));
        getJSONObject().writeJSONString(writer);
        writer.flush();
        writer.close();
    }
    public void launch(String profile) throws Exception {
        launch(profile,(ServerInfo) null, null);
    }
    public void launch(String profile, String server) throws Exception {
        String[] splitIP = server.split(":");
        launch(profile,new ServerInfo(splitIP[0],"Server","",Integer.valueOf(splitIP[1])), null);
    }
    public void launch(String profile, ServerInfo server) throws Exception {
        launch(profile,server,null);

    }

    public void launch(String profile, JProgressBar status) throws Exception {
        launch(profile,(ServerInfo) null, status);
    }
    public void launch(String profile, String server, JProgressBar status) throws Exception {
        String[] splitIP = server.split(":");
        launch(profile,new ServerInfo(splitIP[0],"Server","",Integer.valueOf(splitIP[1])), status);
    }
    public void launch(String profile, ServerInfo server, JProgressBar status) throws Exception {
        ProgressMonitorWorker worker = new ProgressMonitorWorker(monitor -> {
            try {
                Main.backend.updateMinecraft(version, monitor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        },() -> {
            try {
                Main.logger.info("Update complete.");
                UnifiedModdingProfile moddingProfile = new UnifiedModdingProfile();
                Arrays.asList(new File(getLocation(),"jarMods").listFiles()).forEach(moddingProfile::injectAfterLib);
                ProcessBuilder builder = Main.backend.launchMinecraft(GlobalAuthenticationSystem.login(profile), server, mcversion, new DefaultLaunchSettings(), moddingProfile);
                builder.command().set(builder.command().indexOf("--gameDir")+1,getLocation().getAbsolutePath());
                builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                builder.directory(getLocation());
                builder.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        if(status!=null)
            worker.addPropertyChangeListener(new ProgressMonitorListener(Main.logger,status));
        else
            worker.addPropertyChangeListener(e->{
                if(e.getPropertyName().equalsIgnoreCase("note")||e.getPropertyName().equalsIgnoreCase("error"))
                    Main.logger.info((String) e.getNewValue());
            });
        worker.execute();

    }
    public List<String> getPatches() {
        return patches.stream().map(o -> ((JSONObject)o).get("friendlyName").toString()).collect(Collectors.toList());
    }
    public boolean removePatch(String friendlyName) {
        Iterator<Object> it = patches.iterator();
        boolean result = false;
        while(it.hasNext()) {
            if(((JSONObject)it.next()).get("friendlyName").toString().equals(friendlyName)) {
                it.remove();
                result = true;
            }
        }
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
