////////////////////////////////////////////////////////////////////////
//
// Author: Jason Ablott
// Date: 04/01/2025
//
// CS 6013
//
// Outline for ConcurrentQueue class.
//
////////////////////////////////////////////////////////////////////////

#include <mutex>

#pragma once

/*
 * Concurrent queue class that uses a 3rd dummy node and locks to help with synchronous enqueue/dequeue operations
 */
template <typename T>
class ConcurrentQueue {

public:

    /*
     * default constructor to initialize a new linked list queue
     */
    ConcurrentQueue() :
       head_( new Node{ T{}, nullptr } )
    {
        tail_ = head_;
    }

    /*
     * method to add a new node at the tail of the linked list.
     */
    void enqueue( const T & x ) {
        Node * NQNode = new Node{ x, nullptr }; // make a new node with the given data
        { // now we are modifying shared data in a threaded environment, so we need to lock
            std::unique_lock<std::mutex> lock(NQmutex); // use std::unique_lock (scoped)
            tail_->next = NQNode; // link the tail to the new node that we are enqueueing
            tail_ = NQNode; // update tail pointer to point to new node
        } // our lock is scoped, so it will unlock here when it goes out of scope
    }

    /*
     * removes a node from the head of the linked list, and returns the data at the new head in the variable ret.
     * If the queue is empty, dequeue returns false. If an element was dequeued successfully, dequeue returns true.
     */
    bool dequeue( T & ret ) {
        { // now we are modifying shared data in a threaded environment, so we need to lock
            std::unique_lock<std::mutex> lock(DQmutex); // lock before we do anything (coarse scoped lock)
            Node * temp = head_; // get pointer to the head, which is the dummy node
            Node * newHead = temp->next; // get first real node in queue (head_->next)
            if ( newHead == nullptr ) { // handle empty queue
                return false;
            }
            ret = newHead->data; // put data into ret for checks etc.
            head_ = newHead; // update head pointer to next item
            delete temp; // delete the item we are dequeuing

        } // our lock is scoped, so it will unlock here when it goes out of scope here
        return true;
    }

    /*
     * default destructor to initialize a new linked list queue
     */
    ~ConcurrentQueue() {
        while( head_ != nullptr ) {
            Node* temp = head_->next;
            delete head_;
            head_ = temp;
        }
    }

    /*
    * method to check if queue is empty
    */
    bool isEmpty() {
        return head_ == nullptr || head_->next == nullptr;
    }

    /*
    * method to get number of elements in queue
    */
    int getSize() {
        if (head_->next == nullptr) { return 0; } // handle empty queue
        Node * temp = head_->next; // node for iterating
        int size = 0; // start size at 0
        while( temp != nullptr ) { // iterate through nodes
            size++;
            temp = temp->next;
        }
        return size;
    }

private:

    /*
     * Node struct definition to define what a node is
     */
    struct Node {
        T      data;
        Node * next;
    };

    // this queue's member variables
    Node * head_;
    Node * tail_;
    std::mutex NQmutex; // mutex for locking enqueueing
    std::mutex DQmutex; // mutex for locking dequeueing

};