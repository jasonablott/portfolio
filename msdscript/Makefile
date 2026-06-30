# Create a "Makefile" so that make will compile and link "main.cpp" with "cmdline.cpp"
# to create a program named msdscript. make and ./msdscript should work to build and run.

# Makefile for MSDScript
# CS 6015
#
# Targets:
# build - builds everything
# clean - removes files created in build (executable and .o files)
# run - runs the executable
# End Header

#Macros
# gnu c++ compiler
CXX = g++
# C++ version, include warnings, run in debug
CXXFLAGS = -Wall -std=c++20 -g -fsanitize=undefined -fno-sanitize-recover=undefined
# output executable file name
TARGET = msdscript
# source files
CXXSRC = main.cpp cmdline.cpp expr.cpp parse.cpp val.cpp Env.cpp
# ALL object files
CXXOBJS = $(CXXSRC:.cpp=.o)

#Rule to create executable program
build:$(TARGET)

$(TARGET): $(CXXOBJS)
	$(CXX) $(CXXFLAGS) -o $(TARGET) $(CXXOBJS)
    
# Rule to make all .cpp into .o files
%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@
	
# Rule to clean .o files and executable
clean:
	rm -f $(CXXOBJS) $(TARGET)
	
# Rule to run tests
test: $(TARGET)
	./$(TARGET) --test
	
# Rule to generate documentation using Doxygen
doc: $(TARGET)
	cd Documentation && doxygen msdscriptdoc

# Rule to interp
interp: $(TARGET)
	./$(TARGET) --interp

#Rule to to test msdscript
test_msdscript:
	$(CXX) test_msdscript.cpp exec.cpp -o my_msdscript_tester

.PHONY: clean build test doc interp

