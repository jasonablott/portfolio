cmake_minimum_required(VERSION 3.14)

# Project name
project(msdscript)

# Set C++ standard
set(CMAKE_CXX_STANDARD 20)

# Enable testing
enable_testing()

# Include Catch2 (add Catch2 via FetchContent)
include(FetchContent)
FetchContent_Declare(
        Catch2
        GIT_REPOSITORY https://github.com/catchorg/Catch2.git
        GIT_TAG v2.x  # You can use v3.x if you prefer the newer version
)

FetchContent_MakeAvailable(Catch2)

# Add executable target (main program)
add_executable(msdscript
        main.cpp
        cmdline.h
        cmdline.cpp
        expr.cpp
        expr.h
        parse.h
        parse.cpp
        exec.h
        exec.cpp
        test_msdscript.cpp
        val.h
        val.cpp
        pointer.h
        Env.cpp
        Env.h
)

# If you want a separate test executable
# Create a test executable
#add_executable(tests test_file.cpp)  # Replace with your actual test file name
#target_link_libraries(tests Catch2::Catch2)

# Link the main executable (msdscript) with Catch2 if you plan to integrate testing into the same executable
target_link_libraries(msdscript Catch2::Catch2)

# Add the test cases to CTest (CMake's testing framework)
include_directories(${CATCH2_INCLUDE_DIRS})
#add_test(NAME MyTest COMMAND tests)

# Optional: Add your source files here if needed
# set(SOURCES main.cpp cmdline.cpp expr.cpp)

# Add Catch2 test integration here if you want to run Catch2 tests automatically
# This is useful when you run 'make tests' or from CLion's test runner

# Optional custom commands for cleaning and running (if not handled by CMake's `make`)
set(CXX g++)
set(CXXFLAGS -Wall -std=c++20 -g)

# Optional: Custom targets for building
set(TARGET msdscript)
set(CXXSRC main.cpp cmdline.cpp)

# Specify custom rules for build
set(CXXOBJS main.o cmdline.o)
add_custom_target(build
        COMMAND ${CXX} ${CXXFLAGS} -o ${TARGET} ${CXXSRC}
        DEPENDS ${CXXSRC})

# Cleaning rules (useful for custom clean commands)
add_custom_target(my_clean
        COMMAND rm -f ${CXXOBJS} ${TARGET}
        COMMENT "Cleaning up object and executable files")

# Specify the PHONY targets for make (no actual files)
set_property(TARGET my_clean PROPERTY PHONY 1)
set_property(TARGET build PROPERTY PHONY 1)

# Ensure tests are compiled and linked properly
target_link_libraries(msdscript Catch2::Catch2)
