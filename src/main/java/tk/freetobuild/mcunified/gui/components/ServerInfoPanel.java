package tk.freetobuild.mcunified.gui.components;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import tk.freetobuild.mcunified.ServerPinger;
import sk.tomsik68.mclauncher.api.servers.ServerInfo;
import sk.tomsik68.mclauncher.impl.servers.PingedServerInfo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

/**
 * Created by liz on 6/28/16.
 */
public class ServerInfoPanel {
    public JPanel panel1;
    private JLabel serverName;
    private JEditorPane servermotd;
    private JLabel playerCount;

    public ServerInfoPanel(ServerInfo info) {
        try {
            serverName.setText(info.getName());
            if (info.hasIcon())
                serverName.setIcon(new ImageIcon(ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(info.getIcon())))));
            servermotd.setText("Pinging");
            playerCount.setText("<html><span style=\"text-style: italic\">Unknown</span></html>");
            new SwingWorker<PingedServerInfo, Void>() {
                private int tries = 5;

                @Override
                protected PingedServerInfo doInBackground() throws Exception {
                    try {
                        tries--;
                        return ServerPinger.pingServer(info);
                    } catch (Exception ex) {
                        return null;
                    }

                }

                @Override
                protected void done() {
                    try {
                        PingedServerInfo ping = get();
                        if (ping == null) {
                            if (tries == 0)
                                servermotd.setText("Unable to Connect.");
                            else execute();
                        } else {
                            servermotd.setText("<html>" + ping.getMessage() + "</html>");
                            playerCount.setText(String.format("(%d/%d)", ping.getOnlinePlayers(), ping.getMaxPlayers()));
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    super.done();
                }
            }.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        serverName = new JLabel();
        serverName.setText("");
        panel1.add(serverName, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        servermotd = new JEditorPane();
        servermotd.setBackground(new Color(-12828863));
        servermotd.setContentType("text/html");
        servermotd.setEditable(false);
        servermotd.setText("<html>\n  <head>\n    \n  </head>\n  <body>\n  </body>\n</html>\n");
        panel1.add(servermotd, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        playerCount = new JLabel();
        playerCount.setText("");
        panel1.add(playerCount, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
