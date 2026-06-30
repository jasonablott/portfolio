cmake_minimum_required(VERSION 3.30)
project(JShell)

set(CMAKE_CXX_STANDARD 20)

add_executable(JShell main.cpp
        shelpers.h
        shelpers.cpp)
