/**
* Created by Jason Ablott on 1/20/25.
*/

#include "val.h"
#include "expr.h"
#include "catch.h"
#include <stdexcept>

// Expr class functions
std::string Expr::to_string() {
    std::stringstream stringStream("");
    this->print(stringStream);
    return stringStream.str();
}
std::string Expr::to_pretty_string() {
    std::stringstream stringStream("");
    this->prettyPrint(stringStream);
    return stringStream.str();
}
// base implementations, should only be overridden in MultExpr and AddExpr derived classes
void Expr::prettyPrint(std::ostream &os) {
    prettyPrintAt(os, prec_none, os.tellp(), false, 0);
}
void Expr::print(std::ostream &os) {}
void Expr::prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel) {
    this->print(os);
}


// NumExpr class functions



// NumExpr constructor
NumExpr::NumExpr(int val) {
    this->val = val;
}
// equals implementation
bool NumExpr::equals(PTR(Expr)e){
    PTR(NumExpr) num = CAST(NumExpr)(e);
    if (num == nullptr) return false;
    return this->val == num->val;
}
// interp implementation
PTR(Val) NumExpr::interp(PTR(Env) env ) {
    return NEW(NumVal)(this->val);
}
// has_variable implementation
bool NumExpr::has_variable() {
    return false;
}
// // subst implementation, returns copy of this number
// PTR(Expr) NumExpr::subst(std::string s, PTR(Expr) e) {
//     return NEW(NumExpr)(this->val);
//}
// print method
void NumExpr::print(std::ostream &os) {
    os << std::to_string(this->val);
}



// AddExpr class functions



// AddExpr constructor
AddExpr::AddExpr(PTR(Expr)lhs, PTR(Expr) rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}
// equals implementation
bool AddExpr::equals(PTR(Expr)e) {
    PTR(AddExpr) add = CAST(AddExpr)(e);
    if (add == nullptr) return false;
    return this->lhs->equals(add->lhs) && this->rhs->equals(add->rhs);
}
// interp implementation
PTR(Val) AddExpr::interp(PTR(Env) env ) {
    PTR(Val) leftNumVal = lhs->interp(env);
    PTR(Val) rightNumVal = rhs->interp(env);
    PTR(NumVal) leftNum = CAST(NumVal)(leftNumVal);
    PTR(NumVal) rightNum = CAST(NumVal)(rightNumVal);
    // if both sides are NumVals we can add them
    if (leftNum && rightNum) {
        return leftNum->addTo(rightNum);
    }
    // throw exception if either is not a NumVal
    if (leftNum == nullptr || rightNum == nullptr) {
        throw std::runtime_error("AddExpr::interp() failed due to being passed a boolean value");
    }
    return nullptr;
}
// has_variable implementation
bool AddExpr::has_variable() {
    return this->lhs->has_variable() || this->rhs->has_variable();
}
// // subst implementation, recursively attempt subst on both sides
// PTR(Expr) AddExpr::subst(std::string s, PTR(Expr) e) {
//     return NEW(AddExpr)(lhs->subst(s, e), rhs->subst(s, e));
// }
// print method
void AddExpr::print(std::ostream &os) {
    os << "(";
    lhs->print(os);
    os << "+";
    rhs->print(os);
    os << ")";
}
// pretty print method
void AddExpr::prettyPrint(std::ostream &os) {
    prettyPrintAt(os, prec_none, os.tellp(), false, 0);
}
// prettyPrintAt method
void AddExpr::prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel) {
    if (precedence >= prec_add || needsParens) { os << "("; }
    lhs->prettyPrintAt(os, prec_add, os.tellp(), true, indentLevel);
    os << " + ";
    rhs->prettyPrintAt(os, prec_none, os.tellp(), false, indentLevel);
    if (precedence >= prec_add || needsParens) { os << ")"; }
}



// MultExpr class functions



// MultExpr constructor
MultExpr::MultExpr(PTR(Expr)lhs, PTR(Expr) rhs) {
    this->lhs = lhs;
    this->rhs = rhs;
}
// equals implementation
bool MultExpr::equals(PTR(Expr)e) {
    PTR(MultExpr) mult = CAST(MultExpr)(e);
    if (mult == nullptr) return false;
    return this->lhs->equals(mult->lhs) && this->rhs->equals(mult->rhs);
}
// interp implementation
PTR(Val) MultExpr::interp(PTR(Env) env ) {
    PTR(Val) leftNumVal = lhs->interp(env);
    PTR(Val) rightNumVal = rhs->interp(env);
    PTR(NumVal) leftNum = CAST(NumVal)(leftNumVal);
    PTR(NumVal) rightNum = CAST(NumVal)(rightNumVal);
    // if both sides are NumVals we can add them
    if (leftNum && rightNum) {
        return leftNum->multWith(rightNum);
    }
    // throw exception if either is not a NumVal
    if (leftNum == nullptr || rightNum == nullptr) {
        throw std::runtime_error("MultExpr::interp() failed due to being passed a boolean value");
    }
    return nullptr;
}
// has_variable implementation
bool MultExpr::has_variable() {
    return this->lhs->has_variable() || this->rhs->has_variable();
}
// subst implementation, recursively attempt subst on both sides
// PTR(Expr) MultExpr::subst(std::string s, PTR(Expr) e) {
//     return NEW(MultExpr)(lhs->subst(s, e), rhs->subst(s, e));
// }
// print method
void MultExpr::print(std::ostream &os) {
    os << "(";
    lhs->print(os);
    os << "*";
    rhs->print(os);
    os << ")";
}
// pretty print method
void MultExpr::prettyPrint(std::ostream &os) {
    prettyPrintAt(os, prec_none, os.tellp(), false, 0);
}
// prettyPrintAt method
void MultExpr::prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel) {
    if (precedence >= prec_mult || needsParens) { os << "("; }
    lhs->prettyPrintAt(os, prec_mult, os.tellp(), true, indentLevel);
    os << " * ";
    rhs->prettyPrintAt(os, prec_add, os.tellp(), false, indentLevel);
    if (precedence >= prec_mult || needsParens) { os << ")"; }
}


// VarExpr class functions


// VarExpr constructor
VarExpr::VarExpr(std::string name) {
    this->name = name;
}
// equals implementation
bool VarExpr::equals(PTR(Expr)e) {
    PTR(VarExpr) variable = CAST(VarExpr)(e);
    if (variable == nullptr) return false;
    return variable->name == this->name;
}
// interp implementation, can't interpret a variable so throw error
PTR(Val) VarExpr::interp(PTR(Env) env ){
    // before Environments: throw std::runtime_error("variable has no integer value");
    return env->lookup(this->name);
}
// has_variable implementation, variable is a variable, so return true
bool VarExpr::has_variable() {
    return true;
}
// // subst implementation
// PTR(Expr) VarExpr::subst(std::string s, PTR(Expr) e) {
//     if (name == s) {
//         return e;
//     } else {
//         return NEW(VarExpr)(this->name);
//     }
// }
// print method
void VarExpr::print(std::ostream &os) {
    os << this->name;
}


// LetExpr class functions


