// Jason Ablott
// CS 6014
// 2/26/24
// HW 4 Cryptography Question 3

#include <iostream>
#include <vector>

// constants to hold reused values
const int SUBSTITUTION_TABLE_SIZE = 256;
const int NUMBER_OF_TABLES = 8;
const int NUM_ROUNDS = 16;
const int NUM_BLOCK_BYTES = 8;

// Define what a Block is:
using Block = std::array<uint8_t, NUM_BLOCK_BYTES>;

// initialize empty substitution tables to use later
std::array<std::array<uint8_t, SUBSTITUTION_TABLE_SIZE>, NUMBER_OF_TABLES> substTables;
// initialize key to all 0's (default values in empty arr)
Block key = {};
// initialize block to all 0's (default values in empty arr)
Block state = {};
// wrong key to prove password works
Block incorrectKey = {};

// forward declarations of functions
void fillSubstTables(std::array<std::array<uint8_t, SUBSTITUTION_TABLE_SIZE>, NUMBER_OF_TABLES>& substTables);
void hashPassword(const std::string& password, Block &key);
void fisherYatesShuffle(int arr[], int tableSize);
void encrypt(Block &state, Block key, const std::array<std::array<uint8_t, 256>, 8> &tables);
void decrypt(Block &state, Block key, const std::array<std::array<uint8_t, 256>, 8> &tables);
void modOneBit();


int main() {
    // seed rand for shuffling substitution tables
    srand(time(0));
    // fill tables
    fillSubstTables(substTables);
    // get password and store
    std::cout << "Please enter a password with 8 characters: ";
    std::string password;
    std::string message;
    std::getline(std::cin, password);
    if (password.length() > 8) {
        password = password.substr(0, 8);
    }
    // get wrong password and store
    std::cout << "Please enter a DIFFERENT password with 8 characters: ";
    std::string incorrectPassword;
    std::getline(std::cin, incorrectPassword);
    if (incorrectPassword.length() > 8) {
        incorrectPassword = incorrectPassword.substr(0, 8);
    }
    // hash password to update key
    hashPassword(password, key);
    hashPassword(incorrectPassword, incorrectKey);
    std::cout << "Your hashed password is: " << std::endl;
    for (int i = 0; i < 8; i++) {
        std::cout << key[i];
    }
    std::cout << std::endl;
    std::cout << "Please enter a message to encrypt (7 characters or less): " << std::endl;
    std::getline(std::cin, message);
    // set state block with message
    for (int i = 0; i < std::min(static_cast<int>(message.length()), NUM_BLOCK_BYTES); i++) {
        state[i] = static_cast<uint8_t>(message[i]);
    }
    // If the message is shorter than 8 bytes, fill the remaining bytes with padding (e.g., 0x00)
    for (int i = message.length(); i < NUM_BLOCK_BYTES; i++) {
        state[i] = 0; // Padding with 0
    }
    // print state after setting with message
    std::cout << "The state after reading in your message is: " << std::endl;
    for (int i = 0; i < NUM_BLOCK_BYTES; i++) {
        std::cout << state[i];
    }
    std::cout << std::endl;
    std::cout << "About to encrypt message: " << std::endl;
    // encrypt block
    encrypt(state, key, substTables);
    // print state (ciphertext) after encrypting
    std::cout << "The state after your message has been encrypted is:" << std::endl;
    for (int i = 0; i < NUM_BLOCK_BYTES; i++) {
        std::cout << state[i];
    }
    std::cout << std::endl;
    std::cout << "About to decrypt message" <<std::endl;

    // uncomment to flip 1 bit to see result: When testing Hello is now decrypted as "��    =�"
    // v
    //modOneBit();

    // decrypt block, comment this out and uncomment decrypt below to test wrong password
    // v
    decrypt(state, key, substTables);

    // test decrypting with wrong password key to see difference:
    // When using password "password" to encrypt, and password "Password" to decrypt
    // with the message "Hello, instead of "Hello" I get "|}���ޜ�"

    // uncomment this and comment out the correct decrypt above to test wrong password
    // v
    // decrypt(state, incorrectKey, substTables);

    // print state (plaintext) after decrypting. It should match the original state before encryption
    std::cout << "The state after your message has been decrypted is: " << std::endl;
    for (int i = 0; i < NUM_BLOCK_BYTES; i++) {
        std::cout << state[i];
    }
    std::cout << std::endl;
    return 0;
}


