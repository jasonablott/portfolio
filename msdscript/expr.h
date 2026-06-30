/**
* Created by Jason Ablott on 1/20/25.
*/

#include <string>
#include "pointer.h"
#include "Env.h"


#ifndef EXPR_H
#define EXPR_H

/**
 * \brief Val class declaration for Expr::interp to use when interpreting
*/
class Val;

/**
 * \brief Enum definition for handling mathematical precedence
*/
typedef enum {
    prec_none,      // = 0, for NumExpr or VariableExpr
    prec_equal,     // = 1 for EqExpr
    prec_add,       // = 2, for AddExpr
    prec_mult       // = 3, for MultExpr
  } precedence_t; //!< An enum representing the levels of precedence for expressions

/*! \brief Expr class: a class representing a generic expression. See details for expression grammar.
* An expression can be a number, variable, or combination of those with a mathematical operator * or +.
*
* * The abstract grammar of Expr is:
* <expr> = <number>
       | <expr> + <expr>
       | <expr> * <expr>
       | <variable>
       | _let <variable> = <expr> _in <expr>
*/
CLASS(Expr) {
public:
    /**
     * \brief expression destructor
    */
    virtual ~Expr() = default;
    /**
    * \brief determines equality between two expressions
    * \param e another expression that this expression is compared to
    * \return boolean, true for expressions that are equal, otherwise false
    */
    virtual bool equals(PTR(Expr) e) = 0;
    /**
    * \brief interprets an expression, evaluating the math contained within it
    * \return int an integer representing the result of the evaluated expression
    */
    virtual PTR(Val) interp(PTR(Env) env ) = 0;
    /**
    * \brief determines if an expression contains a variable
    * \return boolean, true for expressions that contain any variables, otherwise false
    */
    virtual bool has_variable() = 0;
    // /**
    // * \brief substitutes an expression for a given string
    // * \param s a string in this expression to be replaced with e
    // * \param e an expression to substitute into this expression for string s
    // * \return Expr* a new expression with e substituted for occurrences of s
    // */
    // virtual PTR(Expr) subst(std::string s, PTR(Expr) e) = 0;
    /**
    * \brief prints this expression to an outputstream
    * \param os an output stream for this expression to be printed to
    */
    virtual void print(std::ostream& os) = 0;
    /**
    * \brief prints this expression in a more readable format
    * \param os the outputstream for this expression to be printed to
    */
    virtual void prettyPrint (std::ostream& os);
    /**
    * \brief helper method to enable precedence accumulation for prettyPrint
    * \param os the output stream for this expression to be printed to
    * \param precedence the accumulated precedence of this expression, used to determine parentheses
    * \param lastNewlinePos tracks the position of the last new line for proper indentation of Let expressions
    * \param needsParens tracks whether a nested Let expression needs parentheses
    * \param indentLevel tracks how many nested indents are present for printing nested Let expressions
    */
    virtual void prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel);
    /**
    * \brief allows use of print on an expression
    * \return string a string representing the expression
    */
    std::string to_string();
    /**
    * \brief allows use of prettyPrint on an expression
    * \return string a string representing the expression
    */
    std::string to_pretty_string();
};


/*! \brief Num class: a derived expression class representing a number expression.
*
* More details here:
*/
class NumExpr : public Expr {
public:
    /**
    * MEMBER VARIABLES
    */
    int val; //!< integer value representing this NumExpr expression

    /**
    * FUNCTIONS
    */

