package tk.freetobuild.mcunified.gui.workers;

import tk.freetobuild.mcunified.Main;
import net.minecraftforge.installer.ForgeArtifact;
import net.minecraftforge.installer.ForgeInstallException;
import net.minecraftforge.installer.IMonitor;
import net.minidev.json.JSONObject;

import javax.swing.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Created by liz on 6/30/16.
 */
public class ForgeInstallerWorker extends SwingWorker<JSONObject,Void> {
    private final ForgeArtifact artifact;
    private final Consumer<JSONObject> finished;
    public ForgeInstallerWorker(ForgeArtifact artifact, Consumer<JSONObject> finished) {
        this.artifact = artifact;
        this.finished = finished;
    }
    @Override
    protected JSONObject doInBackground() throws Exception {
        try {
            return artifact.install(new IMonitor() {
                private int max;

                @Override
                public void setMaximum(int max) {
                    this.max = max;
                }

                @Override
                public void setNote(String note) {
                    ForgeInstallerWorker.this.firePropertyChange("note", "", note);
                }

                @Override
                public void setProgress(int progress) {
                    ForgeInstallerWorker.this.setProgress(progress * 100 / max);
                }

                @Override
                public void close() {
                    ForgeInstallerWorker.this.setProgress(100);
                }
            });
        }catch(ForgeInstallException ex) {
            ForgeInstallerWorker.this.firePropertyChange("error","",ex.getMessage());
            return null;
        }
    }

    @Override
    protected void done() {
        try {
            finished.accept(get());
            Main.logger.info("Forge install complete.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
