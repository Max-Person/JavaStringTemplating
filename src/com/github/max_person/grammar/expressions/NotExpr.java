package com.github.max_person.grammar.expressions;

import com.github.max_person.grammar.InterpretationData;

public class NotExpr extends TemplateExpr{
    private TemplateExpr op;
    
    public NotExpr(TemplateExpr op) {
        this.op = op;
    }
    
    @Override
    public Object evaluate(InterpretationData data) {
        return !isTruthy(op.evaluate(data));
    }
}
