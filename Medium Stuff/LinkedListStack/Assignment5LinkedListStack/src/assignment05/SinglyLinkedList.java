package assignment05;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SinglyLinkedList<E> implements List<E> {


    // Member Variables


    // head points to the first node in the list
    private linkedNode<E> head = null;
    // track the size of the list
    private int size = 0;


    // Constructor for SinglyLinkedList


    /**
     * Default, zero parameter constructor
     */
    public SinglyLinkedList(){
    }


    // Node class so we can create nodes to create a linked list

    /**
     * Nested node class
     */
    private class linkedNode<E> {
        // data belonging to node
        E data;
        // pointer to next node
        linkedNode<E> next;

        // constructor to create node
        linkedNode(E data, linkedNode<E> next) {
            this.data = data;
            this.next = next;
        }
    }



    // Iterator Class to traverse our linked list


    /**
     * Nested iterator class
     */
    private class myIterator implements Iterator<E> {

        // member variable to track current node
        public linkedNode<E> current = head;
        // member variable to track index position in linked list
        public int index = -1;
        private boolean nextCalled = false;

        /**
         * Returns {@code true} if the iteration has more elements.
         * (In other words, returns {@code true} if {@link #next} would
         * return an element rather than throwing an exception.)
         *
         * @return {@code true} if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return (current != null && current.next != null);
        }

        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        public E next() {
            // check that there is a next node
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            // iterate to next item
            current = current.next;
            //increment index
            index++;
            nextCalled = true;
            // return data from the current (now previous) element
            return current.data;
        }

        /**
         * Removes from the underlying collection the last element returned
         * by this iterator (optional operation).  This method can be called
         * only once per call to {@link #next}.
         * <p>
         * The behavior of an iterator is unspecified if the underlying collection
         * is modified while the iteration is in progress in any way other than by
         * calling this method, unless an overriding class has specified a
         * concurrent modification policy.
         * <p>
         * The behavior of an iterator is unspecified if this method is called
         * after a call to the {@link #forEachRemaining forEachRemaining} method.
         *
         * @throws UnsupportedOperationException if the {@code remove}
         *                                       operation is not supported by this iterator
         * @throws IllegalStateException         if the {@code next} method has not
         *                                       yet been called, or the {@code remove} method has already
         *                                       been called after the last call to the {@code next}
         *                                       method
         * @implSpec The default implementation throws an instance of
         * {@link UnsupportedOperationException} and performs no other action.
         */
        @Override
        public void remove() {
            if (current == null || !nextCalled) {
                throw new IllegalStateException();
            }
            current = current.next;
            nextCalled = false;
            delete(index);
            index--;
        }
    }


    // Other methods for linked list


    /**
     * Inserts an element at the beginning of the list.
     * O(1) for a singly-linked list.
     *
     * @param element - the element to add
     */
    @Override
    public void insertFirst(E element) {
        if (head == null) {
            linkedNode<E> first = new linkedNode(element, null);
            head = new linkedNode(null, first);
        } else {
            // point new node to the first node
            linkedNode<E> newNode = new linkedNode<E>(element, head.next);
            // point head to new node
            head.next = newNode;
        }
        // increment size
        size++;
    }

    /**
     * Inserts an element at a specific position in the list.
     * O(N) for a singly-linked list.
     *
     * @param index   - the specified position
     * @param element - the element to add
     * @throws IndexOutOfBoundsException if index is out of range (index < 0 || index > size())
     */
    @Override
    public void insert(int index, E element) throws IndexOutOfBoundsException {
        // check for valid index first
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        // if we are inserting first just use insertFirst
        if (index == 0) {
            insertFirst(element);
        }
        // make new node to insert
        linkedNode<E> newNode = new linkedNode<E>(element, null);
        // get iterator to traverse list
        myIterator indexIterator = new myIterator();
        // use iterator to traverse list until at index to insert element
        while (indexIterator.hasNext()) {
            if (indexIterator.index == index-2) {
                // place new node
                indexIterator.next();
                linkedNode<E> oneBefore = indexIterator.current;
                newNode.next = oneBefore.next;
                oneBefore.next = newNode;

                // increment size
                size++;
                break;
            }
            // if not at insertion index, keep iterating
            indexIterator.next();
        }
    }

    /**
     * Gets the first element in the list.
     * O(1) for a singly-linked list.
     *
     * @return the first element in the list
     * @throws NoSuchElementException if the list is empty
     */
    @Override
    public E getFirst() throws NoSuchElementException {
        // if first element exists (i.e head points to something)
        if (head != null && head.next != null) {
            return head.next.data;
        }
        // otherwise there is no first so throw exception
        throw new NoSuchElementException();
    }

    /**
     * Gets the element at a specific position in the list.
     * O(N) for a singly-linked list.
     *
     * @param index - the specified position
     * @return the element at the position
     * @throws IndexOutOfBoundsException if index is out of range (index < 0 || index >= size())
     */
    @Override
    public E get(int index) throws IndexOutOfBoundsException {
        // check for valid index first
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            return getFirst();
        }
        // got to index
        myIterator indexIterator = new myIterator();
        while (indexIterator.hasNext()) {
            if (index-1 == indexIterator.index) {
                // return element at index
                return indexIterator.next();
            }
            indexIterator.next();
        }
        throw new IndexOutOfBoundsException();
    }

    /**
     * Deletes and returns the first element from the list.
     * O(1) for a singly-linked list.
     *
     * @return the first element
     * @throws NoSuchElementException if the list is empty
     */
    @Override
    public E deleteFirst() throws NoSuchElementException {
        // make sure list is not empty
        if (head == null || head.next == null || head.next.data == null) {
            throw new NoSuchElementException();
        }
        // get element to return
        E element = head.next.data;
        // set head to next.next to remove first element
        head.next = head.next.next;
        // decrement size
        size--;
        // return the element
        return element;
    }

    /**
     * Deletes and returns the element at a specific position in the list.
     * O(N) for a singly-linked list.
     *
     * @param index - the specified position
     * @return the element at the position
     * @throws IndexOutOfBoundsException if index is out of range (index < 0 || index >= size())
     */
    @Override
    public E delete(int index) throws IndexOutOfBoundsException {
        // make sure index is valid
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        // if first element just use deleteFirst
        if (index == 0) {
            return deleteFirst();
        }
        // empty element to return deleted element
        E toReturn = null;
        // get iterator to traverse list
        myIterator indexIterator = new myIterator();
        // use iterator to traverse list until at index to delete element
        while (indexIterator.hasNext()) {
            if (indexIterator.index == index-2) {
                indexIterator.next();
                linkedNode<E> oneBefore = indexIterator.current;
                // get data to return before deleting
                toReturn = oneBefore.next.data;
                // remove the next node
                oneBefore.next = oneBefore.next.next;
                // decrement size
                size--;
                return toReturn;
            }
            // if not at insertion index, keep iterating
            indexIterator.next();
        }
        // return element
        return toReturn;
    }

    /**
     * Determines the index of the first occurrence of the specified element in the list,
     * or -1 if this list does not contain the element.
     * O(N) for a singly-linked list.
     *
     * @param element - the element to search for
     * @return the index of the first occurrence; -1 if the element is not found
     */
    @Override
    public int indexOf(E element) {
        myIterator indexIterator = new myIterator();
        while (indexIterator.hasNext()) {
            if (indexIterator.next().equals(element)) {
                return indexIterator.index;
            }
        }
        return -1;
    }

    /**
     * O(1) for a singly-linked list.
     *
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * O(1) for a singly-linked list.
     *
     * @return true if this collection contains no elements; false, otherwise
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Removes all of the elements from this list.
     * O(1) for a singly-linked list.
     */
    @Override
    public void clear() {
        size = 0;
        head = null;
    }

    /**
     * Generates an array containing all of the elements in this list in proper sequence
     * (from first element to last element).
     * O(N) for a singly-linked list.
     *
     * @return an array containing all of the elements in this list, in order
     */
    @Override
    public Object[] toArray() {
        Object[] listDataArray = new Object[size];
        myIterator toArrayIterator = new myIterator();
        int index = 0;
        while (toArrayIterator.hasNext()) {
            listDataArray[index] = toArrayIterator.next();
            index++;
        }
        return listDataArray;
    }

    /**
     * @return an iterator over the elements in this list in proper sequence (from first
     * element to last element)
     */
    @Override
    public Iterator<E> iterator() {
        return new myIterator();
    }
}