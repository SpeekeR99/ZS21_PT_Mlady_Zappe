package src;

import java.util.function.IntConsumer;

/**
 * Abstract Data Structures of Integers
 */
public interface IntADT {
    /**
     * adds the number to the collection
     *
     * @param i the number to add
     */
    void push(int i);

    /**
     * gets the current number
     *
     * @return the current number in the collection
     */
    int pop();

    /**
     * count
     *
     * @return number
     */
    int count();

    /**
     * Performs the action on all of the ADT's elements
     *
     * @param action the action to perform (as a lambda expression)
     */
    void foreach(IntConsumer action);
}

/**
 * Queue of Integers
 */
class IntQueue implements IntADT {
    /**
     * head
     */
    private IntADTEntry head;
    /**
     * tail
     */
    private IntADTEntry tail;
    /**
     * counter
     */
    private int count = 0;

    @Override
    public void push(int i) {
        IntADTEntry n = new IntADTEntry(i, null);
        if (count == 0) {
            head = n;
            tail = n;
        } else {
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
     *
     * @return same as <code>pop()</code>
     */
    public int getNext() {
        push(head.val);
        return pop();
    }

    /**
     * Performs the action on all of the ADT's elements
     *
     * @param action the action to perform (as a lambda expression)
     */
    @Override
    public void foreach(IntConsumer action) {
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
 * Entry
 */
class IntADTEntry {
    /**
     * value
     */
    int val;
    /**
     * next
     */
    IntADTEntry next;

    /**
     * Constructor for Integer entry
     *
     * @param val  value of entry
     * @param next pointer to next entry
     */
    public IntADTEntry(int val, IntADTEntry next) {
        this.val = val;
        this.next = next;
    }
}