// constructor
LetExpr::LetExpr(std::string var, PTR(Expr) rhs, PTR(Expr) body) {
    this -> variable = var;
    this -> rhs = rhs;
    this -> body = body;
}
// equals
bool LetExpr::equals(PTR(Expr)e) {
        if ((this->variable == CAST(LetExpr)(e)->variable) &&
            this->rhs->equals(CAST(LetExpr)(e)->rhs) &&
            this->body->equals(CAST(LetExpr)(e)->body)) {
                return true;
        }
    return false;
}
// interp
PTR(Val) LetExpr::interp(PTR(Env) env ) {
    // return this->body->subst(this->variable, this->rhs)->interp(env);
    PTR(Val) rhs_val = rhs->interp(env);
    PTR(Env) new_env = NEW(ExtendedEnv)(variable, rhs_val, env);
    return body->interp(new_env);
}
// has_variable
bool LetExpr::has_variable() {
    if (this->rhs->has_variable() || this->body->has_variable()) {
        return true;
    }
    return false;
}
// // substitute
// PTR(Expr) LetExpr::subst(std::string s, PTR(Expr) e) {
//     // case 1: substitution value and this variable are same
//     if (s == this->variable) {
//         return NEW(LetExpr)(this->variable, this->rhs->subst(s,e), body);
//     }
//     // case 2: substitution value and this variable are different
//     return NEW(LetExpr)(this->variable, this->rhs->subst(s,e), this->body->subst(s, e));
//     return nullptr;
// }
// print
void LetExpr::print(std::ostream& os) {
    os << "(_let ";
    os << this->variable;
    os << "=";
    rhs->print(os);
    os << " _in ";
    body->print(os);
    os << ")";
}
// pretty print method
void LetExpr::prettyPrint(std::ostream &os) {
    // start position track
    std::streampos lastNewlinePos = os.tellp();
    // let has no prec
    precedence_t precedence = prec_none;
    // let doesn't need parens on it's own
    bool needsParens = false;
    // start w/no indent
    int indentLevel = 0;
    // pass to prettyPrintAt
    prettyPrintAt(os, precedence, lastNewlinePos, needsParens, indentLevel);
}
// prettyPrintAt method
void LetExpr::prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel) {

    // print parens if specified by parent
    if (needsParens) { os << "("; }

    // print beginning of LetExpr, always the same
    os << "_let " << variable << " = ";

    // Print rhs, let has no prec and no parens
    rhs->prettyPrintAt(os, prec_none, lastNewlinePos, false, indentLevel);


    // Print the `_in` keyword, starting a new line with correct indentation
    os << "\n" << std::string(indentLevel, ' ') << "_in  ";
    // update position
    lastNewlinePos = os.tellp();

    // Print the body, accumulate indent but still no prec and parens since parent is let
    body->prettyPrintAt(os, prec_none, lastNewlinePos, false, indentLevel+4);

    // print parens if specified by parent
    if (needsParens) { os << ")"; }
}


// BoolExpr Class Functions


// Bool expression constructor
BoolExpr::BoolExpr(int val) {
    this->val = val;
}
// brief determines equality between two Bool expressions
bool BoolExpr::equals(PTR(Expr)e) {
    PTR(BoolExpr) otherBool = CAST(BoolExpr)(e);
    if (otherBool == nullptr) return false;
    return this->val == otherBool->val;
}
// brief interprets a Bool expression
PTR(Val) BoolExpr::interp(PTR(Env) env ) {
    return NEW(BoolVal)(this->val);
}
// brief determines if a Bool expression contains a variable, always false
bool BoolExpr::has_variable() {
    return false;
}
// // brief substitutes a bool expression for a given string
// PTR(Expr) BoolExpr::subst(std::string s, PTR(Expr) e) {
//     return NEW(BoolExpr)(this->val);
// }
// prints this Bool expression to an outputstream
void BoolExpr::print(std::ostream& os) {
    if (this->val == true) {
        os << "_true";
    } else {
        os << "_false";
    }
}


// IfExpr Class Functions


//If expression constructor
IfExpr::IfExpr(PTR(Expr) testPart, PTR(Expr) thenPart, PTR(Expr) elsePart) {
    this->testPart = testPart;
    this->thenPart = thenPart;
    this->elsePart = elsePart;
}
// brief determines equality between two If expressions
bool IfExpr::equals(PTR(Expr)e) {
    if (CAST(IfExpr)(e) == nullptr) return false;
    if (this->testPart->equals(CAST(IfExpr)(e)->testPart)){
        if (this->thenPart->equals(CAST(IfExpr)(e)->thenPart)) {
            if (this->elsePart->equals(CAST(IfExpr)(e)->elsePart)) {
                return true;
            }
        }
    }
    return false;
}
// interprets an If expression, evaluating the testPart and then either the thenPart or elsePart
PTR(Val) IfExpr::interp(PTR(Env) env ) {
    // interp testPart, make sure it returned a boolVal
    PTR(Val) testResult = this->testPart->interp(env);
    if (CAST(BoolVal)(testResult) == nullptr) {
        throw std::runtime_error("Interp testPart did not return a BoolVal");
    }
    // based on result interp then or else
    if (testResult->isTrue()) {
        return thenPart->interp(env);
    } else {
        return elsePart->interp(env);
    }
}
// determines if an If expression contains a variable
bool IfExpr::has_variable() {
    if (this->testPart->has_variable() || this->thenPart->has_variable() || this->elsePart->has_variable()) {
        return true;
    }
    return false;
}
// // substitution for If expressions
// PTR(Expr) IfExpr::subst(std::string s, PTR(Expr) e) {
//     return NEW(IfExpr)(testPart->subst(s, e), thenPart->subst(s, e), elsePart->subst(s, e));
// }
// prints this If expression to an outputstream
void IfExpr::print(std::ostream& os) {
    os << "(_if ";
    testPart->print(os);
    os << "_then ";
    thenPart->print(os);
    os << " _else ";
    elsePart->print(os);
    os << ")";
}
// prints this If expression in a more readable format
void IfExpr::prettyPrint(std::ostream &os){
    // start position track
    std::streampos lastNewlinePos = os.tellp();
    // let has no prec
    precedence_t precedence = prec_equal;
    // let doesn't need parens on it's own
    bool needsParens = false;
    // start w/no indent
    int indentLevel = 0;
    // pass to prettyPrintAt
    prettyPrintAt(os, precedence, lastNewlinePos, needsParens, indentLevel);
}
// helper method for IfExpr::prettyPrint
void IfExpr::prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel) {
    // print parens if specified by parent
    if (needsParens) { os << "("; }

    // print beginning of LetExpr, always the same
    os << "_if ";

    // Print rhs, let has no prec and no parens
    testPart->prettyPrintAt(os, prec_equal, lastNewlinePos, false, indentLevel);

    // Print the `_in` keyword, starting a new line with correct indentation
    os << "\n" << std::string(indentLevel, ' ') << "_then ";
    // update position
    lastNewlinePos = os.tellp();

    // Print the body, accumulate indent but still no prec and parens since parent is let
    thenPart->prettyPrintAt(os, prec_equal, lastNewlinePos, false, indentLevel);

    // Print the `_in` keyword, starting a new line with correct indentation
    os << "\n" << std::string(indentLevel, ' ') << "_else ";
    // update position
    lastNewlinePos = os.tellp();

    // Print the body, accumulate indent but still no prec and parens since parent is let
    elsePart->prettyPrintAt(os, prec_equal, lastNewlinePos, false, indentLevel);

    // print parens if specified by parent
    if (needsParens) { os << ")"; }
}


// EqExpr Class Functions


