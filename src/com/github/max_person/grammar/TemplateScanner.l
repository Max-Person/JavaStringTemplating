package com.github.max_person.grammar;

import java.io.IOException;
import java.util.Stack;

%%

%byaccj
%class TemplateScanner
%implements TemplateParser.Lexer
%function _yylex

%{

  @Override
  public void yyerror (String s){
    System.err.println(s);
  }

  private Object yylval = null;
  @Override
  public Object getLVal() {
    return yylval;
  }

  private final static int none = -100;
  private int delayedReturn = none;
  @Override
  public int yylex() throws IOException {
    if(delayedReturn != none){
        int tmp = delayedReturn; 
        delayedReturn = none;
        return tmp;
    }
    return _yylex();
  }



  private Stack<Integer> stack = new Stack<Integer>();

  private void yypushState(int newState) {
    stack.push(yystate());
    yybegin(newState);
  }

  private void yypopState() {
    yybegin(stack.pop());
  }

  
  private void _print(String str){
    //System.out.println(str);
  }


  private String stringLiteral = "";

%}

%x STR_SQ
%x STR_DQ
%s STR_INTERPOLATION
%x SIMPLE_INTERPOLATION

LETTER = [A-Za-z]
DIGIT = [0-9]
HEX_DIGIT = [0-9A-Fa-f]
EXPONENT = [eE][+-]?[0-9]+


%init{
  yypushState(STR_DQ);
%init}


%%

\'                          {stringLiteral = ""; yypushState(STR_SQ);}
\"                          {stringLiteral  = ""; yypushState(STR_DQ);}
<STR_SQ,STR_DQ>[^$\"\']+    {stringLiteral = stringLiteral + yytext();}
<STR_SQ,STR_DQ>\\\$         {stringLiteral = stringLiteral + "$";}
<STR_SQ>\\\'                {stringLiteral = stringLiteral + "'";}
<STR_DQ>\\\"                {stringLiteral = stringLiteral + "\"";}
<STR_DQ>\'+                 {stringLiteral = stringLiteral +  yytext();}
<STR_SQ>\"+                 {stringLiteral = stringLiteral +  yytext();}


<STR_SQ,STR_DQ>\$\{     {
    _print("STRING LITERAL: " + stringLiteral);
    _print("STR INTERPOLATION CONCAT (START)");
    yypushState(STR_INTERPOLATION);

    yylval = stringLiteral;
    delayedReturn = INTERPOLATION_START;  
    return STRING_LITERAL;
}

<STR_SQ,STR_DQ>\$       {
    _print("ERR: Interpolation expected at: " + stringLiteral);
    return YYerror;
}

<STR_INTERPOLATION>\}   {
    stringLiteral = "";
    _print("STR INTERPOLATION CONCAT (END)");
    yypopState();
    
    return INTERPOLATION_END;
}

<STR_SQ,STR_DQ>\$/((_|{LETTER})([_$]|{LETTER}|{DIGIT})*)    {
    _print("STRING LITERAL: " + stringLiteral);
    _print("STR INTERPOLATION CONCAT (START)");
    yypushState(SIMPLE_INTERPOLATION);
    
    yylval = stringLiteral;;
    delayedReturn = INTERPOLATION_START;    
    return STRING_LITERAL;
}

<SIMPLE_INTERPOLATION>((_|{LETTER})([_$]|{LETTER}|{DIGIT})*)    {
    stringLiteral = "";
    _print("IDENTIFIER: " + yytext());
    _print("STR INTERPOLATION CONCAT (END)");
    yypopState();

    yylval = yytext();
    delayedReturn = INTERPOLATION_END;    
    return IDENTIFIER;
}

