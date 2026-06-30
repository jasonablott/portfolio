//
// Created by Jason Ablott on 2/24/25.
//
#include <iostream>
#include <stdlib.h>
#include "exec.h"
#include <fstream>

// added random generators
std::string get_random_string();
std::string get_random_variable();
std::string get_random_num();
std::string get_random_let();
std::string get_random_expr();
std::string get_random_addend();
std::string get_random_multicand();
void compare_programs(const char *const prog1[], const char *const prog2[], int num_tests);

int NUM_TESTS = 100;

int main(int argc, char **argv) {
    srand(time(NULL));
    // if only one argument is passed in, compare to itself
    if (argc == 2) {
        const char * const interp_argv[] = { argv[1], "--interp" };
        const char * const print_argv[] = { argv[1], "--print" };
        const char * const pretty_print_argv[] = { argv[1], "--pretty-print" };
        for (int i = 0; i < NUM_TESTS; i++) {

            // std::string in = random_expr_string();
            std::string in = get_random_expr();
            std::cout << "Trying "<< in << "\n";

            // interp this random expression
            ExecResult interp_result = exec_program(2, interp_argv, in);
            // print this random expression
            ExecResult print_result = exec_program(2, print_argv, in);
            std::cout << std::endl << "Print result: " << print_result.out << "\n";
            // pretty print this random expression
            ExecResult pretty_print_result = exec_program(2, pretty_print_argv, in);
            std::cout << std::endl << "Pretty Print result: " << pretty_print_result.out << "\n";

            // interpret the printed random expression again, it should be the same
            ExecResult interp_again_print = exec_program(2, interp_argv, print_result.out);
            // interpret the pretty printed random expression again, it should be the same
            ExecResult interp_again_pretty_print = exec_program(2, interp_argv, pretty_print_result.out);

            if (interp_again_print.out != interp_result.out) {
                std::cout << "Error comparing interpretation of Print on test no: " << i << std::endl;
                std::cout << "interp_result: " << interp_result.out << "\n";
                std::cout << "interp_again_print: " << interp_again_print.out << "\n";
                throw std::runtime_error("different result for printed");
            }
            if (interp_again_pretty_print.out != interp_result.out) {
                std::cout << "Error comparing interpretation of Pretty Print on test no: " << i << std::endl;
                std::cout << "interp_result: " << interp_result.out << "\n";
                std::cout << "interp_again_pretty_print: " << interp_again_pretty_print.out << "\n";
                throw std::runtime_error("different result for pretty printed");
            }
        }
    // if two arguments are passed in, compare the two programs to each other
    } else if (argc == 3) {
        // Compare interp results
        const char * const interp1_argv[] = { argv[1], "--interp" };
        const char * const interp2_argv[] = { argv[2], "--interp" };
        compare_programs(interp1_argv, interp2_argv, NUM_TESTS);

        // Compare print results
        const char * const print1_argv[] = { argv[1], "--print" };
        const char * const print2_argv[] = { argv[2], "--print" };
        compare_programs(print1_argv, print2_argv, NUM_TESTS);

        // Compare pretty-print results
        const char * const pretty_print1_argv[] = { argv[1], "--pretty-print" };
        const char * const pretty_print2_argv[] = { argv[2], "--pretty-print" };
        compare_programs(pretty_print1_argv, pretty_print2_argv, NUM_TESTS);
    } else {
        throw std::runtime_error("incorrect number of arguments provided");
    }
    return 0;
}

