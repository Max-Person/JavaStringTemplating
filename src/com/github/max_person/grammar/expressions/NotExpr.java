package com.github.max_person.grammar.expressions;

import com.github.max_person.grammar.TemplateInterpreter;

public class NotExpr extends TemplateExpr{
    private TemplateExpr op;
    
    public NotExpr(TemplateExpr op) {
        this.op = op;
    }
    
    @Override
    public Object evaluate(TemplateInterpreter data) {
        return !isTruthy(op.evaluate(data));
    }
}
