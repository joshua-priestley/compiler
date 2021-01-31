parser grammar WACCParser;

options {
  tokenVocab=WACCLexer;
}

binaryOper: PLUS | MINUS | MUL | DIV | MOD | GT | GTE |
            LT | LTE | EQ | NEQ | AND | OR;

unaryOper: NOT;

expr: expr binaryOper expr
| unaryOper expr
| INT_LITER
| OPEN_PARENTHESES expr CLOSE_PARENTHESES
| OPEN_SQUARE expr CLOSE_SQUARE
;

// EOF indicates that the program must consume to the end of the input.
prog: (expr)*  EOF ;
