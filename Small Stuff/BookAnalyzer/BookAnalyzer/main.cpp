//
//  main.cpp
//  BookAnalyzer
//
//  Created by Jason Ablott on 8/30/24.
//

#include "BookAnalyzer.hpp"
#include "Tests.h"

int main(int argc, const char * argv[]) {

// initialize vector of strings to store text from file in
    std::vector<std::string> book;
    
    std::string userKeyword = argv[2];
    
// Read in a file named in argv[1]
    
    // open file
    std::ifstream fin (argv[1]);
    
    // ensure it was successful
    if (!fin.is_open()){
        std::cerr << "Could not open the file \n";
        return 1;
    }
    
    // read in from the file
    std::string singleWord;
    while (fin >> singleWord) {
        book.push_back(singleWord);
    }
    
    // close the file
    fin.close();
    
// use functions to calculate stats
    
    std::vector<std::string> title = findTitle(book);
    std::vector<std::string> author = findAuthor(book);
    std::string shortestWord = findShortestWord(book);
    std::string longestWord = findLongestWord(book);
    int numWords = (int)book.size();
    int numChars = findNumChars(book);
    std::vector<KeyWord> keywordsList = findKeyWord(book, userKeyword);
    
    
// run tests
    runTests();
    
// print stats
    
    std::cout << "Statistics for ";
    for (int i = 0; i < title.size(); i++){
        std::cout << title[i];
        if (i+1 != title.size()){
            std::cout << " ";
        }
    }
    std::cout << " by ";
    for (int i = 0; i < author.size(); i++){
        std::cout << author[i];
        if (i+1 != author.size()){
            std::cout << " ";
        }
    }
    std::cout << ": " << std::endl;
    std::cout << "  Number of words: " << numWords << std::endl;
    std::cout << "  Number of characters: " << numChars << std::endl;
    std::cout << "  The shortest word is \"" << shortestWord << "\", and the longest word is \"" << longestWord << "\"" << std::endl;
    std::cout << std::endl;
    std::cout << "The word " << "\"" << userKeyword << "\"" << " appears " << keywordsList.size() << " times: " << std::endl;
    // print out where keyword occurs here by looping through keywordsList and printing the information in the KeyWord structs.
    for (int i = 0; i < keywordsList.size(); i++){
        std::cout << "  at " << (keywordsList[i].location) << "%: \"" << keywordsList[i].beforeWord
        << " " << userKeyword << " " << keywordsList[i].afterWord << "\"" << std::endl;
    }
    
    
        return 0;
}
