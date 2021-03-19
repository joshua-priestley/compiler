parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}

program: (struct)* (classs)* BEGIN (macro)* (func)* stat END EOF;

classs: CLASS ident OPEN_PARENTHESES (param_list)? CLOSE_PARENTHESES
             OPEN_CURLY (class_member)* (func)* CLOSE_CURLY SEMICOLON;

struct: STRUCT ident OPEN_CURLY (member)+ CLOSE_CURLY SEMICOLON;

member: type ident SEMICOLON;

class_member: member | (declare_var SEMICOLON);

func: type ident OPEN_PARENTHESES (param_list)? CLOSE_PARENTHESES IS stat END;

param_list: param (COMMA param)*;

param: type ident;

stat: SKP                                           # skip
  | declare_var                                     # varDeclaration
  | assign_lhs ASSIGN assign_rhs                    # varAssign
  | READ assign_lhs                                 # read
  | FREE expr                                       # free
  | RETURN expr                                     # return
  | EXIT expr                                       # exit
  | PRINT expr                                      # print
  | PRINTLN expr                                    # println
  | IF expr THEN stat (else_if)* (ELSE stat)? FI    # if
  | WHILE expr DO stat DONE                         # while
  | DO stat WHILE expr DONE                         # do_while
  | FOR for_cond DO stat DONE                       # for_loop
  | BEGIN stat END                                  # begin
  | <assoc=right> stat SEMICOLON stat               # sequence
  | assign_lhs sideExpr                             # sideExpression
  | CONTINUE                                        # continue
  | BREAK                                           # break
  | CALL ident OPEN_PARENTHESES (arg_list)? CLOSE_PARENTHESES # call
  | MAP OPEN_PARENTHESES ident  CLOSE_PARENTHESES
        (OPEN_PARENTHESES arg_list  CLOSE_PARENTHESES)?
        ident                                       # map
  ;

declare_var: type ident ASSIGN assign_rhs;

for_cond: OPEN_PARENTHESES declare_var SEMICOLON expr SEMICOLON stat CLOSE_PARENTHESES;

else_if: ELSE IF expr THEN stat;

assign_lhs: ident                                   # assignLhsId
  | struct_access                                   # assignLhsStruct
  | array_elem                                      # assignLhsArray
  | pair_elem                                       # assignLhsPair
  ;

assign_rhs: expr                                                  # assignRhsExpr
  | array_liter                                                   # assignRhsArray
  | NEWPAIR OPEN_PARENTHESES expr COMMA expr CLOSE_PARENTHESES    # assignRhsNewpair
  | pair_elem                                                     # assignRhsPairElem
  | CALL call_func                                                # assignRhsCall
  | FOLDL OPEN_PARENTHESES bin_op CLOSE_PARENTHESES expr ident    # assignRhsFoldl
  | FOLDR OPEN_PARENTHESES bin_op CLOSE_PARENTHESES expr ident    # assignRhsFoldr
  | NEW ident OPEN_PARENTHESES (arg_list)? CLOSE_PARENTHESES      # assignRhsNewObject
  ;

call_func: (ident DOT)? ident OPEN_PARENTHESES (arg_list)? CLOSE_PARENTHESES;

struct_access: ident DOT (ident | array_elem);

arg_list: (expr|array_liter) (COMMA (expr|array_liter))*;

pair_elem: FST expr                             # pairFst
  | SND expr                                    # pairSnd
  ;

type: base_type
  | struct_type
  | void_type
  | type OPEN_SQUARE CLOSE_SQUARE
  | pair_type;

base_type: INT                                  # baseT
  | BOOL                                        # baseT
  | CHAR                                        # baseT
  | STRING                                      # baseT
  ;

void_type: VOID # voidT;

struct_type: ident;

array_type: type OPEN_SQUARE CLOSE_SQUARE;

pair_type: PAIR OPEN_PARENTHESES pair_elem_type COMMA pair_elem_type CLOSE_PARENTHESES;

pair_elem_type: base_type | array_type| PAIR;

expr: (PLUS | MINUS)? INT_LITER                 # liter
  | HEX_LITER                                   # liter
  | OCT_LITER                                   # liter
  | BIN_LITER                                   # liter
  | BOOL_LITER                                  # liter
  | CHAR_LITER                                  # liter
  | STR_LITER                                   # liter
  | pair_liter                                  # pairLiter
  | ident                                       # id
  | array_elem                                  # arrayElem
  | (NOT | MINUS | LEN | ORD
  | CHR | BITWISENOT) expr                     # unaryOp
  | expr (MUL | DIV | MOD) expr                 # binaryOp
  | expr (PLUS | MINUS) expr                    # binaryOp
  | expr (GT | GTE | LT | LTE) expr             # binaryOp
  | expr (EQ | NEQ) expr                        # binaryOp
  | expr (AND) expr                             # binaryOp
  | expr (OR) expr                              # binaryOp
  | expr (BITWISEAND | BITWISEOR) expr          # binaryOp
  | OPEN_PARENTHESES expr CLOSE_PARENTHESES     # parentheses
  | struct_access                               # structExpr
  ;

bin_op: MUL | DIV | PLUS | MINUS | AND | OR | BITWISEAND | BITWISEOR;

pair_liter: NULL;

ident: ID ;

array_elem: ident (OPEN_SQUARE expr CLOSE_SQUARE)+;

array_liter: OPEN_SQUARE (expr (COMMA expr)*)? CLOSE_SQUARE;

sideExpr: nunOp           # sideOperator
  | opN                   # sideOperator
  ;

nunOp: (PLUS PLUS) | (MINUS MINUS);

opN: (ADDN | SUBN | MULN | DIVN) expr;

macro: MACRO type ident OPEN_PARENTHESES param_list CLOSE_PARENTHESES
                        OPEN_PARENTHESES expr CLOSE_PARENTHESES;