// brief EqExpr constructor
EqExpr::EqExpr(PTR(Expr)lhs, PTR(Expr) rhs) {
    this -> lhs = lhs;
    this -> rhs = rhs;
}
// determines equality between two Equality expressions, compares both sides
bool EqExpr::equals(PTR(Expr)e) {
    if (CAST(EqExpr)(e) == nullptr) return false;
    if (this->lhs->equals(CAST(EqExpr)(e)->lhs)) {
        if (this->rhs->equals(CAST(EqExpr)(e)->rhs)) {
            return true;
        }
    }
    return false;
}
// interprets an Equality expression, evaluating the math contained within it
PTR(Val) EqExpr::interp(PTR(Env) env ) {
    PTR(Val) lhs = this->lhs->interp(env);
    PTR(Val) rhs = this->rhs->interp(env);
    // if lhs and rhs are not same type throw exception
    if (!(CAST(BoolVal)(lhs) == nullptr && CAST(BoolVal)(rhs) == nullptr) &&
        !(CAST(NumVal)(lhs) == nullptr && CAST(NumVal)(rhs) == nullptr)) {
        throw std::runtime_error("not comparable in EqExpr::interp");
    }
    // else compare the interped values
    if (lhs->equals(rhs)) {
        return NEW(BoolVal)(true);
    }
    return NEW(BoolVal)(false);
}
// determines if an Equality expression contains a variable
bool EqExpr::has_variable() {
    if (this->lhs->has_variable() || this->rhs->has_variable()) {
        return true;
    }
    return false;
}
// // substitutes an expression for a given string
// PTR(Expr) EqExpr::subst(std::string s, PTR(Expr) e) {
//     return NEW(EqExpr)(lhs->subst(s, e), rhs->subst(s, e));
// }
// prints this Equality expression to an outputstream
void EqExpr::print(std::ostream& os) {
    os << "(";
    lhs->print(os);
    os << "==";
    rhs->print(os);
    os << ")";
}
// prints this Equality expression in a more readable format
void EqExpr::prettyPrint(std::ostream& os) {
    prettyPrintAt(os, prec_equal, os.tellp(), false, 0);
}
// helper method to enable precedence accumulation for prettyPrint
void EqExpr::prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel) {
    if (precedence >= prec_mult || needsParens) { os << "("; }
    lhs->prettyPrintAt(os, prec_equal, os.tellp(), true, indentLevel);
    os << " == ";
    rhs->prettyPrintAt(os, prec_none, os.tellp(), false, indentLevel);
    if (precedence >= prec_mult || needsParens) { os << ")"; }
}


// FunExpr Functions


// FunExpr constructor
FunExpr::FunExpr(std::string formal_arg, PTR(Expr) body) {
    this -> formal_arg = formal_arg;
    this -> body = body;
}
// FunExpr equals method
bool FunExpr::equals(PTR(Expr)e) {
    if (CAST(FunExpr)(e) == nullptr) return false;
    if (this->formal_arg == CAST(FunExpr)(e)->formal_arg) {
        if (this->body->equals(CAST(FunExpr)(e)->body)) {
            return true;
        }
    }
    return false;
}
// FunExpr interp method
PTR(Val) FunExpr::interp(PTR(Env) env ) {
    return NEW(FunVal)(formal_arg, body, env);
}
// FunExpr has_variable method
bool FunExpr::has_variable() {
    return true;
}
// // FunExpr substitution method
// PTR(Expr) FunExpr::subst(std::string s, PTR(Expr) e) {
//     if (s == formal_arg) {
//         return THIS;
//     }
//     return NEW(FunExpr)(formal_arg, body->subst(s,e));
// }
// FunExpr print method
void FunExpr::print(std::ostream& os) {
    os << "_fun";
    os << " (";
    os << formal_arg;
    os << ") ";
    body->print(os);
}
// FunExpr prettyprint method
void FunExpr::prettyPrint(std::ostream& os) {
    // start position track
    std::streampos lastNewlinePos = os.tellp();
    // Fun has top prec
    precedence_t precedence = prec_mult;
    // Fun doesn't need parens on it's own
    bool needsParens = false;
    // start w/no indent
    int indentLevel = 0;
    // pass to prettyPrintAt
    prettyPrintAt(os, precedence, lastNewlinePos, needsParens, indentLevel);
}
//FunExpr prettyPrintAt helper
void FunExpr::prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel) {
    // print parens if specified by parent
    if (needsParens) { os << "("; }

    // print formal arg and keyword
    os << "_fun (" << formal_arg << ") \n";

    // Print space on new line with correct indentation
    indentLevel+=4;
    os << std::string(indentLevel+2, ' ');

    // Print body, Fun has top prec and no parens
    body->prettyPrintAt(os, prec_none, lastNewlinePos, false, indentLevel);

    // update position
    lastNewlinePos = os.tellp();

    // print parens if specified by parent
    if (needsParens) { os << ")"; }
}


// CallExpr Functions


// CallExpr constructor
CallExpr::CallExpr(PTR(Expr) to_be_called, PTR(Expr) actual_arg) {
    this -> to_be_called = to_be_called;
    this -> actual_arg = actual_arg;
}
// CallExpr equals method
bool CallExpr::equals(PTR(Expr)e) {
    if (CAST(CallExpr)(e) == nullptr) return false;
    if (this->to_be_called == CAST(CallExpr)(e)->to_be_called) {
        if (this->actual_arg->equals(CAST(CallExpr)(e)->actual_arg)) {
            return true;
        }
    }
    return false;
}
// CallExpr interp method
PTR(Val) CallExpr::interp(PTR(Env) env ) {
    return to_be_called->interp(env)->call(actual_arg->interp(env));
}
// CallExpr has_variable method
bool CallExpr::has_variable() {
    return true;
}
// // CallExpr substitute method
// PTR(Expr) CallExpr::subst(std::string s, PTR(Expr) e) {
//     return NEW(CallExpr)(to_be_called->subst(s, e), actual_arg->subst(s, e));
// }
// CallExpr print method
void CallExpr::print(std::ostream& os) {
    to_be_called->print(os);
    os << "(";
    actual_arg->print(os);
    os << ")";
}
// CallExpr prettyPrint method
void CallExpr::prettyPrint(std::ostream& os) {
    // start position track
    std::streampos lastNewlinePos = os.tellp();
    // Call has no prec
    precedence_t precedence = prec_none;
    // Call doesn't need parens on it's own
    bool needsParens = false;
    // start w/no indent
    int indentLevel = 0;
    // pass to prettyPrintAt
    prettyPrintAt(os, precedence, lastNewlinePos, needsParens, indentLevel);
}
// CallExpr prettyPrintAt helper
void CallExpr::prettyPrintAt(std::ostream &os, precedence_t precedence, std::streampos lastNewlinePos, bool needsParens, int indentLevel) {
    // print parens if specified by parent
    if (needsParens) { os << "("; }

    // Print to_be_called function recursively
    to_be_called->prettyPrintAt(os, prec_none, lastNewlinePos, false, indentLevel);

    // Print parens and actual arg
    os << "(";
    actual_arg->prettyPrintAt(os, prec_none, lastNewlinePos, false, indentLevel);
    os << ")";

    // update position
    lastNewlinePos = os.tellp();

    // print parens if specified by parent
    if (needsParens) { os << ")"; }
}


// Testing


