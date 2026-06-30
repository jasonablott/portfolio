package assignment06;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Make spellchecker dictionary public to run tests, return to private when done testing!!
 */

class SpellCheckerTest {

    // spellchecker made with file import
    SpellChecker spellChecker;
    // empty spellchecker made with parameterless constructor
    SpellChecker emptyspellChecker;
    // spellchecker made with a list of words
    SpellChecker listSpellChecker;
    // list to make
    ArrayList<String> dictionaryList;

    @BeforeEach
    void setUp() {

        // tests all 3 constructors and sets up 3 spellcheckers to test with
        spellChecker = new SpellChecker(new File("/Users/jasonablott/MSD/6012/Week3/Day11/Assignment6BST/src/dictionary.txt"));
        emptyspellChecker = new SpellChecker();

        dictionaryList = new ArrayList<>();
        dictionaryList.add("Animal");
        dictionaryList.add("Bird");
        dictionaryList.add("Cat");
        dictionaryList.add("Dog");

        listSpellChecker = new SpellChecker(dictionaryList);

    }

    @AfterEach
    void tearDown() {
        // set spellcheckers and list to null
        emptyspellChecker = null;
        spellChecker = null;
        listSpellChecker = null;
        dictionaryList = null;
    }

    @Test
    void addToDictionary() {
        emptyspellChecker.addToDictionary("test");
        assertTrue(emptyspellChecker.dictionary.contains("test"));
        spellChecker.addToDictionary("test");
        assertTrue(spellChecker.dictionary.contains("test"));
        listSpellChecker.addToDictionary("test");
        assertTrue(listSpellChecker.dictionary.contains("test"));
        assertFalse(emptyspellChecker.dictionary.contains("other"));
        assertFalse(listSpellChecker.dictionary.contains("other"));
    }

    @Test
    void removeFromDictionary() {

        emptyspellChecker.addToDictionary("test");
        assertTrue(emptyspellChecker.dictionary.contains("test"));
        emptyspellChecker.removeFromDictionary("test");
        assertFalse(emptyspellChecker.dictionary.contains("test"));

        spellChecker.addToDictionary("test");
        assertTrue(spellChecker.dictionary.contains("test"));
        spellChecker.removeFromDictionary("test");
        assertFalse(spellChecker.dictionary.contains("test"));

        listSpellChecker.addToDictionary("test");
        assertTrue(listSpellChecker.dictionary.contains("test"));
        listSpellChecker.removeFromDictionary("test");
        assertFalse(listSpellChecker.dictionary.contains("test"));

        // make sure can't remove a word that isn't in a dictionary
        assertFalse(emptyspellChecker.dictionary.contains("words"));
        assertFalse(emptyspellChecker.dictionary.remove("words"));
    }

    @Test
    void spellCheck() {
        // check some edge cases. Basic cases demonstrated in demo class
        File invalidDoc = new File("invalidfilename.txt");
        File emptyDoc = new File("/Users/jasonablott/MSD/6012/Week3/Day11/Assignment6BST/src/empty.txt");
        File listDoc = new File("/Users/jasonablott/MSD/6012/Week3/Day11/Assignment6BST/src/matchesListDict.txt");
        // should also print file not found to console
        assertEquals(spellChecker.spellCheck(invalidDoc).toString(), "[]");
        assertEquals(emptyspellChecker.spellCheck(emptyDoc).toString(), "[]");
        assertEquals(listSpellChecker.spellCheck(listDoc).toString(), "[animal, bird, cat, dog]");
    }
}