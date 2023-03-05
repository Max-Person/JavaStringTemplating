package com.github.max_person.grammar.expressions;

abstract public class BinaryOpExpr extends TemplateExpr{
    protected TemplateExpr op1;
    protected TemplateExpr op2;
    
    public BinaryOpExpr(TemplateExpr op1, TemplateExpr op2){
        this.op1 = op1;
        this.op2 = op2;
    }
}
