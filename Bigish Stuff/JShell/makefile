# Compiler
CXX = g++

# Compiler flags
CXXFLAGS = -Wall -g -std=c++17

# Source files
SRC = main.cpp shelpers.cpp

# Object files (corresponding .cpp files)
OBJ = $(SRC:.cpp=.o)

# Output executable
TARGET = JShell

# Default target to build the program
all: $(TARGET)

# Link object files to create the final executable
$(TARGET): $(OBJ)
	$(CXX) $(OBJ) -o $(TARGET)

# Rule to compile .cpp files into .o object files
%.o: %.cpp shelpers.h
	$(CXX) $(CXXFLAGS) -c $< -o $@

# Clean up object files and executable
clean:
	rm -f $(OBJ) $(TARGET)

# Rule to run program after cleaning and compiling
run: clean all
	./$(TARGET)

# Phony targets (not actual files)
.PHONY: all clean run