    /**
    * \brief Num expression constructor
    */
    NumExpr(int val);
    /**
    * \brief determines equality between two Num expressions
    * \param e another Num expression that this Num expression is compared to
    * \return boolean, true for Num expressions that are equal, otherwise false
    */
    bool equals(PTR(Expr)e);
    /**
    * \brief interprets a Num expression, evaluating the math contained within it
    * \return int an integer representing the result of the evaluated Num expression
    */
    PTR(Val) interp(PTR(Env) env );
    /**
    * \brief determines if a Num expression contains a variable
    * \return boolean, always false as a number is not a variable
    */
    bool has_variable();
    // /**
    // * \brief substitutes an expression for a given string
    // * \param s a string in this expression to be replaced with e
    // * \param e an expression to substitute into this expression for string s
    // * \return Expr* a new Num expression with e substituted for occurrences of s
    // */
    // PTR(Expr)
    //  subst(std::string s, PTR(Expr)
    //   e);
    /**
    * \brief prints this Num expression to an outputstream
    * \param os an output stream for this Num expression to be printed to
    */
    void print(std::ostream& os);
};


/*! \brief Add class: a derived expression class representing an addition expression.
*
* More details here:
*/
class AddExpr : public Expr {
public:
    /**
    * MEMBER VARIABLES
    */
    PTR(Expr)
    lhs; //!< An expression representing the left hand side of this AddExpr expression
    PTR(Expr)
    rhs; //!< An expression representing the right hand side of this AddExpr expression

    /**
    * FUNCTIONS
    */

    /**
     * \brief Add expression constructor
    */
    AddExpr(PTR(Expr)
    lhs, PTR(Expr)
    rhs);
    /**
    * \brief determines equality between two Add expressions
    * \param e another Add expression that this Add expression is compared to
    * \return boolean, true for Add expressions that are equal, otherwise false
    */
    bool equals(PTR(Expr)
    e);
    /**
    * \brief interprets an Add expression, evaluating the math contained within it
    * \return int an integer representing the result of the evaluated Add expression
    */
    PTR(Val) interp(PTR(Env) env );
    /**
    * \brief determines if an Add expression contains a variable
    * \return boolean, true for Add expressions that contain any variables, otherwise false
    */
    bool has_variable();
    // /**
    // * \brief substitutes an expression for a given string
    // * \param s a string in this expression to be replaced with e
    // * \param e an expression to substitute into this expression for string s
    // * \return Expr* a new Add expression with e substituted for occurrences of s
    // */
    // PTR(Expr)
    //  subst(std::string s, PTR(Expr)
    //   e);
    /**
    * \brief prints this Add expression to an outputstream
    * \param os an output stream for this Add expression to be printed to
    */
    void print(std::ostream& os);
    /**
    * \brief prints this Add expression in a more readable format
    * \param os the outputstream for this Add expression to be printed to
    */
    void prettyPrint(std::ostream& os);
    /**
    * \brief helper method to enable precedence accumulation for prettyPrint
    * \param os the output stream for this Add expression to be printed to
    * \param precedence the accumulated precedence of this expression, used to determine parentheses
    * \param lastNewlinePos tracks the position of the last new line for proper indentation of Let expressions
    * \param needsParens tracks whether a nested Let expression needs parentheses
    * \param indentLevel tracks how many nested indents are present for printing nested Let expressions
    */
    void prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel);
};


/*! \brief Mult class: a derived expression class representing a multiplication expression.
*
* More details here:
*/
class MultExpr : public Expr {
public:
    /**
    * MEMBER VARIABLES
    */
    PTR(Expr)
    lhs; //!< An expression representing the left hand side of this MultExpr expression
    PTR(Expr)
    rhs; //!< An expression representing the left hand side of this MultExpr expression

    /**
    * FUNCTIONS
    */

