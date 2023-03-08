package com.github.max_person.templating.expressions;

import com.github.max_person.templating.InterpretationData;

public class NeqExpr extends BinaryOpExpr{
    public NeqExpr(TemplateExpr op1, TemplateExpr op2) {
        super(op1, op2);
    }
    
    @Override
    public Object evaluate(InterpretationData data) {
        EqExpr eq = new EqExpr(op1, op2);
        return ! (Boolean) eq.evaluate(data);
    }
}
