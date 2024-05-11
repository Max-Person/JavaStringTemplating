parser grammar TemplatingParser;

options { tokenVocab=TemplatingLexer; }

template: templateSection* ;

templateSection: STR
               | interpolation modifier
               ;

interpolation: INTERPOLATION_START interpolationContent INTERPOL_BR_CLOSE
             | SIMPLE_INTERPOLATION
             ;

interpolationContent: (interpolationBalancedContent | INTERPOL_STR)* ;

interpolationBalancedContent: INTERPOL_BR_OP interpolationContent INTERPOL_BR_CLOSE ;

modifier: NO_MODIFIER?
        | MODIFIER_START modifierContent MODIFIER_END
        ;

modifierContent: mod (COMMA mod)* COMMA? ;

mod: IDENTIFIER
   | IDENTIFIER EQ literal
   | IDENTIFIER LPAR ( literal COMMA )* literal? RPAR
   ;



literal: NULL   #null
     | INTEGER   #int
     | DOUBLE    #double
     | BOOLEAN   #bool
     | STRING    #string
     ;



