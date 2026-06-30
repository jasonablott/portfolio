cmake_minimum_required(VERSION 3.30)
project(A4Malloc)

set(CMAKE_CXX_STANDARD 17)

add_executable(A4Malloc main.cpp
        MyHashTable.cpp
        MyHashTable.h
        myMalloc.cpp
        myMalloc.h)
