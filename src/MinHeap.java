package src;

/**
 * a Min heap for the closest neighbour algorithm
 */
public class MinHeap {

    /**
     * heap
     */
    private final HeapElement[] heap;
    /**
     * count
     */
    private int count;

    /**
     * @param size number of nodes
     */
    public MinHeap(int size) {
        heap = new HeapElement[size + 1];
    }

    /**
     * adds an edge to the heap
     *
     * @param node   ending node
     * @param weight the weight of the edge
     */
    public void push(int node, double weight) {
        count++;
        heap[count] = new HeapElement(node, weight);
        siftUp(count);
    }

    /**
     * returns the edge with the minimum weight and removes it from the heap
     *
     * @return edge with the minimum weight and remove it from the heap
     */
    public HeapElement pop() {
        HeapElement r = heap[1];
        heap[1] = heap[count];
        count--;
        siftDown(1);
        return r;
    }

    /**
     * Correcting heap up
     *
     * @param n node
     */
    private void siftUp(int n) {
        if (n == 1) {
            return;
        }
        int p = n / 2;
        if (heap[p].weight() > heap[n].weight()) {
            swap(n, p);
            siftUp(p);
        }
    }

    /**
     * Correcting heap down
     *
     * @param n node
     */
    private void siftDown(int n) {
        int j;
        if ((j = 2 * n) <= count) {
            if (j + 1 <= count && heap[j + 1].weight() < heap[j].weight()) {
                j++;
            }
            if (heap[n].weight() < heap[j].weight()) {
                return;
            }
            swap(j, n);
            siftDown(j);
        }
    }

    /**
     * Swap two elements
     *
     * @param i element1
     * @param j element2
     */
    private void swap(int i, int j) {
        HeapElement tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }
}

/**
 * Element of a heap representing an edge:
 * knows the value of the ending node of the edge
 * knows the weight of the edge
 */
record HeapElement(int node, double weight) {
}