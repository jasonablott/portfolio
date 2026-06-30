// /*
// * Author: Jason Ablott
//  * Date: 2/22/2025
//  * Class: CS 6013
//  * Project: A4 Malloc Replacement
//  * File: main.cpp
//  **/
//---------------------------------------------------------------------------------------------------------------------

/*
 * Just run the program to run tests. Some constants can be modified in test function calls in main.
 * See corresponding comments for those details.
 **/

#include <iostream>
#include <chrono>
#include <vector>
#include <cstdlib>
#include <ctime>
#include "myMalloc.h"


//---------------------------------------------------------------------------------------------------------------------


// define a myMalloc object
static myMalloc myMallocObj = myMalloc();

// Function to run malloc test with multiple repeats for averaging
void timeAllocationAndDeallocation(const int numIterations, const int numRepeats);

// Function to run malloc test over a range of iterations
void runTimedTests(int numRepeats);

// Function to stress test the malloc with random allocation and deallocation
void randomAllocateDeallocate(int numAllocations);

// Boundary test (allocate 1 byte)
void boundaryTest();

// Boundary test (allocate 0 bytes)
void boundaryTestAllocateNone();

// Large block allocation test
void largeBlockTest();

// Memory leak test (allocate but don't deallocate)
void memoryLeakTest();

// Stress test (allocate a very large number of small objects)
void stressTest();


//---------------------------------------------------------------------------------------------------------------------


// Main test program
int main() {


//---------------------------------------------------------------------------------------------------------------------
// Manual tests for initial debugging, written by hand.


    // basic test, allocate 7 ints
    int* myInt0 = static_cast<int*> (myMallocObj.allocate(sizeof(int)));
    int* myInt1 = static_cast<int*> (myMallocObj.allocate(sizeof(int)));
    int* myInt2 = static_cast<int*> (myMallocObj.allocate(sizeof(int)));
    int* myInt3 = static_cast<int*> (myMallocObj.allocate(sizeof(int)));
    int* myInt4 = static_cast<int*> (myMallocObj.allocate(sizeof(int)));
    int* myInt5 = static_cast<int*> (myMallocObj.allocate(sizeof(int)));
    int* myInt6 = static_cast<int*> (myMallocObj.allocate(sizeof(int)));

    // add data
    *myInt0 = 0;
    *myInt1 = 1;
    *myInt2 = 2;
    *myInt3 = 3;
    *myInt4 = 4;
    *myInt5 = 5;
    *myInt6 = 6;

    // print table
    myMallocObj.printTable();

    // make sure data is stored correctly
    std::cout << "access variables: " << std::endl;
    std::cout <<*myInt0 << std::endl;
    std::cout <<*myInt1 << std::endl;
    std::cout <<*myInt2 << std::endl;
    std::cout <<*myInt3 << std::endl;
    std::cout <<*myInt4 << std::endl;
    std::cout <<*myInt5 << std::endl;
    std::cout <<*myInt6 << std::endl;

    // deallocate the memory
    myMallocObj.deallocate(myInt0);
    myMallocObj.deallocate(myInt1);
    myMallocObj.deallocate(myInt2);
    myMallocObj.deallocate(myInt3);
    myMallocObj.deallocate(myInt4);
    myMallocObj.deallocate(myInt5);
    myMallocObj.deallocate(myInt6);

    // print to verify deallocation
    myMallocObj.printTable();

    // reallocate 7 items
    int* myInt7 = static_cast<int*> (myMallocObj.allocate(sizeof(int)));
    int* myInt8 = static_cast<int*> (myMallocObj.allocate(sizeof(int)));
    int* myInt9 = static_cast<int*> (myMallocObj.allocate(sizeof(int)));
    int* myInt10 = static_cast<int*> (myMallocObj.allocate(sizeof(int)));
    int* myInt11 = static_cast<int*> (myMallocObj.allocate(sizeof(int)));
    int* myInt12 = static_cast<int*> (myMallocObj.allocate(sizeof(int)));
    int* myInt13 = static_cast<int*> (myMallocObj.allocate(sizeof(int)));

    // add data
    *myInt7 = 7;
    *myInt8 = 8;
    *myInt9 = 9;
    *myInt10 = 10;
    *myInt11 = 11;
    *myInt12 = 12;
    *myInt13 = 13;

    // print table
    myMallocObj.printTable();

    // make sure data is stored correctly
    std::cout << "access variables: " << std::endl;
    std::cout <<*myInt7 << std::endl;
    std::cout <<*myInt8 << std::endl;
    std::cout <<*myInt9 << std::endl;
    std::cout <<*myInt10 << std::endl;
    std::cout <<*myInt11 << std::endl;
    std::cout <<*myInt12 << std::endl;
    std::cout <<*myInt13 << std::endl;

    // deallocate the memory
    myMallocObj.deallocate(myInt7);
    myMallocObj.deallocate(myInt8);
    myMallocObj.deallocate(myInt9);
    myMallocObj.deallocate(myInt10);
    myMallocObj.deallocate(myInt11);
    myMallocObj.deallocate(myInt12);
    myMallocObj.deallocate(myInt13);

    // print to verify deallocation
    myMallocObj.printTable();


//---------------------------------------------------------------------------------------------------------------------
// More advanced testing, ***written with assistance from ChatGPT*** See below for implementations.


    // Test random allocation/deallocation, input is number of allocations to make
    randomAllocateDeallocate(500);

    // Boundary test (1 byte allocation)
    boundaryTest();

    // Boundary test (0 byte allocation) This SHOULD throw an error which shows up later than expected in the
    // terminal due to I/O delays
    boundaryTestAllocateNone();

    // Large block allocation test (test with large memory blocks)
    largeBlockTest();

    // Memory leak test
    memoryLeakTest();

    // Stress test (allocate many objects)
    stressTest();

    // Run timed performance tests, input is number of times to repeat at each iteration
    runTimedTests(100);
}

