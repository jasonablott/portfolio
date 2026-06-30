package assignment07;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoodHashFunctorTest {

    GoodHashFunctor test = new GoodHashFunctor();

    @Test
    void hash() {
        String a = "A";
        String b = "B";
        String c = "C";
        String ab = "AB";
        String cat = "Cat";
        String animal = "Animal";
        assertEquals(test.hash(a), 282);
        assertEquals(test.hash(b), 283);
        assertEquals(test.hash(c), 284);
        assertEquals(test.hash(ab), 8808);
        assertEquals(test.hash(cat), 276047);
        assertEquals(test.hash(animal), 411690781);
    }
}