    /**
     * \brief Mult expression constructor
    */
    MultExpr(PTR(Expr)
    lhs, PTR(Expr)
    rhs);
    /**
    * \brief determines equality between two Mult expressions
    * \param e another Mult expression that this Mult expression is compared to
    * \return boolean, true for expressions that are equal, otherwise false
    */
    bool equals(PTR(Expr)
    e);
    /**
    * \brief interprets a Mult expression, evaluating the math contained within it
    * \return int an integer representing the result of the evaluated Mult expression
    */
    PTR(Val) interp(PTR(Env) env );
    /**
    * \brief determines if a Mult expression contains a variable
    * \return boolean, true for Mult expressions that contain any variables, otherwise false
    */
    bool has_variable();
    // /**
    // * \brief substitutes an expression for a given string
    // * \param s a string in this expression to be replaced with e
    // * \param e an expression to substitute into this expression for string s
    // * \return Expr* a new Mult expression with e substituted for occurrences of s
    // */
    // PTR(Expr)
    //  subst(std::string s, PTR(Expr)
    //   e);
    /**
    * \brief prints this Mult expression to an outputstream
    * \param os an output stream for this Mult expression to be printed to
    */
    void print(std::ostream& os);
    /**
    * \brief prints this Mult expression in a more readable format
    * \param os the outputstream for this Mult expression to be printed to
    */
    void prettyPrint(std::ostream& os);
    /**
    * \brief helper method to enable precedence accumulation for prettyPrint
    * \param os the output stream for this Mult expression to be printed to
    * \param precedence the accumulated precedence of this expression, used to determine parentheses
    * \param lastNewlinePos tracks the position of the last new line for proper indentation of Let expressions
    * \param needsParens tracks whether a nested Let expression needs parentheses
    * \param indentLevel tracks how many nested indents are present for printing nested Let expressions
    */
    void prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel);
};


/*! \brief Variable class: a derived expression class representing a variable expression.
*
* More details here:
*/
class VarExpr : public Expr {
public:
    /**
    * MEMBER VARIABLES
    */
    std::string name; //!< A string representing this VarExpr expression

    /**
     * FUNCTIONS
     */

    /**
     * \brief Variable expression constructor
    */
    VarExpr(std::string name);
    /**
    * \brief determines equality between two Variable expressions
    * \param e another expression that this expression is compared to
    * \return boolean, true for Variable expressions that are equal, otherwise false
    */
    bool equals(PTR(Expr)
    e);
    /**
    * \brief interprets a Variable expression, evaluating the math contained within it
    * \return int an integer representing the result of the evaluated expression
    * \exception e throws an exception because a variable cannot be interpreted
    */
    PTR(Val) interp(PTR(Env) env );
    /**
    * \brief determines if a variable expression contains a variable
    * \return boolean, always true as a variable is a variable
    */
    bool has_variable();
    // /**
    // * \brief substitutes an expression for a given string
    // * \param s a string in this expression to be replaced with e
    // * \param e an expression to substitute into this expression for string s
    // * \return Expr* a new Variable expression with e substituted for occurrences of s
    // */
    // PTR(Expr)
    //  subst(std::string s, PTR(Expr)
    //   e);
    /**
    * \brief prints this Variable expression to an outputstream
    * \param os an output stream for this Variable expression to be printed to
    */
    void print(std::ostream& os);
};


/*! \brief Let class: a derived expression class representing a Let expression.
*
* More details here:
*/
class LetExpr : public Expr {
public:
    /**
    * MEMBER VARIABLES
    */
    std::string variable; //!< A string representing the left hand side of this LetExpr expression
    PTR(Expr)
    rhs; //!< An expression representing the right hand side of this LetExpr expression
    PTR(Expr)
     body; //!< An expression representing the body, or the expression that rhs replace via subst

    /**
     * FUNCTIONS
     */

