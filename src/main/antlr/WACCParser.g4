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
  | assign_lhs sideExpr                             # sideExpression
  | CONTINUE                                        # continue
  | BREAK                                           # break
  ;

assign_lhs: ident                                   # assignLhsId
  | array_elem                                      # assignLhsArray
  | pair_elem                                       # assignLhsPair
  ;

assign_rhs: expr                                                  # assignRhsExpr
  | array_liter                                                   # assignRhsArray
  | NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES    # assignRhsNewpair
  | pair_elem                                                     # assignRhsPairElem
  | CALL ident OPEN_PARENTHESES (arg_list)? CLOSE_PARENTHESES     # assignRhsCall
  ;

arg_list: expr (COMMA expr)*;

pair_elem: FST expr                             # pairFst
  | SND expr                                    # pairSnd
  ;

type: base_type
  | type OPEN_SQUARE CLOSE_SQUARE
  | pair_type;

base_type: INT                                  # baseT
  | BOOL                                        # baseT
  | CHAR                                        # baseT
  | STRING                                      # baseT
  ;

array_type: type OPEN_SQUARE CLOSE_SQUARE;

pair_type: PAIR OPEN_PARENTHESES pair_elem_type COMMA pair_elem_type CLOSE_PARENTHESES;

pair_elem_type: base_type | array_type| PAIR;


expr: (PLUS | MINUS)? INT_LITER                 # liter
  | BOOL_LITER                                  # liter
  | CHAR_LITER                                  # liter
  | STR_LITER                                   # liter
  | pair_liter                                  # pairLiter
  | ident                                       # id
  | array_elem                                  # arrayElem
  | (NOT | MINUS | LEN | ORD | CHR) expr        # unaryOp
  | expr (MUL | DIV | MOD) expr                 # binaryOp
  | expr (PLUS | MINUS) expr                    # binaryOp
  | expr (GT | GTE | LT | LTE) expr             # binaryOp
  | expr (EQ | NEQ) expr                        # binaryOp
  | expr (AND) expr                             # binaryOp
  | expr (OR) expr                              # binaryOp
  | OPEN_PARENTHESES expr CLOSE_PARENTHESES     # parentheses
  ;

pair_liter: NULL;

ident: ID ;

array_elem: ident (OPEN_SQUARE expr CLOSE_SQUARE)+;

array_liter: OPEN_SQUARE (expr (COMMA expr)*)? CLOSE_SQUARE;

sideExpr: nunOp           # sideOperator
  | opN                   # sideOperator
  ;

nunOp: (PLUS PLUS) | (MINUS MINUS);

opN: (ADDN | SUBN | MULN | DIVN) expr;