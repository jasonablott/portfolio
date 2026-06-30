package assignment07;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediocreHashFunctorTest {

    MediocreHashFunctor test = new MediocreHashFunctor();

    @Test
    void hash() {
        String a = "A";
        String ab = "AB";
        String cat = "Cat";
        String animal = "Animal";
        assertEquals(test.hash(a), 386880);
        assertEquals(test.hash(ab), 657882);
        assertEquals(test.hash(cat), 1764303);
        assertEquals(test.hash(animal), 3351875);
    }
}