    /**
     * \brief Let expression constructor
    */
    LetExpr(std::string var, PTR(Expr)
    rhs, PTR(Expr)
     body);
    /**
    * \brief determines equality between two Let expressions
    * \param e another Let expression that this Let expression is compared to
    * \return boolean, true for Let expressions that are equal, otherwise false
    */
    bool equals(PTR(Expr)
    e);
    /**
    * \brief interprets a Let expression, by replacing occurrences of the variable with rhs in body
    * \return a new expression representing the result of the evaluated Let expression
    */
    PTR(Val) interp(PTR(Env) env );
    /**
    * \brief determines if a Let expression contains a variable
    * \return boolean, true if a variable is in the rhs of the Let
    */
    bool has_variable();
    // /**
    // * \brief substitutes an expression for a given string
    // * \param s a string in this expression to be replaced with e
    // * \param e an expression to substitute into this expression for string s
    // * \return Expr* a new Variable expression with e substituted for occurrences of s
    // */
    // PTR(Expr)
    //  subst(std::string s, PTR(Expr)
    //   e);
    /**
    * \brief prints this Let expression to an outputstream
    * \param os an output stream for this Let expression to be printed to
    */
    void print(std::ostream& os);
    /**
    * \brief prints this Let expression in a more readable format
    * \param os the outputstream for this Let expression to be printed to
    */
    void prettyPrint(std::ostream &os);
    /**
    * \brief helper method for Let::prettyPrint
    * \param os the output stream for this Let expression to be printed to
    * \param precedence the accumulated precedence of this expression, used to determine parentheses
    * \param lastNewlinePos tracks the position of the last new line for proper indentation of Let expressions
    * \param needsParens tracks whether a nested Let expression needs parentheses
    * \param indentLevel tracks how many nested indents are present for printing nested Let expressions
    */
    void prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel);
};


/*! \brief BoolExpr class: a derived expression class representing a boolean expression.
*
* More details here:
*/
class BoolExpr : public Expr {
public:
    /**
    * MEMBER VARIABLES
    */
    bool val; //!< boolean value representing this BoolExpr expression

    /**
    * FUNCTIONS
    */

    /**
    * \brief Bool expression constructor
    */
    BoolExpr(int val);
    /**
    * \brief determines equality between two Bool expressions
    * \param e another expression that this Bool expression is compared to
    * \return boolean, true for Bool expressions that are equal, otherwise false
    */
    bool equals(PTR(Expr)
    e);
    /**
    * \brief interprets a Bool expression
    * \return bool the literal boolean value this BoolExpr represents
    */
    PTR(Val) interp(PTR(Env) env );
    /**
    * \brief determines if a Bool expression contains a variable
    * \return boolean, always false as a boolean is not a variable
    */
    bool has_variable();
    // /**
    // * \brief substitutes a bool expression for a given string
    // * \param s a string in this expression to be replaced with e
    // * \param e an expression to substitute into this expression for string s
    // * \return Expr* a new Bool expression with e substituted for occurrences of s
    // */
    // PTR(Expr)
    //  subst(std::string s, PTR(Expr)
    //   e);
    /**
    * \brief prints this Bool expression to an outputstream
    * \param os an output stream for this Bool expression to be printed to
    */
    void print(std::ostream& os);
};

/*! \brief IfExpr class: a derived expression class representing an If expression.
*
* More details here:
*/
class IfExpr : public Expr {
public:
    /**
    * MEMBER VARIABLES
    */
    PTR(Expr)
     testPart; //!< An expression representing the if part of this if_then_else expression
    PTR(Expr)
     thenPart; //!< An expression representing the then part of this if_then_else expression
    PTR(Expr)
     elsePart; //!< An expression representing the else part of this if_then_else expression

    /**
     * FUNCTIONS
     */

