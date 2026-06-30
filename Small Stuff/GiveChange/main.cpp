//
//  main.cpp
//  GiveChange
//
//  Created by Jason Ablott on 8/20/24.
//

#include <iostream>

int main(int argc, const char * argv[]) {
    
    // Define variables for change calculation, check for input errors
    int itemPrice, moneyPaid, change;
    
    // Prompt the user to enter the price of an item (in cents), check for input errors
    std::cout << "Enter item price in cents: \n";
    std::cin >> itemPrice;
    if (itemPrice <= 0){
        std::cout << "Item price cannot be less than 0! \n";
        return 1;
    }
    
    // Prompt the user to enter the amount of money paid (in cents), check for input errors
    std::cout << "Enter amount paid in cents: \n";
    std::cin >> moneyPaid;
    if (moneyPaid <= 0){
        std::cout << "Money paid cannot be less than 0! \n";
        return 1;
    }
    // Ensure sufficient funds have been provided
    if (moneyPaid < itemPrice){
        std::cout <<"Insufficient funds for purchase! \n";
        return 1;
    }
   
    // Break down the change into the smallest number of coins possible—quarters, dimes, nickels, and pennies and add/use coin inventory
    // initialize inventory for change available in machine
    int quartersAvailable = 2;
    int dimesAvailable = 2;
    int nickelsAvailable = 2;
    int penniesAvailable = 2;
    
    int changeAvailable = (quartersAvailable * 25) + (dimesAvailable * 10) + (nickelsAvailable * 5) + (penniesAvailable * 1);
    
    // initialize variables to calculate coins owed
    int quarters, dimes, nickels, pennies;
    
    // Calculate the total change to be returned
    change = moneyPaid - itemPrice;
       
    // make sure enough change and pennies are available to complete purchase, and if not return an error message
    if (changeAvailable < change){
        std::cout << "Insufficient change available for purchase. Out of coins! \n";
        return 1;
    }
    if (change % 5 > penniesAvailable){
        std::cout << "Insufficient change available for purchase Out of coins! \n";
        return 1;
    }
    
    // Display the change owed
    std::cout << "Change = " << change << " cents \n";
    
    // calculation to determine how many of each coin is needed to give change
    quarters = change / 25;
    if (quarters > quartersAvailable){
        quarters = quartersAvailable;
        change = change - (25 * quartersAvailable);
    } else {
        change = change % 25;
    }
    dimes = change / 10;
    if (dimes > dimesAvailable){
        dimes = dimesAvailable;
        change = change - (10 * dimesAvailable);
    } else {
        change = change % 10;
    }
    nickels = change / 5;
    if (nickels > nickelsAvailable){
        nickels = nickelsAvailable;
        change = change - (5 * nickelsAvailable);
    } else {
        change = change % 5;
    }
    pennies = change;
    
    // Print out results of change calculations
    std::cout << " Quarters: " << quarters << "\n";
    std::cout << " Dimes: " << dimes << "\n";
    std::cout << " Nickels: " << nickels << "\n";
    std::cout << " Pennies: " << pennies << "\n";
    
    return 0;
}
