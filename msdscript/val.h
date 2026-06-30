/**
* Created by Jason Ablott on 3/3/25.
*/
#pragma once

#include <string>

#include "pointer.h"



/**
 * \brief Expr class declaration for Val::toExpr conversion methods
 */
class Expr;
class Env;


/*! \brief Val class: the Val class represents a value, i.e an integer, boolean, or function value.
 *
 */
CLASS(Val) {

public:


/**
     * \brief Val destructor
    */
    virtual ~Val() = default;

    /**
    * \brief determines equality between two Val objects
    * \param other another Val object that this Val object is compared to
    * \return boolean, true for Val objects that are equal, otherwise false
    */
    virtual bool equals(PTR(Val) other) const = 0;

    /**
    * \brief adds two values to one another to get a new value
    * \param other the other Val object to be added to this
    * \return Val* a new Val object representing the summation
    */
    virtual PTR(Val) addTo(PTR(Val) other) = 0;

    /**
    * \brief multiplies two values to one another to get a new value
    * \param other the other Val object to be multiplied with this
    * \return Val* a new Val object representing the product
    */
    virtual PTR(Val) multWith(PTR(Val) other) = 0;

    /**
    * \brief converts this Val object to a string
    * \return std::string a new string representing this Val
    */
    virtual std::string toString() const = 0;

    // /**
    // * \brief converts this Val object to an Expr object
    // * \return Expr* pointer to a new Expr object representing this Val
    // */
    // virtual PTR(Expr) toExpr() const = 0;

    /**
   * \brief returns whether this value is true for BoolVals
   * \return bool is this Val true?
   */
    virtual bool isTrue() = 0;

    /**
   * \brief
   * \param actual_arg
   * \return Val*
   */
    virtual PTR(Val) call(PTR(Val)actual_arg) = 0;
};


/*! \brief NumVal subclass: the NumVal class represents an integer value and is derived from the Val class
 *
 */
class NumVal : public Val {

private:

    int intValue; //!< integer value representing this NumVal value

public:

    /**
    * \brief NumVal object constructor
    * \param intValue the integer value of this NumVal object
    */
    explicit NumVal(int intValue);

    /**
    * \brief determines equality between two NumVal objects
    * \param other another Val object that this NumVal object is compared to
    * \return boolean, true for NumVal objects that are equal, otherwise false
    */
    bool equals(PTR(Val) other) const override;

    /**
    * \brief adds two num values to one another to get a new num value
    * \param other the other Val object to be added to this
    * \return Val* a new Val object representing the summation
    */
    PTR(Val) addTo(PTR(Val) other) override;

    /**
    * \brief multiplies two num values to one another to get a new num value
    * \param other the other Val object to be multiplied with this
    * \return Val* a new Val object representing the product
    */
    PTR(Val) multWith(PTR(Val) other) override;

    /**
    * \brief converts this NumVal object to a string
    * \return std::string a new string representing this NumVal
    */
    std::string toString() const override;

    // /**
    // * \brief converts this NumVal object to an Expr object
    // * \return Expr* pointer to a new Expr object representing this NumVal
    // */
    // PTR(Expr) toExpr() const override;

    /**
    * \brief returns whether this value is true for BoolVals
    * \return bool is this NumVal true? This always throws an exception!
    */
    bool isTrue() override;

    /**
    * \brief calls this NumVal on an actual argument via substitution, always throws an exception
    * \param actual_arg the Val to be substituted into this NumVal
    * \return Val* a new Val with the substitution completed
    */
    PTR(Val) call(PTR(Val) actual_arg) override;
};


/*! \brief BoolVal subclass: the BoolVal class represents a boolean value and is derived from the Val class
 *
 */
class BoolVal : public Val {

private:

    bool boolValue; //!< boolean value representing this BoolVal value, literal true or false

public:

    /**
    * \brief BoolVal object constructor
    * \param boolValue the literal boolean value of this BoolVal object
    */
    explicit BoolVal(bool boolValue);

    /**
    * \brief determines equality between two BoolVal objects
    * \param other another Val object that this BoolVal object is compared to
    * \return boolean, true for BoolVal objects that are equal, otherwise false
    */
    bool equals(PTR(Val) other) const override;

    /**
    * \brief adds two BoolVal objects, always throws an exception
    * \param other another Val object that this BoolVal object is added to
    * \return throws an exception because BoolVals cannot be added together
    */
    PTR(Val) addTo(PTR(Val) other) override;

    /**
    * \brief multiplies two BoolVal objects, always throws an exception
    * \param other another Val object that this BoolVal object is multiplied with
    * \return throws an exception because BoolVals cannot be multiplied together
    */
    PTR(Val) multWith(PTR(Val) other) override;

    /**
    * \brief converts this BoolVal to a string
    * \return std::string a string representation of this BoolVal
    */
    std::string toString() const override;

    // /**
    // * \brief converts this BoolVal to a BoolExpr
    // * \return Expr* a pointer to a new BoolExpr representing this BoolVal
    // */
    // PTR(Expr) toExpr() const override;

    /**
    * \brief returns whether this value is true for BoolVals
    * \return bool is this BoolVal true?
    */
    bool isTrue() override;

    /**
    * \brief calls this BoolVal on an actual argument via substitution, always throws an exception
    * \param actual_arg the Val to be substituted into this BoolVal
    * \return Val* a new Val with the substitution completed
    */
    PTR(Val) call(PTR(Val) actual_arg) override;
};

/*! \brief FunVal subclass: the FunVal class represents a Function value and is derived from the Val class
 *
 */
class FunVal : public Val {

    private:

        std::string formal_arg;
        PTR(Expr) body;
        PTR(Env) env;

    public:

    /**
    * \brief FunVal object constructor
    * \param formal_arg the formal argument of this function
    * \param body the body of this function
    * \param env the environment for this function to use
    */
    FunVal(std::string formal_arg, PTR(Expr) body, PTR(Env) env);

    /**
    * \brief determines equality between two FunVal objects
    * \param other another Val object that this FunVal object is compared to
    * \return boolean, true for FunVal objects that are equal, otherwise false
    */
    bool equals(PTR(Val) other) const override;

    /**
    * \brief adds two FunVal objects, always throws an exception
    * \param other another Val object that this FunVal object is added to
    * \return throws an exception because FunVals cannot be added together
    */
    PTR(Val) addTo(PTR(Val) other) override;

    /**
    * \brief multiplies two FunVal objects, always throws an exception
    * \param other another Val object that this FunVal object is multiplied with
    * \return throws an exception because FunVals cannot be multiplied together
    */
    PTR(Val) multWith(PTR(Val) other) override;

    /**
    * \brief converts this FunVal to a string
    * \return std::string a string representation of this FunVal
    */
    std::string toString() const override;

    // /**
    // * \brief converts this FunVal to a FunExpr
    // * \return Expr* a pointer to a new FunExpr representing this FunVal
    // */
    // PTR(Expr) toExpr() const override;

    /**
    * \brief returns whether this value is true for FunVals
    * Always throws exception because Functions can't be true/false
    * \return bool is this FunVal true?
    */
    bool isTrue() override;

    /**
    * \brief calls this FunVal on an actual argument via substitution
    * \param actual_arg the Val to be substituted into this FunVal
    * \return Val* a new Val with the substitution completed
    */
    PTR(Val) call(PTR(Val) actual_arg) override;

};

