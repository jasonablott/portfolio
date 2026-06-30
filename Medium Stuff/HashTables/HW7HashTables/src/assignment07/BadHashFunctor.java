package assignment07;

/**
 * a class that has one method to generate a bad hash code
 */

public class BadHashFunctor implements HashFunctor {
    /**
     * * a method to generate a bad hash code
     * @param item a string to generate a hash code for use in a hash table
     * @return returns an integer hash code for use in placing the item in a hash table
     */
    public int hash(String item) {
        // this hash function returns the ascii value of the first letter in string item
        // this should only allow for 26 unique hash codes for string inputs so there will be a
        // large number of collisions
        return item.charAt(0) - 'A';
    }
}