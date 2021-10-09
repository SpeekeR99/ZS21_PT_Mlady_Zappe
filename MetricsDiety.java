/**
 * A diety that lives in a metrics system
 * (a space, where distance function exists)
 */
public interface MetricsDiety {

    /**
     * Distance from this diety to some other
     * @param other the diety, to which measure the distance
     * @return distance between this and other
     */
    double dist(MetricsDiety other) throws IllegalArgumentException;
}

class CaresianDiety implements MetricsDiety{
    final double x, y;

    public CaresianDiety(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public double dist(MetricsDiety other) throws IllegalArgumentException {
        if(other instanceof CaresianDiety){
            CaresianDiety b = (CaresianDiety) other;
            double difX = this.x- b.x;
            double difY = this.y-b.y;
            return Math.sqrt(difX*difX+difY*difY);
        }
        else throw new IllegalArgumentException("Dieties must be of same type");
    }
}

class PolarDiety implements MetricsDiety{
    final double r, phi;

    public PolarDiety(double r, double phi) {
        this.r = r;
        this.phi = phi;
    }

    @Override
    public double dist(MetricsDiety other) throws IllegalArgumentException {
        return 0;
    }
}
