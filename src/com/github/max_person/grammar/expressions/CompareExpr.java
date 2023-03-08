package com.github.max_person.grammar.expressions;

public class CompareExpr extends NumericExpr{
    public enum CompareOp{
        greater,
        greater_eq,
        less,
        less_eq,
    }
    private CompareOp op;
    
    public CompareExpr(CompareOp op, TemplateExpr op1, TemplateExpr op2) {
        super(op1, op2);
        this.op = op;
    }
    
    @Override
    public Object evaluate(Double val1, Double val2) {
        switch (op){
            case greater -> {
                return val1 > val2;
            }
            case greater_eq -> {
                return val1 >= val2;
            }
            case less -> {
                return val1 < val2;
            }
            case less_eq -> {
                return val1 <= val2;
            }
            default -> throw new IllegalArgumentException();
        }
    }
    
    @Override
    public Object evaluate(Integer val1, Integer val2) {
        switch (op){
            case greater -> {
                return val1 > val2;
            }
            case greater_eq -> {
                return val1 >= val2;
            }
            case less -> {
                return val1 < val2;
            }
            case less_eq -> {
                return val1 <= val2;
            }
            default -> throw new IllegalArgumentException();
        }
    }
}
