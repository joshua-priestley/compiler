lexer grammar BasicLexer;

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
XOR: '^';
POW: '**';

//unary operators
NOT: '!';

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

INTEGER: DIGIT+ ;
FLOAT: DIGIT+ '.' DIGIT+;

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
BOOL: TRUE | FALSE;
TRUE: 'true';
FALSE: 'false';

//characters
QUOTE: '\'';
D_QUOTE: '"';
CHARACTER: 'a'..'z' | 'A'..'Z';
CHAR: QUOTE CHARACTER QUOTE;
STRING: D_QUOTE CHARACTER* D_QUOTE;