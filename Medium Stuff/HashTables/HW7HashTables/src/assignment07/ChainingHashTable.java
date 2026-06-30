package assignment07;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Hash table of strings implementation using chaining to handle collisions
 */

public class ChainingHashTable implements Set<String> {


    // Member Variables


    private final HashFunctor hashThis;
    private LinkedList<String>[] storage;
    private int size;
    public int Collisions = 0;



    // Constructor


    /**
     * Constructor to create a new hash table
     *
     * @param capacity - the capacity of the hash table
     * @param functor - the function that hashes values to place in the table
     */
    @SuppressWarnings("unchecked")
    public ChainingHashTable(int capacity, HashFunctor functor){
        storage = (LinkedList<String>[]) new LinkedList[capacity];
        size = 0;
        hashThis = functor;
    }


    // Other Methods


    /**
     * Ensures that this set contains the specified item.
     *
     * @param item - the item whose presence is ensured in this set
     * @return true if this set changed as a result of this method call (that is, if
     * the input item was actually inserted); otherwise, returns false
     */
    @Override
    public boolean add(String item) {
        // hash item to find it's index in storage
        int index = (hashThis.hash(item))%storage.length;
        // if list is empty, add list and add item
        if (storage[index] == null){
            storage[index] = new LinkedList<>();
            storage[index].add(item);
        }
        // if item is there, don't add and return false
        else if (storage[index].contains(item)) {
            return false;
        }
        // if item isn't in the list, add it and count the collision
        else {
            storage[index].add(item);
            Collisions++;
        }
        size++;
        return true;
    }

    /**
     * Ensures that this set contains all items in the specified collection.
     *
     * @param items - the collection of items whose presence is ensured in this set
     * @return true if this set changed as a result of this method call (that is, if
     * any item in the input collection was actually inserted); otherwise,
     * returns false
     */
    @Override
    public boolean addAll(Collection<? extends String> items) {
        // get size before trying to add to see if anything is added
        Integer sizeBefore = size;
        // try to add each item
        for (String item : items) {
            add(item);
        }
        // if size changed, the set has been changed
        return sizeBefore != size;
    }

    /**
     * Removes all items from this set. The set will be empty after this method
     * call.
     */
    @Override
    public void clear() {
        for (int i = 0; i < storage.length; i++) {
            storage[i] = null;
        }
        size = 0;
    }

    /**
     * Determines if there is an item in this set that is equal to the specified
     * item.
     *
     * @param item - the item sought in this set
     * @return true if there is an item in this set that is equal to the input item;
     * otherwise, returns false
     */
    @Override
    public boolean contains(String item) {
        // hash item to find it's index in storage
        int index = (hashThis.hash(item))%storage.length;
        // check if index has any items first
        if (storage[index] == null) {
            return false;
        }
        // if item is in the list at index, return true
        if (storage[index].contains(item)) {
            return true;
        }
        // if item is not in the list at index it is not in the table
        return false;
    }

    /**
     * Determines if for each item in the specified collection, there is an item in
     * this set that is equal to it.
     *
     * @param items - the collection of items sought in this set
     * @return true if for each item in the specified collection, there is an item
     * in this set that is equal to it; otherwise, returns false
     */
    @Override
    public boolean containsAll(Collection<? extends String> items) {
        // compare all items to table contents
        for (String item : items) {
            // if any item is not in the table, return false
            if (!contains(item)) {
                return false;
            }
        }
        // if all items were in table, return true
        return true;
    }

    /**
     * Returns true if this set contains no items.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Ensures that this set does not contain the specified item.
     *
     * @param item - the item whose absence is ensured in this set
     * @return true if this set changed as a result of this method call (that is, if
     * the input item was actually removed); otherwise, returns false
     */
    @Override
    public boolean remove(String item) {
        // hash item to find its index in storage
        int index = (hashThis.hash(item))%storage.length;
        // if no items at index, return false
        if (storage[index] == null) {
            return false;
        }
        // if item is there, remove and return true
        if (storage[index].contains(item)) {
            storage[index].remove(item);
            size--;
            return true;
        }
        return false;
    }

    /**
     * Ensures that this set does not contain any of the items in the specified
     * collection.
     *
     * @param items - the collection of items whose absence is ensured in this set
     * @return true if this set changed as a result of this method call (that is, if
     * any item in the input collection was actually removed); otherwise,
     * returns false
     */
    @Override
    public boolean removeAll(Collection<? extends String> items) {
        // get size before trying to remove to see if anything is added
        Integer sizeBefore = size;
        // try to remove each item
        for (String item : items) {
            remove(item);
        }
        // if size changed, the set has been changed
        return sizeBefore != size;
        // otherwise the set was not changed
    }

    /**
     * Returns the number of items in this set.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns the length of the longest linked list in the set (i.e. lambda)
     */
    public int getLambda() {
        // variable to check lambda/length of longest list
        int lambda = 0;
        // loop through to examine each index
        for (int i = 0; i < storage.length; i++) {
            // if there is a list, look at it and if length is longest replace lambda with it's length
            if (storage[i] != null) {
                LinkedList<String> thisList = storage[i];
                int length = thisList.size();
                if (length > lambda) {
                    lambda = length;
                }
            }
        }
        return lambda;
    }
}
