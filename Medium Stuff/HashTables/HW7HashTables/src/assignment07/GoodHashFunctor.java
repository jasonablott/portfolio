package assignment07;

import static java.lang.Math.abs;

/**
 * a class that has one method to generate a good hash code
 */
public class GoodHashFunctor implements HashFunctor {
    /**
     * a method to generate a good hash code
     * source: stackoverflow.com and IntelliJ autocomplete. The djb2 algorithm written by Dan Bernstein
     * @param item a string to generate a hash code for use in a hash table
     * @return returns an integer hash code for use in placing the item in a hash table
     */
    public int hash(String item) {

    int hash = 5381;
    for (int i = 0; i < item.length(); i++) {
        int c = item.charAt(i);
        hash = ((hash << 5) + hash) + c; /* hash * 33 + c */
    }
    return abs(hash);
    }
}