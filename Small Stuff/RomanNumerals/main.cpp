//
//  main.cpp
//  RomanNumerals
//
//  Created by Jason Ablott on 8/22/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    
    // get user input
    int input;
    std::cout << "Enter decimal number:" << std::endl;
    std::cin >> input;
    
    // validate input
    if (input <= 0 || input > 10000){
        std::cout << "Invalid input" << std::endl;
        return 1;
    }
    // once validated we can print the result header
    std::cout << "Roman numeral version: \n";
    
    // implement loop to convert to roman numerals
    
    // roman numeral variables
    int intM = 1000;
    int intCM = 900;
    int intD = 500;
    int intCD = 400;
    int intC = 100;
    int intXC = 90;
    int intL = 50;
    int intXL = 40;
    int intX = 10;
    int intIX = 9;
    int intV = 5;
    int intIV = 4;
    int intI = 1;
    
    // set remaining equal to input for use in loop
    int remaining = input;
    
    // loop through subtracting "tokens" and printing the according roman numerals until the number has all been converted and equals 0
    while (remaining > 0){
        if (remaining >= intM){
            std::cout << "M";
            remaining = remaining - intM;
        } else if (remaining >= intCM){
            std::cout << "CM";
            remaining = remaining - intCM;
        } else if (remaining >= intD){
            std::cout << "D";
            remaining = remaining - intD;
        } else if (remaining >= intCD){
            std::cout << "CD";
            remaining = remaining - intCD;
        } else if (remaining >= intC){
            std::cout << "C";
            remaining = remaining - intC;
        } else if (remaining >= intXC){
            std::cout << "XC";
            remaining = remaining - intXC;
        } else if (remaining >= intL){
            std::cout << "L";
            remaining = remaining - intL;
        } else if (remaining >= intXL){
            std::cout << "XL";
            remaining = remaining - intXL;
        } else if (remaining >= intX){
            std::cout << "X";
            remaining = remaining - intX;
        } else if (remaining >= intIX){
            std::cout << "IX";
            remaining = remaining - intIX;
        } else if (remaining >= intV){
            std::cout << "V";
            remaining = remaining - intV;
        } else if (remaining >= intIV){
            std::cout << "IV";
            remaining = remaining - intIV;
        } else if (remaining >= intI){
            std::cout << "I";
            remaining = remaining - intI;
        }
    }
    std::cout << std::endl;
    return 0;
}
