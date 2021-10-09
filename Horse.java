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

    public Horse(MetricsDiety position, double weight, double time) {
        super(position);
        this.weight = weight;
        this.time = time;
    }

}
