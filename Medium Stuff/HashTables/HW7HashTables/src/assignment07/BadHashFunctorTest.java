package assignment07;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadHashFunctorTest {

    BadHashFunctor test = new BadHashFunctor();

    @Test
    void hash() {
        String a = "A";
        String ab = "AB";
        String cat = "Cat";
        String animal = "Animal";
        assertEquals(test.hash(a), 0);
        assertEquals(test.hash(ab), 0);
        assertEquals(test.hash(cat), 2);
        assertEquals(test.hash(animal), 0);
    }
}