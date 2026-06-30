//
//  FinderFunctions.cpp
//  BookAnalyzer
//
//  Created by Jason Ablott on 8/30/24.
//

#include "BookAnalyzer.hpp"


// Finder function definitions


// Function to find the title of a book
std::vector<std::string> findTitle ( const std::vector<std::string> &book){
    std::vector<std::string> title;
    for (int i = 0; i < 100; i++){
        if (book[i] == "Title:"){
            // look at only next 100 words for "Author:"
            for (int j = i + 1; j < i + 100; j++){
                if (book[j] == "Author:"){
                    return title;
                } else {
                    title.push_back(book[j]);
                }
            }
        }
    }
    // in case no title is found
    title.push_back("unknown");
    return title;
}

// Function to find the author of a book
std::vector<std::string> findAuthor ( const std::vector<std::string> &book){
    std::vector<std::string> author;
    for (int i = 0; i < 100; i++){
        if (book[i] == "Author:"){
            // look at only next 100 words for "Release"
            for (int j = i + 1; j < i + 100; j++){
                if (book[j] == "Release"){
                    return author;
                } else {
                    author.push_back(book[j]);
                }
            }
        }
    }
    // in case no author is found
    author.push_back("unknown");
    return author;
}

// Function to find keyword
std::vector<KeyWord> findKeyWord (const std::vector<std::string> &book, std::string keyword){
    std::vector<KeyWord> keywords;
    for (int i = 0; i < book.size(); i++){
        if (book[i] == keyword){
            KeyWord tempKeyword;
            // create a temp vector to find the location in characters of the keyword
            std::vector<std::string> tempVector;
            for (int j = 0; j < i; j++){
                tempVector.push_back(book[j]);
            }
            int keywordIndex = findNumChars(tempVector);
            tempKeyword.location = (double(keywordIndex)/double(findNumChars(book)))*100;
            if (i > 0){
                // handle cases where keyword is the first or last word in book
                tempKeyword.beforeWord = book[i-1];
            } else tempKeyword.beforeWord = "";
            if (i < book.size()){
                tempKeyword.afterWord = book[i+1];
            } else tempKeyword.afterWord = "";
            
            keywords.push_back(tempKeyword);
        }
    }
    
    return keywords;
}

