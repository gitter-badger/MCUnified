package tk.freetobuild.mcunified.gui.workers;

import sk.tomsik68.mclauncher.api.ui.IProgressMonitor;

import javax.swing.*;
import java.util.function.Consumer;

/**
 * Created by liz on 6/30/16.
 */
public class ProgressMonitorWorker extends SwingWorker<Void,Void> {
    private Consumer<IProgressMonitor> toDo;
    private Runnable onComplete;
    public ProgressMonitorWorker(Consumer<IProgressMonitor> toDo,Runnable complete) {
        this.toDo = toDo;
        this.onComplete = complete;
    }
    @Override
    protected Void doInBackground() throws Exception {
        toDo.accept(new IProgressMonitor() {
            private long max;
            private long progress;
            @Override
            public void setProgress(long progress) {
                ProgressMonitorWorker.this.setProgress((int)((this.progress=progress)*100/max));
            }

            @Override
            public void setMax(long len) {
                max = len;
            }

            @Override
            public void incrementProgress(long amount) {
                setProgress(this.progress+amount);
            }

            @Override
            public void setStatus(String status) {
                ProgressMonitorWorker.this.firePropertyChange("note","",status);
            }
        });
        return null;
    }

    @Override
    protected void done() {
        onComplete.run();
    }
}
