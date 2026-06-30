package assignment05;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class SinglyLinkedListTests {

    // lists of integers to test with
    SinglyLinkedList<Integer> IntegerListEmpty = new SinglyLinkedList<Integer>();
    SinglyLinkedList<Integer> IntegerListOneElement = new SinglyLinkedList<Integer>();
    SinglyLinkedList<Integer> IntegerListTwoElements = new SinglyLinkedList<Integer>();
    SinglyLinkedList<Integer> IntegerListFiveElements = new SinglyLinkedList<Integer>();

    // lists of strings to test with
    SinglyLinkedList<String> StringListEmpty = new SinglyLinkedList<String>();
    SinglyLinkedList<String> StringListOneElement = new SinglyLinkedList<String>();
    SinglyLinkedList<String> StringListTwoElements = new SinglyLinkedList<String>();
    SinglyLinkedList<String> StringListFiveElements = new SinglyLinkedList<String>();

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // set up integer lists
        IntegerListOneElement.insertFirst(1);
        IntegerListTwoElements.insertFirst(2);
        IntegerListTwoElements.insertFirst(1);
        for (int i = 0; i < 5; i++){
            IntegerListFiveElements.insertFirst(5-i);
        }
        // set up string lists
        StringListOneElement.insertFirst("cat");
        StringListTwoElements.insertFirst("dog");
        StringListTwoElements.insertFirst("cat");
        StringListFiveElements.insertFirst("elephant");
        StringListFiveElements.insertFirst("dog");
        StringListFiveElements.insertFirst("cat");
        StringListFiveElements.insertFirst("bird");
        StringListFiveElements.insertFirst("animal");

    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        // clear lists used for tests
        IntegerListEmpty = null;
        IntegerListOneElement = null;
        IntegerListTwoElements = null;
        IntegerListFiveElements = null;
        StringListEmpty = null;
        StringListOneElement = null;
        StringListTwoElements = null;
        StringListFiveElements = null;
    }

    @org.junit.jupiter.api.Test
    void insertFirst() {
        assertEquals(IntegerListEmpty.size(), 0);
        IntegerListEmpty.insertFirst(1);
        assertEquals(IntegerListEmpty.getFirst(), 1);
        assertEquals(IntegerListEmpty.size(), 1);
    }

    @org.junit.jupiter.api.Test
    void insert() {
        IntegerListFiveElements.insert(0, 0);
        IntegerListFiveElements.insert(5, 6);
        assertEquals(IntegerListFiveElements.size(), 7);
        assertThrows(IndexOutOfBoundsException.class, () -> {IntegerListFiveElements.insert(-1, 1);});
        assertThrows(IndexOutOfBoundsException.class, () -> {IntegerListFiveElements.insert(8, 1);});
    }

    @org.junit.jupiter.api.Test
    void getFirst() {
        assertThrows(NoSuchElementException.class, () -> {IntegerListEmpty.getFirst();});
        assertEquals(IntegerListOneElement.getFirst(), 1);
        assertEquals(IntegerListTwoElements.getFirst(), 1);
        assertEquals(IntegerListFiveElements.getFirst(), 1);
        assertEquals(StringListOneElement.getFirst(), "cat");
        assertEquals(StringListTwoElements.getFirst(), "cat");
        assertEquals(StringListFiveElements.getFirst(), "animal");
    }

    @org.junit.jupiter.api.Test
    void get() {
        assertThrows(IndexOutOfBoundsException.class, () -> {IntegerListEmpty.get(1);});
        assertEquals(IntegerListOneElement.get(0), 1);
        assertEquals(IntegerListTwoElements.get(0), 1);
        assertEquals(IntegerListTwoElements.get(1), 2);
        assertEquals(IntegerListFiveElements.get(0), 1);
        assertEquals(IntegerListFiveElements.get(1), 2);
        assertEquals(IntegerListFiveElements.get(2), 3);
        assertEquals(IntegerListFiveElements.get(3), 4);
        assertEquals(IntegerListFiveElements.get(4), 5);
        assertEquals(StringListFiveElements.get(0), "animal");
        assertEquals(StringListFiveElements.get(1), "bird");
        assertEquals(StringListFiveElements.get(2), "cat");
        assertEquals(StringListFiveElements.get(3), "dog");
        assertEquals(StringListFiveElements.get(4), "elephant");
        assertThrows(IndexOutOfBoundsException.class, () -> {StringListFiveElements.get(5);});
    }

    @org.junit.jupiter.api.Test
    void deleteFirst() {
        assertThrows(NoSuchElementException.class, () -> {IntegerListEmpty.deleteFirst();});
        assertEquals(IntegerListOneElement.deleteFirst(), 1);
        assertEquals(IntegerListTwoElements.deleteFirst(), 1);
        assertEquals(IntegerListTwoElements.deleteFirst(), 2);
        assertEquals(IntegerListTwoElements.size(), 0);

    }

    @org.junit.jupiter.api.Test
    void delete() {
        assertThrows(NoSuchElementException.class, () -> {IntegerListEmpty.delete(0);});
        assertEquals(IntegerListOneElement.delete(0), 1);
        assertEquals(IntegerListTwoElements.delete(1), 2);
        assertEquals(IntegerListTwoElements.size(), 1);
        assertEquals(IntegerListFiveElements.delete(3), 4);
        assertEquals(IntegerListFiveElements.size(), 4);
        assertThrows(IndexOutOfBoundsException.class, () -> {IntegerListFiveElements.delete(5);});
    }

    @org.junit.jupiter.api.Test
    void indexOf() {
        assertEquals(IntegerListOneElement.indexOf(1), 0);
        assertEquals(IntegerListTwoElements.indexOf(1), 0);
        assertEquals(IntegerListTwoElements.indexOf(2), 1);
        assertEquals(IntegerListTwoElements.indexOf(3), -1);
        assertEquals(IntegerListFiveElements.indexOf(1), 0);
        assertEquals(IntegerListFiveElements.indexOf(2), 1);
        assertEquals(IntegerListFiveElements.indexOf(3), 2);
        assertEquals(IntegerListFiveElements.indexOf(4), 3);
        assertEquals(IntegerListFiveElements.indexOf(5), 4);
        assertEquals(IntegerListFiveElements.indexOf(6), -1);

    }

    @org.junit.jupiter.api.Test
    void size() {

        assertEquals(IntegerListEmpty.size(), 0);
        assertEquals(IntegerListOneElement.size(), 1);
        assertEquals(IntegerListTwoElements.size(), 2);
        assertEquals(IntegerListFiveElements.size(), 5);

        assertEquals(StringListEmpty.size(), 0);
        assertEquals(StringListOneElement.size(), 1);
        assertEquals(StringListTwoElements.size(), 2);
        assertEquals(StringListFiveElements.size(), 5);
    }

    @org.junit.jupiter.api.Test
    void isEmpty() {
        assertTrue(IntegerListEmpty.isEmpty());
        assertFalse(IntegerListOneElement.isEmpty());
        assertFalse(IntegerListTwoElements.isEmpty());
        assertFalse(IntegerListFiveElements.isEmpty());
        assertTrue(StringListEmpty.isEmpty());
        assertFalse(StringListOneElement.isEmpty());
        assertFalse(StringListTwoElements.isEmpty());
        assertFalse(StringListFiveElements.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void clear() {
        IntegerListEmpty.clear();
        assertEquals(IntegerListEmpty.size(), 0);
        assertThrows(NoSuchElementException.class, () -> {IntegerListEmpty.getFirst();});
        IntegerListFiveElements.clear();
        assertEquals(IntegerListFiveElements.size(), 0);
        assertThrows(NoSuchElementException.class, () -> {IntegerListFiveElements.getFirst();});
    }

    @org.junit.jupiter.api.Test
    void toArray() {
        Object [] OneIntArray = {1};
        Object [] TwoIntArray = {1,2};
        Object [] OneStringArray = {"cat"};
        Object [] TwoStringArray = {"cat","dog"};
        Object [] OneIntArrayCopy = IntegerListOneElement.toArray();
        Object [] TwoIntArrayCopy = IntegerListTwoElements.toArray();
        Object [] OneStringArrayCopy = StringListOneElement.toArray();
        Object [] TwoStringArrayCopy = StringListTwoElements.toArray();

        assertEquals(IntegerListOneElement.size(), OneIntArray.length);
        assertEquals(IntegerListTwoElements.size(), TwoIntArray.length);
        assertEquals(OneIntArray[0], OneIntArrayCopy[0]);
        assertEquals(TwoIntArray[0], TwoIntArrayCopy[0]);
        assertEquals(TwoIntArray[1], TwoIntArrayCopy[1]);
        assertEquals(OneStringArray[0], OneStringArrayCopy[0]);
        assertEquals(TwoStringArray[0], TwoStringArrayCopy[0]);
        assertEquals(TwoStringArray[1], TwoStringArrayCopy[1]);
    }

    @org.junit.jupiter.api.Test
    void iterator() {
        // used in many previous tests
    }
}