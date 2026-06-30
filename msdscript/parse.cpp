#include <iostream>
#include "parse.h"
#include "expr.h"
#include "catch.h"


// parse expr
PTR(Expr) parse_expr(std::istream &in) {
    // parse addend on lhs
    PTR(Expr) e = parse_comparg(in);
    skip_whitespace(in);
    // check next char after lhs
    int c = in.peek();
    // if it is '=' this is EqExpr, make new add by recursively parsing expr on right
    if (c == '=') {
        consume(in, '=');
        int c = in.peek();
        if (c != '=') {
            throw std::runtime_error("Expected '==' after assignment in equality expression");
        }   consume(in, '=');
            PTR(Expr) rhs = parse_expr(in);
            return NEW(EqExpr)(e, rhs);
    } else {
        return e;
    }
}

// parse comparg
PTR(Expr) parse_comparg(std::istream &in) {
    // parse addend on lhs
    PTR(Expr) e = parse_addend(in);
    skip_whitespace(in);
    // check next char after lhs
    int c = in.peek();
    // if it is '+' this is add, make new add by recursively parsing expr on right
    if (c == '+') {
        consume(in, '+');
        PTR(Expr) rhs = parse_comparg(in); // changed from parse_expr debugging failing test phase 9
        return NEW(AddExpr)(e, rhs);
    } else {
        return e;
    }
}

// parse addend
PTR(Expr) parse_addend(std::istream &in) {
    // parse multicand on lhs
    PTR(Expr) e = parse_multicand(in);
    skip_whitespace(in);
    // check next char after lhs
    int c = in.peek();
    // if it is '*' this is mult, make new mult by recursively parsing addend on right
    if (c == '*') {
        consume(in, '*');
        PTR(Expr) rhs = parse_addend(in);
        return NEW(MultExpr)(e, rhs);
    } else {
        return e;
    }
}

// parse multicand
PTR(Expr) parse_multicand (std::istream & in) {
    PTR(Expr) e = parse_inner(in);
    while (in.peek() == '(') {
        consume(in, '(');
        PTR(Expr) actual_arg = parse_expr(in);
        consume(in, ')');
        e = NEW(CallExpr)(e, actual_arg);
    }
    return e;
}

// parse inner (was parse multicand)
PTR(Expr) parse_inner (std::istream & in) {
    skip_whitespace (in);
    int c = in.peek();
    // KEYWORDS (start with "_")
    if (c == '_') {
      std::string keyword = parse_keyword(in);
      if (keyword == "let") {
          return parse_let(in);
      } else if (keyword == "true") {
          return parse_true(in);
      } else if (keyword == "false") {
          return parse_false(in);
      } else if (keyword == "if") {
          return parse_if(in);
      } else if (keyword == "fun") {
          return parse_fun(in);
      } else {
          throw std::runtime_error("invalid input");
      }
    }
    // NUMBERS (start with a "-" or a number)
    if ((c == '-') || isdigit(c)) { return parse_num(in); }
    // VARIABLES (start with an alpha character)
    if (isalpha(c)){ return parse_var(in); }
    // PARENTHESES
    if (c == '(') {
        consume(in, '(');
        skip_whitespace(in);
        PTR(Expr) e = parse_expr(in);
        skip_whitespace(in);
        c = in.peek();
        if (c != ')') { throw std::runtime_error("bad input"); }
        consume(in, ')');
        return e;
        // Handle other cases
    } else {
      consume(in, c);
      throw std::runtime_error("bad input");
    }
}

// parse numbers
PTR(Expr) parse_num(std::istream &in) {
    int n = 0;
    bool negative = false;
    // handle negative numbers
    if (in.peek() == '-') {
        negative = true;
        consume (in, '-');
        int c = in.peek();
        if (isspace(c)) {
          throw std::runtime_error("invalid input");
        }
    }
    while (true) {
        int c = in.peek();
        if (isdigit(c)) {
            consume(in, c);
            n = n*10 + (c - '0');
        }
        else {
            break;
        }
    }
    // handle negative zero for '-' case
    if (n == 0 && negative){ throw std::runtime_error("invalid input"); }
    // apply negative as needed
    if (negative) { n = -n; }
    return NEW(NumExpr)(n);
}