// helper method to compare 2 different programs to each other for  every argument
void compare_programs(const char *const prog1[], const char *const prog2[], int num_tests) {
    // Open the comparison report file in append mode
    std::ofstream log_file("ComparisonReport.txt", std::ios::app);
    if (!log_file) {
        throw std::runtime_error("Error opening ComparisonReport.txt for writing.");
    }
    // print outs for readability in report
    log_file << std::endl;
    log_file << std::endl;
    log_file << std::endl;
    log_file << "******************************************************************************\n";
    log_file << "Comparing: " << prog1[0] << " : " << prog1[1] << "\n"
    <<"      and: " << prog2[0] << " : " << prog2[1] << std::endl;
    log_file << "*******************************************************************************\n";
    log_file << std::endl;
    log_file << std::endl;
    log_file << std::endl;

    int failedcount = 0;

    for (int i = 0; i < num_tests; i++) {
        std::string in = get_random_expr();
        std::cout << "Trying " << in << "\n";

        ExecResult prog1_result = exec_program(2, prog1, in);
        ExecResult prog2_result = exec_program(2, prog2, in);

        if (prog1_result.out != prog2_result.out) {
            // Log the error details to the file
            log_file << "Error comparing results of test #: " << i << std::endl;
            log_file << "Expression: " << in << std::endl;
            log_file << "Program 1 result: " << prog1_result.out << "\n";
            log_file << "Program 2 result: " << prog2_result.out << "\n";
            log_file << "-------------------------------------------\n";
            failedcount++;
        }
    }
    std::cout << std::endl;
    std::cout << "Failure Rate: " << failedcount << "/" << NUM_TESTS << std::endl;
    // Close the log file (automatically done when ofstream goes out of scope)
    log_file.close();
}


/*
 *Random expression generators. Recursive calls work the same as parsing
 *Here is the grammar of input expressions:
 *
 *-------------------------------------------------------

     〈expr〉 = 〈addend〉
             | 〈addend〉 + 〈expr〉
   〈addend〉 = 〈multicand〉
             | 〈multicand〉 * 〈addend〉
〈multicand〉 = 〈number〉
             | ( 〈expr〉 )
             | 〈variable〉
             | _let 〈variable〉 = 〈expr〉 _in 〈expr〉

 *-------------------------------------------------------
 *
 **/

// make random string, returns a random string between 1 and 10 characters between a and z
std::string get_random_string() {
    std::string str = "";
    int choice = rand() % 3;
    if (choice == 0) {
        str += 'x';
    }
    if (choice == 1) {
        str += 'y';
    }
    else {
        int length = rand() % 5 + 1;
        for (int i = 0; i < length; ++i) {
            char randomChar = rand() % 26 + 'a';
            str += randomChar;
        }
    }
    return str;
}
// helper since variables are strings
std::string get_random_variable() {
    return get_random_string();
}
// make random number, returns a random number
std::string get_random_num() {
    int choice = rand() % 3;
    if (choice == 1) {
        return std::to_string((rand()%100)*(-1));
    }
    return std::to_string(rand()%100);
}
// make random let: uses make random string, make random expr, and make random expr
std::string get_random_let() {
    std::string variable = get_random_string();
    std::string expr1 = get_random_expr();
    std::string expr2 = get_random_expr();
    return "_let " + variable + " = " + expr1 + " _in " + expr2;
}
// make random expr uses make random expr and recursively uses make random expr
std::string get_random_expr() {
    std::string addend = get_random_addend();
    int choice = rand() % 3;
    if (choice == 1) {
        return addend + " + " + get_random_expr();
    } else {
        return addend;
    }
}
// make random addend uses make random multicand and recursively make random addend
std::string get_random_addend() {
    std::string multicand = get_random_multicand();
    int choice = rand() % 3;
    if (choice == 1) {
        return multicand + " * " + get_random_addend();
    } else {
        return multicand;
    }
}
// make random multicand uses make random number, or make random expression (with parentheses), or make random variable,
// or make random let
std::string get_random_multicand() {
    int choice = rand() % 10;
    switch (choice) {
        case 1:
            return get_random_num();
        case 2:
            return "(" + get_random_expr() + ")";
        case 3:
            return get_random_variable();
        case 4:
            return get_random_let();
        default:
            return get_random_num();
    }
}
