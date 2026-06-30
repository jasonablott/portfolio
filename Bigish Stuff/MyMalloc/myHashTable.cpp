/*
* Author: Jason Ablott
 * Date: 2/22/2025
 * Class: CS 6013
 * Project: A4 Malloc Replacement
 * File: myHashTable.cpp
 **/
//---------------------------------------------------------------------------------------------------------------------


#include "myHashTable.h"
#include <iostream>
#include <sys/mman.h>


//---------------------------------------------------------------------------------------------------------------------
// Private Hash Table Methods


// Method to grow table when at a capacity threshold
void myHashTable::grow() {
    // store old size for  traversing old table
    size_t oldCapacity = this->capacity;
    // make new capacity double size of old capacity
    int newCapacity = static_cast<int>(this->capacity * 2);
    // update current capacity in my table (It is still the size of the old capacity right now)
    this->capacity = newCapacity;
    // Make a new myHashTable object with new capacity
    myHashTable newHashTable(newCapacity);
    // copy the active contents of this table into the new table, ignoring deleted entries. This
    // requires rehashing with the new capacity to place entries in new slots. Insert handles this.
    for (size_t i = 0; i < oldCapacity; i++) {
        if (table[i].address != nullptr && !table[i].deleted) {
            void* copyAddress = table[i].address;
            size_t copySize = table[i].size;
            newHashTable.insert(copyAddress, copySize);
        }
    }
    // use std::swap to swap the new table for the old one
    std::swap(this->table, newHashTable.table);
    // deallocate space used for growing table
    munmap(newHashTable.table, oldCapacity*sizeof(Entry));
}

// simple address hash function, needs to be mod by table size when used!
size_t myHashTable::hash(void* address) {
    size_t addressToHash = reinterpret_cast<size_t>(address);
    return (addressToHash >> 12); // 12 bits for 64-bit system, update if needed for other systems
}

// linear probing method to get index of next usable entry slot in hash table
size_t myHashTable::linearProbe(size_t startIndex) {
    size_t index = startIndex;
    while (true) {
        // if the index is unused or deleted it can be used/overwritten
        if (this->table[index].address == nullptr || this->table[index].deleted == true) {
            return index;
        }
        // increment within size of table
        index = (index + 1) % this->capacity;
        // handle full loop through table
        if (index == startIndex) {
            return SIZE_MAX;
        }
    }
}


//---------------------------------------------------------------------------------------------------------------------
// Public Hash Table Methods


