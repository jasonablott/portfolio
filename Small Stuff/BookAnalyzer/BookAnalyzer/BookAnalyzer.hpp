//
//  BookAnalyzer.hpp
//  BookAnalyzer
//
/*

Building and Running

Clone this project repository and navigate to the project directory, then compile with:
- clang++ -std=c++17 -o BookAnalyzer main.cpp FinderFunctions.cpp WordFunctions.cpp

Run the program by passing a `.txt` file and a keyword to search for:
- ./BookAnalyzer <filename.txt> <keyword>

Example:
- ./BookAnalyzer mobydick.txt whale

*/
//
//  Created by Jason Ablott on 8/30/24.
//

#ifndef BookAnalyzer_h
#define BookAnalyzer_h

#include <iostream>
#include <stdio.h>
#include <string>
#include <vector>
#include <fstream>


// Struct definitions


// KeyWord struct
// beforeWord is the word before the keyword
// afterWord is the word after the keyword
// location is the location of the first letter of the keyword as a percentage (0-100)
struct KeyWord {
    std::string beforeWord;
    std::string afterWord;
    int location;
};


// Finder function definitions


// Function to find the title of a book
/**
@brief finds the title of a book in .txt form
@param book is a vector of strings
@pre the title is preceded by "Title:" and followed by "Author"
@post returns a vector of strings
*/
std::vector<std::string> findTitle ( const std::vector<std::string> &book);

// Function to find the author of a book
/**
@brief finds the author of a book in .txt form
@param book is a vector of strings
@pre the author is preceded by "Author:" and followed by "Release Date"
@post returns a vector of strings
*/
std::vector<std::string> findAuthor ( const std::vector<std::string> &book);

// Function to find keyword
/**
@brief finds occurrences of a keyword and stores their location and the word before and after the keyword
@param book is a vector of strings
@pre the keyword is a string in the vector book
@post returns a vector of struct KeyWord
*/
std::vector<KeyWord> findKeyWord ( const std::vector<std::string> &book, std::string keyword);


// Word function definitions


// Function to find number of characters in a book
/**
@brief finds the number of characters in a book in .txt form
@param book is a vector of strings
@pre each letter and punctuation is a character, whitespace is not
@post returns an integer of the number of characters found
*/
int findNumChars ( const std::vector<std::string> &book);

// Function to find shortest word
/**
@brief finds the shortest word in a book in .txt form
@param book is a vector of strings
@pre each string in book is a word, words followed immediately by punctuation include it
@post returns a string
*/
std::string findShortestWord ( const std::vector<std::string> &book);

// Function to find longest word
/**
@brief finds the longest word in a book in .txt form
@param book is a vector of strings
@pre each string in book is a word, words followed immediately by punctuation include it
@post returns a string
*/
std::string findLongestWord ( const std::vector<std::string> &book);

#endif /* BookAnalyzer_h */
