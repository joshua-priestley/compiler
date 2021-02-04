parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}

program: BEGIN (func)* stat END EOF;

func: type ident OPEN_PARENTHESES (param_list)? CLOSE_PARENTHESES IS stat END;

param_list: param (COMMA param)*;

param: type ident;

stat: SKP                                           # skip
  | type ident ASSIGN assign_rhs                    # varDeclaration
  | assign_lhs ASSIGN assign_rhs                    # varAssign
  | READ assign_lhs                                 # read
  | FREE expr                                       # free
  | RETURN expr                                     # return
  | EXIT expr                                       # exit
  | PRINT expr                                      # print
  | PRINTLN expr                                    # println
  | IF expr THEN stat ELSE stat FI                  # if
  | WHILE expr DO stat DONE                         # while
  | BEGIN stat END                                  # begin
  | <assoc=right> stat SEMICOLON stat               # sequence
  ;

assign_lhs: ident
  | array_elem
  | pair_elem;

assign_rhs: expr
  | array_liter
  | NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES
  | pair_elem
  | CALL ident OPEN_PARENTHESES (arg_list)? CLOSE_PARENTHESES;

arg_list: expr (COMMA expr)*;

pair_elem: FST expr | SND expr;

type: base_type | type OPEN_SQUARE CLOSE_SQUARE | pair_type;

base_type: INT | BOOL | CHAR | STRING;

pair_type: PAIR OPEN_PARENTHESES pair_elem_type COMMA pair_elem_type CLOSE_PARENTHESES;

pair_elem_type: base_type | type OPEN_SQUARE CLOSE_SQUARE | PAIR;

expr: INT_LITER                                 # intLiter
  | BOOL_LITER                                  # boolLiter
  | CHAR_LITER                                  # charLiter
  | STR_LITER                                   # strLiter
  | pair_liter                                  # pairLiter
  | ident                                       # id
  | array_elem                                  # arrayElem
  | unaryOper expr                              # unaryOp
  | expr binaryOper expr                        # binaryOp
  | OPEN_PARENTHESES expr CLOSE_PARENTHESES     # parentheses
  ;

pair_liter: NULL;

unaryOper: NOT | MINUS | LEN | ORD | CHR;

binaryOper: pre1 | pre2 | pre3 | pre4 | pre5 | pre6 ;

pre1: MUL | DIV | MOD ;

pre2: PLUS | MINUS ;

pre3: GT | GTE | LT | LTE ;

pre4: EQ | NEQ ;

pre5: AND ;

pre6: OR ;

ident: ID ;

array_elem: ident (OPEN_SQUARE expr CLOSE_SQUARE)+;

array_liter: OPEN_SQUARE (expr (COMMA expr)*)? CLOSE_SQUARE;