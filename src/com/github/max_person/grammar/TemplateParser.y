%define api.package {com.github.max_person.grammar }
%define api.parser.class {TemplateParser}
%define parse.error verbose

%code imports{
  import java.io.InputStream;
  import java.io.InputStreamReader;
  import java.io.Reader;
  import java.io.IOException;
}

%code {
  private Object yyroot = null;
  public Object _parse() throws java.io.IOException{
    if(parse()){
      return yyroot;
    }
    else throw new IllegalArgumentException("Parse failed.");
  }

}

%token INTERPOLATION_START
%token INTERPOLATION_END

%token INC
%token DEC
%right '?'
%right ';'
%right ':'

//операторы
%right CONDITIONAL
%left IFNULL
%left OR
%left AND
%nonassoc EQ NEQ
%nonassoc '>' '<' GREATER_EQ LESS_EQ AS IS
%left '+' '-'
%left '*' '/'
%nonassoc UMINUS '!' PREFIX_INC PREFIX_DEC
%nonassoc '.' '(' ')' '[' ']' POSTFIX_INC POSTFIX_DEC

//литералы
%token<Integer>INTEGER_LITERAL
%token<Double>DOUBLE_LITERAL
%token<String>STRING_LITERAL
%token<Boolean>BOOLEAN_LITERAL
%token<String>IDENTIFIER



%%

string: string INTERPOLATION_START expr INTERPOLATION_END STRING_LITERAL 
      | STRING_LITERAL                    
;

expr: IDENTIFIER
;

%%