import java.util.function.IntConsumer;

public interface IntADT{
    /**
     * adds the number to the collection
     * @param i the number to add
     */
    void push(int i);

    /**
     * gets the current number
     * @return the current number in the collection
     */
    int pop();

    int count();

    /**
     * Performs the action on all of the ADT's elements
     * @param action the action to perform (as a lambda expression)
     */
    void foreach(IntConsumer action);
}

class IntStack implements IntADT{

    private IntADTEntry head;
    private int count = 0;

    @Override
    public void push(int i){
        head = new IntADTEntry(i, head);
        count++;
    }
    @Override
    public int pop(){
        int r = head.val;
        head = head.next;
        count--;
        return r;
    }

    @Override
    public int count() {
        return count;
    }

    /**
     * Performs the action on all of the ADT's elements
     * @param action the action to perform (as a lambda expression)
     */
    @Override
    public void foreach(IntConsumer action){
        IntADTEntry cur = head;
        for (int i = 0; i < count; i++) {
            action.accept(cur.val);
            cur = cur.next;
        }
    }
}

class IntQueue implements IntADT{
    private IntADTEntry head;
    private IntADTEntry tail;

    private int count = 0;

    @Override
    public void push(int i) {
        IntADTEntry n = new IntADTEntry(i,null);
        if(count==0){
            head = n;
            tail = n;
        }
        else{
            tail.next = n;
            tail = n;
        }

        count++;
    }

    @Override
    public int pop() {
        int r = head.val;
        head = head.next;
        count--;
        return r;
    }

    /**
     * Pushes the head value and then pops it, allowing for iterating over the Queue
     * @return same as <code>pop()</code>
     */
    public int getNext(){
        push(head.val);
        return pop();
    }

    /**
     * Performs the action on all of the ADT's elements
     * @param action the action to perform (as a lambda expression)
     */
    @Override
    public void foreach(IntConsumer action){
        for (int i = 0; i < count; i++) {
            action.accept(getNext());
        }
    }

    @Override
    public int count() {
        return count;
    }
}

/**
 * a Min heap for the closest neighbour algorithm
 */
class MinHeap{

    private final HeapElement[] heap;
    private int count;

    /**
     * @param size number of nodes
     */
    public MinHeap(int size){
        heap = new HeapElement[size+1];
    }

    /**
     * adds an edge to the heap
     * @param node ending node
     * @param weight the weight of the edge
     */
    public void push(int node, double weight) {
        count++;
        heap[count] = new HeapElement(node, weight);
        siftUp(count);
    }

    /**
     * returns the edge with the minimum weight and removes it from the heap
     */
    public HeapElement pop() {
        HeapElement r = heap[1];
        heap[1] = heap[count];
        count--;
        siftDown(1);
        return r;
    }

    /**
     * like <code>pop()</code>, but does not remove the min
     * @return the edge with the min weight
     */
    public HeapElement getMin(){
        return heap[1];
    }

    /**
     * @return the number of edges stored in the heap
     */
    public int count() {
        return count;
    }
    public int size() {return heap.length-1;}

    private void siftUp(int n){
        if(n==1) return;
        int p = n/2;
        if(heap[p].weight>heap[n].weight){
            swap(n, p);
            siftUp(p);
        }
    }
    private void siftDown(int n){
        int j;
        if((j = 2*n)<=count){
            if(j+1<=count && heap[j+1].weight<heap[j].weight)
                j++;
            if(heap[n].weight<heap[j].weight)
                return;
            swap(j,n);
            siftDown(j);
        }
    }
    private void swap(int i, int j){
        HeapElement tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }
}

class IntADTEntry {
    int val;
    IntADTEntry next;

    public IntADTEntry(int val, IntADTEntry next) {
        this.val = val;
        this.next = next;
    }
}

/**
 * Element of a heap representing an edge:
 * knows the value of the ending node of the edge
 * knows the weight of the edge
 */
class HeapElement{
    /**
     * final node in the edge
     * i. e. in the edge (i,j), node=j
     */
    final int node;
    /**
     * weight of the edge
     */
    final double weight;

    public HeapElement(int node, double weight) {
        this.node = node;
        this.weight = weight;
    }
}