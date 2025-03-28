/**
 * A diety that lives in a metrics system
 * (a space, where distance function exists)
 */
public interface DistFunction {

    /**
     * Distance between two GraphNodes
     *
     * @param a Point a
     * @param b Point b
     * @return distance
     */
    double dist(GraphNode a, GraphNode b);
}

/**
 * Cartesian X, Y 2D
 */
class CartesianDist implements DistFunction {

    @Override
    public double dist(GraphNode a, GraphNode b) throws IllegalArgumentException {
        double difX = a.x - b.x;
        double difY = a.y - b.y;
        return Math.sqrt(difX * difX + difY * difY);
    }
}