// Function to stress test the malloc with random allocation and deallocation sizes
void randomAllocateDeallocate(int numAllocations) {
    std::cout << "\nRandom Allocation/Deallocation Test:" << std::endl;
    const int myAllocations = numAllocations; // Number of allocations and deallocations
    int* allocations[myAllocations];

    srand(static_cast<unsigned>(time(0)));  // Seed the random number generator

    // Randomly allocate memory
    for (int i = 0; i < myAllocations; ++i) {
        allocations[i] = static_cast<int*>(myMallocObj.allocate(rand() % 1000 + 1)); // Random size between 1 and 1000 bytes
        if (allocations[i] == nullptr) {
            std::cerr << "Error: Allocation failed for index " << i << " with size " << rand() % 1000 + 1 << " bytes." << std::endl;
        }
    }

    // Deallocate random memory
    for (int i = 0; i < numAllocations; ++i) {
        myMallocObj.deallocate(allocations[i]);
    }
}

// Boundary test (allocate 1 byte)
void boundaryTest() {
    std::cout << "\nBoundary Test (Allocating 1 byte):" << std::endl;
    void* ptr = myMallocObj.allocate(1); // Allocating 1 byte
    if (ptr == nullptr) {
        std::cerr << "Error: Allocation of 1 byte failed!" << std::endl;
    }
    myMallocObj.deallocate(ptr);  // Deallocating 1 byte
}

// Boundary test (allocate 0 bytes)
void boundaryTestAllocateNone() {
    std::cout << "\nBoundary Test (Allocating 0 bytes):" << std::endl;
    void* ptr = myMallocObj.allocate(0); // Allocating 0 bytes
    if (ptr != nullptr) {
        std::cerr << "Error: Allocation of 0 did not return nullptr!" << std::endl;
    }
}

// Large block allocation test
void largeBlockTest() {
    std::cout << "\nLarge Block Allocation Test:" << std::endl;
    size_t largeSize = 10 * 1024 * 1024; // Allocating 10MB
    void* largeBlock = myMallocObj.allocate(largeSize);
    if (largeBlock == nullptr) {
        std::cerr << "Error: Failed to allocate a large block of size " << largeSize << " bytes." << std::endl;
    } else {
        std::cout << "Successfully allocated a large block of size " << largeSize << " bytes." << std::endl;
        myMallocObj.deallocate(largeBlock);
    }
}

