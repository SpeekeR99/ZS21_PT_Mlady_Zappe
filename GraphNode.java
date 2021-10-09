/**
 * Class represents generic GraphNode
 * is to be extended by Horse and Aircraft
 */
public class GraphNode {

    /** The x position in cartesian, the radius in polar coordinates */
    double x,
    /** The y position in cartesian, the angle in polar coordinates    */
            y;

    /**
     * Creates a GraphNode with position in a metric system (a map)
     * @param x the x coordinate in cartesian system, the radius in polar
     * @param y the y coordinate in cartesian system, the angle in polar
     */
    public GraphNode(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
