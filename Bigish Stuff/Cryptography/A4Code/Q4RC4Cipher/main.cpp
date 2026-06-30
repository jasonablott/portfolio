// Jason Ablott
// 2/26/25
// CS 6014
// HW4 Q4 RC4 Cipher

#include <iostream>
#include <vector>


/*
 * Implement the RC4 cipher as described in class. It's not very much code! Probably a struct/class with
 * a constructor and a single method to get the next pseudorandom byte. Pseudocode is in the class slides
 * or many other places.
 **/

class RC4 {
private:
    // member variables
    std::vector <unsigned char> state = std::vector <unsigned char>(256, 0);
    unsigned char i = 0;
    unsigned char j = 0;
    std::vector<unsigned char> key;
public:
    RC4(std::vector<unsigned char>& key) {
        this->key = key;
        key_scheduler(key);
    }
    /* Key Scheduling Algorithm (KSA) function
     *
     * This function initializes the state vector S based on the provided key
     *
     **/
    void key_scheduler(std::vector <unsigned char> &key) {
        //initialize state to size of 256 and set initial values to 0-255
        for (int i = 0; i < 256; i++) {
            state[i] = i;
        }
        // loop through, randomizing the state based on the key. This code is from the slides
        int j = 0;
        for (int i = 0; i < 256; i++) {
            j = (j + state[i] + key[i % key.size()]) % 256;
            std::swap(state[i], state[j]);
        }
        // reset i and j to 0
        i = 0;
        j = 0;
    }

    /* Pseudo-Random Generation Algorithm (PRGA) function
     *
     * This function generates the keystream for the state vector S and the key
     *
     **/
    unsigned char psuedo_random_generator() {
        // This code is from the slides
        i = (i + 1) % 256;
        j = (j + state[i]) % 256;
        std::swap(state[i], state[j]);
        return state[(state[i] + state[j]) % 256];
    }

    /* Encrpyt/Decrypt function
     *
     * This function XORs the plaintext or ciphertext with the keystream to encrypt/decrypt
     *
     **/
    std::string encrypt_or_decrypt(std::string& input) {
        // initialize return string
        std::string ret;
        // loop through input characters
        for (char c : input) {
            unsigned char keystreambyte = psuedo_random_generator();
            ret += (c ^ keystreambyte);
        }
        return ret;
    }
};


// ****** Test cases generated with some help from chatgpt *******

// Test Case 0: Decrypting with a correct key should reveal the plaintext
void testCorrectKeyDecryption() {
    std::string message = "I should see this message";
    std::vector<unsigned char> key1 = {'K', 'E', 'Y', '1'};

    RC4 cipher1(key1);
    std::string encryptedMessage = cipher1.encrypt_or_decrypt(message);

    RC4 cipher2(key1);
    std::string decryptedMessage = cipher2.encrypt_or_decrypt(encryptedMessage);

    std::cout << "Original Message: " << message << std::endl;
    std::cout << "Decrypted with correct key: " << decryptedMessage << std::endl;

    if (decryptedMessage == message) {
        std::cout << "Success: Decrypting with the correct key reveals the plaintext." << std::endl;
    }
}

// Test Case 1: Decrypting with a wrong key should not reveal the plaintext
void testWrongKeyDecryption() {
    std::string message = "I should NOT see this message";
    std::vector<unsigned char> key1 = {'K', 'E', 'Y', '1'};
    std::vector<unsigned char> key2 = {'k', 'e', 'y', '2'};

    RC4 cipher1(key1);
    std::string encryptedMessage = cipher1.encrypt_or_decrypt(message);

    RC4 cipher2(key2);
    std::string decryptedMessage = cipher2.encrypt_or_decrypt(encryptedMessage);

    std::cout << "Original Message: " << message << std::endl;
    std::cout << "Decrypted with wrong key: " << decryptedMessage << std::endl;

    if (decryptedMessage != message) {
        std::cout << "Success: Decrypting with a different key does not reveal the plaintext." << std::endl;
    }
}

