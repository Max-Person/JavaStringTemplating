package com.github.max_person.grammar.expressions;

import com.github.max_person.grammar.TemplateInterpreter;

public class NeqExpr extends BinaryOpExpr{
    public NeqExpr(TemplateExpr op1, TemplateExpr op2) {
        super(op1, op2);
    }
    
    @Override
    public Object evaluate(TemplateInterpreter data) {
        EqExpr eq = new EqExpr(op1, op2);
        return ! (Boolean) eq.evaluate(data);
    }
}
