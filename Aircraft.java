/**
 * Class represents an Aircraft that exports horses
 * Aircraft has some position - x , y
 * Aircraft has a maximum weight it can take - m
 * Aircraft has some speed (velocity) - v
 */
public class Aircraft extends GraphNode{

    /** maximum weight that the aircraft can take */
    public double weightCapacity;
    /** speed of the aircraft (velocity) */
    public double speed;

    /**
     * Creates a new Aircraft object
     * @param x the x coordinate in cartesian system, the radius in polar
     * @param y the y coordinate in cartesian system, the angle in polar
     * @param weightCapacity the capacity cap of the aircraft
     * @param speed the aircraft's velocity
     */
    public Aircraft(double x, double y, double weightCapacity, double speed) {
        super(x,y);
        this.weightCapacity = weightCapacity;
        this.speed = speed;
    }

}
