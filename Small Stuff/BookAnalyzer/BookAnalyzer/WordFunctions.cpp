//
//  WordFunctions.cpp
//  BookAnalyzer
//
//  Created by Jason Ablott on 8/30/24.
//

#include "BookAnalyzer.hpp"


// Word function definitions


// Function to find number of characters in a book
int findNumChars ( const std::vector<std::string> &book){
    int numChars = 0;
    for (int i = 0; i < book.size(); i++){
        numChars += book[i].length();
        }
    return numChars;
}

// Function to find shortest word
std::string findShortestWord ( const std::vector<std::string> &book){
    std::string shortestWord = book[0];
    for (int i = 1; i < book.size(); i++){
        if (book[i].length() < shortestWord.length()){
            shortestWord = book[i];
        }
    }
    return shortestWord;
}

// Function to find longest word
std::string findLongestWord ( const std::vector<std::string> &book){
    std::string longestWord = book[0];
    for (int i = 1; i < book.size(); i++){
        if (book[i].length() > longestWord.length()){
            longestWord = book[i];
        }
    }
    return longestWord;
}
