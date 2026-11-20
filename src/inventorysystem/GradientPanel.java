package inventorysystem;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class GradientPanel extends JPanel {

    private Color color1;
    private Color color2;
    private boolean vertical;

    public GradientPanel() {
        this(new Color(23, 43, 77), new Color(13, 148, 136), false);
    }

    public GradientPanel(Color color1, Color color2, boolean vertical) {
        this.color1 = color1;
        this.color2 = color2;
        this.vertical = vertical;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        GradientPaint gradient;

        if (vertical) {
            gradient = new GradientPaint(
                0, 0, color1,
                0, getHeight(), color2
            );
        } else {
            gradient = new GradientPaint(
                0, 0, color1,
                getWidth(), getHeight(), color2
            );
        }

        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}