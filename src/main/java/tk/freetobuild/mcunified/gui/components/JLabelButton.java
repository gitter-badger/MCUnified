package tk.freetobuild.mcunified.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by liz on 7/7/16.
 */
public class JLabelButton extends JLabel {
    public JLabelButton(ImageIcon image, ImageIcon hoverImage) {
        super();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setIcon(hoverImage);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setIcon(image);
            }
        });
        setIcon(image);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    public void addActionListener(ActionListener al) {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                al.actionPerformed(new ActionEvent(JLabelButton.this,-1,""));
            }
        });
    }
}
