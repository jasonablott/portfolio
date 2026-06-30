//
// Created by Jason Ablott on 1/13/25.
//

#include "cmdline.h"
#include <iostream>
#include <cstring>
#define CATCH_CONFIG_RUNNER
#include "catch.h"
#include "expr.h"
#include "parse.h"

run_mode_t use_arguments(int argc, char *argv[]) {

    bool testSeen = false;

    for (int i = 1; i < argc; i++) { // loop through arguments

        // handle help argument
        if (strcmp(argv[i], "--help") == 0) {
            std::cout << "Arguments allowed: \n"
                         "--help: Show this help message\n"
                         "--test: Test seen\n"
            << std::endl;
            return do_nothing;
        }

        // handle test argument
        if (strcmp(argv[i], "--test") == 0 && testSeen == false) {
            // run tests, if catch returns a non-zero value exit(1) immediately
            std::cout << "Tests Results:" << std::endl;
            if (Catch::Session().run(1, argv) != 0) {
                exit(1);
            }
            testSeen = true;
            return do_nothing;
        }
        if (strcmp(argv[i], "--test") == 0 && testSeen == true) {
            std::cerr << "Error: tests already run" << std::endl;
            return do_nothing;
            exit(1);
        }

        // handle interp argument in main
        if (strcmp(argv[i], "--interp") == 0) {
            return do_interp;
        }

        // handle print argument in main
        if (strcmp(argv[i], "--print") == 0) {
            return do_print;
        }

        // handle pretty print argument in main
        if (strcmp(argv[i], "--pretty-print") == 0) {
            return do_pretty_print;
        }

        // otherwise argument is not allowed
        std::cerr << "Error: illegal argument" << std::endl;
        exit(1);
    }
    if (testSeen == false) {
        std::cerr << "No arguments!" << std::endl;
        exit(1);
    }
        std::cout << "msdscript finished running" << std::endl;
        exit(0);
}