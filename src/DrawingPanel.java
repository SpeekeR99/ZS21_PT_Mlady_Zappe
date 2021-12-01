package src;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Random;

/**
 * Simple visualization
 */
public class DrawingPanel extends JPanel {

    /**
     * Array of horses in order of being picked up, indexed by airplane
     */
    ArrayList<GraphNode>[] nodesInOrderOrderedByAirplane;
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
     * If being verbose in console is wanted or not
     */
    final boolean print;
    /**
     * Unique colors for unique airplanes
     */
    AbstractList<Color> uniqueColors;
    /**
     * Random for unique colors
     */
    Random rand;

    /**
     * Constructor sets needed values
     *
     * @param nodesInOrderOrderedByAirplane Array of horses in order of being picked up, indexed by airplane
     * @param print                         if being verbose is wanted
     */
    public DrawingPanel(ArrayList<GraphNode>[] nodesInOrderOrderedByAirplane, boolean print) {
        int width = 1000;
        int height = 1000;
        this.setPreferredSize(new Dimension(width, height));
        this.nodesInOrderOrderedByAirplane = nodesInOrderOrderedByAirplane;
        calculateMinMaxes();
        setRelatives(width, height);
        this.print = print;
        uniqueColors = new ArrayList<>();
        rand = new Random();
        setUpUniqueColorForDrawing();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(1));
        for (int i = 0; i < nodesInOrderOrderedByAirplane.length; i++) {
            g2.setColor(uniqueColors.get(i));
            drawFlight(g2, nodesInOrderOrderedByAirplane[i]);
        }
    }

    /**
     * Draws line between representing a flight
     *
     * @param g2           the graphics context
     * @param nodesInOrder Horses in order of being picked up by given airplane
     */
    public void drawFlight(Graphics2D g2, AbstractList<GraphNode> nodesInOrder) {
        for (int i = 0; i < nodesInOrder.size() - 1; i++) {
            GraphNode cur = nodesInOrder.get(i);
            GraphNode next = nodesInOrder.get(i + 1);
            Shape line = new Line2D.Double((cur.x - minX) * relativeX, (cur.y - minY) * relativeY, (next.x - minX) * relativeX, (next.y - minY) * relativeY);
            g2.draw(line);
            if (print) {
                System.out.println((cur instanceof Horse ? ((Horse) cur).index : "Paris") + " | x = " + cur.x + " y = " + cur.y);
            }
        }
    }

    /**
     * Sets up unique color for each unique aircraft
     */
    private void setUpUniqueColorForDrawing() {
        addBasicColors();
        while (nodesInOrderOrderedByAirplane.length > uniqueColors.size()) {
            uniqueColors.add(new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
        }
    }

    /**
     * Adds basic 15 colors to color palette
     */
    private void addBasicColors() {
        uniqueColors.add(new Color(255, 0, 0));
        uniqueColors.add(new Color(0, 255, 0));
        uniqueColors.add(new Color(0, 0, 255));
        uniqueColors.add(new Color(255, 255, 0));
        uniqueColors.add(new Color(255, 0, 255));
        uniqueColors.add(new Color(0, 255, 255));
        uniqueColors.add(new Color(255, 128, 0));
        uniqueColors.add(new Color(255, 0, 128));
        uniqueColors.add(new Color(128, 255, 0));
        uniqueColors.add(new Color(0, 255, 128));
        uniqueColors.add(new Color(128, 0, 255));
        uniqueColors.add(new Color(0, 128, 255));
        uniqueColors.add(new Color(255, 128, 128));
        uniqueColors.add(new Color(128, 255, 128));
        uniqueColors.add(new Color(128, 128, 255));
        uniqueColors.add(new Color(128, 128, 128));
    }

    /**
     * Calculates minimum and maximum values for drawing into small windows
     */
    private void calculateMinMaxes() {
        minX = Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        maxX = -Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;
        for (ArrayList<GraphNode> nodesInOrder : nodesInOrderOrderedByAirplane) {
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
