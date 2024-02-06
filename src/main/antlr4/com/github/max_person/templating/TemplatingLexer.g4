lexer grammar TemplatingLexer;

STR : (~[$] | '\\$')+ ;

SIMPLE_INTERPOLATION: '$' ('_'|Letter)([_$]|Letter|Digit)* ;

INTERPOLATION_START: '${' -> pushMode(INTERPOL);

mode INTERPOL;

INTERPOL_STR: (~[{}] | '\\'[{}])+ ;
INTERPOL_BR_OP: '{' -> pushMode(INTERPOL) ;
INTERPOL_BR_CLOSE: '}' -> popMode ;


fragment Letter: [a-zA-Z];

fragment Digit: [0-9];



