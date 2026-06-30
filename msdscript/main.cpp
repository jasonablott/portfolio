/**
* \mainpage MSDScript
* \author Jason Ablott
* \date 04-02-2025
*/

#include <iostream>

#include "cmdline.h"
#include "parse.h"
#include "val.h"

int main(int argc, char* argv[]) {
    try {
        while (true) {
            // get mode
            run_mode_t mode = use_arguments(argc, argv);
            // parse to get expression
            PTR(Expr) e = parse_expr(std::cin);
            // handle do nothing for test and help commands
            if (mode == do_nothing) {
                exit(0);
            }
            // handle interp mode
            if (mode == do_interp) {
                PTR(Val) inter = e->interp(Env::empty);
                std::cout << inter->toString() << std::endl;
                exit(0);
            }
            // handle print mode
            if (mode == do_print) {
                std::cout << e->to_string() << std::endl;
                exit (0);
            }
            // handle pretty print mode
            if (mode == do_pretty_print) {
                std::cout << e->to_pretty_string() << std::endl;
                exit (0);
            }
        }
        return 0;
    } catch (std::exception &e) {
        std::cerr << e.what() << "\n";
        return 1;
    }
}

