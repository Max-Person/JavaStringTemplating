package com.github.max_person.templating.expressions;

import com.github.max_person.templating.InterpretationData;

public class NegExpr extends TemplateExpr{
    private TemplateExpr op;
    
    public NegExpr(TemplateExpr op) {
        this.op = op;
    }
    
    @Override
    public Object evaluate(InterpretationData data) {
        Object val = op.evaluate(data);
        if(!(val instanceof Double || val instanceof Integer ))
            throw new IllegalArgumentException(String.format("Type mismatch in a numeric operation in a template: expected a number (Double/Integer), '%s' found", val.getClass().getSimpleName())); //TODO позиция
        
        if(val instanceof Integer)
            return -(Integer) val;
        else
            return -(Double) val;
    }
}
