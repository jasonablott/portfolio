package assignment07;

/**
 * a class that has one method to generate a mediocre hash code
 */
public class MediocreHashFunctor implements HashFunctor {
    /**
     * * a method to generate a mediocre hash code
     * @param item a string to generate a hash code for use in a hash table
     * @return returns an integer hash code for use in placing the item in a hash table
     */
    public int hash(String item) {
        // start sum at a prime number
        int itemSum = 31;
        // sums the ascii values of all chars in item
        for (int i = 0; i < item.length(); i++) {
            itemSum += item.charAt(i);
        }
        // multiply ascii sum by the length of the item and a prime number to get a semi unique hash code
        return itemSum * ((item.charAt(0)+item.charAt(item.length()-1))*31);
    }
}