// Memory leak test (allocate but don't deallocate)
void memoryLeakTest() {
    std::cout << "\nMemory Leak Test (Allocate but don't deallocate):" << std::endl;
    const int numAllocations = 1000;
    void* allocations[numAllocations];

    // Allocate memory but don't deallocate
    for (int i = 0; i < numAllocations; ++i) {
        allocations[i] = myMallocObj.allocate(100); // Allocate 100 bytes each
        if (allocations[i] == nullptr) {
            std::cerr << "Error: Allocation of 100 bytes failed at index " << i << "." << std::endl;
        }
    }
}

// Stress test (allocate a very large number of small objects)
void stressTest() {
    std::cout << "\nStress Test (Allocating many small objects):" << std::endl;
    const int numObjects = 100000; // 10,000 allocations
    int* objects[numObjects];

    // Allocate a large number of small objects
    for (int i = 0; i < numObjects; ++i) {
        objects[i] = static_cast<int*>(myMallocObj.allocate(sizeof(int)));
        if (objects[i] == nullptr) {
            std::cerr << "Error: Allocation failed for object " << i << "." << std::endl;
        }
    }

    std::cout << "Allocated " << numObjects << " small objects." << std::endl;

    // Deallocate all objects
    for (int i = 0; i < numObjects; ++i) {
        myMallocObj.deallocate(objects[i]);
    }

    std::cout << "Deallocated all objects." << std::endl;
}

// Function to run malloc test over a range of iterations and average results
void runTimedTests(int numRepeats) {
    // Define the iteration counts to test
    std::vector<int> iterationRanges = {1000, 10000, 100000, 1000000};

    // Run the test for each iteration range
    for (int iterations : iterationRanges) {
        std::cout << "\nTesting with " << iterations << " iterations..." << std::endl;

        // Timing
        std::cout << "\nTiming myMalloc:" << std::endl;
        timeAllocationAndDeallocation(iterations, numRepeats);  // Test using myMalloc (numRepeats repeats at each range)
    }
}

// Function to run malloc test for a given number of iterations and repeats
void timeAllocationAndDeallocation(const int numIterations, const int numRepeats) {
    const int Repeats = numRepeats;
    const int iterations = numIterations;

    auto totalDurationMyMalloc = std::chrono::milliseconds::zero();
    auto totalDurationSystemMalloc = std::chrono::milliseconds::zero();

    for (int i = 0; i < Repeats; ++i) {
        // Timing myMalloc
        auto start = std::chrono::high_resolution_clock::now();
        for (int i = 0; i < iterations; ++i) {
            int* temp = static_cast<int*>(myMallocObj.allocate(sizeof(int)));
            if (temp == nullptr) {
                std::cerr << "Error: Allocation failed during timing test!" << std::endl;
            }
            myMallocObj.deallocate(temp);
        }
        auto end = std::chrono::high_resolution_clock::now();
        totalDurationMyMalloc += std::chrono::duration_cast<std::chrono::milliseconds>(end - start);

        // Timing system malloc
        start = std::chrono::high_resolution_clock::now();
        for (int i = 0; i < iterations; ++i) {
            int* temp = static_cast<int*>(malloc(sizeof(int)));
            if (temp == nullptr) {
                std::cerr << "Error: Allocation failed during system malloc timing test!" << std::endl;
            }
            free(temp);
        }
        end = std::chrono::high_resolution_clock::now();
        totalDurationSystemMalloc += std::chrono::duration_cast<std::chrono::milliseconds>(end - start);
    }

    // Calculate and print average durations
    auto avgDurationMyMalloc = totalDurationMyMalloc.count() / Repeats;
    auto avgDurationSystemMalloc = totalDurationSystemMalloc.count() / Repeats;

    std::cout << "Average time for myMalloc with " << iterations << " iterations: " << avgDurationMyMalloc << " milliseconds." << std::endl;
    std::cout << "Average time for system malloc with " << iterations << " iterations: " << avgDurationSystemMalloc << " milliseconds." << std::endl;
}
