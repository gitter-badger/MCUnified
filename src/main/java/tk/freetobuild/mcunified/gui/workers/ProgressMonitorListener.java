package tk.freetobuild.mcunified.gui.workers;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

/**
 * Created by liz on 6/30/16.
 */
public class ProgressMonitorListener implements PropertyChangeListener {
    private final JProgressBar status;
    private final Logger logger;
    public ProgressMonitorListener(Logger logger, JProgressBar status) {
        this.logger = logger;
        this.status = status;
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equalsIgnoreCase("note")) {
            logger.info((String) evt.getNewValue());
        } else if(evt.getPropertyName().equalsIgnoreCase("progress")) {
            status.setValue((Integer) evt.getNewValue());
        } else if(evt.getPropertyName().equalsIgnoreCase("error")) {
            JOptionPane.showMessageDialog(null,evt.getNewValue(),"Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
