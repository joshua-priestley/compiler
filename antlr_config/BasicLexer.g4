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