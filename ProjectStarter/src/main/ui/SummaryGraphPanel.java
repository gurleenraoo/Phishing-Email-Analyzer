package ui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/*
 * SummaryGraphPanel - Custom JPanel for drawing a pie chart.
 *
 * This panel displays the distribution of flagged emails by major phishing indicator.
 *
 * Requires: A valid Map<String, Double> containing indicator names as keys and their percentage values.
 * Modifies: The internal data representing the indicator percentages.
 * Effects: Draws a pie chart where each slice represents the percentage of flagged emails for a given indicator.
*/

public class SummaryGraphPanel extends JPanel {
    private Map<String, Double> data; // Data: indicator -> percentage

    /*
     * Updates the pie chart data and repaints the panel.
     *
     * Requires: data is a valid map (can be empty).
     * Modifies: Sets the internal data field.
     * Effects: Calls repaint() to redraw the pie chart.
     */
    public void setData(Map<String, Double> data) {
        this.data = data;
        repaint();
    }

    @Override
    @SuppressWarnings("methodlength") 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (data == null || data.isEmpty()) {
            g.drawString("No data to display.", 20, 20);
            return;
        }
        
        int width = getWidth();
        int height = getHeight();
        int diameter = Math.min(width, height) - 40;
        int x = (width - diameter) / 2;
        int y = (height - diameter) / 2;
        
        int startAngle = 0;
        Color[] colors = { Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK };
        int colorIndex = 0;
        
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double percentage = entry.getValue();
            int arcAngle = (int)Math.round((percentage / 100.0) * 360);
            g.setColor(colors[colorIndex % colors.length]);
            g.fillArc(x, y, diameter, diameter, startAngle, arcAngle);
            g.setColor(Color.BLACK);
            g.drawArc(x, y, diameter, diameter, startAngle, arcAngle);

            String labelText = entry.getKey() + " (" + String.format("%.1f", percentage) + "%)";
            g.drawString(labelText, x + 10, y + 20 + (colorIndex * 15));
           
            startAngle += arcAngle;
            colorIndex++;
        }
    }
}
