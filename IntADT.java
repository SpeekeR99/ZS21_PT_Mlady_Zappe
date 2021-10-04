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
}

class IntQueue implements IntADT{
    private IntADTEntry head;
    private IntADTEntry tail;

    private int count = 0;

    @Override
    public void push(int i) {
        IntADTEntry n = new IntADTEntry(i,tail);
        tail.next = n;
        tail = n;
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
     * Performs the action on all of the Queue's elements
     * this method is here just to FLEX
     * @param action the action to perform (as a lambda expression)
     */
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

class IntADTEntry {
    int val;
    IntADTEntry next;

    public IntADTEntry(int val, IntADTEntry next) {
        this.val = val;
        this.next = next;
    }
}