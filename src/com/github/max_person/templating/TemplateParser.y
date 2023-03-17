%define api.package {com.github.max_person.templating }
%define api.parser.class {TemplateParser}
%define parse.error verbose

%code imports{
import com.github.max_person.templating.expressions.*;
import java.util.List;
}

%code {
  private StringConcatExpr rootExpr = null;
  public TemplateExpr _parse() throws java.io.IOException{
    rootExpr = null;
    if(parse()){
      return rootExpr;
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
%token NULL

%nterm<StringConcatExpr>string
%nterm<TemplateExpr>primary postfixExpr expr
%nterm<List<TemplateExpr>>exprList arguments

%%

string: string INTERPOLATION_START expr INTERPOLATION_END STRING_LITERAL      {$1.addInterpolation($3, new LiteralExpr<String>($5)); $$ = $1; rootExpr = (StringConcatExpr)$$;}
      | STRING_LITERAL                                                        {$$ = new StringConcatExpr(new LiteralExpr<String>($1)); rootExpr = (StringConcatExpr)$$;}
;

primary: NULL           {$$ = new LiteralExpr<Object>(null);}
    | INTEGER_LITERAL   {$$ = new LiteralExpr<Integer>($1);}
    | DOUBLE_LITERAL    {$$ = new LiteralExpr<Double>($1);}
    | BOOLEAN_LITERAL   {$$ = new LiteralExpr<Boolean>($1);}
    | string            {$$ = $1;}
    | IDENTIFIER        {$$ = new AccessExpr(null, $1);}
    | '(' expr ')'      {$$ = $2;}
;

arguments: '(' exprList ',' ')'     {$$ = $2;}
    | '(' exprList ')'              {$$ = $2;}
    | '(' ')'                       {$$ = new ArrayList();}
;

postfixExpr: primary                            {$$ = $1;}                           
    | IDENTIFIER arguments                      {$$ = new CallExpr(null, $1, $2);}                           
    | postfixExpr '.' IDENTIFIER arguments      {$$ = new CallExpr($1, $3, $4);}
    | postfixExpr '.' IDENTIFIER                {$$ = new AccessExpr($1, $3);}
;

expr: postfixExpr             {$$ = $1;}
    | expr IFNULL expr        {$$ = new ConditionalExpr($1, $1, $3);}
    | expr OR expr            {$$ = new OrExpr($1, $3);}  
    | expr AND expr           {$$ = new AndExpr($1, $3);}
    | expr EQ expr            {$$ = new EqExpr($1, $3);}
    | expr NEQ expr           {$$ = new NeqExpr($1, $3);}
    | expr '>' expr           {$$ = new CompareExpr(CompareExpr.CompareOp.greater, $1, $3);}      
    | expr '<' expr           {$$ = new CompareExpr(CompareExpr.CompareOp.less, $1, $3);}      
    | expr GREATER_EQ expr    {$$ = new CompareExpr(CompareExpr.CompareOp.greater_eq, $1, $3);}
    | expr LESS_EQ expr       {$$ = new CompareExpr(CompareExpr.CompareOp.less_eq, $1, $3);}
    | expr '?' expr ':' expr %prec CONDITIONAL      {$$ = new ConditionalExpr($1, $3, $5);}          
    | expr '+' expr           {$$ = new ArithmeticExpr(ArithmeticExpr.ArithmeticOp.add, $1, $3);}      
    | expr '-' expr           {$$ = new ArithmeticExpr(ArithmeticExpr.ArithmeticOp.sub, $1, $3);}      
    | expr '*' expr           {$$ = new ArithmeticExpr(ArithmeticExpr.ArithmeticOp.mul, $1, $3);}       
    | expr '/' expr           {$$ = new ArithmeticExpr(ArithmeticExpr.ArithmeticOp.div, $1, $3);} 
    | '-'  expr %prec UMINUS  {$$ = new NegExpr($2);}
    | '!'  expr               {$$ = new NotExpr($2);}
;

exprList: expr              {$$ = new ArrayList<TemplateExpr>(List.of($1));}
    | exprList ',' expr     {$1.add($3); $$ = $1;}
;

%%