    /**
     * \brief If expression constructor
    */
    IfExpr(PTR(Expr)
     testPart, PTR(Expr)
      thenPart, PTR(Expr)
       elsePart);
    /**
    * \brief determines equality between two If expressions
    * \param e another If expression that this If expression is compared to
    * \return boolean, true for If expressions that are equal, otherwise false
    */
    bool equals(PTR(Expr)
    e);
    /**
    * \brief interprets an If expression, evaluating the testPart and then either the thenPart or elsePart
    * \return a new val representing the result of the evaluated If expression
    */
    PTR(Val) interp(PTR(Env) env );
    /**
    * \brief determines if an If expression contains a variable
    * \return boolean
    */
    bool has_variable();
    // /**
    // * \brief
    // * \param s
    // * \param e
    // * \return Expr*
    // */
    // PTR(Expr)
    //  subst(std::string s, PTR(Expr)
    //   e);
    /**
    * \brief prints this If expression to an outputstream
    * \param os an output stream for this If expression to be printed to
    */
    void print(std::ostream& os);
    /**
    * \brief prints this If expression in a more readable format
    * \param os the outputstream for this If expression to be printed to
    */
    void prettyPrint(std::ostream &os);
    /**
    * \brief helper method for IfExpr::prettyPrint
    * \param os the output stream for this If expression to be printed to
    * \param precedence the accumulated precedence of this expression, used to determine parentheses
    * \param lastNewlinePos tracks the position of the last new line for proper indentation of Let expressions
    * \param needsParens tracks whether a nested If expression needs parentheses
    * \param indentLevel tracks how many nested indents are present for printing nested If expressions
    */
    void prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel);
};

/*! \brief EqExpr class: a derived expression class representing an equality expression.
*
* More details here:
*/
class EqExpr : public Expr {
public:
    /**
    * MEMBER VARIABLES
    */
    PTR(Expr)
    lhs; //!< An expression representing the left hand side of this EqExpr expression
    PTR(Expr)
    rhs; //!< An expression representing the right hand side of this EqExpr expression

    /**
    * FUNCTIONS
    */

    /**
     * \brief EqExpr constructor
    */
    EqExpr(PTR(Expr)
    lhs, PTR(Expr)
    rhs);
    /**
    * \brief determines equality between two Equality expressions
    * \param e another Equality expression that this Equality expression is compared to
    * \return boolean, true for Equality expressions that are equal, otherwise false
    */
    bool equals(PTR(Expr)
    e);
    /**
    * \brief interprets an Equality expression, evaluating the math contained within it
    * \return Val* a value representing the result of the evaluated Equality expression
    */
    PTR(Val) interp(PTR(Env) env );
    /**
    * \brief determines if an Equality expression contains a variable
    * \return boolean, true for Equality expressions that contain any variables, otherwise false
    */
    bool has_variable();
    // /**
    // * \brief substitutes an expression for a given string
    // * \param s a string in this expression to be replaced with e
    // * \param e an expression to substitute into this expression for string s
    // * \return throws an exception as there is no substitution in an Equality expression
    // */
    // PTR(Expr)
    //  subst(std::string s, PTR(Expr)
    //   e);
    /**
    * \brief prints this Equality expression to an outputstream
    * \param os an output stream for this Equality expression to be printed to
    */
    void print(std::ostream& os);
    /**
    * \brief prints this Equality expression in a more readable format
    * \param os the outputstream for this Equality expression to be printed to
    */
    void prettyPrint(std::ostream& os);
    /**
    * \brief helper method to enable precedence accumulation for prettyPrint
    * \param os the output stream for this Equality expression to be printed to
    * \param precedence the accumulated precedence of this expression, used to determine parentheses
    * \param lastNewlinePos tracks the position of the last new line for proper indentation of Let expressions
    * \param needsParens tracks whether a nested expression needs parentheses
    * \param indentLevel tracks how many nested indents are present for printing nested expressions
    */
    void prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel);
};

/*! \brief FunExpr class: a derived expression class representing mathematical function
*
* More details here:
*/
class FunExpr : public Expr {
public:
    /**
    * MEMBER VARIABLES
    */
    std::string formal_arg; //!< the formal argument this function uses for evalutation, i.e. "x", "temp", etc
    PTR(Expr)
    body; //!< the body of this function, i.e. "x+1"
    /**
    * FUNCTIONS
    */

