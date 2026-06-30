//
// Created by Jason Ablott on 4/2/25.
//

#ifndef ENV_H
#define ENV_H
#include <string>
#include "pointer.h"
#include "val.h"

/*! \brief Env class: the Env class represents an environment for expressions to track a dictionary of key:values
 *
 */
class Env {
public:
    virtual ~Env() = default; // destructor
    virtual PTR(Val) lookup(std::string find_name) = 0; // pure virtual lookup method
    static PTR(Env) empty; // declare empty for testing purposes
};

/*! \brief Sub class EmptyEnv is an Env with no data
 *
 */
class EmptyEnv : public Env {
    PTR(Val) lookup(std::string find_name);
};

/*! \brief Sub class ExtendedEnv is an Env with data
 *
 */
class ExtendedEnv : public Env {
    std::string name;
    PTR(Val) val;
    PTR(Env) rest;
    PTR(Val) lookup(std::string find_name);

    /**
    * \brief ExtendedEnv object constructor
    */
public: ExtendedEnv(std::string name, PTR(Val) val, PTR(Env) rest);
};
#endif //ENV_H
