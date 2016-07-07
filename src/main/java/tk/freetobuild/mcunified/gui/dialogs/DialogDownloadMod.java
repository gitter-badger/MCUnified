package tk.freetobuild.mcunified.gui.dialogs;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import tk.freetobuild.mcunified.UnifiedMCInstance;
import tk.freetobuild.mcunified.Utils;
import tk.freetobuild.mcunified.curse.CurseArtifact;
import tk.freetobuild.mcunified.gui.workers.ProgressMonitorListener;
import tk.freetobuild.mcunified.gui.workers.ProgressMonitorWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class DialogDownloadMod extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JProgressBar progressBar1;
    private JLabel statusLabel;

    public DialogDownloadMod(UnifiedMCInstance instance, CurseArtifact artifact) {
        setPreferredSize(new Dimension(320, 120));
        Logger logger = Logger.getLogger("Download");
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                statusLabel.setText(record.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };
        logger.addHandler(handler);
        setContentPane(contentPane);
        setModal(true);
        buttonCancel.addActionListener(e ->
                onCancel());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        pack();
        new SwingWorker<List<CurseArtifact>, Void>() {
            @Override
            protected List<CurseArtifact> doInBackground() throws Exception {
                List<CurseArtifact> artifacts = new ArrayList<>();
                artifacts.add(artifact);
                artifacts.addAll(artifact.getAllDependencies());
                return artifacts;
            }

            @Override
            protected void done() {
                try {
                    progressBar1.setIndeterminate(false);
                    doDownloads(logger, handler, instance, get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private List<CurseArtifact> getArtifacts(UnifiedMCInstance instance, CurseArtifact artifact) {
        List<CurseArtifact> urls = new ArrayList<>();
        urls.add(artifact);
        urls.addAll(artifact.getAllDependencies());
        File outDir = new File(instance.getLocation(), "mods");
        if (!outDir.exists())
            outDir.mkdirs();
        return urls;
    }

    public void doDownloads(Logger logger, Handler handler, UnifiedMCInstance instance, List<CurseArtifact> artifacts) {
        long totalSize = artifacts.stream().mapToLong(a -> {
            try {
                return Utils.getFileSize(new URL(a.getDownload()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return 0;
            }
        }).sum();
        File outputDir = new File(instance.getLocation(), "mods");
        if (!outputDir.exists())
            outputDir.mkdirs();
        ProgressMonitorWorker worker = new ProgressMonitorWorker(iProgressMonitor -> {
            iProgressMonitor.setMax((int) (totalSize));
            for (int i = 0; i < artifacts.size(); i++) {
                CurseArtifact a = artifacts.get(i);
                iProgressMonitor.setStatus("Downloading " + a.getModId() + "(" + (i + 1) + "/" + artifacts.size() + ")");
                byte[] buf = new byte[4096];
                try {
                    InputStream is = new URL(a.getDownload()).openStream();
                    FileOutputStream fos = new FileOutputStream(new File(outputDir, a.getName()));
                    while (true) {
                        int r = is.read(buf);
                        if (r == -1)
                            break;
                        fos.write(buf, 0, r);
                        iProgressMonitor.incrementProgress(r);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, () -> {
            statusLabel.setText("Download Complete");
            buttonCancel.setText("Close");
            progressBar1.setVisible(false);
            logger.removeHandler(handler);
        });

        worker.addPropertyChangeListener(new ProgressMonitorListener(logger, progressBar1));
        worker.execute();
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonCancel = new JButton();
        buttonCancel.setText("Cancel");
        panel2.add(buttonCancel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        statusLabel = new JLabel();
        statusLabel.setText("Getting Downloads");
        panel3.add(statusLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        progressBar1 = new JProgressBar();
        progressBar1.setIndeterminate(true);
        panel3.add(progressBar1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
