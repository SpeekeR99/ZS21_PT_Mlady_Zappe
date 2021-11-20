package src;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.AbstractList;

/**
 * Simple visualization
 */
public class DrawingPanel extends JPanel {

    /**
     * Horses in order of being picked up
     */
    AbstractList<GraphNode> nodesInOrder;
    /**
     * Minimal value of X
     */
    double minX;
    /**
     * Maximal value of X
     */
    double maxX;
    /**
     * Minimal value of Y
     */
    double minY;
    /**
     * Maximal value of Y
     */
    double maxY;
    /**
     * Ratio of map X and screen window size X
     */
    double relativeX;
    /**
     * Ratio of map Y and screen window size Y
     */
    double relativeY;
    /**
     * Index of current horse
     */
    int curHorseIndex;
    /**
     * If being verbose in console is wanted or not
     */
    final boolean print;

    /**
     * Constructor sets needed values
     *
     * @param nodesInOrder Horses in order of being picked up
     * @param print        if being verbose is wanted
     */
    public DrawingPanel(AbstractList<GraphNode> nodesInOrder, boolean print) {
        int width = 1000;
        int height = 1000;
        this.setPreferredSize(new Dimension(width, height));
        this.nodesInOrder = nodesInOrder;
        calculateMinMaxes();
        setRelatives(width, height);
        curHorseIndex = 0;
        this.print = print;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        g2.setFont(new Font("Times New Roman", Font.PLAIN, 21));
        g2.drawString("START", (float) (nodesInOrder.get(0).x - minX * relativeX), (float) (nodesInOrder.get(0).y - minY * relativeY));
    }

    /**
     * Draws line between representing a flight
     *
     * @param g the graphics context
     * @return true, if al flights are drawn, else returns false
     */
    public boolean drawFlight(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.RED);

        curHorseIndex++;
        if (curHorseIndex >= nodesInOrder.size() - 1) {
            return true;
        }

        GraphNode cur = nodesInOrder.get(curHorseIndex);
        GraphNode next = nodesInOrder.get(curHorseIndex + 1);
        Shape line = new Line2D.Double((cur.x - minX) * relativeX, (cur.y - minY) * relativeY, (next.x - minX) * relativeX, (next.y - minY) * relativeY);
        g2.draw(line);
        if (print) {
            System.out.println((cur instanceof Horse ? ((Horse) cur).index : "Paris") + " | x = " + cur.x + " y = " + cur.y);
        }

        return false;
    }

    /**
     * Calculates minimum and maximum values for drawing into small windows
     */
    private void calculateMinMaxes() {
        minX = Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        maxX = -Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;
        for (GraphNode h : nodesInOrder) {
            if (h.x > maxX) {
                maxX = h.x;
            }
            if (h.x < minX) {
                minX = h.x;
            }
            if (h.y > maxY) {
                maxY = h.y;
            }
            if (h.y < minY) {
                minY = h.y;
            }
        }
        if (!print) {
            return;
        }
        System.out.println("minX = " + minX);
        System.out.println("maxX = " + maxX);
        System.out.println("minY = " + minY);
        System.out.println("maxY = " + maxY);
    }

    /**
     * Relative X and Y are just ratio's between the width and height of actual map and the small window
     * so the drawing would fit in the window
     *
     * @param thiswidth  width of window
     * @param thisheight height of window
     */
    private void setRelatives(int thiswidth, int thisheight) {
        double width = maxX - minX;
        double height = maxY - minY;
        relativeX = thiswidth / width;
        relativeY = thisheight / height;
        if (!print) {
            return;
        }
        System.out.println("relativeX = " + relativeX);
        System.out.println("relativeY = " + relativeY);
    }
}