TEST_CASE("Expr Tests") {
    // constructor and equals testing


    // // Tests for NumExpr class
    // SECTION("NumExpr")
    // {
    //     // Failing test to check that program exits when Catch::session().run returns a non-zero value
    //     // REQUIRE( (new NumExpr(1))->equals(new NumExpr(2)) == true );
    //     // 2 = 2 = true
    //     CHECK( (new NumExpr(2))->equals(new NumExpr(2)) == true );
    //     // 2 = 1 = false
    //     CHECK( (new NumExpr(2))->equals(new NumExpr(1)) == false );
    //     // 0 = 0 = true
    //     CHECK( (new NumExpr(0))->equals(new NumExpr(0)) == true );
    //     // 0 = 2 = false
    //     CHECK( (new NumExpr(0))->equals(new NumExpr(2)) == false );
    //     // 0 = -2 = false
    //     CHECK( (new NumExpr(0))->equals(new NumExpr(-2)) == false );
    //     // 30001 = 30001 = true
    //     CHECK( (new NumExpr(30001))->equals(new NumExpr(30001)) == true );
    //     // 30001 = 300002 = false
    //     CHECK( (new NumExpr(30001))->equals(new NumExpr(30002)) == false );
    // }
    //
    // // Tests for AddExpr class
    // SECTION("AddExpr")
    // {
    //     // 4+1 = 4+1 = true
    //     CHECK( (new AddExpr(new NumExpr(4),new NumExpr(1)))->equals(new AddExpr(new NumExpr(4),new NumExpr(1))) == true );
    //     // 4+1 = 2+3 = false
    //     CHECK( (new AddExpr(new NumExpr(4),new NumExpr(1)))->equals (new AddExpr(new NumExpr(2),new NumExpr(3))) == false  );
    //     // 2+1 = -2+1 = false
    //     CHECK( (new AddExpr(new NumExpr(2),new NumExpr(1)))->equals (new AddExpr(new NumExpr(-2),new NumExpr(1))) == false  );
    //     // -2+1 = 1+-2 = false
    //     CHECK( (new AddExpr(new NumExpr(-2),new NumExpr(1)))->equals (new AddExpr(new NumExpr(1),new NumExpr(-2))) == false  );
    //     // 0+0 = 0+0 = true
    //     CHECK( (new AddExpr(new NumExpr(0),new NumExpr(0)))->equals (new AddExpr(new NumExpr(0),new NumExpr(0))) == true  );
    //     // 0+1 = 1+0 = false
    //     CHECK( (new AddExpr(new NumExpr(0),new NumExpr(1)))->equals (new AddExpr(new NumExpr(1),new NumExpr(0))) == false  );
    //     // 0+0 = 0+1 = false
    //     CHECK( (new AddExpr(new NumExpr(0),new NumExpr(0)))->equals (new AddExpr(new NumExpr(0),new NumExpr(1))) == false  );
    // }
    //
    // // Tests for MultExpr class
    // SECTION("MultExpr")
    // {
    //     //4x1 = 4x1 = true
    //     CHECK( (new MultExpr (new NumExpr(4), new NumExpr(1)))->equals(new MultExpr(new NumExpr(4), new NumExpr (1))) == true);
    //     // 4x1 = 1x4 = false
    //     CHECK( (new MultExpr (new NumExpr(4), new NumExpr(1)))->equals(new MultExpr(new NumExpr(1), new NumExpr (4))) == false);
    //     // 0x0 = 0x0 = true
    //     CHECK( (new MultExpr (new NumExpr(0), new NumExpr(0)))->equals(new MultExpr(new NumExpr(0), new NumExpr (0))) == true);
    //     // -4x1 = 4x1 = false
    //     CHECK( (new MultExpr (new NumExpr(-4), new NumExpr(1)))->equals(new MultExpr(new NumExpr(4), new NumExpr (1))) == false);
    //     // -4x-1 = -4x-1 = true
    //     CHECK( (new MultExpr (new NumExpr(-4), new NumExpr(-1)))->equals(new MultExpr(new NumExpr(-4), new NumExpr (-1))) == true);
    //     // -4x-1 = 4x1 = false
    //     CHECK( (new MultExpr (new NumExpr(-4), new NumExpr(-1)))->equals(new MultExpr(new NumExpr(4), new NumExpr (1))) == false);
    // }
    //
    // // Tests for VarExpr class
    // SECTION("VarExpr")
    // {
    //     // x = x = true
    //     CHECK( (new VarExpr("x"))->equals(new VarExpr("x")) == true );
    //     // x = y = false
    //     CHECK( (new VarExpr("x"))->equals(new VarExpr("y")) == false );
    //     // ab = ab = true
    //     CHECK( (new VarExpr("ab"))->equals(new VarExpr("ab")) == true );
    //     // ab = ba = false
    //     CHECK( (new VarExpr("ab"))->equals(new VarExpr("ba")) == false );
    //     // " " = " " = true
    //     CHECK( (new VarExpr(" "))->equals(new VarExpr(" ")) == true );
    //     // "The quick brown fox jumped over the lazy dog." = "The quick brown fox jumped over the lazy dog." = true
    //     CHECK( (new VarExpr("The quick brown fox jumped over the lazy dog."))->equals(new VarExpr("The quick brown fox jumped over the lazy dog.")) == true );
    //     // "The quick brown fox jumped over the lazy dog." = "the quick brown fox jumped over the lazy dog." = false
    //     CHECK( (new VarExpr("The quick brown fox jumped over the lazy dog."))->equals(new VarExpr("the quick brown fox jumped over the lazy dog.")) == false );
    // }
    // Tests for NumExpr class
    SECTION("NumExpr")
    {
        // Failing test to check that program exits when Catch::session().run returns a non-zero value
        // REQUIRE( (NEW(NumExpr)(1))->equals(NEW(NumExpr)(2)) == true );
        // 2 = 2 = true
        CHECK( (NEW(NumExpr)(2))->equals(NEW(NumExpr)(2)) == true );
        // 2 = 1 = false
        CHECK( (NEW(NumExpr)(2))->equals(NEW(NumExpr)(1)) == false );
        // 0 = 0 = true
        CHECK( (NEW(NumExpr)(0))->equals(NEW(NumExpr)(0)) == true );
        // 0 = 2 = false
        CHECK( (NEW(NumExpr)(0))->equals(NEW(NumExpr)(2)) == false );
        // 0 = -2 = false
        CHECK( (NEW(NumExpr)(0))->equals(NEW(NumExpr)(-2)) == false );
        // 30001 = 30001 = true
        CHECK( (NEW(NumExpr)(30001))->equals(NEW(NumExpr)(30001)) == true );
        // 30001 = 300002 = false
        CHECK( (NEW(NumExpr)(30001))->equals(NEW(NumExpr)(30002)) == false );
    }

    // Tests for AddExpr class
    SECTION("AddExpr")
    {
        // 4+1 = 4+1 = true
        CHECK( (NEW(AddExpr)(NEW(NumExpr)(4), NEW(NumExpr)(1)))->equals(NEW(AddExpr)(NEW(NumExpr)(4), NEW(NumExpr)(1))) == true );
        // 4+1 = 2+3 = false
        CHECK( (NEW(AddExpr)(NEW(NumExpr)(4), NEW(NumExpr)(1)))->equals(NEW(AddExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3))) == false );
        // 2+1 = -2+1 = false
        CHECK( (NEW(AddExpr)(NEW(NumExpr)(2), NEW(NumExpr)(1)))->equals(NEW(AddExpr)(NEW(NumExpr)(-2), NEW(NumExpr)(1))) == false );
        // -2+1 = 1+-2 = false
        CHECK( (NEW(AddExpr)(NEW(NumExpr)(-2), NEW(NumExpr)(1)))->equals(NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(-2))) == false );
        // 0+0 = 0+0 = true
        CHECK( (NEW(AddExpr)(NEW(NumExpr)(0), NEW(NumExpr)(0)))->equals(NEW(AddExpr)(NEW(NumExpr)(0), NEW(NumExpr)(0))) == true );
        // 0+1 = 1+0 = false
        CHECK( (NEW(AddExpr)(NEW(NumExpr)(0), NEW(NumExpr)(1)))->equals(NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(0))) == false );
        // 0+0 = 0+1 = false
        CHECK( (NEW(AddExpr)(NEW(NumExpr)(0), NEW(NumExpr)(0)))->equals(NEW(AddExpr)(NEW(NumExpr)(0), NEW(NumExpr)(1))) == false );
    }

    // Tests for MultExpr class
    SECTION("MultExpr")
    {
        //4x1 = 4x1 = true
        CHECK( (NEW(MultExpr)(NEW(NumExpr)(4), NEW(NumExpr)(1)))->equals(NEW(MultExpr)(NEW(NumExpr)(4), NEW(NumExpr)(1))) == true );
        // 4x1 = 1x4 = false
        CHECK( (NEW(MultExpr)(NEW(NumExpr)(4), NEW(NumExpr)(1)))->equals(NEW(MultExpr)(NEW(NumExpr)(1), NEW(NumExpr)(4))) == false );
        // 0x0 = 0x0 = true
        CHECK( (NEW(MultExpr)(NEW(NumExpr)(0), NEW(NumExpr)(0)))->equals(NEW(MultExpr)(NEW(NumExpr)(0), NEW(NumExpr)(0))) == true );
        // -4x1 = 4x1 = false
        CHECK( (NEW(MultExpr)(NEW(NumExpr)(-4), NEW(NumExpr)(1)))->equals(NEW(MultExpr)(NEW(NumExpr)(4), NEW(NumExpr)(1))) == false );
        // -4x-1 = -4x-1 = true
        CHECK( (NEW(MultExpr)(NEW(NumExpr)(-4), NEW(NumExpr)(-1)))->equals(NEW(MultExpr)(NEW(NumExpr)(-4), NEW(NumExpr)(-1))) == true );
        // -4x-1 = 4x1 = false
        CHECK( (NEW(MultExpr)(NEW(NumExpr)(-4), NEW(NumExpr)(-1)))->equals(NEW(MultExpr)(NEW(NumExpr)(4), NEW(NumExpr)(1))) == false );
    }

    // Tests for VarExpr class
    SECTION("VarExpr")
    {
        // x = x = true
        CHECK( (NEW(VarExpr)("x"))->equals(NEW(VarExpr)("x")) == true );
        // x = y = false
        CHECK( (NEW(VarExpr)("x"))->equals(NEW(VarExpr)("y")) == false );
        // ab = ab = true
        CHECK( (NEW(VarExpr)("ab"))->equals(NEW(VarExpr)("ab")) == true );
        // ab = ba = false
        CHECK( (NEW(VarExpr)("ab"))->equals(NEW(VarExpr)("ba")) == false );
        // " " = " " = true
        CHECK( (NEW(VarExpr)(" "))->equals(NEW(VarExpr)(" ")) == true );
        // "The quick brown fox jumped over the lazy dog." = "The quick brown fox jumped over the lazy dog." = true
        CHECK( (NEW(VarExpr)("The quick brown fox jumped over the lazy dog."))->equals(NEW(VarExpr)("The quick brown fox jumped over the lazy dog.")) == true );
        // "The quick brown fox jumped over the lazy dog." = "the quick brown fox jumped over the lazy dog." = false
        CHECK( (NEW(VarExpr)("The quick brown fox jumped over the lazy dog."))->equals(NEW(VarExpr)("the quick brown fox jumped over the lazy dog.")) == false );
    }



    //interp, has_variable, and subst testing



    // Tests for Interp
    SECTION("Interp") {
        // Interp NumExpr

        // Interp AddExpr
        CHECK( (NEW(AddExpr)(NEW(AddExpr)(NEW(NumExpr)(10), NEW(NumExpr)(15)),NEW(AddExpr)(NEW(NumExpr)(20),NEW(NumExpr)(20))))->interp(Env::empty)->equals(NEW(NumVal)(65)));
        // Interp MultExpr
        CHECK( (NEW(MultExpr)(NEW(NumExpr)(3), NEW(NumExpr)(2)))->interp(Env::empty)->equals(NEW(NumVal)(6)));
        // Interp VarExpr
        CHECK_THROWS_WITH( (NEW(VarExpr)("x"))->interp(Env::empty), "free variable: x" );

        // added

        // Interp for NumExpr (simple constant)
        CHECK( (NEW(NumExpr)(42))->interp(Env::empty)->equals(NEW(NumVal)(42)));  // Simple test for a number

        // Interp for AddExpr (nested addition)
        CHECK( (NEW(AddExpr)(NEW(AddExpr)(NEW(NumExpr)(10), NEW(NumExpr)(15)), NEW(AddExpr)(NEW(NumExpr)(20), NEW(NumExpr)(20))))->interp(Env::empty)->equals(NEW(NumVal)(65)));
        // Adding multiple numbers together
        CHECK( (NEW(AddExpr)(NEW(NumExpr)(1), NEW(AddExpr)(NEW(NumExpr)(2), NEW(AddExpr)(NEW(NumExpr)(3), NEW(NumExpr)(4)))))->interp(Env::empty)->equals(NEW(NumVal)(10)));

        // Interp for MultExpr (multiplication)
        CHECK( (NEW(MultExpr)(NEW(NumExpr)(3), NEW(NumExpr)(2)))->interp(Env::empty)->equals(NEW(NumVal)(6)));
        // Multiplying multiple numbers together
        CHECK( (NEW(MultExpr)(NEW(NumExpr)(2), NEW(MultExpr)(NEW(NumExpr)(3), NEW(NumExpr)(4))))->interp(Env::empty)->equals(NEW(NumVal)(24)));

        // Interp for VarExpr (no value assigned, should throw)
        CHECK_THROWS_WITH( (NEW(VarExpr)("x"))->interp(Env::empty), "free variable: x" );

        // Interp for VarExpr with unknown variable (should throw)
        CHECK_THROWS_WITH( (NEW(VarExpr)("z"))->interp(Env::empty), "free variable: z" );

        // Edge case: Interp for empty or invalid operations
        CHECK_THROWS_WITH( (NEW(AddExpr)(NEW(NumExpr)(3), NEW(VarExpr)("unknown")))->interp(Env::empty), "free variable: unknown" );  // undefined variable
        CHECK_THROWS_WITH( (NEW(MultExpr)(NEW(NumExpr)(0), NEW(VarExpr)("x")))->interp(Env::empty), "free variable: x" );  // undefined variable in multiplication
    }

    // Tests for has_variable
    SECTION("has_variable") {
        // has_variable NumExpr

        // has_variable AddExpr
        CHECK( (NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))->has_variable() == true );
        // has_variable MultExpr
        CHECK( (NEW(MultExpr)(NEW(NumExpr)(2), NEW(NumExpr)(1)))->has_variable() == false );
        // has_variable VarExpr

        //added

        // has_variable for NumExpr (should always be false because a NumExpr has no variables)
        CHECK( (NEW(NumExpr)(42))->has_variable() == false );  // NumExpr doesn't contain a variable

        // has_variable for AddExpr (check if left or right side has a variable)
        CHECK( (NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))->has_variable() == true );  // Left side has a variable
        CHECK( (NEW(AddExpr)(NEW(NumExpr)(1), NEW(VarExpr)("y")))->has_variable() == true );  // Right side has a variable
        CHECK( (NEW(AddExpr)(NEW(NumExpr)(3), NEW(NumExpr)(4)))->has_variable() == false );  // No variables in AddExpr

        // has_variable for MultExpr (check if left or right side has a variable)
        CHECK( (NEW(MultExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(2)))->has_variable() == true );  // Left side has a variable
        CHECK( (NEW(MultExpr)(NEW(NumExpr)(2), NEW(VarExpr)("y")))->has_variable() == true );  // Right side has a variable
        CHECK( (NEW(MultExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3)))->has_variable() == false );  // No variables in MultExpr

        // has_variable for VarExpr (should always be true because it's a variable)
        CHECK( (NEW(VarExpr)("x"))->has_variable() == true );  // A VarExpr is a variable, so it should return true
        CHECK( (NEW(VarExpr)("y"))->has_variable() == true );  // Same for another VarExpr

        // has_variable for complex expressions (nested AddExpr and MultExpr)
        CHECK( (NEW(AddExpr)(NEW(MultExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(2)), NEW(NumExpr)(3)))->has_variable() == true );  // MultExpr has a variable
        CHECK( (NEW(AddExpr)(NEW(NumExpr)(3), NEW(MultExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(4))))->has_variable() == true );  // MultExpr has a variable
        CHECK( (NEW(MultExpr)(NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5)), NEW(NumExpr)(3)))->has_variable() == true );  // AddExpr contains a variable

        // has_variable for complex nested expressions with multiple AddExpr and MultExpr
        CHECK( (NEW(AddExpr)(NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5)), NEW(NumExpr)(3)))->has_variable() == true );  // Inner AddExpr has a variable
        CHECK( (NEW(MultExpr)(NEW(MultExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(2)), NEW(NumExpr)(3)))->has_variable() == true );  // Nested MultExpr has a variable
        CHECK( (NEW(AddExpr)(NEW(NumExpr)(1), NEW(MultExpr)(NEW(NumExpr)(2), NEW(VarExpr)("y"))))->has_variable() == true );  // Nested MultExpr has a variable
        CHECK( (NEW(MultExpr)(NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(4)), NEW(NumExpr)(2)))->has_variable() == true );  // AddExpr contains a variable

        // has_variable for expressions that do not contain variables
        CHECK( (NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)))->has_variable() == false );  // No variables
        CHECK( (NEW(MultExpr)(NEW(NumExpr)(3), NEW(NumExpr)(4)))->has_variable() == false );  // No variables
        CHECK( (NEW(AddExpr)(NEW(NumExpr)(5), NEW(NumExpr)(6)))->has_variable() == false );  // No variables in this AddExpr
        CHECK( (NEW(MultExpr)(NEW(NumExpr)(7), NEW(NumExpr)(8)))->has_variable() == false );  // No variables in this MultExpr

        // has_variable for more complex nested expressions (with no variables)
        CHECK( (NEW(AddExpr)(NEW(MultExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3)), NEW(NumExpr)(5)))->has_variable() == false );  // No variables in this expression
        CHECK( (NEW(MultExpr)(NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)), NEW(NumExpr)(3)))->has_variable() == false );  // No variables in this expression
    }

    // // Tests for subst
    // SECTION("Subst") {
    //     //subst NumExpr
    //
    //     // subst AddExpr
    //     CHECK( (NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(7)))
    //    ->subst("x", NEW(VarExpr)("y"))
    //    ->equals(NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(7))) );
    //     // subst MultExpr
    //
    //     // subst VarExpr
    //     CHECK( (NEW(VarExpr)("x"))
    //    ->subst("x", NEW(AddExpr)(NEW(VarExpr)("y"),NEW(NumExpr)(7)))
    //    ->equals(NEW(AddExpr)(NEW(VarExpr)("y"),NEW(NumExpr)(7))) );
    //
    //     // added
    //
    //     // Substitution for NumExpr (should not change the expression)
    //     CHECK( (NEW(NumExpr)(42))
    //     ->subst("x", NEW(VarExpr)("y"))
    //     ->equals(NEW(NumExpr)(42)) );  // NumExpr should stay the same since it doesn't contain any variables
    //
    //     // Substitution for AddExpr (variable in the left side)
    //     CHECK( (NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(7)))
    //     ->subst("x", NEW(VarExpr)("y"))
    //     ->equals(NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(7))) );
    //
    //     // Substitution for AddExpr (variable in the right side)
    //     CHECK( (NEW(AddExpr)(NEW(NumExpr)(7), NEW(VarExpr)("x")))
    //     ->subst("x", NEW(VarExpr)("y"))
    //     ->equals(NEW(AddExpr)(NEW(NumExpr)(7), NEW(VarExpr)("y"))) );
    //
    //     // Substitution for Mul (variable in the left side)
    //     CHECK( (NEW(MultExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(7)))
    //     ->subst("x", NEW(VarExpr)("y"))
    //     ->equals(NEW(MultExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(7))) );
    //
    //     // Substitution for Mul (variable in the right side)
    //     CHECK( (NEW(MultExpr)(NEW(NumExpr)(7), NEW(VarExpr)("x")))
    //     ->subst("x", NEW(VarExpr)("y"))
    //     ->equals(NEW(MultExpr)(NEW(NumExpr)(7), NEW(VarExpr)("y"))) );
    //
    //     // Substitution in VarExpr (replacing x with an AddExpr expression)
    //     CHECK( (NEW(VarExpr)("x"))
    //     ->subst("x", NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(7)))
    //     ->equals(NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(7))) );
    //
    //     // Substitution in VarExpr (no match, should return a copy of the same variable)
    //     CHECK( (NEW(VarExpr)("x"))
    //     ->subst("z", NEW(NumExpr)(5))
    //     ->equals(NEW(VarExpr)("x")) );  // No substitution should occur
    //
    //     // Complex Substitution with AddExpr and Mul (nested)
    //     CHECK( (NEW(AddExpr)(NEW(MultExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(7)), NEW(VarExpr)("y")))
    //     ->subst("x", NEW(VarExpr)("z"))
    //     ->equals(NEW(AddExpr)(NEW(MultExpr)(NEW(VarExpr)("z"), NEW(NumExpr)(7)), NEW(VarExpr)("y"))) );
    //
    //     // Complex Substitution (substitute a variable inside a nested expression)
    //     CHECK( (NEW(MultExpr)(NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(7)), NEW(VarExpr)("y")))
    //     ->subst("x", NEW(VarExpr)("z"))
    //     ->equals(NEW(MultExpr)(NEW(AddExpr)(NEW(VarExpr)("z"), NEW(NumExpr)(7)), NEW(VarExpr)("y"))) );
    //
    //     // Substituting with the same variable (should return the same expression)
    //     CHECK( (NEW(VarExpr)("x"))
    //     ->subst("x", NEW(VarExpr)("x"))
    //     ->equals(NEW(VarExpr)("x")) );  // No change should happen
    //
    //     // Substituting with identical expressions (should still return the same object)
    //     CHECK( (NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(7)))
    //     ->subst("x", NEW(VarExpr)("x"))
    //     ->equals(NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(7))) );  // Should be identical
    //
    //     // Testing for deep substitution with AddExpr and Mul (substituting inside nested AddExpr-Mul expressions)
    //     CHECK( (NEW(MultExpr)(NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(5)), NEW(VarExpr)("y")))
    //     ->subst("x", NEW(VarExpr)("z"))
    //     ->equals(NEW(MultExpr)(NEW(AddExpr)(NEW(VarExpr)("z"), NEW(NumExpr)(5)), NEW(VarExpr)("y"))) );
    // }

    SECTION("Printing") {

        CHECK((NEW(NumExpr)(10))->to_string() == "10");
        CHECK((NEW(MultExpr)(NEW(NumExpr)(5), NEW(NumExpr)(2)))->to_string() == "(5*2)");
        CHECK((NEW(MultExpr)(NEW(NumExpr)(1), NEW(AddExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3))))->to_pretty_string() == "1 * (2 + 3)");
        CHECK((NEW(MultExpr)(NEW(MultExpr)(NEW(NumExpr)(8), NEW(NumExpr)(1)), NEW(VarExpr)("y")))->to_pretty_string() == "(8 * 1) * y");
        CHECK((NEW(MultExpr)(NEW(AddExpr)(NEW(NumExpr)(3), NEW(NumExpr)(5)), NEW(MultExpr)(NEW(NumExpr)(6), NEW(NumExpr)(1))))->to_pretty_string() == "(3 + 5) * 6 * 1");
        CHECK((NEW(MultExpr)(NEW(MultExpr)(NEW(NumExpr)(7), NEW(NumExpr)(7)), NEW(AddExpr)(NEW(NumExpr)(9), NEW(NumExpr)(2))))->to_pretty_string() == "(7 * 7) * (9 + 2)");

        // added

        // 1. Simple multiplication
        CHECK((NEW(MultExpr)(NEW(NumExpr)(4), NEW(NumExpr)(3)))->to_pretty_string() == "4 * 3");

        // 2. Addition inside multiplication with no parentheses around the multiplication (e.g., 3 * (2 + 5))
        CHECK((NEW(MultExpr)(NEW(NumExpr)(3), NEW(AddExpr)(NEW(NumExpr)(2), NEW(NumExpr)(5))))->to_pretty_string() == "3 * (2 + 5)");

        // 3. Complex nested multiplication with addition inside it (e.g., (7 * 3) * (9 + 5))
        CHECK((NEW(MultExpr)(NEW(MultExpr)(NEW(NumExpr)(7), NEW(NumExpr)(3)), NEW(AddExpr)(NEW(NumExpr)(9), NEW(NumExpr)(5))))->to_pretty_string() == "(7 * 3) * (9 + 5)");

        // 4. Test with a variable and multiplication (e.g., 3 * x)
        CHECK((NEW(MultExpr)(NEW(NumExpr)(3), NEW(VarExpr)("x")))->to_pretty_string() == "3 * x");
    }

    // LetExpr

    SECTION("LET") {
        CHECK((NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(VarExpr)("x"))));
        CHECK((NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(VarExpr)("x")))->equals((NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(VarExpr)("x")))));
        CHECK((NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(VarExpr)("x")))->interp(Env::empty)->equals(NEW(NumVal)(5)));
        CHECK((NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(AddExpr)(NEW(LetExpr)("y", NEW(NumExpr)(3), NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(2))), NEW(VarExpr)("x"))))->to_string() == "(_let x=5 _in ((_let y=3 _in (y+2))+x))");

        // added

        // LetExpr with no variables in rhs or body
        // (_let x=2 _in x)
        CHECK((NEW(LetExpr)("x", NEW(NumExpr)(2), NEW(VarExpr)("x")))->equals(NEW(LetExpr)("x", NEW(NumExpr)(2), NEW(VarExpr)("x"))) == true);
        // Expected output: (x) is equal to the input LetExpr expression with x defined as 2

        // LetExpr with nested LetExpr expressions
        // (_let x=2 _in (_let y=3 _in (x+y)))
        PTR(LetExpr) expr1 = NEW(LetExpr)("x", NEW(NumExpr)(2), NEW(LetExpr)("y", NEW(NumExpr)(3), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))));
        PTR(LetExpr) expr2 = NEW(LetExpr)("x", NEW(NumExpr)(2), NEW(LetExpr)("y", NEW(NumExpr)(3), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))));
        CHECK(expr1->equals(expr2) == true);  // Both LetExpr expressions are equal

        // LetExpr with variables in both rhs and body
        // (_let x=3 _in (x+1))
        PTR(LetExpr) expr3 = NEW(LetExpr)("x", NEW(NumExpr)(3), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));
        CHECK(expr3->interp(Env::empty)->equals(NEW(NumVal)(4)));  // Expected result: 3 + 1 = 4

        // LetExpr with rhs containing an expression involving variables
        // (_let x=(y+1) _in (x+2))
        PTR(LetExpr) expr4 = NEW(LetExpr)("x", NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(1)), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(2)));
        CHECK_THROWS_WITH(expr4->interp(Env::empty), "free variable: y");  // VarExpr 'y' should cause an error

        // LetExpr with rhs and body containing variables
        // (_let x=y _in x)
        PTR(LetExpr) expr5 = NEW(LetExpr)("x", NEW(VarExpr)("y"), NEW(VarExpr)("x"));
        CHECK_THROWS_WITH(expr5->interp(Env::empty), "free variable: y");  // 'y' is undefined, so should throw an error

        // LetExpr with an expression where the variable is not used in the body
        // (_let x=2 _in 5)
        PTR(LetExpr) expr6 = NEW(LetExpr)("x", NEW(NumExpr)(2), NEW(NumExpr)(5));
        CHECK(expr6->interp(Env::empty)->equals(NEW(NumVal)(5)));  // Expected result: the body is just 5, so it should return 5 regardless of x

        // example from homework spec
        PTR(LetExpr) expr7 = NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(AddExpr)(NEW(LetExpr)("y", NEW(NumExpr)(3), NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(2))), NEW(VarExpr)("x")));
        CHECK(expr7->to_pretty_string() == "_let x = 5\n_in  (_let y = 3\n    _in  y + 2) + x");

        // basic let
        CHECK((NEW(LetExpr)("x", NEW(NumExpr)(1), NEW(VarExpr)("x")))->to_pretty_string() == "_let x = 1\n_in  x");

        // let in lhs of mult
        CHECK((NEW(MultExpr)(NEW(LetExpr)("x", NEW(NumExpr)(1), NEW(VarExpr)("x")), NEW(NumExpr)(1)))->to_pretty_string() == "(_let x = 1\n_in  x) * 1");

        // let in lhs of add
        CHECK((NEW(AddExpr)(NEW(LetExpr)("x", NEW(NumExpr)(1), NEW(VarExpr)("x")), NEW(NumExpr)(1)))->to_pretty_string() == "(_let x = 1\n_in  x) + 1");

        // let in rhs of add
        CHECK((NEW(AddExpr)(NEW(NumExpr)(1), NEW(LetExpr)("x", NEW(NumExpr)(1), NEW(VarExpr)("x"))))->to_pretty_string() == "1 + _let x = 1\n_in  x");

        // let in rhs of mult
        CHECK((NEW(MultExpr)(NEW(NumExpr)(1), NEW(LetExpr)("x", NEW(NumExpr)(1), NEW(VarExpr)("x"))))->to_pretty_string() == "1 * _let x = 1\n_in  x");

        // let nested in let
        CHECK((NEW(LetExpr)("x", NEW(NumExpr)(1), NEW(LetExpr)("y", NEW(NumExpr)(2), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y")))))->to_pretty_string() == "_let x = 1\n_in  _let y = 2\n    _in  x + y");

        // example from hw spec
        CHECK((NEW(MultExpr)(NEW(MultExpr)(NEW(NumExpr)(2), NEW(LetExpr)("x", NEW(NumExpr)(5), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))), NEW(NumExpr)(3)))->to_pretty_string() == "(2 * _let x = 5\n_in  x + 1) * 3");

    }

    SECTION("BoolExpr") {
        // Test for creation of BoolExpr true and false
        // _true should evaluate to _true
        CHECK((NEW(BoolExpr)(true))->interp(Env::empty)->equals(NEW(BoolVal)(true)));
        // _false should evaluate to _false
        CHECK((NEW(BoolExpr)(false))->interp(Env::empty)->equals(NEW(BoolVal)(false)));

        // Test equality of BoolExpr
        // true == true should return true
        CHECK((NEW(BoolExpr)(true))->equals(NEW(BoolExpr)(true)));
        // true != false should return false
        CHECK_FALSE((NEW(BoolExpr)(true))->equals(NEW(BoolExpr)(false)));
        // false != true should return false
        CHECK_FALSE((NEW(BoolExpr)(false))->equals(NEW(BoolExpr)(true)));

        // // Test substitution on BoolExpr (should return the same since no variables)
        // CHECK((NEW(BoolExpr)(true))->subst("x", NEW(BoolExpr)(false))->equals(NEW(BoolExpr)(true)));
    }
    SECTION("EqExpr") {
        // (1 == 2) + 3 should throw an exception
        CHECK_THROWS_WITH((NEW(AddExpr)(NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)), NEW(NumExpr)(3)))->interp(Env::empty), "AddExpr::interp() failed due to being passed a boolean value");
        // 1==2+3 ->interp is _false
        CHECK((NEW(EqExpr)(NEW(NumExpr)(1), NEW(AddExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3))))->interp(Env::empty)->equals(NEW(BoolVal)(false)));
        // 1+1 == 2+0 should evaluate to _true
        CHECK((NEW(EqExpr)(NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(1)), NEW(AddExpr)(NEW(NumExpr)(2), NEW(NumExpr)(0))))->interp(Env::empty)->equals(NEW(BoolVal)(true)));
        // Test comparison between numbers (1 == 2), should be false
        CHECK((NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)))->interp(Env::empty)->equals(NEW(BoolVal)(false)));
        // 1+1 == 2+0 should return true
        CHECK((NEW(EqExpr)(NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(1)), NEW(AddExpr)(NEW(NumExpr)(2), NEW(NumExpr)(0))))->interp(Env::empty)->equals(NEW(BoolVal)(true)));
        // Test substitution in EqExpr
        PTR(Expr) expr1 = NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2));
        // // Substituting should not change the expression in this case
        // PTR(Expr) expr2 = expr1->subst("x", NEW(NumExpr)(3));
        // Should be the same
        // CHECK(expr1->equals(expr2));
    }
    SECTION("IfExpr") {
        // _if 4 + 1 _then 2 _else 3 does not have a value; interpreting it should raise an exception, because 5 is not a boolean.
        PTR(Expr) expr1 = NEW(IfExpr)(NEW(AddExpr)(NEW(NumExpr)(4), NEW(NumExpr)(1)), NEW(NumExpr)(2), NEW(NumExpr)(3));
        CHECK_THROWS_WITH(expr1->interp(Env::empty), "Interp testPart did not return a BoolVal");
        // nested BoolExpr in IfExpr in LetExpr
        CHECK((NEW(LetExpr)("same", NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)), NEW(IfExpr)(NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)), NEW(AddExpr)(NEW(BoolExpr)(false), NEW(NumExpr)(5)), NEW(NumExpr)(88))))->interp(Env::empty)->equals(NEW(NumVal)(88)));
        // _if 1 == 1 _then 2 _else 3 should return 2 because the condition is true
        PTR(Expr) expr2 = NEW(IfExpr)(NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(1)), NEW(NumExpr)(2), NEW(NumExpr)(3));
        // Should evaluate to 2 because the condition is true
        CHECK((expr2->interp(Env::empty)->equals(NEW(NumVal)(2))));
        // _if 1 == 2 _then 2 _else 3 should return 3 because the condition is false
        PTR(Expr) expr3 = NEW(IfExpr)(NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)), NEW(NumExpr)(2), NEW(NumExpr)(3));
        // Should evaluate to 3 because the condition is false
        CHECK((expr3->interp(Env::empty)->equals(NEW(NumVal)(3))));
        // _if 4 + 1 _then 2 _else 3 should throw because 5 is not a BoolVal
        PTR(Expr) expr4 = NEW(IfExpr)(NEW(AddExpr)(NEW(NumExpr)(4), NEW(NumExpr)(1)), NEW(NumExpr)(2), NEW(NumExpr)(3));
        // Should throw an exception
        CHECK_THROWS_WITH(expr4->interp(Env::empty), "Interp testPart did not return a BoolVal");
        // Nested BoolExpr in IfExpr in LetExpr
        CHECK((NEW(LetExpr)("same", NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)), NEW(IfExpr)(NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)), NEW(AddExpr)(NEW(BoolExpr)(false), NEW(NumExpr)(5)), NEW(NumExpr)(88))))->interp(Env::empty)->equals(NEW(NumVal)(88)));  // Should evaluate to 88
        // Nested IfExpr inside IfExpr
        PTR(Expr) expr5 = NEW(IfExpr)(NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(1)), NEW(IfExpr)(NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(1)), NEW(NumExpr)(5), NEW(NumExpr)(10)), NEW(NumExpr)(20));
        CHECK((expr5->interp(Env::empty)->equals(NEW(NumVal)(5))));  // Should evaluate to 5 as both conditions are true
    }

    SECTION("CallExpr") {
        PTR(Expr) CallEx = NEW(CallExpr)(NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))), NEW(NumExpr)(5));
        CHECK(CallEx->interp(Env::empty)->equals(NEW(NumVal)(6)));
    }

    SECTION("FunExpr") {
        // Creating a simple function: _fun (x) x + 1
        PTR(Expr) funExpr = NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));

        // Check if the function interprets correctly
        CHECK(funExpr->interp(Env::empty)->equals(NEW(FunVal)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)), Env::empty)));
    }

    SECTION("FunExpr Equality") {
        // Two identical functions: _fun (x) x + 1
        PTR(Expr) funExpr1 = NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));
        PTR(Expr) funExpr2 = NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));

        // They should be equal
        CHECK(funExpr1->equals(funExpr2));

        // Modify one of the functions: _fun (x) x + 2
        PTR(Expr) funExpr3 = NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(2)));

        // They should not be equal
        CHECK_FALSE(funExpr1->equals(funExpr3));
    }

    SECTION("CallExpr") {
        // Create a function: _fun (x) x + 1
        PTR(Expr) funExpr = NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));

        // Call the function with argument 5: (x + 1) with x = 5
        PTR(Expr) callExpr = NEW(CallExpr)(funExpr, NEW(NumExpr)(5));

        // Should evaluate to 6 (5 + 1)
        CHECK(callExpr->interp(Env::empty)->equals(NEW(NumVal)(6)));
    }

    SECTION("CallExpr Equality") {
        // Create two identical calls: (_fun (x) x + 1) (5)
        PTR(Expr) funExpr = NEW(FunExpr)("x", NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)));
        PTR(Expr) callExpr1 = NEW(CallExpr)(funExpr, NEW(NumExpr)(5));
        PTR(Expr) callExpr2 = NEW(CallExpr)(funExpr, NEW(NumExpr)(5));

        // They should be equal
        CHECK(callExpr1->equals(callExpr2));

        // Modify one of the calls: (_fun (x) x + 1) (6)
        PTR(Expr) callExpr3 = NEW(CallExpr)(funExpr, NEW(NumExpr)(6));

        // They should not be equal
        CHECK_FALSE(callExpr1->equals(callExpr3));
    }
}
