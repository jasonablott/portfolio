////////////////////////////////////////////////////////////////////////
//
// Author: Jason Ablott
// Date: 04/01/2025
//
// CS 6013
//
// Testing code for the concurrent version of the queue
//
////////////////////////////////////////////////////////////////////////

#include <iostream>
#include <ostream>
#include <vector>
#include <thread>
#include "ConcurrentQueue.cpp"

#pragma once

// Compile & link with ConcurrentQueue.cpp with <g++ -std=c++20 -o ConcurrentTest ConcurrentQueue.cpp ConcurrentQueueTest.cpp>
// Then <./ConcurrentTest 1000 1000 1000> to test with 1000 producers, consumers, and integers.

/*
 * function to test a concurrent queue with a given number of producers and consumers for a given
 * number of integer items.
 *
 * This version starts all producers first, then all consumers
 *
 */
bool ProducersThenConsumers( int num_producers, int num_consumers, int num_ints ) {
    // a. Create a std::vector of std::threads.
    std::vector<std::thread> producerThreads;
    // b. reserve space in this vector for all producer threads
    producerThreads.reserve(num_producers);
    // c. Create a ConcurrentQueue object statically.
    ConcurrentQueue<int> queue;
    // d. Create num_producer producer threads that enqueue num_ints ints into the ConcurrentQueue.
    for( int i = 0; i < num_producers; ++i ) {
        producerThreads.emplace_back([&queue, num_ints]{
            for( int j = 0; j < num_ints; ++j ) {
                queue.enqueue( j );
         }
        });
    }
    // e. Wait for all producer threads to join (i.e., finish).
    for (std::thread& t : producerThreads) {
        t.join();
    }
    // f. Create another std::vector of std::threads.
    std::vector<std::thread> consumerThreads;
    // g. reserve space in this vector for all consumer threads
    consumerThreads.reserve(num_consumers);
    // h. Create num_consumer consumer threads that dequeue num_ints ints from the ConcurrentQueue.
    for( int i = 0; i < num_consumers; ++i ) {
        consumerThreads.emplace_back([&queue, num_ints]{
            for( int j = 0; j < num_ints; ++j ) {
                int expected;
                queue.dequeue(expected);
            }
        });
    }
    // i. Wait for all consumer threads to join (ie, finish).
    for (std::thread& t : consumerThreads ) {
        t.join();
    }
    // j. Return true if number of elements in queue = (num_producers - num_consumers)*num_ints, false otherwise.
    int expectedRemaining = 0;
    if (num_consumers > num_producers) {
        expectedRemaining = 0;// if more consumers than producers, there should be 0 items left in queue
    } else {
        expectedRemaining= (num_producers - num_consumers) * num_ints; // else do the math to find out expected
    }
    int actualRemaining = queue.getSize(); // find out how many items are actually left in the queue
    std::cout << "      Expected remaining: " << expectedRemaining << std::endl;
    std::cout << "      Actual remaining:   " << actualRemaining << std::endl;
    return(expectedRemaining == actualRemaining); // return expected vs actual to check if test worked
}

/*
 * function to test a concurrent queue with a given number of producers and consumers for a given
 * number of integer items.
 *
 * This version starts running consumers first, then waits 1 second, then starts running producers
 *
 */
bool WaitBetween(int num_producers, int num_consumers, int num_ints) {
    // a. Create a std::vector of std::threads.
    std::vector<std::thread> threads;
    // b. Reserve space in this vector for all producer and consumer threads.
    threads.reserve(num_producers + num_consumers);
    // c. Create a ConcurrentQueue object statically.
    ConcurrentQueue<int> queue;
    // d. Create a mutex to synchronize the start of consumers after 1 sec delay.
    std::mutex mtx;
    bool producers_done = false;
    // e. Create num_producer producer threads that enqueue num_ints ints into the ConcurrentQueue.
    for (int i = 0; i < num_producers; ++i) {
        threads.emplace_back([&queue, num_ints, &mtx, &producers_done]{
            // Enqueue items into the queue.
            for (int j = 0; j < num_ints; ++j) {
                queue.enqueue(j);
            }
            // After producing all items, notify the consumers.
            {
                std::lock_guard<std::mutex> lock(mtx);
                producers_done = true;
            }
        });
    }
    // f. Wait 1 second before starting consumers.
    std::this_thread::sleep_for(std::chrono::seconds(1));
    // g. Create num_consumer consumer threads that dequeue num_ints ints from the ConcurrentQueue.
    for (int i = 0; i < num_consumers; ++i) {
        threads.emplace_back([&queue, num_ints, &mtx, &producers_done]{
            // Wait for producers to finish (will get signal after 1 second delay).
            {
                std::unique_lock<std::mutex> lock(mtx);
                while (!producers_done) {
                    lock.unlock();
                    std::this_thread::sleep_for(std::chrono::milliseconds(10)); // Sleep briefly before checking again
                    lock.lock();
                }
            }
            // Dequeue items from the queue.
            for (int j = 0; j < num_ints; ++j) {
                int expected;
                queue.dequeue(expected);
            }
        });
    }
    // h. Wait for all threads to join (i.e., finish).
    for (std::thread& t : threads) {
        t.join();
    }
    // i. Return true if number of elements in queue = (num_producers - num_consumers)*num_ints, false otherwise.
    int expectedRemaining = 0;
    if (num_consumers > num_producers) {
        expectedRemaining = 0; // If more consumers than producers, the queue should be empty.
    } else {
        expectedRemaining = (num_producers - num_consumers) * num_ints;
    }
    int actualRemaining = queue.getSize(); // find out how many items are actually left in the queue
    std::cout << "      Expected remaining: " << expectedRemaining << std::endl;
    std::cout << "      Actual remaining:   " << actualRemaining << std::endl;
    return (expectedRemaining == actualRemaining); // return expected vs actual to check if test worked
}

