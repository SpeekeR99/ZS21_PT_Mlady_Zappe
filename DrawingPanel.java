import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 * Simple visualization
 */
public class DrawingPanel extends JPanel {

    ArrayList<Horse> horsesInOrder;
    double minX, maxX, minY, maxY;
    double relativeX, relativeY;

    public DrawingPanel(ArrayList<Horse> horsesInOrder) {
        int width = 1000;
        int height = 1000;
        this.setPreferredSize(new Dimension(width, height));
        this.horsesInOrder = horsesInOrder;
        calculateMinMaxes();
        setRelatives(width, height);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(1));
        g2.setFont(new Font("Times New Roman", Font.PLAIN, 21));
        g2.drawString("START", (float)(horsesInOrder.get(0).x - minX * relativeX), (float)(horsesInOrder.get(0).y - minY * relativeY));
        g2.setColor(Color.RED);
        for(int i = 0; i < horsesInOrder.size() - 1; i++) {
            Horse cur = horsesInOrder.get(i);
            Horse next = horsesInOrder.get(i + 1);
            Shape line = new Line2D.Double((cur.x - minX) * relativeX, (cur.y - minY) * relativeY, (next.x - minX) * relativeX, (next.y - minY) * relativeY);
            g2.draw(line);
            System.out.println(cur.index + " | x = " + cur.x + " y = " + cur.y);
        }
        g2.setColor(Color.BLACK);
        g2.drawString("START", (float)(horsesInOrder.get(0).x - minX * relativeX), (float)(horsesInOrder.get(0).y - minY * relativeY));
    }

    private void calculateMinMaxes() {
        minX = Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        maxX = Double.MIN_VALUE;
        maxY = Double.MIN_VALUE;
        for (Horse h : horsesInOrder) {
            if (h.x > maxX) maxX = h.x;
            if (h.x < minX) minX = h.x;
            if (h.y > maxY) maxY = h.y;
            if (h.y < minY) minY = h.y;
        }
        System.out.println("minX = " + minX);
        System.out.println("maxX = " + maxX);
        System.out.println("minY = " + minY);
        System.out.println("maxY = " + maxY);
    }

    private void setRelatives(int thiswidth, int thisheight) {
        double width = maxX - minX;
        double height = maxY - minY;
        relativeX = thiswidth / width;
        relativeY = thisheight / height;
        System.out.println("relativeX = " + relativeX);
        System.out.println("relativeY = " + relativeY);
    }
}
