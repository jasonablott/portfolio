////////////////////////////////////////////////////////////////////////
//
// Author: Jason Ablott
// Date: 04/01/2025
//
// CS 6013
//
// Testing code for the serial version of the queue
//
////////////////////////////////////////////////////////////////////////

#include <cassert>
#include <cstdlib>
#include <iostream>
#include <ostream>

#include "SerialQueue.cpp"

#pragma once

// Compile & link with SerialQueue.cpp with <g++ -std=c++20 -o SerialTest SerialQueue.cpp SerialQueueTest.cpp>
// Then <./SerialTest> to test with TEST_SIZE producers, consumers, and integers.
const int TEST_SIZE = 10000;

/*
 * function that takes a queue and # iterations, then enqueue and dequeue that many random elements.
 * Verifies that elements are dequeued in reverse order of enqueueing (if enqueued 1 first, first dequeue returns 1.)
 */
bool SerialQueueDynamicTest(SerialQueue<int> queue, int numElements) {
    // make a list to store data for confirming ordering
    int* myElements = new int[numElements];
    // enqueue and dequeue that many random elements.
    for (int i = 0; i < numElements; i++) {
        int randomNumber = rand() % numElements;
        // store number in list for later confirmation
        myElements[i] = randomNumber;
        // add number to queue
        queue.enqueue(randomNumber);
    }
    for (int i = 0; i < numElements; i++) {
        int ret;
        // dequeue and confirm value using list
        if (!queue.dequeue(ret)) {
            std::cerr << "Dequeue failed" << std::endl;
            delete[] myElements;
            return false;
        }
        if (ret != myElements[i]) {
            std::cerr << "Dequeue mismatch" << std::endl;
            delete[] myElements;
            return false;
        }
    }
    delete[] myElements;
    return true;
}

/*
 * main code to run tests here: Part of this test would include enqueuing 100 (as specified) integers,
 * and the verifying that when you dequeue them, you get the correct values back.
 */
int main() {


    // Basic testing: manually add 1-2 values to a queue and immediately dequeue


    // Test creation of queue
    SerialQueue<int> queue1;

    // Test enqueuing
    queue1.enqueue(1); // add an integer
    assert(queue1.size() == 1); // make sure size is correct
    queue1.enqueue(2); // add another integer
    assert(queue1.size() == 2); // make sure size is correct

    // Test dequeueing
    int result; // result variable to store dequeue data
    queue1.dequeue(result); // dequeue an element
    // make sure the correct element was removed
    if (result != 1) { std::cerr << "Dequeue failed" << std::endl; }
    assert(queue1.size() == 1); // make sure size is still correct
    queue1.dequeue(result); // dequeue an element
    // make sure the correct element was removed
    if (result != 2) { std::cerr << "Dequeue mismatch" << std::endl; }
    assert(queue1.size() == 0); // make sure size is still correct

    // all done with basic tests
    std::cout << "Basic tests passed" << std::endl;


    // Dynamic testing:


    // testing variables
    int numElements = TEST_SIZE;
    SerialQueue<int> testQueue;
    // run test
    bool testResult = SerialQueueDynamicTest(testQueue, numElements);
    // report result
    if (testResult == true) {
        std::cout << "Test with " << numElements << " random integers passed" << std::endl;
    } else {
        std::cout << "Test with " << numElements << " random integers failed" << std::endl;
    }

    // all done with dynamic testing
    std::cout << "Dynamic tests passed" << std::endl;

    // all done with testing
    return 0;
}
