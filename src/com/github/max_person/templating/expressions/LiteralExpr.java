package com.github.max_person.templating.expressions;

import com.github.max_person.templating.InterpretationData;

public class LiteralExpr<ValueType> extends TemplateExpr {
    public final ValueType value;
    public LiteralExpr(ValueType value){
        this.value = value;
    }
    
    @Override
    public Object evaluate(InterpretationData data) {
        return value;
    }
}
