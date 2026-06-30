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
        assertEquals(test.hash(a), 177638);
        assertEquals(test.hash(b), 177639);
        assertEquals(test.hash(c), 177640);
        assertEquals(test.hash(ab), 5862120);
        assertEquals(test.hash(cat), 193453277);
        assertEquals(test.hash(animal), 1484763785);
    }
}