/*
 * function to test a concurrent queue with a given number of producers and consumers for a given
 * number of integer items.
 *
 * This version starts running and producers all at the same time
 *
 */
bool SameStartTime(int num_producers, int num_consumers, int num_ints) {
    int DQmisses = 0;  // Initialize the dequeue misses counter for attempts at dequeueing an empty queue
    std::mutex misses_mtx;  // Mutex to protect DQmisses
    // a. Create a std::vector of std::threads.
    std::vector<std::thread> threads;
    // b. Reserve space in this vector for all producer and consumer threads.
    threads.reserve(num_producers + num_consumers);
    // c. Create a ConcurrentQueue object statically.
    ConcurrentQueue<int> queue;
    // d. Create a mutex and condition variable to synchronize thread starts.
    std::mutex mtx;
    std::condition_variable cv;
    bool ready = false;
    // e. Create num_producer producer threads.
    for (int i = 0; i < num_producers; ++i) {
        threads.emplace_back([&queue, num_ints, &mtx, &cv, &ready]{
            // Wait for the signal to start.
            {
                std::unique_lock<std::mutex> lock(mtx);
                cv.wait(lock, [&ready] { return ready; });
            }
            // Perform the work after the signal to start.
            for (int j = 0; j < num_ints; ++j) {
                queue.enqueue(j);
            }
        });
    }
    // f. Create num_consumer consumer threads.
    for (int i = 0; i < num_consumers; ++i) {
        threads.emplace_back([&queue, num_ints, &mtx, &cv, &ready, &DQmisses, &misses_mtx]{
            // Wait for the signal to start.
            {
                std::unique_lock<std::mutex> lock(mtx);
                cv.wait(lock, [&ready] { return ready; });
            }
            // Perform the work after the signal to start.
            for (int j = 0; j < num_ints; ++j) {
                int expected;
                if (!queue.dequeue(expected)) {
                    // Increment the DQmisses counter if a dequeue attempt fails, protect with mutex for accuracy
                    std::lock_guard<std::mutex> lock(misses_mtx);
                    DQmisses++;
                }
            }
        });
    }
    // g. Start all threads by notifying them.
    {
        std::lock_guard<std::mutex> lock(mtx);
        ready = true;
    }
    cv.notify_all();
    // h. Wait for all threads to join (i.e., finish).
    for (std::thread& t : threads) {
        t.join();
    }
    // i. The test passes if the number of misses equals the expected remaining items
    int expectedRemaining = 0;
    if (num_consumers == num_producers) {
        expectedRemaining = DQmisses; // If more consumers than producers, the queue should only have missed items.
    } else {
        expectedRemaining = DQmisses + ((num_producers - num_consumers) * num_ints);
    }
    int actualRemaining = queue.getSize(); // find out how many items are actually left in the queue
    std::cout << "      Expected remaining: " << expectedRemaining << std::endl;
    std::cout << "      Actual remaining:   " << actualRemaining << std::endl;
    return (actualRemaining == expectedRemaining); // return expected vs actual to check if test worked
}


// main code to run tests here:
int main(int argc, char **argv) {
    // use argv to read in num_producers, num_consumers, and num_ints from the command line.
    int num_producers = std::stoi(argv[1]);
    int num_consumers = std::stoi(argv[2]);
    int num_ints = std::stoi(argv[3]);
    // Call ProducersThenConsumers test with these parameters
    std::cout << "\nProducers then Consumers Test:" << std::endl; // 35
    bool result1 = ProducersThenConsumers( num_producers, num_consumers, num_ints );
    // check the return value to make sure code is working correctly
    if ( result1 ) {
        std::cout << "Producers then Consumers Test:    PASS" << std::endl; // 35
    } else {
        std::cout << "Producers then Consumers Test:    FAIL" << std::endl;
    }
    // Call WaitBetween test with these parameters
    std::cout << "\nWait Between Test:" << std::endl;
    bool result2 = WaitBetween( num_producers, num_consumers, num_ints );
    // check the return value to make sure code is working correctly
    if ( result2 ) {
        std::cout << "Wait Between Test:                 PASS" << std::endl;
    } else {
        std::cout << "Wait Between Test:                 FAIL" << std::endl;
    }
    // Call SameStartTime test with these parameters
    std::cout << "\nSame Start Time Test:" << std::endl;
    bool result3 = SameStartTime( num_producers, num_consumers, num_ints );
    // check the return value to make sure code is working correctly
    if ( result3 ) {
        std::cout << "Same Start Time Test:              PASS" << std::endl;
    } else {
        std::cout << "Same Time Test:              FAIL" << std::endl;
    }
    std::cout << "\n";
    return 0;
}