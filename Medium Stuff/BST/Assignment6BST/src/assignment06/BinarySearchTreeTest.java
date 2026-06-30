package assignment06;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class BinarySearchTreeTest {

    BinarySearchTree<Integer> emptyTree = new BinarySearchTree<>();
    BinarySearchTree<Integer> oneItemTree = new BinarySearchTree<>();
    BinarySearchTree<Integer> twoItemTree = new BinarySearchTree<>();
    BinarySearchTree<Integer> threeItemTree = new BinarySearchTree<>();
    BinarySearchTree<Integer> tenItemTree = new BinarySearchTree<>();
    BinarySearchTree<Integer> RandomItemTree = new BinarySearchTree<>();

    ArrayList<Integer> oneItem = new ArrayList<>();
    ArrayList<Integer> twoItems = new ArrayList<>();
    ArrayList<Integer> threeItems = new ArrayList<>();
    ArrayList<Integer> tenItems = new ArrayList<>();
    ArrayList<Integer> RandomItems = new ArrayList<>();

    @org.junit.jupiter.api.BeforeEach
    void setUp() {

        oneItem.add(1);

        twoItems.add(1);
        twoItems.add(2);

        threeItems.add(1);
        threeItems.add(2);
        threeItems.add(3);

        tenItems.add(1);
        tenItems.add(2);
        tenItems.add(3);
        tenItems.add(4);
        tenItems.add(5);
        tenItems.add(6);
        tenItems.add(7);
        tenItems.add(8);
        tenItems.add(9);
        tenItems.add(10);

        // items to create a tree with all three cases for delete
        RandomItems.add(20);
        RandomItems.add(30);
        RandomItems.add(10);
        RandomItems.add(25);
        RandomItems.add(35);
        RandomItems.add(5);
        RandomItems.add(15);
        RandomItems.add(4);
        RandomItems.add(6);
        RandomItems.add(26);


        oneItemTree.addAll(oneItem);
        twoItemTree.addAll(twoItems);
        threeItemTree.addAll(threeItems);
        tenItemTree.addAll(tenItems);
        RandomItemTree.addAll(RandomItems);

    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        emptyTree = null;
        oneItemTree = null;
        twoItemTree = null;
        threeItemTree = null;
        tenItemTree = null;
        oneItem = null;
        twoItems = null;
        threeItems = null;
        tenItems = null;
        RandomItems = null;
        RandomItemTree = null;
    }

    @org.junit.jupiter.api.Test
    void add() {
        emptyTree.add(1);
        emptyTree.add(2);
        emptyTree.add(3);
        assertEquals(3, emptyTree.size());
        emptyTree.clear();
        assertTrue(emptyTree.add(4));
        assertEquals(1, emptyTree.size());
        assertEquals(emptyTree.last(), 4);
    }

    @org.junit.jupiter.api.Test
    void addAll() {
        assertEquals(0, emptyTree.size());
        assertTrue(emptyTree.addAll(tenItems));
        assertEquals(10, emptyTree.size());
        assertFalse(emptyTree.addAll(tenItems));
    }

    @org.junit.jupiter.api.Test
    void clear() {
        assertEquals(emptyTree.size(), 0);
        assertEquals(oneItemTree.size(), 1);
        assertEquals(tenItemTree.size(),10);
        emptyTree.clear();
        oneItemTree.clear();
        tenItemTree.clear();
        assertEquals(emptyTree.size(), 0);
        assertEquals(oneItemTree.size(), 0);
        assertEquals(tenItemTree.size(), 0);
        assertNull(oneItemTree.root);
        assertNull(tenItemTree.root);
    }

    @org.junit.jupiter.api.Test
    void contains() {
        for (Integer i : tenItems) {
            assertTrue(tenItemTree.contains(i));
        }
        for (Integer i : tenItems) {
            assertFalse(emptyTree.contains(i));
        }
    }

    @org.junit.jupiter.api.Test
    void containsAll() {
        assertTrue(tenItemTree.containsAll(tenItems));
        assertTrue(threeItemTree.containsAll(threeItems));
        assertTrue(twoItemTree.containsAll(twoItems));
        assertTrue(oneItemTree.containsAll(oneItem));
        assertTrue(tenItemTree.containsAll(oneItem));
        assertTrue(twoItemTree.containsAll(oneItem));
        assertFalse(emptyTree.containsAll(oneItem));
        assertFalse(threeItemTree.containsAll(tenItems));
        assertTrue(RandomItemTree.containsAll(RandomItems));
    }

    @org.junit.jupiter.api.Test
    void first() {
        assertEquals(oneItemTree.first(), 1);
        assertEquals(twoItemTree.first(), 1);
        assertEquals(threeItemTree.first(), 1);
        assertEquals(tenItemTree.first(), 1);
        assertThrows(NoSuchElementException.class, () -> emptyTree.first());
    }

    @org.junit.jupiter.api.Test
    void isEmpty() {
        assertTrue(emptyTree.isEmpty());
        assertFalse(oneItemTree.isEmpty());
        assertFalse(twoItemTree.isEmpty());
        assertEquals(twoItemTree.size(), 2);
        twoItemTree.remove(1);
        twoItemTree.remove(2);
        assertTrue(twoItemTree.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void last() {
        assertEquals(oneItemTree.last(), 1);
        assertEquals(twoItemTree.last(), 2);
        assertEquals(threeItemTree.last(), 3);
        assertEquals(tenItemTree.last(), 10);
        assertThrows(NoSuchElementException.class, () -> emptyTree.last());
    }

    @org.junit.jupiter.api.Test
    void remove() {
        assertTrue(tenItemTree.remove(1));
        assertFalse(emptyTree.remove(1));
        assertEquals(tenItemTree.first(), 2);
        tenItemTree.remove(10);
        assertEquals(tenItemTree.last(), 9);

    }

    @org.junit.jupiter.api.Test
    void removeAll() {
        // remove all ten items from ten item tree
        assertTrue(tenItemTree.removeAll(tenItems));
        // make sure size is now 0
        assertEquals(tenItemTree.size(), 0);
        // check that no items can be removed as they are all already removed
        assertFalse(emptyTree.removeAll(tenItems));
        // double check that none of the items are left
        for (Integer i : tenItems) {
            assertFalse(tenItemTree.contains(i));
        }
        // remove all of one item from a tree containing it
        assertTrue(twoItemTree.removeAll(oneItem));
        // try again to remove an item not in ten item tree
        assertFalse(tenItemTree.removeAll(oneItem));
        // add all ten items back to tree
        assertTrue(tenItemTree.addAll(tenItems));
        // make sure size is now 10 again
        assertEquals(tenItemTree.size(), 10);
        // check that we can now remove all of a smaller list that all items are in tree
        assertTrue(tenItemTree.removeAll(twoItems));
        // try again and make sure the items can't be removed
        assertFalse(tenItemTree.removeAll(twoItems));
        // add all ten items again, 2 should be added so it will be true
        assertTrue(tenItemTree.addAll(tenItems));
        // try removing all 10 from list of two
        assertTrue(twoItemTree.addAll(twoItems));
        // use bigger list to remove all
        assertTrue(twoItemTree.removeAll(tenItems));
        // try on a better tree
        assertTrue(RandomItemTree.removeAll(RandomItems));
    }

    @org.junit.jupiter.api.Test
    void size() {
        assertEquals(0, emptyTree.size());
        assertEquals(1, oneItemTree.size());
        assertEquals(3, threeItemTree.size());
        assertEquals(10, tenItemTree.size());
        tenItemTree.remove(1);
        assertEquals(tenItemTree.size(), 9);
        tenItemTree.remove(10);
        assertEquals(tenItemTree.size(), 8);
        tenItemTree.addAll(tenItems);
        assertEquals(tenItemTree.size(), 10);
        tenItemTree.removeAll(tenItems);
        assertEquals(tenItemTree.size(), 0);
    }

    @org.junit.jupiter.api.Test
    void toArrayList() {
        assertEquals(tenItemTree.toArrayList(), tenItems);
        assertEquals(oneItemTree.toArrayList(), oneItem);
        assertEquals(emptyTree.toArrayList(), new ArrayList<>());
    }
}