    /**
     * \brief FunExpr constructor
    */
    FunExpr(std::string formal_arg, PTR(Expr)
    body);
    /**
    * \brief
    * \param e another Fun expression that this Fun expression is compared to
    * \return boolean, true for Fun expressions that are equal, otherwise false
    */
    bool equals(PTR(Expr)
    e);
    /**
    * \brief interprets a Fun expression, returning itself as a function cannot be avaluated without an actual argument
    * \return Val* a value representing the result of the evaluated Fun expression
    */
    PTR(Val) interp(PTR(Env) env );
    /**
    * \brief determines if a Fun expression contains a variable
    * \return boolean, true for Fun expressions that contain any variables, otherwise false
    */
    bool has_variable();
    // /**
    // * \brief substitutes e for s in this FunExpr components
    // * \param s a string in this expression to be replaced with e
    // * \param e an expression to substitute into this expression for string s
    // * \return Expr* a pointer to a new expression with the substitution completed
    // */
    // PTR(Expr)
    //  subst(std::string s, PTR(Expr)
    //   e);
    /**
    * \brief prints this Fun expression to an outputstream
    * \param os an output stream for this FunExpr to be printed to
    */
    void print(std::ostream& os);
    /**
    * \brief prints this FunExpr in a more readable format
    * \param os the outputstream for this Fun expression to be printed to
    */
    void prettyPrint(std::ostream& os);
    /**
    * \brief helper method to enable precedence accumulation for prettyPrint
    * \param os the output stream for this Fun expression to be printed to
    * \param precedence the accumulated precedence of this expression, used to determine parentheses
    * \param lastNewlinePos tracks the position of the last new line for proper indentation of Let expressions
    * \param needsParens tracks whether a nested expression needs parentheses
    * \param indentLevel tracks how many nested indents are present for printing nested expressions
    */
    void prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel);
};

/*! \brief CallExpr class: a derived expression class representing mathematical function call passing an argument to
* the function
*
* More details here:
*/
class CallExpr : public Expr {
public:
    /**
    * MEMBER VARIABLES
    */
    PTR(Expr)
    to_be_called; //!< the function expression to be called with/on an argument
    PTR(Expr)
    actual_arg; //!< the argument that the function to_be_called is called with/on
    /**
    * FUNCTIONS
    */

    /**
     * \brief CallExpr constructor
    */
    CallExpr(PTR(Expr)
     to_be_called, PTR(Expr)
     actual_arg);
    /**
    * \brief
    * \param e another Call expression that this Call expression is compared to
    * \return boolean, true for Call expressions that are equal, otherwise false
    */
    bool equals(PTR(Expr)
    e);
    /**
    * \brief interprets a Call expression, substituting the actual_arg and then evaluating the math contained within it
    * \return Val* a value representing the result of the evaluated Call expression
    */
    PTR(Val) interp(PTR(Env) env );
    /**
    * \brief determines if a Call expression contains a variable
    * \return boolean, true for Call expressions that contain any variables, otherwise false
    */
    bool has_variable();
    // /**
    // * \brief substitutes string s into expression e if it exists in this CallExpr
    // * \param s a string in this expression to be replaced with e
    // * \param e an expression to substitute into this expression for string s
    // * \return Expr* a pointer to a new expression with the substitution completed
    // */
    // PTR(Expr)
    //  subst(std::string s, PTR(Expr)
    //   e);
    /**
    * \brief prints this Call expression to an outputstream
    * \param os an output stream for this Call expression to be printed to
    */
    void print(std::ostream& os);
    /**
    * \brief prints this Call expression in a more readable format
    * \param os the outputstream for this Call expression to be printed to
    */
    void prettyPrint(std::ostream& os);
    /**
    * \brief helper method to enable precedence accumulation for prettyPrint
    * \param os the output stream for this Call expression to be printed to
    * \param precedence the accumulated precedence of this expression, used to determine parentheses
    * \param lastNewlinePos tracks the position of the last new line for proper indentation of Let expressions
    * \param needsParens tracks whether a nested expression needs parentheses
    * \param indentLevel tracks how many nested indents are present for printing nested expressions
    */
    void prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel);
};



#endif //EXPR_H
