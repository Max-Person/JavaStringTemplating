grammar ExpressionsTemplateSyntax;

expr: postfixExpr                                   #postfix_node
    | <assoc=right> op=('-'|'!') right=expr               #prefix
    | left=expr op=('*'|'/') right=expr             #mulDiv
    | left=expr op=('+'|'-') right=expr             #addSub
    | left=expr op=('>'|'>='|'<'|'<=') right=expr   #compare
    | left=expr op=('=='|'!=') right=expr           #equality
    | left=expr '&&' right=expr                     #and
    | left=expr '||' right=expr                     #or
    | <assoc=right> left=expr '?:' right=expr                  #elvis
    | <assoc=right> cond=expr '?' first=expr ':' second=expr          #ternary
;

primary: NULL   #null
    | INTEGER   #int
    | DOUBLE    #double
    | BOOLEAN   #bool
    | STRING    #string
    | IDENTIFIER    #identifier
    | '(' expr ')'  #parenthesis
;

arguments: '(' ( expr ',' )* expr? ')'
;

postfixExpr: primary                 #primary_node
    | IDENTIFIER arguments                   #plainCall
    | postfixExpr '.' IDENTIFIER arguments   #qualifiedCall
    | postfixExpr '.' IDENTIFIER             #qualifiedAccess
;



//LEXER RULES ----

//Literals

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