// consume a character
void consume(std::istream &in, int expect) {
    int c = in.get();
    if (c!=expect) {
        throw std::runtime_error("consume mismatch");
    }
}

// consume/skip whitespace
void skip_whitespace(std::istream &in) {
    while (1) {
        int c = in.peek();
        if (!isspace(c)){ break; }
        consume(in, c);
    }
}

// parse_var helper
PTR(Expr) parse_var(std::istream &in) {
    int c = in.peek();
    if (!isalpha(c)) {
      throw std::runtime_error("invalid input");
    }
    return NEW(VarExpr)(parse_string(in));
}

// parse_let helper
PTR(Expr) parse_let(std::istream &in) {
    skip_whitespace (in);
    // parse a string which will be the let->variable
    std::string variable = parse_string(in);
    skip_whitespace (in);
    consume(in, '=');
    // parse an expression which will be let->rhs
    skip_whitespace(in);
    PTR(Expr) rhs = parse_expr(in);
    skip_whitespace(in);
    // consume '_in' keyword
    if (in.peek() != '_'){
      throw std::runtime_error("incorrect let syntax");
    }
    if (in.peek() == '_'){
      std::string keyword = parse_keyword(in);
      if (keyword != "in") {
        throw std::runtime_error("invalid input");
      }
    }
    skip_whitespace(in);
    // parse a variable which will be let->body
    PTR(Expr) body = parse_expr(in);
    return NEW(LetExpr)(variable, rhs, body);
}

// parse true helper
PTR(Expr) parse_true(std::istream &in) {
    return NEW(BoolExpr)(true);
}

// parse false helper
PTR(Expr) parse_false(std::istream &in) {
    return NEW(BoolExpr)(false);
}

// parse if helper
PTR(Expr) parse_if(std::istream &in) {
    skip_whitespace (in);
    // parse the testPart expr
    PTR(Expr) testPart = parse_expr(in);
    skip_whitespace (in);
    // handle _then keyword
    if (in.peek() != '_'){
        throw std::runtime_error("incorrect if syntax");
    }
    if (in.peek() == '_'){
        std::string keyword = parse_keyword(in);
        if (keyword != "then") {
            throw std::runtime_error("invalid input");
        }
    }
    skip_whitespace(in);
    // parse the thenPart
    PTR(Expr) thenPart = parse_expr(in);
    skip_whitespace(in);
    if (in.peek() != '_'){
        throw std::runtime_error("incorrect if syntax");
    }
    if (in.peek() == '_'){
        std::string keyword = parse_keyword(in);
        if (keyword != "else") {
            throw std::runtime_error("invalid input");
        }
    }
    skip_whitespace(in);
    // parse the elsePart
    PTR(Expr) elsePart = parse_expr(in);
    skip_whitespace(in);
    return NEW(IfExpr)(testPart, thenPart, elsePart);
}

// parse_keyword helper
std::string parse_keyword(std::istream &in) {
    consume(in, '_');
    return parse_string(in);
}

// parse string function
std::string parse_string(std::istream &in) {
    // read in characters until whitespace is found
    std::string name = "";
    while (true) {
        int c = in.peek();
        if (isalnum(c) || c == '_'){
            consume(in, c);
            name += c;
        } else { break; }
    }
    return name;
}

// parse fun helper
PTR(Expr) parse_fun(std::istream &in) {
    skip_whitespace(in);
    consume(in, '(');
    std::string formal_arg = parse_string(in);
    skip_whitespace(in);
    consume(in, ')');
    skip_whitespace(in);
    PTR(Expr) body = parse_expr(in);
    return NEW(FunExpr)(formal_arg, body);
}

// wrapper for testing
PTR(Expr) parse_str(std::string s){
    std::istringstream inputStream(s);
    return parse_expr(inputStream);
}

// PARSE TESTING

