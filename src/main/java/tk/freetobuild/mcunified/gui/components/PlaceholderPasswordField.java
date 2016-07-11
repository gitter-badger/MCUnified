package tk.freetobuild.mcunified.gui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Created by liz on 6/27/16.
 */
@SuppressWarnings("serial")
public class PlaceholderPasswordField extends JPasswordField {

    private String placeholder;

    public PlaceholderPasswordField() {
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        if (placeholder.length() == 0 || getPassword().length > 0) {
            return;
        }

        final Graphics2D g = (Graphics2D) pG;
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(getDisabledTextColor());
        g.drawString(placeholder, getInsets().left, pG.getFontMetrics()
                .getMaxAscent() + getInsets().top);
    }

    public void setPlaceholder() {
        placeholder = "Password";
    }

}
