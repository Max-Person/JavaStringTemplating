package com.github.max_person.grammar.expressions;

import com.github.max_person.grammar.InterpretationData;

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
