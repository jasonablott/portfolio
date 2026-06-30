//
// Created by Jason Ablott on 2/11/25.
//
#pragma once

#include <iostream>
#include "pointer.h"
#include "expr.h"

#ifndef PARSE_H
#define PARSE_H

/**
    * \brief consumes a single ascii character
    * \param in the input stream to read from and consume from
    * \param expect ascii integer representing the expected character to be consumed
    */
void consume(std::istream &in, int expect);

/**
    * \brief skips whitespace and consumes it until non-whitespace is found
    * \param in the input stream to read from and skip white space in
    */
void skip_whitespace(std::istream &in);

/**
    * \brief parses a comparg expression in a stream, which can be an addend or an addend + a comparg
    * \param in the input stream to read from for parsing
    * \return Expr* new expression to be created by parsing
    */
PTR(Expr) parse_comparg(std::istream &in);

/**
    * \brief parses a numeric expression in a stream
    * \param in the input stream to read from for parsing
    * \return Expr* new Num expression to be created by parsing
    */
PTR(Expr) parse_num(std::istream &in);

/**
    * \brief parses a generic expression, calling parse addend and recursing the right side
    * \param in the input stream to read from for parsing
    * \return Expr* new generic expression to be created by parsing
    */
PTR(Expr) parse_expr(std::istream &in);

/**
    * \brief wrapper function to convert a string into a stream for parsing
    * \param s the string to parse
    * \return Expr*
    */
PTR(Expr) parse_str(std::string s);

/**
    * \brief parses an addend expression and returns new expr objects for its contents
    * \param in the input stream to read from for parsing
    * \return Expr* new addend expression to be created by parsing
    */
PTR(Expr) parse_addend(std::istream &in);

/**
    * \brief parses a multicand expression and returns new expr objects for its contents
    * \param in the input stream to read from for parsing
    * \return Expr* new multicand expression to be created by parsing
    */
PTR(Expr) parse_multicand (std::istream & in);

/**
    * \brief parses an inner expression and returns new expr objects for its contents
    * \param in the input stream to read from for parsing
    * \return Expr* new expression to be created by parsing
    */
PTR(Expr) parse_inner (std::istream & in);

/**
    * \brief parses a variable to create a new Variable object.
    *
    *Variables are defined as beginning with a letter and containing only letters,
    *numbers, or the '_' special character. No spaces are allowed in variable names.
    *
    * \param in the input stream to read from for parsing
    * \return Expr* new Variable expression to be created by parsing
    */
PTR(Expr) parse_var(std::istream &in);

/**
    * \brief parses a let expression to create a Let object
    * \param in the input stream to read from for parsing
    * \return Expr* new Let expression to be created by parsing
    */
PTR(Expr) parse_let(std::istream &in);

/**
    * \brief parses a true expression to create a BoolExpr object
    * \param in the input stream to read from for parsing
    * \return Expr* new BoolExpr created by parsing
    */
PTR(Expr) parse_true(std::istream &in);

/**
    * \brief parses a false expression to create a BoolExpr object
    * \param in the input stream to read from for parsing
    * \return Expr* new BoolExpr created by parsing
    */
PTR(Expr) parse_false(std::istream &in);

/**
    * \brief parses an if expression to create a IfExpr object
    * \param in the input stream to read from for parsing
    * \return Expr* new IfExpr created by parsing
    */
PTR(Expr) parse_if(std::istream &in);

/**
    * \brief parses a keyword which identifies a unique operation
    * \param in the input stream to read from for parsing
    * \return std::string a string representing a keyword, which all begin with '_'
    */
std::string parse_keyword(std::istream &in);

/**
    * \brief parses a variable from a stream
    * \param in the input stream to read from for parsing
    * \return std::string a string representing a stream from the stream
    */
std::string parse_string(std::istream &in);

/**
    * \brief parses a function from a stream
    * \param in the input stream to read from for parsing
    * \return Expr* a new expression representing this function
    */
PTR(Expr) parse_fun(std::istream &in);

#endif //PARSE_H
