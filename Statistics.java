import java.util.AbstractList;

/**
 * Generator for statistics
 * Report from all airplanes, which horses when and where it took, statistics of weight capacity, time of flying and waiting
 * Report from all horses, where and how it travelled, how soon it was in Paris
 * Total time of all flights, all waitings and total time of moving
 */
public class Statistics {

    private AbstractList<GraphNode> nodesInOrder;

    public Statistics(AbstractList<GraphNode> nodesInOrder) {
        this.nodesInOrder = nodesInOrder;
    }

    public void doStuff() {
        int i = 0;
        for (GraphNode gn : nodesInOrder) {
            if (gn instanceof Aircraft) {
                i++;
            }
        }
        System.out.println(i);
    }

}