// Default constructor, makes a hash table of capacity 4. Real usage would likely start this higher, but 4 is
// good for testing
myHashTable::myHashTable() {
    this->capacity = 4;
    // need to call mmap() to get location of memory big enough for default capacity
    void* startLocation = mmap(nullptr, (capacity*sizeof(Entry)), PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
    // handle mmap error
    if (startLocation == MAP_FAILED) {
        perror("mmap failed when constructing hash table (In myHashTable constructor) ");
        exit(1);
    }
    // store location of table
    table = static_cast<Entry *>(startLocation);
    // new table has no entries yet
    this->current_size = 0;
    // initialize table entries
    for (size_t i = 0; i < this->capacity; i++) {
        this->table[i].address = nullptr;
        this->table[i].size = 0;
        this->table[i].deleted = false;
    }
}

// parameterized constructor to create a hash table of specified size, used when growing
myHashTable::myHashTable(size_t thisCapacity) {
    // need to call mmap() to get location of memory big enough for default capacity
    void* startLocation = mmap(nullptr, (thisCapacity*sizeof(Entry)), PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
    // handle mmap error
    if (startLocation == MAP_FAILED) {
        perror("mmap failed when constructing hash table (In myHashTable constructor) ");
        exit(1);
    }
    // store location of table
    table = static_cast<Entry *>(startLocation);
    // new table has no entries yet
    this->current_size = 0;
    // in this constructor we set the capacity based on given parameter
    this->capacity = thisCapacity;
    // initialize table entries
    for (size_t i = 0; i < this->capacity; i++) {
        this->table[i].address = nullptr;
        this->table[i].size = 0;
        this->table[i].deleted = false;
    }
}

// insert method to add an entry to hash table
void myHashTable::insert(void* address, size_t size) {
    // check if current_size after insert will exceed 70% full, and if so call grow()
    if (static_cast<float>(current_size + 1) >= (static_cast<float>(capacity) * (this->growThresholdPercentage) / (100.0))) {
        grow();
    }
    // hash the address to get the desired index into the table
    size_t targetIndex = hash(address) % this->capacity;
    // if that location is empty, insert the address and size into the entry
    if (table[targetIndex].address == nullptr || table[targetIndex].deleted == true) {
        table[targetIndex].address = address;
        table[targetIndex].size = size;
        table[targetIndex].deleted = false;
        current_size++;
    } else {
        // if the location is not empty, use linear probing to look for an empty or deleted slot
        // if an empty slot is found, insert the entry and increment current_size
        // if a deleted slot is found, overwrite it and mark is as un-deleted
        targetIndex = linearProbe(targetIndex);
        table[targetIndex].address = address;
        table[targetIndex].size = size;
        table[targetIndex].deleted = false;
        current_size++;
    }
}

// method to remove an item from hash table (this only marks entry as deleted, memory deallocation is in myMalloc methods
void myHashTable::remove(void* address) {
    // find the address.
    size_t index = find(address);
    // if not found handle error with log to terminal and empty return.
    if (index == SIZE_MAX) {
        std::cout << "Address " << address << " not found. (In myHashTable.remove) " << std::endl;
        return;
    }
    // mark as deleted and decrement current_size if deletion was performed
    table[index].deleted = true;
    current_size--;
}

// method to find a given address in hash table
size_t myHashTable::find(void* address) {
    // start by hashing to get anticipated location
    size_t index = hash(address) % this->capacity;
    // Check if the entry at the hash value's location has the same address and is not deleted
    while (table[index].address != nullptr) {
        if (table[index].address == address && !table[index].deleted) {
            return index;  // Found the entry that is not deleted
        }
        // Mif not found, move to the next index (circular probing)
        index = (index + 1) % this->capacity;
    }
    // If not found or reached an empty slot, return SIZE_MAX to indicate error
    return SIZE_MAX;
}

//---------------------------------------------------------------------------------------------------------------------
// Getters are self-explanatory

size_t myHashTable::getCapacity() {
    return this->capacity;
}
void* myHashTable::getLocation() {
    return static_cast<void*>(this->table);
}
void* myHashTable::getAddress(size_t index) {
    if (index >= this->capacity || index < 0) {
        throw std::out_of_range("index out of range (In myHashTable.getAddress) ");
    }
    return table[index].address;
}
size_t myHashTable::getSize(size_t index) {
    if (index >= this->capacity || index < 0) {
        throw std::out_of_range("index out of range (In myHashTable.getSize) ");
    }
    return table[index].size;
}

//---------------------------------------------------------------------------------------------------------------------
// Print method to print table contents for testing/debugging

void myHashTable::print() {
    std::cout << "----------------------" << std::endl;
    std::cout << "Hash table at: " << this->table << std::endl;
    std::cout << "Capacity: " << this->capacity << std::endl;
    std::cout << "Current size: " << this->current_size << std::endl;
    std::cout << "----------------------" << std::endl;
    for (int i = 0; i < this->capacity; i++) {
        std::cout << "Entry: " << std::endl;
        std::cout << "    Address: " << this->table[i].address << std::endl;
        std::cout << "    Size: " << this->table[i].size << std::endl;
        std::cout << "    Deleted: " << this->table[i].deleted << std::endl;
        std::cout << std::endl;
    }
    std::cout << "----------------------" << std::endl;
}


//---------------------------------------------------------------------------------------------------------------------