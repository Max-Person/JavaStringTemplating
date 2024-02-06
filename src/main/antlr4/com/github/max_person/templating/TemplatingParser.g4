parser grammar TemplatingParser;

options { tokenVocab=TemplatingLexer; }

template: templateSection* ;

templateSection: STR
               | interpolation
               ;

interpolation: INTERPOLATION_START interpolationContent INTERPOL_BR_CLOSE
             | SIMPLE_INTERPOLATION
             ;

interpolationContent: (interpolationBalancedContent | INTERPOL_STR)* ;

interpolationBalancedContent: INTERPOL_BR_OP interpolationContent INTERPOL_BR_CLOSE ;



