import java.util.Comparator;

/**
 * Compares two <code>GraphNode</code>s based on their angle and distance
 * from another (reference) <code>GraphNode</code>. The reference node has to be set before comparing!
 * <br><br>
 * Main idea: all points that have similar angle from the reference node are close to each other.
 * If they share the same angle, then they're close, if their distances from the reference node is similar.
 * The angle has to be from the whole range, so [-PI,+PI] is sufficient (since the "length" of the interval is
 * the length of the period, which is 2*PI) - <code>Math.atan2(x,y)</code> does exactly what needed
 *
 * <strong>The reference node should be paris</strong>
 */
public class CartesianDistComparator implements Comparator<GraphNode> {
    /**
     * reference X
     */
    double refX;
    /**
     * reference Y
     */
    double refY;
    /**
     * Accuracy
     */
    double compAccuracy = .000_000_1;

    @Override
    public int compare(GraphNode o1, GraphNode o2) {
        int ret;
        double angleDif = angle(o1.y, o1.x) - angle(o2.y, o2.x);
        double distDif = distance(o1.x, o1.y) - distance(o2.x, o2.y);

        if (Math.abs(angleDif) > compAccuracy) {
            ret = angleDif > 0 ? 1 : -1;
        } else {
            //angles are basically the same, so order them by distances to paris


            if (Math.abs(distDif) > compAccuracy) {
                ret = distDif > 0 ? 1 : -1;
            } else {
                //nodes are basically the same
                ret = 0;
            }
        }
        if (o1 instanceof Horse && o2 instanceof Horse && ret == 0) {
            ret = ((Horse) o1).index - ((Horse) o2).index;
        }

        return ret;

    }

    /**
     * sets the reference node (should be paris)
     *
     * @param paris the reference node
     */
    public void setReferenceNode(GraphNode paris) {
        this.refX = paris.x;
        this.refY = paris.y;
    }

    /**
     * sets the accuracy when comparing two floating points<br>
     * <code>Math.abs(double1 - double2) is less than accuracy</code><br>
     * default: .0000001
     *
     * @param accuracy .
     */
    public void setAccuracy(double accuracy) {
        compAccuracy = accuracy;
    }

    /**
     * @param y .
     * @param x .
     * @return angle between point at [x,y] and paris
     */
    private double angle(double y, double x) {
        return Math.atan2(y - refY, x - refX);
    }

    /**
     * @param x .
     * @param y .
     * @return distance between point at [x,y] and paris
     */
    private double distance(double x, double y) {
        double dx = refX - x, dy = refY - y;
        return dx * dx + dy * dy;
    }

}