TEST_CASE("parse") {
    // provided test cases
    CHECK_THROWS_WITH( parse_str("()"), "bad input" );
    CHECK( parse_str("(1)")->equals(NEW(NumExpr)(1)) );
    CHECK( parse_str("(((1)))")->equals(NEW(NumExpr)(1)) );
    CHECK_THROWS_WITH( parse_str("(1"), "bad input" );
    CHECK( parse_str("1")->equals(NEW(NumExpr)(1)) );
    CHECK( parse_str("10")->equals(NEW(NumExpr)(10)) );
    CHECK( parse_str("-3")->equals(NEW(NumExpr)(-3)) );
    CHECK( parse_str(" \n 5 ")->equals(NEW(NumExpr)(5)) );
    CHECK_THROWS_WITH( parse_str("-"), "invalid input" );
    CHECK_THROWS_WITH( parse_str(" - 5 "), "invalid input" );
    CHECK( parse_str("x")->equals(NEW(VarExpr)("x")) );
    CHECK( parse_str("xyz")->equals(NEW(VarExpr)("xyz")) );
    CHECK( parse_str("xYz")->equals(NEW(VarExpr)("xYz")) );
    CHECK( parse_str("x_z")->equals(NEW(VarExpr)("x_z")) );
    CHECK( parse_str("x + y")->equals(NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))) );
    CHECK( parse_str("x * y")->equals(NEW(MultExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))) );
    CHECK( parse_str("z * x + y")
    ->equals(NEW(AddExpr)(NEW(MultExpr)(NEW(VarExpr)("z"), NEW(VarExpr)("x")),
    NEW(VarExpr)("y"))) );
    CHECK( parse_str("z * (x + y)")
    ->equals(NEW(MultExpr)(NEW(VarExpr)("z"),
    NEW(AddExpr)(NEW(VarExpr)("x"), NEW(VarExpr)("y"))) ));

    // added from Ben Lemon:

    //check let
    CHECK( parse_str("_let x = 1 _in x + 1")->equals(NEW(LetExpr)("x", NEW(NumExpr)(1), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))));
    //nested let rhs
    CHECK( parse_str("_let x = _let y = 2 _in y + 1 _in x + 1")->equals(NEW(LetExpr)("x", NEW(LetExpr)("y", NEW(NumExpr)(2), NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(1))), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))));
    //nested let body
    CHECK( parse_str("_let x = 1 _in _let x = 2 _in x + 1")->equals(NEW(LetExpr)("x", NEW(NumExpr)(1), NEW(LetExpr)("x", NEW(NumExpr)(2), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))))));

    // added in phase 9 for parsing _true, _false, and _if expressions
    CHECK(parse_str("_false")->equals(NEW(BoolExpr)(false)));
    CHECK(parse_str("_true")->equals(NEW(BoolExpr)(true)));
    CHECK(parse_str("_if 1 _then 2 _else 3")->equals(NEW(IfExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2), NEW(NumExpr)(3))));
    CHECK(parse_str(" _if 4 + 1 _then 2 _else 3 ")->equals(NEW(IfExpr)(NEW(AddExpr)(NEW(NumExpr)(4), NEW(NumExpr)(1)), NEW(NumExpr)(2), NEW(NumExpr)(3))));
    CHECK(parse_str("_if 1 == 2 _then _false + 5 _else 88")->equals(NEW(IfExpr)(NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)), NEW(AddExpr)(NEW(BoolExpr)(false), NEW(NumExpr)(5)), NEW(NumExpr)(88))));
    CHECK(parse_str("_let same = 1 == 2 _in _if 1 == 2 _then _false + 5 _else 88")->equals(NEW(LetExpr)("same", NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)), NEW(IfExpr)(NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)), NEW(AddExpr)(NEW(BoolExpr)(false), NEW(NumExpr)(5)), NEW(NumExpr)(88)))));
}

TEST_CASE("parse _true and _false") {
    CHECK(parse_str("_true")->equals(NEW(BoolExpr)(true)));
    CHECK(parse_str("_false")->equals(NEW(BoolExpr)(false)));
}

TEST_CASE("parse expressions with addition and multiplication") {
    CHECK(parse_str("1 + 2")->equals(NEW(AddExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2))));
    CHECK(parse_str("3 * 4")->equals(NEW(MultExpr)(NEW(NumExpr)(3), NEW(NumExpr)(4))));
    CHECK(parse_str("2 + 3 * 4")->equals(NEW(AddExpr)(NEW(NumExpr)(2), NEW(MultExpr)(NEW(NumExpr)(3), NEW(NumExpr)(4)))));
    CHECK(parse_str("5 * 6 + 7")->equals(NEW(AddExpr)(NEW(MultExpr)(NEW(NumExpr)(5), NEW(NumExpr)(6)), NEW(NumExpr)(7))));
}

