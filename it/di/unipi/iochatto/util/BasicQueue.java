/* FileName: it/di/unipi/iochatto/util/BasicQueue.java Date: 2006/09/13 22:01
*IoChatto - P2P Final Term 
* @author Giorgio Zoppi
* @author zoppi@cli.di.unipi.it

*/
package it.di.unipi.iochatto.util;
import java.util.*;

/**
 * A bounded {@linkplain BlockingQueue blocking queue} backed by an
 * array.  This queue orders elements FIFO (first-in-first-out).  The
 * <em>head</em> of the queue is that element that has been on the
 * queue the longest time.  The <em>tail</em> of the queue is that
 * element that has been on the queue the shortest time. New elements
 * are inserted at the tail of the queue, and the queue retrieval
 * operations obtain elements at the head of the queue.
 *
 * <p>This is a classic &quot;bounded buffer&quot;, in which a
 * fixed-sized array holds elements inserted by producers and
 * extracted by consumers.  Once created, the capacity cannot be
 * increased.  Attempts to put an element to a full queue will
 * result in the put operation blocking; attempts to retrieve an
 * element from an empty queue will similarly block.
 *
 * <p> This class supports an optional fairness policy for ordering
 * waiting producer and consumer threads.  By default, this ordering
 * is not guaranteed. However, a queue constructed with fairness set
 * to <tt>true</tt> grants threads access in FIFO order. Fairness
 * generally decreases throughput but reduces variability and avoids
 * starvation.
 *
 * <p>This class and its iterator implement all of the
 * <em>optional</em> methods of the {@link Collection} and {@link
 * Iterator} interfaces.
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../guide/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @since 1.5
 * @author Doug Lea
 * @param <E> the type of elements held in this collection
 */
