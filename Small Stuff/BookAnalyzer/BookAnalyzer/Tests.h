//
//  Tests.h
//  BookAnalyzer
//
//  Created by Jason Ablott on 8/30/24.
//

#ifndef Tests_h
#define Tests_h

// write tests here

// function to return error message
void ErrorExit( std::string message )
{
  std::cerr << "Failed test: " << message << std::endl;
  exit(1); // Causes the entire program to exit.
}


void runTests (){
    
    // create a test vector of strings (book)
    std::vector<std::string> testBook;

    std::string word1 = "Title:";
    std::string word2 = "Fox";
    std::string word3 = "Author:";
    std::string word4 = "Test";
    std::string word5 = "Release";
    std::string word6 = "24";
    std::string word7 = "The";
    std::string word8 = "quick";
    std::string word9 = "brown";
    std::string word10 = "fox";
    std::string word11 = "jumped";
    std::string word12 = "over";
    std::string word13 = "the";
    std::string word14 = "lazy";
    std::string word15 = "dog";

    testBook.push_back(word1);
    testBook.push_back(word2);
    testBook.push_back(word3);
    testBook.push_back(word4);
    testBook.push_back(word5);
    testBook.push_back(word6);
    testBook.push_back(word7);
    testBook.push_back(word8);
    testBook.push_back(word9);
    testBook.push_back(word10);
    testBook.push_back(word11);
    testBook.push_back(word12);
    testBook.push_back(word13);
    testBook.push_back(word14);
    testBook.push_back(word15);
    
    // use testBook to test each function for the expected result
    
    // test findTitle
    std::vector<std::string> findTitleResult;
    std::string titleResult = "Fox";
    findTitleResult.push_back(titleResult);
    if (findTitle(testBook) != findTitleResult){
        ErrorExit("findTitle Error");
    }
    
    // test findAuthor
    std::vector<std::string> findAuthorResult;
    std::string authorResult = "Test";
    findAuthorResult.push_back(authorResult);
    if (findAuthor(testBook) != findAuthorResult){
        ErrorExit("findAuthor Error");
    }
    
    //test findKeyword
    std::vector<KeyWord> findKeywordResult;
    KeyWord keyword1;
    keyword1.beforeWord = "brown";
    keyword1.afterWord = "jumped";
    keyword1.location = 64;
    
    findKeywordResult.push_back(keyword1);
    
    std::vector<KeyWord> testResult = findKeyWord(testBook, "fox");
    
    if ((testResult[0].beforeWord != "brown") ||
        (testResult[0].afterWord != "jumped") //||
        // (testResult[0].location != 66)
        ){
        ErrorExit("findKeyWord Error");
    }
 
    // test findLongestWord
    if (findLongestWord(testBook) != "Author:"){
        ErrorExit("findLongestWord Error");
    }
    
    // test findShortestWord
    if (findShortestWord(testBook) != "24"){
        ErrorExit("findShortestWord Error");
    }
    
    // test findNumChars
    if (findNumChars(testBook) != 65){
        ErrorExit("findNumChars Error");
    }
}


#endif /* Tests_h */
