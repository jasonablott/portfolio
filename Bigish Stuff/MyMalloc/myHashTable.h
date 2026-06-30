/*
* Author: Jason Ablott
 * Date: 2/22/2025
 * Class: CS 6013
 * Project: A4 Malloc Replacement
 * File: myHashTable.h
 **/
//---------------------------------------------------------------------------------------------------------------------


#ifndef MYHASHTABLE_H
#define MYHASHTABLE_H

#include <cstddef>


//---------------------------------------------------------------------------------------------------------------------
// Entry struct for hash table to use


/*! \brief struct to represent each entry in the hash table. An entry contains the virtual memory address of the block,
 * the size of the block in bytes, and a boolean to indicate if the memory has been deallocated/deleted. This allows the
 * implementation of lazy deletion in the hash table.
 */
struct Entry {
    void* address; //!< the virtual address of this chunk of memory that has been allocated
    size_t size; //!< the size of this chunk of memory that has been allocated, in bytes
    bool deleted; //!< boolean representing if this entry been de-allocated/deleted
    /**
    * \brief constructor to create a new entry of given or default values.
    */
    explicit Entry(void* address = nullptr, size_t size = 0, bool deleted = false) :
    address(address), size(size), deleted(deleted) {}
};


//---------------------------------------------------------------------------------------------------------------------
// myHashTable class


/*! \brief allows storage of memory addresses and the size of the block at that address. Public methods allow the user
 * to create a new myHashTable object, insert entries provided a virtual memory address and size in bytes, and remove
 * an entry provided the virtual address of the entry.
 */
class myHashTable {

    size_t capacity; //!< the capacity of this hash table (Max # entries)
    size_t current_size; //!< current size of the table (number of entries)
    Entry* table; //!< the hash table itself is an array of entries starting at this entry
    float growThresholdPercentage = 70.0;

    /**
    * \brief double the size of the hash table and rehash contents of the old table into the new table. Uses std::swap
    * to swap the new table with the old one and deallocate the old table within this method. Deleted items do not need
    * to be copied over. This method uses a call to mmap() via a constructor to allocate memory for the new table.
    */
    void grow();

    /**
    * \brief this is the hash function to find an entry in the table. It returns the index of the entry in the table
    * The hash function uses < x >> VirtualAddressOffsetSizeInBits > to hash the address.
    *
    * \param address the address to be hashed to place an entry representing it into the table.
    * \return size_t the number representing the index of the address in the hash table based on its hashing.
    */
    size_t hash(void* address);

    /**
    * \brief circular buffer linear probing to search for empty slot in array-backed hash table.
    *
    * \return size_t the next available index that can be used. Returns SIZE_MAX if no usable index is found.
    * \param startIndex the index to begin probing at and step forward from.
    */
    size_t linearProbe(size_t startIndex);

public:

    /**
    * \brief the default constructor to create a new myHashTable object. This constructor should create a new table
    * of a default capacity 4, and set the capacity to that default capacity and current_size to 0.
    *
    * \return a new myHashTable object.
    */
    myHashTable();

    /**
    * \brief the parameterized constructor to create a new myHashTable object. This constructor creates a new table
    * of a given capacity, and sets the capacity to that given capacity and current_size to 0.
    *
    * \return a new myHashTable object.
    */
    explicit myHashTable(size_t capacity);

    /**
    * \brief inserts an item into the hash table. If table doesn't have room, call the private grow()
    * function to make room. This uses the hash function to place the entry, and uses linear probing if a
    * collision occurs upon the placement attempt. If a collision occurs with a deleted entry it overwrites that
    * entry. This method will also increment the current_size variable if an empty slot is filled.
    *
    * \param address the address of the new entry to be added to the hash table.
    * \param size the size, in bytes, of the new entry to be added to the table.
    */
    void insert(void* address, size_t size);

    /**
    * \brief removes an item from the table. This method uses the find() method to look up the entry, and then marks
    * it as deleted.
    *
    * \param address the address of the entry to be removed from the table.
    */
    void remove(void* address);

    /**
    * \brief return the index of an entry. It will hash an address, and then if it does not exist at the given index
    * it will use linear probing until it is located, and return the index in the table where it is located. If it is
    * found but deleted, keep searching. If an empty slot is found while probing it is assumed the address does not
    * exist in the table and the method indicates that by returning SIZE_MAX.
    *
    * \param address the address of an entry we wish to find in the table.
    * \return size_t the index in the hash table of the active entry containing address.
    */
    size_t find(void* address);

    /**
    * \brief get the capacity of this table.
    *
    * \return size_t the capacity of this table.
    */
    size_t getCapacity();

    /**
    * \brief get the location of this table in virtual memory.
    *
    * \return void * the location of this table in virtual memory.
    */
    void* getLocation();

    /**
    * \brief get the address at a given index.
    *
    * \param index the index of this table to get the address at.
    * \return void* the address of this block of memory.
    */
    void* getAddress(size_t index);

    /**
    * \brief get the size at a given index.
    *
    * \param index the index of this table to get the size at.
    * \return size_t the size of this block of memory.
    */
    size_t getSize(size_t index);

    /**
    * \brief print the contents of the hash table for testing purposes.
    */
    void print();
};

#endif