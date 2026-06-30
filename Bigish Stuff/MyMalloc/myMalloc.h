/*
* Author: Jason Ablott
 * Date: 2/22/2025
 * Class: CS 6013
 * Project: A4 Malloc Replacement
 * File: myMalloc.h
 **/
//---------------------------------------------------------------------------------------------------------------------


#ifndef MYMALLOC_H
#define MYMALLOC_H

#include "myHashTable.h"


//---------------------------------------------------------------------------------------------------------------------
// myMalloc class


/*! \brief creates and stores a hash table to track blocks of allocated memory. Provides user ability to allocate
 * new blocks of memory of a specified size and to deallocate any memory block allocated at a specified address.
 */
class myMalloc {

    myHashTable myTable; //!< this is the hash table of addresses and sizes this myMalloc uses for memory tracking.

    /**
    * \brief this method uses the myHashTable insert function to add an entry to the table.
    *
    * \param address the virtual address of the newly allocated block of memory, returned from mmap()
    * \param size the size in bytes of memory allocated in virtual memory at address
    */
    void storeAllocationInTable(void* address, size_t size);

    /**
    * \brief this function uses the myHashTable remove() function which uses the myHashTable find() function to lazy
    * delete an entry.
    *
    * \param address the virtual address of the memory block to be deleted from the table.
    */
    void deleteAllocationInTable(void* address);

public:

    /**
    * \brief this is the default constructor to return a new myMalloc object, which will contain a new myHashTable
    * object to store mapped memory blocks and their sizes.
    */
    myMalloc();

    /**
    * \brief this is the default destructor to destroy a myMalloc object. It deallocates any allocated memory
    * stored in its myHashTable by calling munmap() on all active entries, and the memory used to store the hash table.
    */
    ~myMalloc();

    /**
    * \brief allocates new memory of a certain size. This uses the mmap() system call to get an address of virtual
    * memory that is large enough to place the block at, and then adds an entry to the hash table to store the newly
    * allocated address and size of the block for future use. If mmap() returns MAP_FAILED return nullptr.
    *
    * \param bytes_to_allocate the amount of bytes of memory needed.
    * \return void* a pointer to the newly allocated block of memory of size bytes_to_allocate.
    */
    void* allocate(size_t bytes_to_allocate);


    /**
    * \brief de-allocates memory at a certain address. This method will use the munmap() system call to deallocate the
    * virtual memory at the provided address. Then, this will use the deleteAllocationInTable() private method which
    * uses the myHashTable remove method to mark the block as deleted in the hash table.
    *
    * \param address the address to deallocate from virtual memory and remove from the hash table.
    */
    void deallocate(void* address);

    /**
    * \brief print the contents of the hash table for testing purposes.
    */
    void printTable();

};

#endif