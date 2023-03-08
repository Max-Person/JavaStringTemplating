package com.github.max_person.templating.expressions;


public class ArithmeticExpr extends NumericExpr{
    public enum ArithmeticOp{
        add,
        sub,
        mul,
        div,
    }
    private ArithmeticOp op;
    
    public ArithmeticExpr(ArithmeticOp op, TemplateExpr op1, TemplateExpr op2) {
        super(op1, op2);
        this.op = op;
    }
    
    @Override
    public Object evaluate(Double val1, Double val2) {
        switch (op){
            case add -> {
                return val1 + val2;
            }
            case sub -> {
                return val1 - val2;
            }
            case mul -> {
                return val1 * val2;
            }
            case div -> {
                return val1 / val2;
            }
            default -> throw new IllegalArgumentException();
        }
    }
    
    @Override
    public Object evaluate(Integer val1, Integer val2) {
        switch (op){
            case add -> {
                return val1 + val2;
            }
            case sub -> {
                return val1 - val2;
            }
            case mul -> {
                return val1 * val2;
            }
            case div -> {
                return val1.doubleValue()  / val2;
            }
            default -> throw new IllegalArgumentException();
        }
    }
}
