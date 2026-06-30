//
//  main.cpp
//  Palindromes
//
// Created by Jacob Barson and Jason Ablott on 8/22/24.
//
#include <iostream>
#include <string>
int main(int argc, const char * argv[]) {
    
    // define and get string input
    std::string palindrome;
    std::string palindromeR;
    std::cout << "Please enter a word to see if it is a Palindrome. \n";
    std::cin >> palindrome;
    
    // use forloop to rewrite the input backwards
    for (int i=1; i<palindrome.length()+1; i++) {
        palindromeR = palindromeR + palindrome[palindrome.length()-i];
    }
    
    // compare the input and reversed input we just created
    if (palindrome != palindromeR){
        std::cout << "The word " << palindrome << " is NOT a palindrome. \n";
        return 1;
    } else {
        std::cout << "The word: " << palindrome << " IS a palindrome. \n";
    }
    return 0;
}
