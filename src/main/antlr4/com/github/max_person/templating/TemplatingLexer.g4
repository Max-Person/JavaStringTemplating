lexer grammar TemplatingLexer;

STR : (~[$] | '\\$')+ ;

SIMPLE_INTERPOLATION: '$' ('_'|Letter)([_$]|Letter|Digit)* ;

INTERPOLATION_START: '${' -> pushMode(EXPECT_MODIFIER), pushMode(INTERPOL);

mode INTERPOL;

INTERPOL_STR: (~[{}] | '\\'[{}])+ ;
INTERPOL_BR_OP: '{' -> pushMode(INTERPOL) ;
INTERPOL_BR_CLOSE: '}' -> popMode ;


mode EXPECT_MODIFIER;

MODIFIER_START: '[' -> popMode, pushMode(MODIFIER);
NO_MODIFIER: () -> popMode;

mode MODIFIER;

MODIFIER_END: ']' -> popMode;

EQ: '=' ;
COMMA: ',' ;
LPAR: '(' ;
RPAR: ')' ;

INTEGER : DECIMAL ;

DECIMAL : ( '0' | [1-9] Digit* ) ;

DOUBLE : (Digit+ '.' Digit* | '.' Digit+) ExponentPart?
       | Digit+ ExponentPart
       | DECIMAL [dD]
       ;

BOOLEAN : TRUE
        | FALSE
        ;

STRING : '"' (~["] | EscapeSequence)* '"'       //Change later? Linebreaks do not interrupt strings
       | '\'' (~['] | EscapeSequence)* '\''
       ;


TRUE : 'true' ;
FALSE : 'false' ;

NULL : 'null';


IDENTIFIER : Letter LetterOrDigit* ;

// Whitespace and comments

WS: WhiteSpace+ -> channel(HIDDEN);

// Fragments

fragment WhiteSpace : [ \t\r\n\u000C] ;
fragment NonWhiteSpace : ~[ \t\r\n\u000C] ;

fragment EscapeSequence
    : '\\' [btnfr"'\\]
//    | '\\' 'u005c'? ([0-3]? [0-7])? [0-7]
//    | '\\' 'u'+ HexDigit HexDigit HexDigit HexDigit
    ;

fragment LetterOrDigit
    : Letter
    | Digit
    ;

fragment Letter
    : [a-zA-Z$_]
    ;

fragment Digit
    : [0-9]
    ;

fragment HexDigit
    : [0-9a-fA-F]
    ;

fragment ExponentPart
    : [eE] [+-]? Digit+
    ;
