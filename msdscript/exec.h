//
// Created by Jason Ablott on 2/21/25.
//

#ifndef EXEC_H
#define EXEC_H

#include <string>
class ExecResult {
public:
    int exit_code;
    std::string out;
    std::string err;
    ExecResult() {
        exit_code = 0;
        out = "";
        err = "";
    }
};
extern ExecResult exec_program(int argc, const char * const *argv, std::string
input);

#endif //EXEC_H
