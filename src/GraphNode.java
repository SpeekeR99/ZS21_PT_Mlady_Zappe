package src;

import java.util.Objects;

/**
 * Class represents generic src.GraphNode
 * is to be extended by src.Horse and src.Aircraft
 */
public class GraphNode {

    /**
     * The x position in cartesian, the radius in polar coordinates
     */
    double x,
    /**
     * The y position in cartesian, the angle in polar coordinates
     */
    y;

    /**
     * Creates a src.GraphNode with position in a metric system (a map)
     *
     * @param x the x coordinate in cartesian system, the radius in polar
     * @param y the y coordinate in cartesian system, the angle in polar
     */
    public GraphNode(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GraphNode graphNode = (GraphNode) o;
        return Double.compare(graphNode.x, x) == 0 && Double.compare(graphNode.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