<STR_SQ>\'    {
    _print("STRING LITERAL: " + stringLiteral);
    yypopState();

    yylval = stringLiteral;;    
    return STRING_LITERAL;
}
<STR_DQ>\"    {
    _print("STRING LITERAL: " + stringLiteral);
    yypopState();

    yylval = stringLiteral;;    
    return STRING_LITERAL;
}
<STR_SQ,STR_DQ><<EOF>>    {
    _print("STRING LITERAL: " + stringLiteral);
    yypopState();

    yylval = stringLiteral;;
    return STRING_LITERAL;
}

[\t \n\r]+      {_print("WHITESPACE");}


null   {
    _print("NULL LITERAL: " + yytext());
    yylval = null;
    return NULL;
}

(false|true)   {
    _print("BOOLEAN LITERAL: " + yytext());
    yylval = Boolean.valueOf(yytext());
    return BOOLEAN_LITERAL;
}

([_$]|{LETTER})([_$]|{LETTER}|{DIGIT})*   {
    _print("IDENTIFIER: " + yytext());
    yylval = yytext();
    return IDENTIFIER;
}

{DIGIT}+        {
    _print("INTEGER LITERAL: " + yytext());
    yylval = Integer.decode(yytext());
    return INTEGER_LITERAL;
}

(0x|0X){HEX_DIGIT}+     {
    _print("INTEGER LITERAL: " + yytext());
    yylval = Integer.decode(yytext());
    return INTEGER_LITERAL;
}

({DIGIT}*\.{DIGIT}+{EXPONENT}?|{DIGIT}+{EXPONENT})      {
    _print("DOUBLE LITERAL: " + yytext());
    yylval = Double.valueOf(yytext());
    return DOUBLE_LITERAL;
}



\=\=        {_print("EQUALITY LEXEM ==: " + yytext()); return EQ;}
\!\=        {_print("INEQUALITY LEXEM !=: " + yytext()); return NEQ;}

\&\&        {_print("BOOLAND LEXEM &&: " + yytext()); return AND;}
\|\|        {_print("BOOLOR LEXEM ||: " + yytext()); return OR;}

\>\=         {_print("GREATEREQ LEXEM >=: " + yytext()); return GREATER_EQ;}
\>           {_print("ARROWRIGHT LEXEM >: " + yytext()); return '>';}
\<\=         {_print("LESSEQ LEXEM <=: " + yytext()); return LESS_EQ;}
\<           {_print("ARROWLEFT LEXEM <: " + yytext()); return '<';}
\?\?         {_print("IFNULL LEXEM ??: " + yytext()); return IFNULL;}
\+\+         {_print("UNARY INCREMENT LEXEM ++: " + yytext()); return INC;}    
\-\-         {_print("UNARY DENCREMENT LEXEM --: " + yytext()); return DEC;}
\+           {_print("PLUS LEXEM +: " + yytext()); return '+';}
\-           {_print("MINUS LEXEM -: " + yytext()); return '-';}
\*           {_print("MUL LEXEM *: " + yytext()); return '*';}
\/           {_print("DIV LEXEM /: " + yytext()); return '/';}
\=           {_print("EQ LEXEM =: " + yytext()); return '=';}
\!           {_print("EXCLAMATION LEXEM !: " + yytext()); return '!';}
\?           {_print("QUESTION LEXEM ?: " + yytext()); return '?';}

\(           {_print("PARL LEXEM (: " + yytext()); return '(';}
\)           {_print("PARR LEXEM ): " + yytext()); return ')';}

\{           {yypushState(YYINITIAL); _print("BRACEL LEXEM {: " + yytext()); return '{';}
\}           {yypopState(); _print("BRACER LEXEM }: " + yytext()); return '}';}

\[           {_print("BRACKETL LEXEM [: " + yytext()); return '[';}
\]           {_print("BRACKETR LEXEM ]: " + yytext()); return ']';}

\.           {_print("DOT LEXEM .: " + yytext()); return '.';}
\,           {_print("COMMA LEXEM .: " + yytext()); return ',';}
\;           {_print("SEMICOLON LEXEM ;: " + yytext());  return ';';}
\:           {_print("COLON LEXEM :: " + yytext()); return ':';}