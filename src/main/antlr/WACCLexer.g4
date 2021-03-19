lexer grammar WACCLexer;

//binary operators
PLUS: '+';
MINUS: '-';
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
ADDN: '+=';
SUBN: '-=';
MULN: '*=';
DIVN: '/=';

//extension
BITWISEAND: '&';
BITWISEOR: '|';
BITWISENOT: '~';

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
fragment LOWER_HEX: 'a'..'f';
fragment UPPER_HEX: 'A'..'F';
fragment OCT_DIGIT: '0'..'7';

INT_LITER: DIGIT+ ;
HEX_LITER: HEX_PREFIX (DIGIT | LOWER_HEX | UPPER_HEX)+ ;
BIN_LITER: BIN_PREFIX DIGIT+ ;
OCT_LITER: OCT_PREFIX OCT_DIGIT+ ;

HEX_PREFIX: '0x' | '0X' ;
BIN_PREFIX: '0b' | '0B' | '2_' ;
OCT_PREFIX: '8_' ;

//skips
SKP: 'skip';
BREAK: 'break';
CONTINUE: 'continues';
EXIT: 'exit';

//
DOT: '.';
SEMICOLON: ';';
COLON: ':';
COMMA: ',';
ASSIGN: '=';

//statements
READ: 'read' ;
FREE: 'free' ;
RETURN: 'return' ;
PRINT: 'print' ;
PRINTLN: 'println' ;

//conditional
IF: 'if';
THEN: 'then';
ELSE: 'else';
FI: 'fi';

//while
WHILE: 'while';
DO: 'do';
DONE: 'done';
FOR: 'for';

//higher order functions
MAP: 'map';
FOLDL: 'foldl';
FOLDR: 'foldr';

//null
NULL: 'null';

//bools
BOOL_LITER: 'true' | 'false';

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
VOID: 'void';

//structs + classes
STRUCT: 'struct';
CLASS: 'class';
NEW: 'new';

//characters
fragment QUOTE: '\'';
fragment D_QUOTE: '"';
fragment CHARACTER: ~[\\'"] | '\\' ESCAPED_CHAR;
CHAR_LITER: QUOTE CHARACTER QUOTE;
STR_LITER: D_QUOTE CHARACTER* D_QUOTE;

//whitespace
WS: [ \t\n\r]+ -> skip;
COMMENT: '#' ~[\n]* '\n' -> skip;

ID: ('_' | [a-zA-Z]) ('_' | [a-zA-Z0-9])* ;

MACRO: 'define ';