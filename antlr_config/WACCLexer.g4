lexer grammar WACCLexer;

//binary operators
PLUS: '+' ;
MINUS: '-' ;
MUL: '*';
DIV: '/';
MOD: '%';
GT: '>';
GTE: '>=';
LT: '<';
LTE: '<=';
EQ: '==';
NEQ: '!=';
AND: '&&';
OR: '||';

//unary operators
NOT: '!';
LEN: 'len';
ORD: 'ord';
CHR: 'chr';

//program
BEGIN: 'begin';
END: 'end';
IS: 'is';

//brackets
OPEN_PARENTHESES: '(' ;
CLOSE_PARENTHESES: ')' ;
OPEN_SQUARE: '[';
CLOSE_SQUARE: ']';
OPEN_CURLY: '{';
CLOSE_CURLY: '}';

//numbers
fragment DIGIT: '0'..'9' ;
fragment SIGN: PLUS | MINUS;

INT-LITER: SIGN? DIGIT+ ;

//skips
SKP: 'skip';
BREAK: 'break';
CONTINUE: 'continue';
EXIT: 'exit';

//
SEMICOLON: ';';
COLON: ':';
COMMA: ',';
ASSIGN: '=';

//conditional
IF: 'if';
THEN: 'then';
ELSE: 'else';
FI: 'fi';

//while
WHILE: 'while';
DO: 'do';
DONE: 'done';

//for
FOR: 'for'; //rest follows from WHILE
//null
NULL: 'null';

//bools
BOOL-LITER: 'true' | 'false';

//escaped characters
fragment ESCAPED_CHAR:
 ('0'
 |'b'
 |'t'
 |'n'
 |'f'
 |'r'
 |'"'
 |'\''
 |'\\');

NEWPAIR: 'newpair';
FST: 'fst';
SND: 'snd';
CALL: 'call';

//types
INT: 'int';
BOOL: 'bool';
CHAR: 'char';
STRING: 'string';
PAIR: 'pair';

//characters
fragment QUOTE: '\'';
fragment D_QUOTE: '"';
fragment CHARACTER: 
~('\\' 
 | QUOTE 
 | D_QUOTE 
 | '\\' ESCAPED_CHAR);
CHAR-LITER: QUOTE CHARACTER QUOTE;
STR-LITER: D_QUOTE CHARACTER* D_QUOTE;

//whitespace
SPACE: ' ';
WS: [ \n\t]+;
COMMENT: '#' ~(\n)* \n -> skip;
