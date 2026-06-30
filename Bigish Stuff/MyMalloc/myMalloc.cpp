/*
 * Author: Jason Ablott
 * Date: 2/22/2025
 * Class: CS 6013
 * Project: A4 Malloc Replacement
 * File: myMalloc.cpp
 **/
//---------------------------------------------------------------------------------------------------------------------


#include "myMalloc.h"
#include <iostream>
#include <ostream>
#include <sys/mman.h>


//---------------------------------------------------------------------------------------------------------------------
// Private myMalloc methods


// private method used to call myHashTable insert method
void myMalloc::storeAllocationInTable(void* address, size_t size) {
    myTable.insert(address, size);
};

// private method used to call myHashTable remove method
void myMalloc::deleteAllocationInTable(void* address) {
    myTable.remove(address);
}


//---------------------------------------------------------------------------------------------------------------------
// Public myMalloc methods


// default constructor for myMalloc object
myMalloc::myMalloc() = default;

//default destructor for myMalloc object
myMalloc::~myMalloc() {
    // loop through all entries in the table
    for (int i = 0; i < myTable.getCapacity(); i++) {
        // if the entry isn't empty and isn't marked deleted, use munmap() to deallocate the virtual memory
        void* address = myTable.getAddress(i);
        size_t size = myTable.getSize(i);
        if (address != nullptr && size > 0) {
            if(munmap(address, size) == -1 ) {
                perror("munmap error in myMalloc::myMalloc() destructor");
            }
        }
    }
    // munmap the table memory now that we are done with it as well
    if (munmap(myTable.getLocation(), myTable.getCapacity() * sizeof(Entry)) == -1 ) {
        perror ("munmap error in myMalloc::deleteAllocationInTable");
    }
}

// method to allocate memory with mmap and mhHashTable.insert
void* myMalloc::allocate(size_t bytes_to_allocate) {
    // make sure value is a valid
    if (bytes_to_allocate == 0 || bytes_to_allocate >= SIZE_MAX) {
        std::cerr << "Allocation size not allowed (In myMalloc.allocate) " << std::endl;
        return nullptr;
    }
    // call mmap() to allocate the necessary memory in virtual memory. If the return is MAP_FAILED return nullptr
    // immediately
    void* location = mmap(nullptr, bytes_to_allocate, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
    if (location == MAP_FAILED) {
        perror("Allocation failed (In myMalloc.allocate) ");
        return nullptr;
    }
    // use the return pointer from mmap and the size bytes_to_allocate to add an entry to myTable.table with
    // the storeAllocationInTable method
    storeAllocationInTable(location, bytes_to_allocate);
    // return the pointer to the allocated block to the caller
    return location;
}

// method to deallocate memory with munmap and myHashTable.remove
void myMalloc::deallocate(void* address) {
    // check that the address is valid
    if (address == nullptr) {
        std::cerr << "Invalid address to de-allocate (In myMalloc.deallocate)" << std::endl;
        return;
    }
    // call munmap() on the address to deallocate the virtual memory. If return is SIZE_MAX it failed, use perror()
    size_t index = myTable.find(address);
    if (index == SIZE_MAX) {
        perror("de-allocation failed, item not in table (In myMalloc.deallocate) ");
        return;
    }
    // get size from table to use when calling munmap
    size_t size = myTable.getSize(index);
    // deallocate virtual memory using munmap
    if (munmap(address, size) != 0) {
        perror("de-allocation failed, address not allocated (In myMalloc.deallocate) ");
        return;
    }
    // use the deleteAllocationInTable method to delete it from myTable
    deleteAllocationInTable(address);
}

// print the contents of the hash table for testing purposes
void myMalloc::printTable() {
    myTable.print();
}


//---------------------------------------------------------------------------------------------------------------------