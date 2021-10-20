/**
 * Class represents an Aircraft that exports horses
 * Aircraft has some position - x , y
 * Aircraft has a maximum weight it can take - m
 * Aircraft has some speed (velocity) - v
 */
public class Aircraft extends GraphNode{

    /** maximum weight that the aircraft can take */
    public double weightCapacity;
    double currCapacity;
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
        currCapacity = 0.0;
        this.speed = speed;
    }

    /**
     * Updates the aircraft's position (The plane flies to a different location)
     * @param x new x coordinate (or radius)
     * @param y new y coordinate (or angle)
     */
    public void flyTo(double x, double y){
        this.x = x;
        this.y = y;
    }

}