public class BasicQueue<E> extends AbstractQueue<E>
        implements java.io.Serializable {

    /**
     * Serialization ID. This class relies on default serialization
     * even for the items array, which is default-serialized, even if
     * it is empty. Otherwise it could not be declared final, which is
     * necessary here.
     */
   
    /**
	 * 
	 */
	private static final long serialVersionUID = 5389190949917117988L;
	/** The queued items  */
    private E[] items;
    /** items index for next take, poll or remove */
    private transient int takeIndex;
    /** items index for next put, offer, or add. */
    private transient int putIndex;
    /** Number of items in the queue */
    private int count;

    /*
     * Concurrency control uses the classic two-condition algorithm
     * found in any textbook.
     */

    // Internal helper methods

    /**
     * Circularly increment i.
     */
    final int inc(int i) {
        return (++i == items.length)? 0 : i;
    }

    /**
     * Insert element at current put position, advance, and signal.
     * Call only when holding lock.
     */
    private void insert(E x) {
        items[putIndex] = x;
        putIndex = inc(putIndex);
        ++count;
    	}
  
    public E head() {
    	if (count == 0)
    	    	return null;
       	return items[takeIndex];
    }
    

    /**
     * Extract element at current take position, advance, and signal.
     * Call only when holding lock.
     */
    private E extract() {
        final E[] items = this.items;
        E x = items[takeIndex];
        items[takeIndex] = null;
        takeIndex = inc(takeIndex);
        --count;
  //      notFull.signal();
        return x;
    }

    /**
     * Utility for remove and iterator.remove: Delete item at position i.
     * Call only when holding lock.
     */
    void removeAt(int i) {
        final E[] items = this.items;
        // if removing front item, just advance
        if (i == takeIndex) {
            items[takeIndex] = null;
            takeIndex = inc(takeIndex);
        } else {
            // slide over all others up through putIndex.
            for (;;) {
                int nexti = inc(i);
                if (nexti != putIndex) {
                    items[i] = items[nexti];
                    i = nexti;
                } else {
                    items[i] = null;
                    putIndex = i;
                    break;
                }
            }
        }
        --count;
    //    notFull.signal();
    }

    /**
     * Creates an <tt>ArrayBlockingQueue</tt> with the given (fixed)
     * capacity and default access policy.
     * @param capacity the capacity of this queue
     * @throws IllegalArgumentException if <tt>capacity</tt> is less than 1
     */
    public BasicQueue(int capacity) {
        this(capacity, false);
    }

    /**
     * Creates an <tt>ArrayBlockingQueue</tt> with the given (fixed)
     * capacity and the specified access policy.
     * @param capacity the capacity of this queue
     * @param fair if <tt>true</tt> then queue accesses for threads blocked
     * on insertion or removal, are processed in FIFO order; if <tt>false</tt>
     * the access order is unspecified.
     * @throws IllegalArgumentException if <tt>capacity</tt> is less than 1
     */
    public BasicQueue(int capacity, boolean fair) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        this.items = (E[]) new Object[capacity];
       
    }

    /**
     * Creates an <tt>ArrayBlockingQueue</tt> with the given (fixed)
     * capacity, the specified access policy and initially containing the
     * elements of the given collection,
     * added in traversal order of the collection's iterator.
     * @param capacity the capacity of this queue
     * @param fair if <tt>true</tt> then queue accesses for threads blocked
     * on insertion or removal, are processed in FIFO order; if <tt>false</tt>
     * the access order is unspecified.
     * @param c the collection of elements to initially contain
     * @throws IllegalArgumentException if <tt>capacity</tt> is less than
     * <tt>c.size()</tt>, or less than 1.
     * @throws NullPointerException if <tt>c</tt> or any element within it
     * is <tt>null</tt>
     */
    public BasicQueue(int capacity, boolean fair,
                              Collection<? extends E> c) {
        this(capacity, fair);
        if (capacity < c.size())
            throw new IllegalArgumentException();

        for (Iterator<? extends E> it = c.iterator(); it.hasNext();)
            add(it.next());
    }

    /**
     * Inserts the specified element at the tail of this queue if possible,
     * returning immediately if this queue is full.
     *
     * @param o the element to add.
     * @return <tt>true</tt> if it was possible to add the element to
     *         this queue, else <tt>false</tt>
     * @throws NullPointerException if the specified element is <tt>null</tt>
     */
    public boolean offer(E o) {
        if (o == null) throw new NullPointerException();
        try {
            if (count == items.length)
                return false;
            else {
                insert(o);
                return true;
            }
        } finally {
        }
    }

    /**
     * Inserts the specified element at the tail of this queue, waiting if
     * necessary up to the specified wait time for space to become available.
     * @param o the element to add
     * @param timeout how long to wait before giving up, in units of
     * <tt>unit</tt>
     * @param unit a <tt>TimeUnit</tt> determining how to interpret the
     * <tt>timeout</tt> parameter
     * @return <tt>true</tt> if successful, or <tt>false</tt> if
     * the specified waiting time elapses before space is available.
     * @throws InterruptedException if interrupted while waiting.
     * @throws NullPointerException if the specified element is <tt>null</tt>.
     */
    

    public E poll() {
          try {
            if (count == 0)
                return null;
            E x = extract();
            return x;
        } finally {
        }
    }

        /**
     * Removes a single instance of the specified element from this
     * queue, if it is present.
     */
    public boolean remove(Object o) {
        if (o == null) return false;
        final E[] items = this.items;
         try {
            int i = takeIndex;
            int k = 0;
            for (;;) {
                if (k++ >= count)
                    return false;
                if (o.equals(items[i])) {
                    removeAt(i);
                    return true;
                }
                i = inc(i);
            }

        } finally {
        }
    }

    public E peek() {
        
        try {
            return (count == 0) ? null : items[takeIndex];
        } finally {
       }
    }

    
    /**
     * Adds the specified element to the tail of this queue, waiting if
     * necessary for space to become available.
     * @param o the element to add
     * @throws InterruptedException if interrupted while waiting.
     * @throws NullPointerException if the specified element is <tt>null</tt>.
     */
    public void put(E o) {
        if (o == null) throw new NullPointerException();
        final E[] items = this.items;
        if (count == items.length)
        {
        	  E[] tmpobj = (E[]) new Object[count*2];
              System.arraycopy(items, 0, tmpobj, 0, items.length);
              this.items = null;
              this.items = tmpobj;
        }
        insert(o);
    }

    // this doc comment is overridden to remove the reference to collections
    // greater in size than Integer.MAX_VALUE
    /**
     * Returns the number of elements in this queue.
     *
     * @return  the number of elements in this queue.
     */
    public int size() {
        try {
            return count;
        } finally {
        }
    }

    // this doc comment is a modified copy of the inherited doc comment,
    // without the reference to unlimited queues.
    /**
     * Returns the number of elements that this queue can ideally (in
     * the absence of memory or resource constraints) accept without
     * blocking. This is always equal to the initial capacity of this queue
     * less the current <tt>size</tt> of this queue.
     * <p>Note that you <em>cannot</em> always tell if
     * an attempt to <tt>add</tt> an element will succeed by
     * inspecting <tt>remainingCapacity</tt> because it may be the
     * case that a waiting consumer is ready to <tt>take</tt> an
     * element out of an otherwise full queue.
     */
    public int remainingCapacity() {
        try {
            return items.length - count;
        } finally {
        }
    }


    public boolean contains(Object o) {
        if (o == null) return false;
        final E[] items = this.items;
        try {
            int i = takeIndex;
            int k = 0;
            while (k++ < count) {
                if (o.equals(items[i]))
                    return true;
                i = inc(i);
            }
            return false;
        } finally {
        }
    }

    public Object[] toArray() {
        final E[] items = this.items;
        try {
            Object[] a = new Object[count];
            int k = 0;
            int i = takeIndex;
            while (k < count) {
                a[k++] = items[i];
                i = inc(i);
            }
            return a;
        } finally {
        }
    }

    public <T> T[] toArray(T[] a) {
        final E[] items = this.items;
        try {
            if (a.length < count)
                a = (T[])java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(),
                    count
                    );

            int k = 0;
            int i = takeIndex;
            while (k < count) {
                a[k++] = (T)items[i];
                i = inc(i);
            }
            if (a.length > count)
                a[count] = null;
            return a;
        } finally {
        }
    }

    public String toString() {
        try {
            return super.toString();
        } finally {
        }
    }


    /**
     * Atomically removes all of the elements from this queue.
     * The queue will be empty after this call returns.
     */
    public void clear() {
        final E[] items = this.items;
        try {
            int i = takeIndex;
            int k = count;
            while (k-- > 0) {
                items[i] = null;
                i = inc(i);
            }
            count = 0;
            putIndex = 0;
            takeIndex = 0;
        } finally {
        }
    }

    public int drainTo(Collection<? super E> c) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        final E[] items = this.items;
        try {
            int i = takeIndex;
            int n = 0;
            int max = count;
            while (n < max) {
                c.add(items[i]);
                items[i] = null;
                i = inc(i);
                ++n;
            }
            if (n > 0) {
                count = 0;
                putIndex = 0;
                takeIndex = 0;
            }
            return n;
        } finally {
        }
    }


    public int drainTo(Collection<? super E> c, int maxElements) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        if (maxElements <= 0)
            return 0;
        final E[] items = this.items;
        try {
            int i = takeIndex;
            int n = 0;
            int sz = count;
            int max = (maxElements < count)? maxElements : count;
            while (n < max) {
                c.add(items[i]);
                items[i] = null;
                i = inc(i);
                ++n;
            }
            if (n > 0) {
                count -= n;
                takeIndex = i;
            }
            return n;
        } finally {
        }
    }


    /**
     * Returns an iterator over the elements in this queue in proper sequence.
     * The returned <tt>Iterator</tt> is a "weakly consistent" iterator that
     * will never throw {@link java.util.ConcurrentModificationException},
     * and guarantees to traverse elements as they existed upon
     * construction of the iterator, and may (but is not guaranteed to)
     * reflect any modifications subsequent to construction.
     *
     * @return an iterator over the elements in this queue in proper sequence.
     */
    public Iterator<E> iterator() {
        try {
            return new Itr();
        } finally {
        }
    }

    /**
     * Iterator for BasicQueue
     */
    private class Itr implements Iterator<E> {
        /**
         * Index of element to be returned by next,
         * or a negative number if no such.
         */
        private int nextIndex;

        /**
         * nextItem holds on to item fields because once we claim
         * that an element exists in hasNext(), we must return it in
         * the following next() call even if it was in the process of
         * being removed when hasNext() was called.
         **/
        private E nextItem;

        /**
         * Index of element returned by most recent call to next.
         * Reset to -1 if this element is deleted by a call to remove.
         */
        private int lastRet;

        Itr() {
            lastRet = -1;
            if (count == 0)
                nextIndex = -1;
            else {
                nextIndex = takeIndex;
                nextItem = items[takeIndex];
            }
        }

        public boolean hasNext() {
            /*
             * No sync. We can return true by mistake here
             * only if this iterator passed across threads,
             * which we don't support anyway.
             */
            return nextIndex >= 0;
        }

        /**
         * Check whether nextIndex is valid; if so setting nextItem.
         * Stops iterator when either hits putIndex or sees null item.
         */
        private void checkNext() {
            if (nextIndex == putIndex) {
                nextIndex = -1;
                nextItem = null;
            } else {
                nextItem = items[nextIndex];
                if (nextItem == null)
                    nextIndex = -1;
            }
        }

        public E next() {
            try {
                if (nextIndex < 0)
                    throw new NoSuchElementException();
                lastRet = nextIndex;
                E x = nextItem;
                nextIndex = inc(nextIndex);
                checkNext();
                return x;
            } finally {
            }
        }

        public void remove() {
            try {
                int i = lastRet;
                if (i == -1)
                    throw new IllegalStateException();
                lastRet = -1;

                int ti = takeIndex;
                removeAt(i);
                // back up cursor (reset to front if was first element)
                nextIndex = (i == ti) ? takeIndex : i;
                checkNext();
            } finally {
            }
        }
    }
}


