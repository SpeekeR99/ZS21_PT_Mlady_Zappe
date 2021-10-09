/**
 * Class represents a horse
 * Horse has some position - x , y
 * Horse also weights something - m
 * It takes some time to get a horse onboard - n
 */
public class Horse extends GraphNode {

    /** weight of the horse and it's equipment */
    public double weight;
    /** time taken to get onboard / off the aircraft */
    public double time;

    /**
     * Creates a new Horse object
     * @param x the x coordinate in cartesian system, the radius in polar
     * @param y the y coordinate in cartesian system, the angle in polar
     * @param weight the horse's weight
     * @param time the time required to load the horse (and its stuff) onto an aircraft
     */
    public Horse(double x, double y, double weight, double time) {
        super(x,y);
        this.weight = weight;
        this.time = time;
    }

}
