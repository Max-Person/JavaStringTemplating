package com.github.max_person.templating.expressions;

import com.github.max_person.templating.TemplateInterpolationParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Parser for the default interpolation syntax used in templates
 * <p>
 * This syntax currently supports basic arithmetic, comparisons,
 * boolean operations, ternary conditions, field access and method calls
 */
public class TemplateExprParser implements TemplateInterpolationParser<TemplateExpr> {
    @Override
    public TemplateExpr parse(InterpolationContent interpolationContent) {
        ExpressionsTemplateSyntaxParser p = new ExpressionsTemplateSyntaxParser(
            new CommonTokenStream(
                new ExpressionsTemplateSyntaxLexer(CharStreams.fromString(interpolationContent.content()))
            )
        );
        return new ExprBuilder().visit(p.expr());
    }
    
    private static class ExprBuilder extends ExpressionsTemplateSyntaxBaseVisitor<TemplateExpr> {
        
        @Override
        public TemplateExpr visitCompare(ExpressionsTemplateSyntaxParser.CompareContext ctx) {
            CompareExpr.CompareOp op;
            switch (ctx.op.getText()){
                case ">" -> op = CompareExpr.CompareOp.greater;
                case ">=" -> op = CompareExpr.CompareOp.greater_eq;
                case "<" -> op = CompareExpr.CompareOp.less;
                case "<=" -> op = CompareExpr.CompareOp.less_eq;
                default -> throw new IllegalArgumentException();
            }
            return new CompareExpr(op, visit(ctx.left), visit(ctx.right));
        }
        
        @Override
        public TemplateExpr visitOr(ExpressionsTemplateSyntaxParser.OrContext ctx) {
            return new OrExpr(visit(ctx.left), visit(ctx.right));
        }
        
        @Override
        public TemplateExpr visitPrefix(ExpressionsTemplateSyntaxParser.PrefixContext ctx) {
            TemplateExpr right = visit(ctx.right);
            switch (ctx.op.getText()){
                case "-" -> {
                    return new NegExpr(right);
                }
                case "!" -> {
                    return new NotExpr(right);
                }
                default -> throw new IllegalArgumentException();
            }
        }
        
        @Override
        public TemplateExpr visitAnd(ExpressionsTemplateSyntaxParser.AndContext ctx) {
            return new AndExpr(visit(ctx.left), visit(ctx.right));
        }
        
        @Override
        public TemplateExpr visitAddSub(ExpressionsTemplateSyntaxParser.AddSubContext ctx) {
            ArithmeticExpr.ArithmeticOp op;
            switch (ctx.op.getText()){
                case "-" -> op = ArithmeticExpr.ArithmeticOp.sub;
                case "+" -> op = ArithmeticExpr.ArithmeticOp.add;
                default -> throw new IllegalArgumentException();
            }
            return new ArithmeticExpr(op, visit(ctx.left), visit(ctx.right));
        }
        
        @Override
        public TemplateExpr visitElvis(ExpressionsTemplateSyntaxParser.ElvisContext ctx) {
            TemplateExpr left = visit(ctx.left);
            TemplateExpr right = visit(ctx.right);
            return new ConditionalExpr(left, left, right);
        }
        
        @Override
        public TemplateExpr visitEquality(ExpressionsTemplateSyntaxParser.EqualityContext ctx) {
            TemplateExpr left = visit(ctx.left);
            TemplateExpr right = visit(ctx.right);
            switch (ctx.op.getText()){
                case "==" -> {
                    return new EqExpr(left, right);
                }
                case "!=" -> {
                    return new NeqExpr(left, right);
                }
                default -> throw new IllegalArgumentException();
            }
        }
        
        @Override
        public TemplateExpr visitTernary(ExpressionsTemplateSyntaxParser.TernaryContext ctx) {
            return new ConditionalExpr(visit(ctx.cond), visit(ctx.first), visit(ctx.second));
        }
        
        @Override
        public TemplateExpr visitMulDiv(ExpressionsTemplateSyntaxParser.MulDivContext ctx) {
            ArithmeticExpr.ArithmeticOp op;
            switch (ctx.op.getText()){
                case "*" -> op = ArithmeticExpr.ArithmeticOp.mul;
                case "/" -> op = ArithmeticExpr.ArithmeticOp.div;
                default -> throw new IllegalArgumentException();
            }
            return new ArithmeticExpr(op, visit(ctx.left), visit(ctx.right));
        }
        
        @Override
        public TemplateExpr visitNull(ExpressionsTemplateSyntaxParser.NullContext ctx) {
            return new LiteralExpr<>(null);
        }
        
        @Override
        public TemplateExpr visitInt(ExpressionsTemplateSyntaxParser.IntContext ctx) {
            return new LiteralExpr<>(Integer.valueOf(ctx.getText()));
        }
        
        @Override
        public TemplateExpr visitDouble(ExpressionsTemplateSyntaxParser.DoubleContext ctx) {
            return new LiteralExpr<>(Double.valueOf(ctx.getText()));
        }
        
        @Override
        public TemplateExpr visitBool(ExpressionsTemplateSyntaxParser.BoolContext ctx) {
            return new LiteralExpr<>(Boolean.valueOf(ctx.getText()));
        }
        
        @Override
        public TemplateExpr visitString(ExpressionsTemplateSyntaxParser.StringContext ctx) {
            String text = ctx.getText();
            text = text.substring(1, text.length()-1)
                .replace( "\\\\", "\\")
                .replace("\\b","\b")
                .replace("\\t","\t")
                .replace("\\n","\n")
                .replace("\\r","\r")
                .replace("\\'","\'")
                .replace("\\\"","\"");
            return new LiteralExpr<>(text);
        }
        
        @Override
        public TemplateExpr visitIdentifier(ExpressionsTemplateSyntaxParser.IdentifierContext ctx) {
            return new AccessExpr(null, ctx.getText());
        }
        
        @Override
        public TemplateExpr visitParenthesis(ExpressionsTemplateSyntaxParser.ParenthesisContext ctx) {
            return visit(ctx.expr());
        }
        
        @Override
        public TemplateExpr visitQualifiedAccess(ExpressionsTemplateSyntaxParser.QualifiedAccessContext ctx) {
            return new AccessExpr(visit(ctx.postfixExpr()), ctx.IDENTIFIER().getText());
        }
        
        @Override
        public TemplateExpr visitQualifiedCall(ExpressionsTemplateSyntaxParser.QualifiedCallContext ctx) {
            return new CallExpr(visit(ctx.postfixExpr()), ctx.IDENTIFIER().getText(), getArguments(ctx.arguments()));
        }
        
        @Override
        public TemplateExpr visitPlainCall(ExpressionsTemplateSyntaxParser.PlainCallContext ctx) {
            return new CallExpr(null, ctx.IDENTIFIER().getText(), getArguments(ctx.arguments()));
        }
        
        private List<TemplateExpr> getArguments(ExpressionsTemplateSyntaxParser.ArgumentsContext ctx){
            return ctx.expr().stream().map(this::visit).collect(Collectors.toList());
        }
    }
}
