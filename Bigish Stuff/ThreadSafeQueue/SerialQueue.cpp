////////////////////////////////////////////////////////////////////////
//
// Author: Jason Ablott
// Date: 04/01/2025
//
// CS 6013
//
// Outline for SerialQueue class.  Fill in the missing data, comments, etc.
//
////////////////////////////////////////////////////////////////////////

#pragma once

/*
 * Serial queue class that uses a 3rd dummy node.
 */
template <typename T>
class SerialQueue {

public:

    /*
     * default constructor to initialize a new linked list queue
     */
    SerialQueue() :
       head_( new Node{ T{}, nullptr } ), size_( 0 )
    {
        tail_ = head_;
    }

    /*
     * method to add a new node at the tail of the linked list.
     */
    void enqueue( const T & x ) {
        // make a new node with the given data
        Node * newNode = new Node{ x, nullptr };
        // link the tail to the new node that we are enqueueing
        tail_->next = newNode;
        // update tail pointer to point to new node
        tail_ = newNode;
        // increment size as an element has been added
        size_++;
    }

    /*
     * removes a node from the head of the linked list, and returns the data at the new head in the variable ret.
     * If the queue is empty, dequeue returns false. If an element was dequeued successfully, dequeue returns true.
     */
    bool dequeue( T & ret ) {
        // handle empty queue
        if (size_ == 0) {
            return false;
        }
        // get first node in queue (head_->next)
        Node * DQNode = head_->next;
        // put data into ret for checks etc.
        ret = DQNode->data;
        // update head pointer to next item
        head_->next = DQNode->next;
        // delete the item we are dequeuing
        delete DQNode;
        size_--;
        return true;
    }

    /*
     * default destructor to initialize a new linked list queue
     */
    ~SerialQueue() {
        Node * current = head_->next;
        while(current != nullptr ) {
            Node* temp = current->next;
            delete current;
            current = temp;
        }
    }

    /*
     * method to get the size of this queue
     */
    int size() const { return size_; }

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
    int    size_;
};
