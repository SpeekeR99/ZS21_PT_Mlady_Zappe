/**
 * A diety that lives in a metrics system
 * (a space, where distance function exists)
 */
public interface DistFunction {

    /**
     * Distance between two GraphNodes
     * @return distance
     */
    double dist(GraphNode a, GraphNode b);
}

class CartesianDist implements DistFunction {

    @Override
    public double dist(GraphNode a, GraphNode b) throws IllegalArgumentException {
            double difX = a.x - b.x;
            double difY = a.y - b.y;
            return Math.sqrt(difX*difX+difY*difY);
    }
}

class PolarDist implements DistFunction {
    @Override
    public double dist(GraphNode a, GraphNode b) {
        return 0;
    }
}
