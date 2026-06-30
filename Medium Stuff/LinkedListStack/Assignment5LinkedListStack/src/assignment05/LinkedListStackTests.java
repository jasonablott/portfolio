package assignment05;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListStackTests {

    // lists of integers to test with
    LinkedListStack<Integer> IntegerListEmpty = new LinkedListStack<>();
    LinkedListStack<Integer> IntegerListOneElement = new LinkedListStack<>();
    LinkedListStack<Integer> IntegerListTwoElements = new LinkedListStack<>();
    LinkedListStack<Integer> IntegerListFiveElements = new LinkedListStack<>();

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // fill lists for testing
        IntegerListOneElement.push(1);
        IntegerListTwoElements.push(2);
        IntegerListTwoElements.push(1);
        IntegerListFiveElements.push(5);
        IntegerListFiveElements.push(4);
        IntegerListFiveElements.push(3);
        IntegerListFiveElements.push(2);
        IntegerListFiveElements.push(1);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        // make test lists null
        IntegerListEmpty = null;
        IntegerListOneElement = null;
        IntegerListTwoElements = null;
        IntegerListFiveElements = null;
    }

    @org.junit.jupiter.api.Test
    void clear() {
        IntegerListEmpty.clear();
        assertEquals(IntegerListEmpty.size(), 0);
        assertTrue(IntegerListEmpty.isEmpty());
        IntegerListOneElement.clear();
        assertEquals(IntegerListOneElement.size(), 0);
        assertTrue(IntegerListOneElement.isEmpty());
        IntegerListTwoElements.clear();
        assertEquals(IntegerListTwoElements.size(), 0);
        assertTrue(IntegerListTwoElements.isEmpty());
        IntegerListFiveElements.clear();
        assertEquals(IntegerListFiveElements.size(), 0);
        assertTrue(IntegerListFiveElements.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void isEmpty() {
        assertTrue(IntegerListEmpty.isEmpty());
        assertFalse(IntegerListOneElement.isEmpty());
        // tested further in clear tests
    }

    @org.junit.jupiter.api.Test
    void peek() {
        assertThrows(NoSuchElementException.class, () -> IntegerListEmpty.peek());
        assertEquals(IntegerListOneElement.peek(), 1);
        assertEquals(IntegerListTwoElements.peek(), 1);
        assertEquals(IntegerListFiveElements.peek(), 1);
    }

    @org.junit.jupiter.api.Test
    void pop() {
        assertThrows(NoSuchElementException.class, () -> IntegerListEmpty.pop());
        assertEquals(IntegerListFiveElements.pop(), 1);
        assertEquals(IntegerListFiveElements.pop(), 2);
        assertEquals(IntegerListFiveElements.pop(), 3);
        assertEquals(IntegerListFiveElements.pop(), 4);
        assertEquals(IntegerListFiveElements.pop(), 5);
        assertEquals(IntegerListFiveElements.size(), 0);
        assertTrue(IntegerListFiveElements.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void push() {
        // tested in setup
    }

    @org.junit.jupiter.api.Test
    void size() {
        assertEquals(0, IntegerListEmpty.size());
        assertEquals(1, IntegerListOneElement.size());
        assertEquals(2, IntegerListTwoElements.size());
        assertEquals(5, IntegerListFiveElements.size());
    }
}