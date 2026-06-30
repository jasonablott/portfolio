//
// Created by Jason Ablott on 4/2/25.
//

#include "Env.h"


// define empty for testing purposes, now Env::empty is a default empty environment
PTR(Env) Env::empty = NEW(EmptyEnv)();

// EmptyEnv methods

PTR(Val) EmptyEnv::lookup(std::string find_name) {
        throw std::runtime_error("free variable: " + find_name);
}

// ExtendedEnv methods

ExtendedEnv::ExtendedEnv(std::string name, PTR(Val) val, PTR(Env) rest) {
    this->name = name;
    this->val = val;
    this->rest = rest;
}

PTR(Val) ExtendedEnv::lookup(std::string find_name) {
    if (find_name == name)
        return val;
    else
        return rest->lookup(find_name);
}
