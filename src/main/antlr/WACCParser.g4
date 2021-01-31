parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}

program: BEGIN (func)* stat END EOF;

func: type ident OPEN_PARENTHESES (param_list)? CLOSE_PARENTHESES IS stat END;

param_list: param (COMMA param)*;

param: type ident;

stat: SKP
  | type ident EQ assign_rhs
  | assign_lhs EQ assign_rhs
  | READ assign_lhs
  | FREE expr
  | RETURN expr
  | EXIT expr
  | PRINT expr
  | PRINTLN expr
  | IF expr THEN stat ELSE stat FI
  | WHILE expr DO stat DONE
  | BEGIN stat END
  | stat SEMICOLON stat;

assign_lhs: ident
  | array_elem
  | pair_elem;

assign_rhs: expr
  | array_liter
  | NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES
  | pair_elem
  | CALL ident OPEN_PARENTHESES arg_list CLOSE_PARENTHESES;

arg_list: expr (COMMA expr)*;

pair_elem: FST expr | SND expr;

type: base_type | type OPEN_SQUARE CLOSE_SQUARE | pair_type;

base_type: INT | BOOL | CHAR | STRING;

pair_type: PAIR OPEN_PARENTHESES pair_elem_type COMMA pair_elem_type CLOSE_PARENTHESES;

pair_elem_type: base_type | type OPEN_SQUARE CLOSE_SQUARE | PAIR;

expr: INT_LITER
  | BOOL_LITER
  | CHAR_LITER
  | STR_LITER
  | pair_liter
  | ident
  | array_elem
  | unaryOper expr
  | expr binaryOper expr
  | OPEN_PARENTHESES expr CLOSE_PARENTHESES;

pair_liter: NULL;

unaryOper: NOT | MINUS | LEN | ORD | CHR;

binaryOper: PLUS | MINUS | MUL | DIV | MOD | GT | GTE |
            LT | LTE | EQ | NEQ | AND | OR;

ident: ID;

array_elem: ident (OPEN_SQUARE expr CLOSE_SQUARE)+;

array_liter: OPEN_SQUARE (expr (COMMA expr)*)? CLOSE_SQUARE;