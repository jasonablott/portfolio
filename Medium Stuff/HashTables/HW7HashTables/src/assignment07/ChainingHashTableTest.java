package assignment07;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ChainingHashTableTest {


    ChainingHashTable badHashTable;
    ChainingHashTable mediocreHashTable;
    ChainingHashTable goodHashTable;
    ChainingHashTable emptyHashTable;
    BadHashFunctor badHash;
    MediocreHashFunctor mediocreHash;
    GoodHashFunctor goodHash;
    ArrayList<String> allLetters;


    @BeforeEach
    void setUp() {
        // tests constructor and sets up variables for tests
        mediocreHash = new MediocreHashFunctor();
        goodHash = new GoodHashFunctor();
        badHash = new BadHashFunctor();
        badHashTable = new ChainingHashTable(26, badHash);
        mediocreHashTable = new ChainingHashTable(26, mediocreHash);
        goodHashTable = new ChainingHashTable(26, goodHash);
        emptyHashTable = new ChainingHashTable(26, goodHash);
        allLetters = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));
    }

    @AfterEach
    void tearDown() {
        // sets all testing variables to null
        mediocreHash = null;
        goodHash = null;
        badHash = null;
        badHashTable = null;
        mediocreHashTable = null;
        goodHashTable = null;
        emptyHashTable = null;
        allLetters = null;
    }

    @org.junit.jupiter.api.Test
    void add() {
        // make sure table is empty to start
        assertTrue(emptyHashTable.isEmpty());
        assertFalse(emptyHashTable.contains("a"));
        // add a string
        emptyHashTable.add("a");
        // prove it was added correctly
        assertEquals(1, emptyHashTable.size());
        assertTrue(emptyHashTable.contains("a"));
        // make sure it can't be added again
        assertFalse(emptyHashTable.add("a"));
    }

    @org.junit.jupiter.api.Test
    void addAll() {
        assertTrue(emptyHashTable.isEmpty());
        assertEquals(emptyHashTable.size(), 0);
        // add a set to a table
        assertTrue(emptyHashTable.addAll(allLetters));
        // make sure it was added properly
        assertEquals(allLetters.size(), emptyHashTable.size());
        for (int i = 0; i < allLetters.size(); i++) {
            assertTrue(emptyHashTable.contains(allLetters.get(i)));
        }
        // make sure it can't be added again
        assertFalse(emptyHashTable.addAll(allLetters));
    }

    @org.junit.jupiter.api.Test
    void clear() {
        // get an empty table
        assertTrue(emptyHashTable.isEmpty());
        assertEquals(emptyHashTable.size(), 0);
        // try clearing an empty table
        emptyHashTable.clear();
        // make sure it didn't get changed
        assertTrue(emptyHashTable.isEmpty());
        assertEquals(emptyHashTable.size(), 0);
        // fill table
        emptyHashTable.addAll(allLetters);
        assertEquals(emptyHashTable.size(), allLetters.size());
        assertFalse(emptyHashTable.isEmpty());
        // clear table now hat it is full of items
        emptyHashTable.clear();
        // make sure it was cleared correctly
        assertTrue(emptyHashTable.isEmpty());
        assertEquals(emptyHashTable.size(), 0);
    }

    @org.junit.jupiter.api.Test
    void contains() {
        // check for all letters in an empty table
        for (int i = 0; i < allLetters.size(); i++) {
            assertFalse(emptyHashTable.contains(allLetters.get(i)));
        }
        // add all letters to table
        emptyHashTable.addAll(allLetters);
        // check that all letters are now contained in table
        for (int i = 0; i < allLetters.size(); i++) {
            assertTrue(emptyHashTable.contains(allLetters.get(i)));
        }
    }

    @org.junit.jupiter.api.Test
    void containsAll() {
        // check an empty table
        assertFalse(emptyHashTable.containsAll(allLetters));
        // add all letters to table
        emptyHashTable.addAll(allLetters);
        // check that all letters are now contained
        assertTrue(emptyHashTable.containsAll(allLetters));
        // clear the table and add just 2 letters
        emptyHashTable.clear();
        emptyHashTable.add("a");
        emptyHashTable.add("b");
        // make sure containsAll works on partial set
        assertFalse(emptyHashTable.containsAll(allLetters));
    }

    @org.junit.jupiter.api.Test
    void isEmpty() {
        // make sure an empty table is empty
        assertTrue(emptyHashTable.isEmpty());
        // add all letters to table
        emptyHashTable.addAll(allLetters);
        // make sure it is no longer empty
        assertFalse(emptyHashTable.isEmpty());
        // clear and check if empty
        emptyHashTable.clear();
        assertTrue(emptyHashTable.isEmpty());
        // add one item and check that it is not empty
        emptyHashTable.add("a");
        assertFalse(emptyHashTable.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void remove() {
        // try removing item that doesn't exist yet
        assertFalse(emptyHashTable.remove("a"));
        // add all letters to empty table
        emptyHashTable.addAll(allLetters);
        // ensure "a" is in table
        assertTrue(emptyHashTable.contains("a"));
        // remove "a"
        assertTrue(emptyHashTable.remove("a"));
        // check that it was removed as expected
        assertEquals(25, emptyHashTable.size());
        assertFalse(emptyHashTable.contains("a"));
        // make sure it can't be removed again
        assertFalse(emptyHashTable.remove("a"));
    }

    @org.junit.jupiter.api.Test
    void removeAll() {
        // add all letters to table
        emptyHashTable.addAll(allLetters);
        assertEquals(allLetters.size(), emptyHashTable.size());
        // remove all letters from table
        assertTrue(emptyHashTable.removeAll(allLetters));
        assertTrue(emptyHashTable.isEmpty());
        // make sure they can't be removed again
        assertFalse(emptyHashTable.removeAll(allLetters));
        // try removing all with one letter in table
        emptyHashTable.add("a");
        assertTrue(emptyHashTable.contains("a"));
        assertTrue(emptyHashTable.removeAll(allLetters));
        assertTrue(emptyHashTable.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void size() {
        // make sure empty table size is 0
        assertEquals(emptyHashTable.size(), 0);
        // add set and check size is correct
        emptyHashTable.addAll(allLetters);
        assertEquals(allLetters.size(), emptyHashTable.size());
        // empty table and make sure size is 0 again
        assertTrue(emptyHashTable.removeAll(allLetters));
        assertEquals(emptyHashTable.size(), 0);
        // add all letters again and check result
        emptyHashTable.add("A");
        assertEquals(1, emptyHashTable.size());
        // clear table to check clear and size
        emptyHashTable.clear();
        assertEquals(emptyHashTable.size(), 0);
    }

}