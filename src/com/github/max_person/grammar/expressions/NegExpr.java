package com.github.max_person.grammar.expressions;

import com.github.max_person.grammar.InterpretationData;

public class NegExpr extends TemplateExpr{
    private TemplateExpr op;
    
    public NegExpr(TemplateExpr op) {
        this.op = op;
    }
    
    @Override
    public Object evaluate(InterpretationData data) {
        Object val = op.evaluate(data);
        if(!(val instanceof Double || val instanceof Integer ))
            throw new TemplateEvaluationException(); //TODO
        
        if(val instanceof Integer)
            return -(Integer) val;
        else
            return -(Double) val;
    }
}
