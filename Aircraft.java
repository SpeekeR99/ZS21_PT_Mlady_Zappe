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

    public Aircraft(MetricsDiety position, double weightCapacity, double speed) {
        super(position);
        this.weightCapacity = weightCapacity;
        this.speed = speed;
    }

}