/* function to generate substitution tables
 *
 * We need 8 tables, 1 for each byte in our 64 byte block
 * represented as a 256 element array
 **/
 void fillSubstTables(std::array<std::array<uint8_t, SUBSTITUTION_TABLE_SIZE>, NUMBER_OF_TABLES>& substTables){
    // fill the tables
    for (int i = 0; i < NUMBER_OF_TABLES; i++) {
        for (int j = 0; j < SUBSTITUTION_TABLE_SIZE; j++) {
            substTables[i][j] = j;
        }
    }
    // shuffle tables using Fisher Yates shuffle
    for (int i = 0; i < NUMBER_OF_TABLES; i++) {
        for (int j = 0; j < SUBSTITUTION_TABLE_SIZE; j++) {
            int k = rand() % (j + 1);
            std::swap(substTables[i][j], substTables[i][k]);
        }
    }
 }
/* function to hash password and get key
 *
 * This is a terrible hash function because XOR of anything with 0 is
 * that thing, so the effectively does nothing!
 *
 **/
void hashPassword(const std::string& password, Block &key) {
    for (int i = 0; i < password.length(); i++) {
        key[i%8] = key[i%8] ^ password[i];
    }
}

/* encryption function
 *
* Now that we have the substitution tables and the key, initialize the algorithm's state to the input message.
*
* Then, for each of 16 rounds: xor the current state with the key;
*
* for each byte in the state, substitute that byte using the appropriate substitution table (byte 0 should use table 0,
* byte 1 should use table 1, etc);
*
* rotate the entire state 1 bit to the left (so, after this, byte 0 will contain contributions from bytes 0 and 1,
* while byte 7 will contain contributions from byte 7 and byte 0).
* The algorithm's output is the state after these 16 rounds.
*
**/
void encrypt(Block &state, const Block key, const std::array<std::array<uint8_t, 256>, 8> &tables) {
    // for 16 rounds
    for (int i = 0; i < NUM_ROUNDS; i++) {
        // XOR current state with key
        for (int j = 0; j < NUM_BLOCK_BYTES; j++) {
            state[j] = static_cast<uint8_t>(state[j] ^ key[j]);
        }
        // for each block in state substitute using correct substitution table
        for (int k = 0; k < NUM_BLOCK_BYTES; k++) {
            state[k] = tables[k][state[k]];
        }
        // once all blocks are substituted for this round, rotate state one bit to the left
        uint8_t carry = 0;
        for (int l = 0; l < NUM_BLOCK_BYTES; l++) {
            uint8_t nextCarry = (state[l] >> 7) & 1;
            state[l] = (state[l] << 1) | carry;
            carry = nextCarry;
        }
        state[0] = (state[0] & 0xFE) | carry;
    }
}

/* decryption function
*
* implement the decryption algorithm. The tricky part is implementing the reverse substitution tables, which you can
* do with a single for loop.
*
**/
void decrypt(Block &state, Block key, const std::array<std::array<uint8_t, 256>, 8> &tables) {
    // For 16 rounds
    for (int i = 0; i < NUM_ROUNDS; i++) {
        // reverse the rotation by shifting state 1 bit to right
        uint8_t carry = 0;
        for (int j = NUM_BLOCK_BYTES - 1; j >= 0; j--) {
            uint8_t nextCarry = state[j] & 1;
            state[j] = (state[j] >> 1) | (carry << 7);
            carry = nextCarry;
        }
        state[NUM_BLOCK_BYTES - 1] = (state[NUM_BLOCK_BYTES - 1] & 0x7F) | (carry << 7);

        // after reverse rotation, do reverse substitution by matching state byte with substitution table byte
        // the index of the matching byte is the new state byte for that location
        for (int k = NUM_BLOCK_BYTES - 1; k >= 0; k--) {
            uint8_t encryptedByte = state[k];
            bool found = false;
            for (int l = 0; l < SUBSTITUTION_TABLE_SIZE; l++) {
                if (tables[k][l] == encryptedByte) {
                    state[k] = static_cast<uint8_t>(l); // ??????????/
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw std::invalid_argument("Substitution table element does not exist");
            }
        }
        // XOR the updated state with the key
        for (int m = 0; m < NUM_BLOCK_BYTES; m++) {
            state[m] = static_cast<uint8_t>(state[m] ^ key[m]);
        }
    }
}

// function to flip one bit in the middle of the state before decryption
void modOneBit() {
    // flip the bit at position 3 in state byte 5
    state[4] ^= (1 << 3);
}