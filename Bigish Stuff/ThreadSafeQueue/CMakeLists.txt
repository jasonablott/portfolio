cmake_minimum_required(VERSION 3.30)
project(ThreadSafeQueue)

set(CMAKE_CXX_STANDARD 20)

add_executable(ThreadSafeQueue
        SerialQueue.cpp
        ConcurrentQueue.cpp
        SerialQueueTest.cpp
        ConcurrentQueueTest.cpp)