TEST_CASE("parse if-else expressions") {
    CHECK(parse_str("_if 1 _then 2 _else 3")->equals(NEW(IfExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2), NEW(NumExpr)(3))));
    CHECK(parse_str("_if 4 + 1 _then 2 _else 3")->equals(NEW(IfExpr)(NEW(AddExpr)(NEW(NumExpr)(4), NEW(NumExpr)(1)), NEW(NumExpr)(2), NEW(NumExpr)(3))));
    CHECK(parse_str("_if 1 == 2 _then _false + 5 _else 88")->equals(NEW(IfExpr)(
        NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)),
        NEW(AddExpr)(NEW(BoolExpr)(false), NEW(NumExpr)(5)),
        NEW(NumExpr)(88)
    )));
}

TEST_CASE("parse equality expressions") {
    CHECK(parse_str("1 == 2")->equals(NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2))));
    CHECK(parse_str("1 == 2 == 3")->equals(NEW(EqExpr)(NEW(NumExpr)(1), NEW(EqExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3)))));
}

TEST_CASE("parse let expressions") {
    CHECK(parse_str("_let x = 1 _in x + 1")->equals(NEW(LetExpr)(
        "x",
        NEW(NumExpr)(1),
        NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))
    )));
    CHECK(parse_str("_let x = _let y = 2 _in y + 1 _in x + 1")->equals(NEW(LetExpr)(
        "x",
        NEW(LetExpr)(
            "y", NEW(NumExpr)(2), NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(1))
        ),
        NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))
    )));
    CHECK(parse_str("_let x = 1 _in _let x = 2 _in x + 1")->equals(NEW(LetExpr)(
        "x",
        NEW(NumExpr)(1),
        NEW(LetExpr)("x", NEW(NumExpr)(2), NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1)))
    )));
}

TEST_CASE("parse complex nested expressions") {
    CHECK(parse_str("1 + 2 * 3")->equals(NEW(AddExpr)(NEW(NumExpr)(1), NEW(MultExpr)(NEW(NumExpr)(2), NEW(NumExpr)(3)))));
    CHECK(parse_str("_let x = _if 1 == 2 _then 3 _else 4 _in x + 1")->equals(NEW(LetExpr)(
        "x",
        NEW(IfExpr)(
            NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)),
            NEW(NumExpr)(3),
            NEW(NumExpr)(4)
        ),
        NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(1))
    )));
}

TEST_CASE("parse invalid inputs") {
    CHECK_THROWS_WITH(parse_str("1 + (2"), "bad input");
    CHECK_THROWS_WITH(parse_str("1 + * 2"), "bad input");
    CHECK_THROWS_WITH(parse_str("_let x 1 _in x + 1"), "consume mismatch");
    CHECK_THROWS_WITH(parse_str("1 == "), "bad input");
    CHECK_THROWS_WITH(parse_str("_if 1 _then 2"), "incorrect if syntax");
}

TEST_CASE("parse deeply nested expressions") {
    CHECK(parse_str("_let x = _let y = _let z = 3 _in z + 4 _in y + 5 _in x + 6")
          ->equals(NEW(LetExpr)("x",
              NEW(LetExpr)("y",
                  NEW(LetExpr)("z", NEW(NumExpr)(3), NEW(AddExpr)(NEW(VarExpr)("z"), NEW(NumExpr)(4))),
                  NEW(AddExpr)(NEW(VarExpr)("y"), NEW(NumExpr)(5))
              ),
              NEW(AddExpr)(NEW(VarExpr)("x"), NEW(NumExpr)(6))
          )));
    CHECK(parse_str("_if _if 1 == 2 _then _true _else _false _then 3 _else 4")
          ->equals(NEW(IfExpr)(
              NEW(IfExpr)(NEW(EqExpr)(NEW(NumExpr)(1), NEW(NumExpr)(2)), NEW(BoolExpr)(true), NEW(BoolExpr)(false)),
              NEW(NumExpr)(3),
              NEW(NumExpr)(4)
          )));
}