// Test Case 2: Encrypting two messages with the same keystream. THe result should be the XOR of the plaintexts,
// which can provide information about how the plaintexts differ. If there are many similarities between the
// plaintexts it can reveal common patterns in them.
void testSameKeystreamInsecurity() {
    std::string message1 = "Hello!";
    std::string message2 = "Goodbye!";

    std::vector<unsigned char> key = {'K', 'E', 'Y'};

    RC4 cipher(key);
    std::string encryptedMessage1 = cipher.encrypt_or_decrypt(message1);
    std::string encryptedMessage2 = cipher.encrypt_or_decrypt(message2);

    std::cout << "Encrypted Message 1: ";
    for (char c : encryptedMessage1) {
        std::cout << c << " ";
    }
    std::cout << std::endl;

    std::cout << "Encrypted Message 2: ";
    for (char c : encryptedMessage2) {
        std::cout << c << " ";
    }
    std::cout << std::endl;

    // XOR the two ciphertexts
    std::string xored = encryptedMessage1;
    for (size_t i = 0; i < encryptedMessage1.size(); i++) {
        xored[i] ^= encryptedMessage2[i];
    }

    // XOR the two plaintexts
    std::string xoredPT = encryptedMessage1;
    for (size_t i = 0; i < encryptedMessage1.size(); i++) {
        xoredPT[i] ^= encryptedMessage2[i];
    }

    std::cout << "XOR of the two ciphertexts, which is the XOR of the plaintexts!: ";
    for (char c : xored) {
        std::cout << c << " ";
    }
    std::cout << std::endl;

    std::cout << "XOR of the two plaintexts, which is the XOR of the ciphertexts!: ";
    for (char c : xoredPT) {
        std::cout << c << " ";
    }
    std::cout << std::endl;

    // XOR the result with one of the ciphertexts
    std::string xored2 = encryptedMessage2;
    for (size_t i = 0; i < encryptedMessage2.size(); i++) {
        xored2[i] ^= encryptedMessage2[i];
    }
    std::cout << "XOR of the result with the first ciphertext should be all 0: ";
    for (char c : xored2) {
        std::cout << c << " ";
    }
    std::cout << std::endl;
}

// Test Case 3: Bit-flipping attack. Need to change ascii byte values 0x24_0x31_0x30_0x30_0x30 ($1000) to
// ascii byte values 0x24_0x39_0x39_0x39 ($9999)
void testBitFlippingAttack() {
    std::string originalMessage = "Your salary is $1000";
    std::vector<unsigned char> key = {'K', 'E', 'Y'};

    // Encrypt the message
    RC4 cipher(key);
    std::string encryptedMessage = cipher.encrypt_or_decrypt(originalMessage);

    std::cout << "Original Message: " << originalMessage << std::endl;

    // Find the position of "$1000" in the original message
    size_t pos = originalMessage.find("$1000");

    // Original ASCII values of "$1000"
    unsigned char originalBytes[] = {0x24, 0x31, 0x30, 0x30, 0x30}; // '$' = 0x24, '1' = 0x31, '0' = 0x30
    // Target ASCII values of "$9999"
    unsigned char targetBytes[] = {0x24, 0x39, 0x39, 0x39, 0x39}; // '$' = 0x24, '9' = 0x39

    // XOR the encrypted ciphertext to flip bits from "$1000" to "$9999"
    for (size_t i = 0; i < 5; i++) {
        // XOR the encrypted byte with the difference between the original and target byte
        encryptedMessage[pos + i] ^= (originalBytes[i] ^ targetBytes[i]);
    }

    // Decrypt the modified message
    RC4 cipher2(key);
    std::string modifiedDecryptedMessage = cipher2.encrypt_or_decrypt(encryptedMessage);

    std::cout << "Modified Decrypted Message: " << modifiedDecryptedMessage << std::endl;
}

// Just run the test cases using the RC4 class.

int main() {
    // Test decryption with Correct Key
    testCorrectKeyDecryption();

    // Test Decryption with Wrong Key
    testWrongKeyDecryption();

    // Test Encrypting with Same Keystream
    testSameKeystreamInsecurity();

    // Test Bit-flipping Attack
    testBitFlippingAttack();

    return 0;
}


