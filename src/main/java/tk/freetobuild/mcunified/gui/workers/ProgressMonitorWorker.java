package tk.freetobuild.mcunified.gui.workers;

import sk.tomsik68.mclauncher.api.ui.IProgressMonitor;

import javax.swing.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by liz on 6/30/16.
 */
public class ProgressMonitorWorker extends SwingWorker<Void,Void> {
    private BiConsumer<IProgressMonitor,ProgressMonitorWorker> toDo;
    private Consumer<ProgressMonitorWorker> onComplete;
    public ProgressMonitorWorker(BiConsumer<IProgressMonitor,ProgressMonitorWorker> toDo, Consumer<ProgressMonitorWorker> complete) {
        this.toDo = toDo;
        this.onComplete = complete;
    }
    public ProgressMonitorWorker(Consumer<IProgressMonitor> toDo, Runnable complete) {
        this.toDo = (iProgressMonitor, progressMonitorWorker) -> toDo.accept(iProgressMonitor);
        this.onComplete = progressMonitorWorker -> complete.run();
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
        },this);
        return null;
    }

    @Override
    protected void done() {
        onComplete.accept(this);
    }
}
