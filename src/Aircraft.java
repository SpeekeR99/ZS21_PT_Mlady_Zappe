import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Class represents an src.Aircraft that exports horses
 * src.Aircraft has some position - x , y
 * src.Aircraft has a maximum weight it can take - m
 * src.Aircraft has some speed (velocity) - v
 */
public class Aircraft extends GraphNode {

    /**
     * maximum weight that the aircraft can take
     */
    public double weightCapacity;
    /**
     * current filled capacity (currently wielding weight)
     */
    double currCapacity;
    /**
     * speed of the aircraft (velocity)
     */
    public double speed;
    /**
     * Horses currently onboard (statistics)
     */
    public AbstractList<Horse> loadedHorses;
    /**
     * Time when airplane ended its flight (statistics)
     */
    public long endOfFlightTime;

    /**
     * Creates a new src.Aircraft object
     *
     * @param x              the x coordinate in cartesian system, the radius in polar
     * @param y              the y coordinate in cartesian system, the angle in polar
     * @param weightCapacity the capacity cap of the aircraft
     * @param speed          the aircraft's velocity
     */
    public Aircraft(double x, double y, double weightCapacity, double speed) {
        super(x, y);
        this.weightCapacity = weightCapacity;
        currCapacity = 0.0;
        this.speed = speed;
        this.loadedHorses = new ArrayList<>();
    }

    /**
     * Updates the aircraft's position (The plane flies to a different location)
     *
     * @param x new x coordinate (or radius)
     * @param y new y coordinate (or angle)
     */
    public void flyTo(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Plane tries to load a horse. If the plane can load the horse (current weight + added weight less or equal to capacity),
     * the plane loads it. Otherwise, it doesn't.
     *
     * @param weight the weight of the horse
     * @return true, if successfully loaded, false otherwise
     * @throws RuntimeException when the current capacity + weight > maximum capacity
     */
    public boolean loadHorse(double weight) {
        double w = currCapacity + weight;
        if (w > weightCapacity) {
            throw new RuntimeException("src.Aircraft tried to overload itself");
        }
        currCapacity = w;
        return true;
    }

    /**
     * Checks if the airplane is able to load a horse or not
     *
     * @param weight weight of the horse
     * @return true if airplane can load a horse, false otherwise
     */
    public boolean canLoadHorse(double weight) {
        return currCapacity + weight <= weightCapacity;
    }

    /**
     * Resets current capacity to 0
     */
    public void unload() {
        currCapacity = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Aircraft aircraft = (Aircraft) o;
        return Double.compare(aircraft.weightCapacity, weightCapacity) == 0 && Double.compare(aircraft.speed, speed) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), weightCapacity, speed);
    }

    /**
     * for testing purposes
     */
    @Override
    public String toString() {
        return "src.Aircraft{" +
                "speed=" + speed +
                '}';
    }
}
